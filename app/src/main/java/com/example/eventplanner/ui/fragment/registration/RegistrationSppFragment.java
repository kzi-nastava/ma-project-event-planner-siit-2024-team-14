package com.example.eventplanner.ui.fragment.registration;

import android.Manifest;
import android.content.Intent;
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
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.registration.RegistrationSppModel;
import com.example.eventplanner.data.network.registration.ApiClient;
import com.example.eventplanner.data.network.registration.RegistrationSppApi;
import com.example.eventplanner.ui.activity.LoginActivity;
import com.example.eventplanner.ui.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationSppFragment extends Fragment {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText,
            nameEditText, surnameEditText, addressEditText, phoneNumberEditText, cityEditText;

    private Button uploadPhotoButton, confirmButton;
    private final List<Uri> selectedPhotoUris = new ArrayList<>(); // up to 3

    private ActivityResultLauncher<String> requestImagePermission;
    private ActivityResultLauncher<String> singlePicker;         // fallback if you prefer single
    private ActivityResultLauncher<String> multiPickerLegacy;    // not used; kept for clarity
    private ActivityResultLauncher<String[]> multiPicker;        // Actually we’ll use GetMultipleContents (string MIME)

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // (A) Ask runtime permission when needed
        requestImagePermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        pickImages();
                    } else {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // (B) Multiple image picker
        // GetMultipleContents expects a single String mime type; older contract in some libs allowed array, but we pass "image/*".
        multiPicker = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        // limit to max 3
                        List<Uri> toAdd = uris.size() > 3 ? uris.subList(0, 3) : uris;
                        selectedPhotoUris.clear();
                        selectedPhotoUris.addAll(toAdd);
                        Toast.makeText(requireContext(),
                                "Selected " + selectedPhotoUris.size() + " photo(s)",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Optional: single picker if you want it
        singlePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPhotoUris.clear();
                        selectedPhotoUris.add(uri);
                        Toast.makeText(requireContext(), "Selected 1 photo", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reuse your SPP layout (it must contain cityEditText and all other ids you showed)
        return inf.inflate(R.layout.registration_spp_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        emailEditText        = v.findViewById(R.id.emailEditText);
        passwordEditText     = v.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = v.findViewById(R.id.confirmPasswordEditText);
        nameEditText         = v.findViewById(R.id.nameEditText);
        surnameEditText      = v.findViewById(R.id.surnameEditText); // description field per your XML
        addressEditText      = v.findViewById(R.id.addressEditText);
        phoneNumberEditText  = v.findViewById(R.id.phoneNumberEditText);
        cityEditText         = v.findViewById(R.id.cityEditText);    // make sure you added this in XML
        uploadPhotoButton    = v.findViewById(R.id.uploadPhotoButton);
        confirmButton        = v.findViewById(R.id.confirmButton);

        uploadPhotoButton.setOnClickListener(vw -> ensurePermissionThenPick());
        confirmButton.setOnClickListener(vw -> submit());
    }

    private void ensurePermissionThenPick() {
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
        pickImages();
    }

    private void pickImages() {
        // Use the multiple-document picker to allow up to 3
        // Note: OpenMultipleDocuments shows a system picker; user can select multiple.
        multiPicker.launch(new String[]{"image/*"});
        // If you prefer a simpler UI, comment the above and use:
        // singlePicker.launch("image/*");
    }

    private void submit() {
        String email   = text(emailEditText);
        String pass    = text(passwordEditText);
        String cpass   = text(confirmPasswordEditText);
        String name    = text(nameEditText);
        String descr   = text(surnameEditText); // your XML uses this id for description
        String address = text(addressEditText);
        String phone   = text(phoneNumberEditText);
        String city    = (cityEditText != null) ? text(cityEditText) : "";

        if (TextUtils.isEmpty(email))    { emailEditText.setError("Email is required"); return; }
        if (TextUtils.isEmpty(pass))     { passwordEditText.setError("Password is required"); return; }
        if (!pass.equals(cpass))         { confirmPasswordEditText.setError("Passwords do not match"); return; }
        if (TextUtils.isEmpty(name))     { nameEditText.setError("Company name is required"); return; }
        if (TextUtils.isEmpty(descr))    { surnameEditText.setError("Company description is required"); return; }
        if (TextUtils.isEmpty(address))  { addressEditText.setError("Address is required"); return; }
        if (TextUtils.isEmpty(city))     { cityEditText.setError("City is required"); return; }
        if (TextUtils.isEmpty(phone))    { phoneNumberEditText.setError("Phone is required"); return; }

        ProgressBar pb = new ProgressBar(requireContext());
        ViewGroup root = (ViewGroup) getView();
        if (root != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            root.addView(pb, lp);
        }

        // Build DTO (adjust field names to your actual model)
        RegistrationSppModel dto = new RegistrationSppModel(
                email, pass, cpass, name, descr, address, city, phone
        );
        String json = new Gson().toJson(dto);

        MultipartBody.Part dtoPart = FileUtils.toJsonPart("dto", json);

        // Build up to 3 photo parts named "photos"
        List<MultipartBody.Part> photoParts = new ArrayList<>();
        int count = Math.min(3, selectedPhotoUris.size());
        for (int i = 0; i < count; i++) {
            Uri uri = selectedPhotoUris.get(i);
            String filename = email.isEmpty() ? ("photo" + (i+1) + ".png") : (email + "-" + (i+1) + ".png");
            MultipartBody.Part p = FileUtils.toImagePart(requireContext(), "photos", uri, filename);
            if (p != null) photoParts.add(p);
        }

        RegistrationSppApi api = ApiClient.get().create(RegistrationSppApi.class);
        // Your Retrofit interface should look like:
        // @Multipart
        // @POST("api/registration/spp")
        // Call<ResponseBody> registerSpp(@Part MultipartBody.Part dto, @Part List<MultipartBody.Part> photos);
        api.register(dtoPart, photoParts).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> resp) {
                if (root != null) root.removeView(pb);

                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "Registration successful! Please verify your email, then log in.",
                            Toast.LENGTH_LONG).show();

                    // Redirect to Login, clear back stack, and prefill email
                    Intent i = new Intent(requireContext(), LoginActivity.class);
                    i.putExtra("prefill_email", email);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else if (resp.code() == 409) {
                    toast("User with that email already exists");
                } else {
                    toast("Registration failed. Please check your inputs.");
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
