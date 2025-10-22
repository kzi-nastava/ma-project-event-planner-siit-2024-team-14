package com.example.eventplanner.ui.fragment.profiles;

import android.Manifest;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.model.profiles.ChangePasswordRequest;
import com.example.eventplanner.data.model.profiles.UpdateSppRequest;
import com.example.eventplanner.data.model.reports.ReportModel;
import com.example.eventplanner.data.model.users.ProviderModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.profiles.ProviderService;
import com.example.eventplanner.ui.adapter.FavoriteEventsAdapter;
import com.example.eventplanner.ui.adapter.FavoriteSolutionAdapter;
import com.example.eventplanner.ui.fragment.chat.ChatFragment;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProviderProfileFragment extends Fragment {

    private static final String ARG_PROVIDER_ID = "providerId";
    private static final String BASE = "http://10.0.2.2:8080/";

    public static ViewProviderProfileFragment newInstance(int providerId) {
        Bundle b = new Bundle();
        b.putInt(ARG_PROVIDER_ID, providerId);
        ViewProviderProfileFragment f = new ViewProviderProfileFragment();
        f.setArguments(b);
        return f;
    }

    // UI
    private ProgressBar progress;
    private TextView errorText;
    private ScrollView contentScroll;

    private ImageView profileImage;
    private TextView companyNameText, emailText, descriptionText, addressText, cityText, phoneText;
    private Button reportButton, chatButton;

    // Self-actions (like organizer)
    private Button editPhotoButton, editInfoButton, changePasswordButton, deactivateButton;

    // Favorites
    private androidx.recyclerview.widget.RecyclerView favEventsList;
    private TextView favEventsEmpty;
    private FavoriteEventsAdapter favEventsAdapter;

    private androidx.recyclerview.widget.RecyclerView favSolutionsList;
    private TextView favSolutionsEmpty;
    private FavoriteSolutionAdapter favSolutionsAdapter;

    // State
    private int providerId = -1;
    private int loggedUserId = -1;
    private ProviderModel provider;

    // Photo picking
    private ActivityResultLauncher<String> openImagePicker;
    private ActivityResultLauncher<String> requestImagePermission;
    private Uri selectedPhotoUri;

    public ViewProviderProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_provider_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // Bind basics
        progress      = v.findViewById(R.id.progress);
        errorText     = v.findViewById(R.id.errorText);
        contentScroll = v.findViewById(R.id.contentScroll);

        profileImage     = v.findViewById(R.id.profile_image);
        companyNameText  = v.findViewById(R.id.company_name);
        emailText        = v.findViewById(R.id.email);
        descriptionText  = v.findViewById(R.id.description);
        addressText      = v.findViewById(R.id.address);
        cityText         = v.findViewById(R.id.city);
        phoneText        = v.findViewById(R.id.phone_number);

        reportButton = v.findViewById(R.id.report_button);
        chatButton   = v.findViewById(R.id.chat_button);

        // Self-actions
        editPhotoButton      = v.findViewById(R.id.edit_photo_button);
        editInfoButton       = v.findViewById(R.id.edit_info_button);
        changePasswordButton = v.findViewById(R.id.change_password_button);
        deactivateButton     = v.findViewById(R.id.deactivate_button);

        // Favorites
        favEventsList   = v.findViewById(R.id.fav_events_list);
        favEventsEmpty  = v.findViewById(R.id.fav_events_empty);
        favSolutionsList  = v.findViewById(R.id.fav_solutions_list);
        favSolutionsEmpty = v.findViewById(R.id.fav_solutions_empty);

        // Args & session
        if (getArguments() != null) {
            providerId = getArguments().getInt(ARG_PROVIDER_ID, -1);
        }
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
        loggedUserId = prefs.getInt("userId", -1);

        // Favorites adapters
        if (favEventsList != null) {
            favEventsList.setLayoutManager(new LinearLayoutManager(requireContext()));
            favEventsAdapter = new FavoriteEventsAdapter(event -> {
                Fragment details = com.example.eventplanner.ui.fragment.events.EventDetailsFragment
                        .newInstance(event.getId());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, details)
                        .addToBackStack(null)
                        .commit();
            });
            favEventsList.setAdapter(favEventsAdapter);
        }

        if (favSolutionsList != null) {
            favSolutionsList.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            favSolutionsAdapter = new FavoriteSolutionAdapter(item -> {
                // TODO: open solution details when available
            });
            favSolutionsList.setAdapter(favSolutionsAdapter);
        }

        // Photo pickers (same flow as organizer)
        openImagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPhotoUri = uri;
                        uploadNewPhoto();
                    }
                }
        );
        requestImagePermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openImagePicker.launch("image/*");
                    else Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
        );

        // Load data
        setLoading(true);
        if (providerId > 0) fetchProvider(providerId); else showError("Invalid provider.");

        // Report / Chat
        reportButton.setOnClickListener(v1 -> openReportDialog());
        chatButton.setOnClickListener(v12 -> {
            if (provider != null && provider.getId() > 0) {
                ChatFragment chat = ChatFragment.newInstance(provider.getId());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, chat)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(requireContext(), "Provider not available.", Toast.LENGTH_SHORT).show();
            }
        });

        // Self-actions (visibility set after data load)
        editPhotoButton.setOnClickListener(v13 -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can edit only your own photo.", Toast.LENGTH_SHORT).show();
                return;
            }
            pickImage();
        });

        editInfoButton.setOnClickListener(v14 -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can edit only your own profile.", Toast.LENGTH_SHORT).show();
                return;
            }
            openEditDialog();
        });

        changePasswordButton.setOnClickListener(v15 -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can change only your own password.", Toast.LENGTH_SHORT).show();
                return;
            }
            openChangePasswordDialog();
        });

        deactivateButton.setOnClickListener(v16 -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can deactivate only your own account.", Toast.LENGTH_SHORT).show();
                return;
            }
            openDeactivateConfirm();
        });
    }

    private boolean isOwnProfile() {
        return provider != null && loggedUserId > 0 && loggedUserId == provider.getId();
    }

    private void setSelfActionsVisibility() {
        int vis = isOwnProfile() ? View.VISIBLE : View.GONE;
        if (editPhotoButton != null)      editPhotoButton.setVisibility(vis);
        if (editInfoButton != null)       editInfoButton.setVisibility(vis);
        if (changePasswordButton != null) changePasswordButton.setVisibility(vis);
        if (deactivateButton != null)     deactivateButton.setVisibility(vis);
    }

    private void fetchProvider(int id) {
        ProviderService service = ClientUtils.providerService;
        service.getProviderById(id).enqueue(new Callback<ProviderModel>() {
            @Override public void onResponse(Call<ProviderModel> call, Response<ProviderModel> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful() && resp.body() != null) {
                    provider = resp.body();
                    bind(provider);
                    setSelfActionsVisibility();
                    loadFavoriteEvents(provider.getId());
                    loadFavoriteSolutions(provider.getId());
                    setLoading(false);
                } else {
                    showError("Failed to load provider.");
                }
            }
            @Override public void onFailure(Call<ProviderModel> call, Throwable t) {
                if (!isAdded()) return;
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void bind(ProviderModel u) {
        companyNameText.setText(orFallback(u.getCompanyName(), "Provider"));
        emailText.setText("Email: " + orFallback(u.getEmail(), "No email"));
        descriptionText.setText("Description: " + orFallback(u.getDescription(), "No description"));
        addressText.setText("Address: " + orFallback(u.getAddress(), "No address"));
        cityText.setText("City: " + orFallback(u.getCity(), "No city"));

        String phone = u.getPhoneNumber();
        if (TextUtils.isEmpty(phone)) phone = "No phone";
        else if (!phone.startsWith("0")) phone = "0" + phone;
        phoneText.setText("Phone: " + phone);

        String photo = u.getProfilePhoto();
        String finalUrl = null;
        if (!TextUtils.isEmpty(photo)) {
            finalUrl = photo.startsWith("http") ? photo
                    : (BASE + (photo.startsWith("/") ? photo.substring(1) : photo));
        }

        Glide.with(this)
                .load(TextUtils.isEmpty(finalUrl) ? R.drawable.profile_placeholder : finalUrl)
                .centerCrop()
                .placeholder(R.drawable.profile_placeholder)
                .into(profileImage);
    }

    // ===== Favorites: Events =====
    private void loadFavoriteEvents(int userId) {
        if (favEventsAdapter == null) return;
        ProviderService service = ClientUtils.providerService;
        service.getFavoriteEvents(userId).enqueue(new Callback<List<EventModel>>() {
            @Override public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful() && resp.body() != null) {
                    List<EventModel> data = resp.body();
                    favEventsAdapter.submit(data);
                    boolean empty = data.isEmpty();
                    toggleFavEventsEmpty(empty);
                } else toggleFavEventsEmpty(true);
            }
            @Override public void onFailure(Call<List<EventModel>> call, Throwable t) {
                if (!isAdded()) return;
                toggleFavEventsEmpty(true);
            }
        });
    }

    private void toggleFavEventsEmpty(boolean empty) {
        if (favEventsAdapter != null && empty) favEventsAdapter.submit(Collections.emptyList());
        if (favEventsEmpty != null) favEventsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (favEventsList  != null) favEventsList.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    // ===== Favorites: Solutions (stubbed to empty until API is ready) =====
    private void loadFavoriteSolutions(int userId) {
        if (!isAdded() || favSolutionsAdapter == null) return;
        favSolutionsAdapter.submit(Collections.emptyList());
        if (favSolutionsEmpty != null) favSolutionsEmpty.setVisibility(View.VISIBLE);
        if (favSolutionsList  != null) favSolutionsList.setVisibility(View.GONE);
    }

    // ===== Report dialog =====
    private void openReportDialog() {
        if (loggedUserId == -1) {
            Toast.makeText(requireContext(), "You must be logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (provider == null) {
            Toast.makeText(requireContext(), "Profile not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        TextView tv = new TextView(requireContext());
        tv.setText(String.format(Locale.getDefault(),"Reporting: %s", orFallback(provider.getCompanyName(), "Provider")));
        tv.setTextSize(16f);

        EditText etReason = new EditText(requireContext());
        etReason.setHint("Enter reason...");

        root.addView(tv);
        root.addView(etReason);

        new AlertDialog.Builder(requireContext())
                .setTitle("Report User")
                .setView(root)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", (d, w) -> {
                    String reason = etReason.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter a reason.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ReportModel report = new ReportModel(loggedUserId, provider.getId(), reason);
                    submitReport(report);
                })
                .show();
    }

    private void submitReport(ReportModel report) {
        ClientUtils.reportUserService.reportUser(report).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(requireContext(),
                        response.isSuccessful() ? "Report submitted!" : "Error submitting report.",
                        Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===== Self actions (same flow as organizer, but via ProviderService) =====
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

    private void uploadNewPhoto() {
        if (selectedPhotoUri == null || provider == null) return;
        setLoading(true);
        try (InputStream is = requireContext().getContentResolver().openInputStream(selectedPhotoUri);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            if (is == null) {
                setLoading(false);
                Toast.makeText(requireContext(), "Cannot read image", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] buf = new byte[8192];
            int r;
            while ((r = is.read(buf)) != -1) bos.write(buf, 0, r);

            RequestBody imgBody = RequestBody.create(bos.toByteArray(), MediaType.parse("image/*"));
            MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", "photo.png", imgBody);

            ClientUtils.providerService.updatePhoto(provider.getId(), photoPart)
                    .enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                            setLoading(false);
                            if (resp.isSuccessful()) {
                                Toast.makeText(requireContext(), "Photo updated", Toast.LENGTH_SHORT).show();
                                Glide.with(ViewProviderProfileFragment.this)
                                        .load(BASE + "api/providers/get-photo/" + provider.getId() + "?t=" + System.currentTimeMillis())
                                        .placeholder(R.drawable.profile_placeholder)
                                        .centerCrop()
                                        .into(profileImage);
                            } else {
                                Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override public void onFailure(Call<Void> call, Throwable t) {
                            setLoading(false);
                            Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            setLoading(false);
            Toast.makeText(requireContext(), "Cannot read image", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEditDialog() {
        if (provider == null) return;

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        final EditText etCompany = makeEt("Company name", provider.getCompanyName());
        final EditText etDesc    = makeEt("Description",  provider.getDescription());
        final EditText etAddress = makeEt("Address",      provider.getAddress());
        final EditText etCity    = makeEt("City",         provider.getCity());
        final EditText etPhone   = makeEt("Phone",        provider.getPhoneNumber());

        root.addView(etCompany);
        root.addView(etDesc);
        root.addView(etAddress);
        root.addView(etCity);
        root.addView(etPhone);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit profile")
                .setView(root)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (d, w) -> {
                    UpdateSppRequest body = new UpdateSppRequest(
                            provider.getId(),
                            etCompany.getText().toString().trim(),
                            etDesc.getText().toString().trim(),
                            etAddress.getText().toString().trim(),
                            etCity.getText().toString().trim(),
                            etPhone.getText().toString().trim()
                    );

                    setLoading(true);
                    ClientUtils.providerService.updateProvider(body)
                            .enqueue(new Callback<ProviderModel>() {
                                @Override public void onResponse(Call<ProviderModel> call, Response<ProviderModel> resp) {
                                    setLoading(false);
                                    if (resp.isSuccessful() && resp.body() != null) {
                                        provider = resp.body();
                                        bind(provider);
                                        setSelfActionsVisibility();
                                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override public void onFailure(Call<ProviderModel> call, Throwable t) {
                                    setLoading(false);
                                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    private void openChangePasswordDialog() {
        if (provider == null) return;

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        final EditText etOld = makePasswordEt("Old password");
        final EditText etNew = makePasswordEt("New password");
        final EditText etConfirm = makePasswordEt("Confirm new password");

        root.addView(etOld);
        root.addView(etNew);
        root.addView(etConfirm);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change password")
                .setView(root)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Change", (d, w) -> {
                    String oldP = etOld.getText().toString();
                    String newP = etNew.getText().toString();
                    String conf = etConfirm.getText().toString();

                    if (TextUtils.isEmpty(newP) || !newP.equals(conf)) {
                        Toast.makeText(requireContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ChangePasswordRequest body = new ChangePasswordRequest(oldP, newP);
                    setLoading(true);
                    ClientUtils.userService.changePassword(body)
                            .enqueue(new Callback<Void>() {
                                @Override public void onResponse(Call<Void> call, Response<Void> response) {
                                    setLoading(false);
                                    if (response.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Old password incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override public void onFailure(Call<Void> call, Throwable t) {
                                    setLoading(false);
                                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    private void openDeactivateConfirm() {
        if (provider == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Deactivate account")
                .setMessage("Are you sure? This action cannot be undone.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (d, w) -> {
                    setLoading(true);
                    ClientUtils.providerService.deactivate(provider.getId())
                            .enqueue(new Callback<Void>() {
                                @Override public void onResponse(Call<Void> call, Response<Void> response) {
                                    setLoading(false);
                                    if (response.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Account deactivated", Toast.LENGTH_SHORT).show();
                                        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
                                        prefs.edit().clear().apply();
                                        requireActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.home_page_fragment, new com.example.eventplanner.ui.fragment.HomeFragment())
                                                .commit();
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to deactivate", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override public void onFailure(Call<Void> call, Throwable t) {
                                    setLoading(false);
                                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    // Utils
    private void setLoading(boolean loading) {
        if (progress != null) progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (contentScroll != null) contentScroll.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (errorText != null) errorText.setVisibility(View.GONE);
    }

    private void showError(String msg) {
        if (progress != null) progress.setVisibility(View.GONE);
        if (contentScroll != null) contentScroll.setVisibility(View.GONE);
        if (errorText != null) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(msg);
        }
    }

    private static String orFallback(String s, String fb) {
        return TextUtils.isEmpty(s) ? fb : s;
    }

    private EditText makeEt(String hint, String value) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        et.setText(value == null ? "" : value);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return et;
    }

    private EditText makePasswordEt(String hint) {
        EditText et = makeEt(hint, "");
        et.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return et;
    }
}
