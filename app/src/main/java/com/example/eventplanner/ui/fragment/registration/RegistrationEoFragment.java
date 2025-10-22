package com.example.eventplanner.ui.fragment.registration;

import android.Manifest;
import android.content.Intent; // <-- added
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import com.google.gson.Gson;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.registration.RegistrationEoModel;
import com.example.eventplanner.data.network.registration.ApiClient;
import com.example.eventplanner.data.network.registration.RegistrationEoApi;
import com.example.eventplanner.ui.activity.LoginActivity; // <-- added
import com.example.eventplanner.ui.util.FileUtils;

public class RegistrationEoFragment extends Fragment {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText,
            nameEditText, surnameEditText, addressEditText, phoneNumberEditText, cityEditText;

    private Button uploadPhotoButton, confirmButton;
    private Uri selectedPhotoUri;

    private ActivityResultLauncher<String> openImagePicker;
    private ActivityResultLauncher<String> requestImagePermission;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openImagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPhotoUri = uri;
                        Toast.makeText(requireContext(), "Selected photo", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestImagePermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker.launch("image/*");
                    } else {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Uses the same layout you already had
        return inf.inflate(R.layout.registration_eo_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        emailEditText = v.findViewById(R.id.emailEditText);
        passwordEditText = v.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = v.findViewById(R.id.confirmPasswordEditText);
        nameEditText = v.findViewById(R.id.nameEditText);
        surnameEditText = v.findViewById(R.id.surnameEditText);
        addressEditText = v.findViewById(R.id.addressEditText);
        phoneNumberEditText = v.findViewById(R.id.phoneNumberEditText);
        cityEditText = v.findViewById(R.id.cityEditText);

        uploadPhotoButton = v.findViewById(R.id.uploadPhotoButton);
        confirmButton = v.findViewById(R.id.confirmButton);

        uploadPhotoButton.setOnClickListener(vw -> pickImage());
        confirmButton.setOnClickListener(vw -> submit());
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestImagePermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestImagePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                return;
            }
        }
        openImagePicker.launch("image/*");
    }

    private void submit() {
        String email = text(emailEditText);
        String pass = text(passwordEditText);
        String cpass = text(confirmPasswordEditText);
        String name = text(nameEditText);
        String surname = text(surnameEditText);
        String address = text(addressEditText);
        String phone = text(phoneNumberEditText);
        String city = (cityEditText != null) ? text(cityEditText) : "";

        if (TextUtils.isEmpty(email)) { emailEditText.setError("Email is required"); return; }
        if (TextUtils.isEmpty(pass)) { passwordEditText.setError("Password is required"); return; }
        if (!pass.equals(cpass)) { confirmPasswordEditText.setError("Passwords do not match"); return; }
        if (TextUtils.isEmpty(name)) { nameEditText.setError("Name is required"); return; }
        if (TextUtils.isEmpty(surname)) { surnameEditText.setError("Surname is required"); return; }
        if (TextUtils.isEmpty(address)) { addressEditText.setError("Address is required"); return; }
        if (TextUtils.isEmpty(phone)) { phoneNumberEditText.setError("Phone is required"); return; }

        ProgressBar pb = new ProgressBar(requireContext());
        ViewGroup root = (ViewGroup) getView();
        if (root != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            root.addView(pb, lp);
        }

        RegistrationEoModel dto = new RegistrationEoModel(
                email, pass, cpass, name, surname, address, city, phone
        );
        String json = new Gson().toJson(dto);

        MultipartBody.Part dtoPart = FileUtils.toJsonPart("dto", json);
        MultipartBody.Part photoPart = null;
        if (selectedPhotoUri != null) {
            String filename = email.isEmpty() ? "photo.png" : email + ".png";
            photoPart = FileUtils.toImagePart(requireContext(), "photo", selectedPhotoUri, filename);
        }

        RegistrationEoApi api = ApiClient.get().create(RegistrationEoApi.class);
        api.registerEo(dtoPart, photoPart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> resp) {
                if (root != null) root.removeView(pb);

                if (resp.isSuccessful()) {
                    // Tell the user
                    Toast.makeText(requireContext(),
                            "Registration successful! Please verify your email, then log in.",
                            Toast.LENGTH_LONG).show();

                    // Redirect to Login and clear back stack
                    Intent i = new Intent(requireContext(), LoginActivity.class);
                    i.putExtra("prefill_email", email); // optional prefill
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else if (resp.code() == 409) {
                    toast("User with that email already exists");
                } else {
                    toast("Registration failed. Check your inputs.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (root != null) root.removeView(pb);
                toast("Network error: " + t.getMessage());
            }
        });
    }

    private String text(EditText et) { return et.getText().toString().trim(); }
    private void toast(String s) { Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show(); }
}
