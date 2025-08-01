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
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.users.UpgradeOrganizerModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.FileUtils;
import com.example.eventplanner.ui.activity.MainActivity;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpgradeOrganizerFragment extends Fragment {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText,
            surnameEditText, addressEditText, cityEditText, phoneEditText;
    private Button selectPhotoButton, confirmButton;
    private TextView selectedPhotoTextView;
    private Uri selectedImageUri;
    private String email, password;

    public UpgradeOrganizerFragment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrade_organizer, container, false);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        surnameEditText = view.findViewById(R.id.surnameEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        selectPhotoButton = view.findViewById(R.id.selectPhotoButton);
        selectedPhotoTextView = view.findViewById(R.id.selectedPhotoTextView);
        confirmButton = view.findViewById(R.id.confirmButton);

        emailEditText.setText(email);
        passwordEditText.setText(password);

        selectPhotoButton.setOnClickListener(v -> openImageChooser());
        confirmButton.setOnClickListener(v -> submitForm());

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            selectedPhotoTextView.setText(selectedImageUri.getLastPathSegment());
        }
    }

    private void submitForm() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);


        UpgradeOrganizerModel model = new UpgradeOrganizerModel(
                userId,
                email,
                password,
                confirmPasswordEditText.getText().toString(),
                nameEditText.getText().toString(),
                surnameEditText.getText().toString(),
                addressEditText.getText().toString(),
                cityEditText.getText().toString(),
                phoneEditText.getText().toString()
        );

        RequestBody dtoPart = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(model));
        MultipartBody.Part photoPart = FileUtils.prepareFilePart(getContext(), selectedImageUri, "photo");

        ClientUtils.organizerService.upgradeToOrganizer(dtoPart, photoPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Upgrade successful! Please log in again.", Toast.LENGTH_LONG).show();

                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).logoutUser();
                            }



                        } else {
                            Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
