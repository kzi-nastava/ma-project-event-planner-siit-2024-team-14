package com.example.eventplanner.ui.activity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.data.network.registration.ApiClient;
import com.example.eventplanner.data.network.registration.RegistrationSppApi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationSppActivity extends AppCompatActivity {

    private EditText emailEt, passEt, confirmEt, nameEt, descEt, addressEt, cityEt, phoneEt;
    private Button uploadBtn, confirmBtn;
    private final List<Uri> selectedUris = new ArrayList<>();

    // File picker
    private final ActivityResultLauncher<String[]> pickImages =
            registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uris -> {
                if (uris == null) return;
                if (selectedUris.size() + uris.size() > 3) {
                    Toast.makeText(this, "Max 3 photos.", Toast.LENGTH_SHORT).show();
                    int slots = 3 - selectedUris.size();
                    selectedUris.addAll(uris.subList(0, Math.max(0, slots)));
                } else {
                    selectedUris.addAll(uris);
                }
            });

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_spp_activity);

        emailEt   = findViewById(R.id.emailEditText);
        passEt    = findViewById(R.id.passwordEditText);
        confirmEt = findViewById(R.id.confirmPasswordEditText);
        nameEt    = findViewById(R.id.nameEditText);
        descEt    = findViewById(R.id.surnameEditText); // your layout uses this id for description
        addressEt = findViewById(R.id.addressEditText);
        cityEt    = findViewById(R.id.cityEditText);    // add this id in XML if missing
        phoneEt   = findViewById(R.id.phoneNumberEditText);
        uploadBtn = findViewById(R.id.uploadPhotoButton);
        confirmBtn= findViewById(R.id.confirmButton);

        uploadBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 33) {
                // No runtime permission needed for ACTION_OPEN_DOCUMENT
                pickImages.launch(new String[] {"image/*"});
            } else {
                pickImages.launch(new String[] {"image/*"});
            }
        });

        confirmBtn.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String email = val(emailEt), pwd = val(passEt), cpwd = val(confirmEt),
                companyName = val(nameEt), companyDesc = val(descEt),
                address = val(addressEt), city = val(cityEt), phone = val(phoneEt);

        if (email.isEmpty() || pwd.isEmpty() || cpwd.isEmpty() || companyName.isEmpty()
                || companyDesc.isEmpty() || address.isEmpty() || city.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pwd.equals(cpwd)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // dto part as JSON
            String json = "{"
                    + "\"email\":\"" + escape(email) + "\","
                    + "\"password\":\"" + escape(pwd) + "\","
                    + "\"confirmPassword\":\"" + escape(cpwd) + "\","
                    + "\"companyName\":\"" + escape(companyName) + "\","
                    + "\"companyDescription\":\"" + escape(companyDesc) + "\","
                    + "\"address\":\"" + escape(address) + "\","
                    + "\"city\":\"" + escape(city) + "\","
                    + "\"phoneNumber\":\"" + escape(phone) + "\""
                    + "}";

            RequestBody dtoBody = RequestBody.create(
                    json, MediaType.parse("application/json; charset=utf-8"));

            MultipartBody.Part dtoPart = MultipartBody.Part.createFormData("dto", "dto.json", dtoBody);

            // photos[]
            List<MultipartBody.Part> photoParts = new ArrayList<>();
            for (Uri uri : selectedUris) {
                String fileName = getFileName(uri);
                byte[] bytes = readAll(uri);
                RequestBody fileBody = RequestBody.create(bytes, MediaType.parse(getContentResolver().getType(uri)));
                photoParts.add(MultipartBody.Part.createFormData("photos", fileName, fileBody));
            }
            MultipartBody.Part[] photoArray = photoParts.toArray(new MultipartBody.Part[0]);

            RegistrationSppApi api = ApiClient.get().create(RegistrationSppApi.class);
            api.register(dtoPart, photoArray).enqueue(new Callback<ResponseBody>() {
                @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> resp) {
                    if (resp.isSuccessful()) {
                        Toast.makeText(RegistrationSppActivity.this,
                                "Registration successful. Check your email to verify.",
                                Toast.LENGTH_LONG).show();
                        // Optionally navigate to login screen:
                        // startActivity(new Intent(RegistrationSppActivity.this, LoginActivity.class));
                        // finish();
                    } else {
                        Toast.makeText(RegistrationSppActivity.this,
                                "Registration failed: " + resp.code(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(RegistrationSppActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error preparing request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String val(EditText et) { return et.getText() == null ? "" : et.getText().toString().trim(); }

    private String escape(String s) { return s.replace("\"","\\\""); }

    private String getFileName(Uri uri) {
        String name = "photo.jpg";
        try (android.database.Cursor c = getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) name = c.getString(idx);
            }
        }
        return name;
    }

    private byte[] readAll(Uri uri) throws Exception {
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) return new byte[0];
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) bos.write(buf, 0, r);
            return bos.toByteArray();
        }
    }
}
