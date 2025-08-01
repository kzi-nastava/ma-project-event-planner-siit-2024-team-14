package com.example.eventplanner.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.users.UpgradeProviderModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.FileUtils;
import com.example.eventplanner.ui.activity.MainActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpgradeAsProviderFragment extends Fragment {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText,
            companyNameEditText, companyDescriptionEditText,
            addressEditText, cityEditText, phoneEditText;
    private Button selectPhotosButton, confirmButton;

    private final int PICK_IMAGES_CODE = 1002;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();

    private String email, password;

    public UpgradeAsProviderFragment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upgrade_as_provider, container, false);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        companyNameEditText = view.findViewById(R.id.companyNameEditText);
        companyDescriptionEditText = view.findViewById(R.id.companyDescriptionEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        selectPhotosButton = view.findViewById(R.id.selectPhotosButton);
        confirmButton = view.findViewById(R.id.submitButton);

        emailEditText.setText(email);
        passwordEditText.setText(password);

        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);

        selectPhotosButton.setOnClickListener(v -> openImagePicker());
        confirmButton.setOnClickListener(v -> submitForm());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select up to 3 images"), PICK_IMAGES_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count && selectedImageUris.size() < 3; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                if (selectedImageUris.size() < 3) {
                    selectedImageUris.add(data.getData());
                }
            }

            Toast.makeText(getContext(), selectedImageUris.size() + " images selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitForm() {
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one photo", Toast.LENGTH_SHORT).show();
            return;
        }

        String confirmPassword = confirmPasswordEditText.getText().toString();
        String companyName = companyNameEditText.getText().toString();
        String companyDescription = companyDescriptionEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        UpgradeProviderModel model = new UpgradeProviderModel(
                userId,
                email,
                password,
                confirmPassword,
                companyName,
                companyDescription,
                address,
                city,
                phone
        );

        // Pretvori DTO u JSON deo
        RequestBody dtoPart = RequestBody.create(
                MediaType.parse("application/json"),
                new Gson().toJson(model)
        );

        // Pripremi multipart za slike
        List<MultipartBody.Part> photoParts = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            MultipartBody.Part part = FileUtils.prepareFilePart(getContext(), uri, "photos");
            if (part != null) photoParts.add(part);
        }

        // Retrofit poziv
        ClientUtils.providerService.upgradeToProvider(dtoPart, photoParts)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Upgrade successful! Please log in again.", Toast.LENGTH_LONG).show();

                            // Logout i preusmeravanje (ako je aktivnost)
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).logoutUser();
                            }
                        } else {
                            Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
