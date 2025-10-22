package com.example.eventplanner.ui.fragment.profiles;

import android.Manifest;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.model.profiles.ChangePasswordRequest;
import com.example.eventplanner.data.model.profiles.UpdateEoRequest;
import com.example.eventplanner.data.model.reports.ReportModel;
import com.example.eventplanner.data.model.users.OrganizerModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.profiles.OrganizerService;
import com.example.eventplanner.ui.adapter.FavoriteEventsAdapter;
import com.example.eventplanner.ui.adapter.FavoriteSolutionAdapter;
import com.example.eventplanner.ui.fragment.chat.ChatFragment;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrganizerProfileFragment extends Fragment {

    private static final String ARG_ORGANIZER_ID = "organizerId";
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    public static ViewOrganizerProfileFragment newInstance(int organizerId) {
        Bundle b = new Bundle();
        b.putInt(ARG_ORGANIZER_ID, organizerId);
        ViewOrganizerProfileFragment f = new ViewOrganizerProfileFragment();
        f.setArguments(b);
        return f;
    }

    // Views
    private ProgressBar progress;
    private TextView errorText;
    private ScrollView contentScroll;

    private ImageView profileImage;
    private TextView fullNameText, emailText, addressText, cityText, phoneText;
    private Button reportButton, chatButton;

    private Button editPhotoButton, editInfoButton, changePasswordButton, deactivateButton;

    // State
    private OrganizerModel profileUser;
    private int organizerId = -1;
    private int loggedUserId = -1;

    // Photo picking
    private ActivityResultLauncher<String> openImagePicker;
    private ActivityResultLauncher<String> requestImagePermission;
    private Uri selectedPhotoUri;

    // for favorite events
    // Favorite events UI
    private androidx.recyclerview.widget.RecyclerView favEventsList;
    private TextView favEventsEmpty;
    private FavoriteEventsAdapter favEventsAdapter;

    // Favorite solutions UI
    private androidx.recyclerview.widget.RecyclerView favSolutionsList;
    private TextView favSolutionsEmpty;
    private FavoriteSolutionAdapter favSolutionsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register result launchers once
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_organizer_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views (including loading & error placeholders)
        progress      = view.findViewById(R.id.progress);
        errorText     = view.findViewById(R.id.errorText);
        contentScroll = view.findViewById(R.id.contentScroll);

        profileImage  = view.findViewById(R.id.profile_image);
        fullNameText  = view.findViewById(R.id.full_name);
        emailText     = view.findViewById(R.id.email);
        addressText   = view.findViewById(R.id.address);
        cityText      = view.findViewById(R.id.city);
        phoneText     = view.findViewById(R.id.phone_number);
        reportButton  = view.findViewById(R.id.report_button);
        chatButton    = view.findViewById(R.id.chat_button);

        editPhotoButton      = view.findViewById(R.id.edit_photo_button);
        editInfoButton       = view.findViewById(R.id.edit_info_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        deactivateButton     = view.findViewById(R.id.deactivate_button);

        favEventsList  = view.findViewById(R.id.fav_events_list);
        favEventsEmpty = view.findViewById(R.id.fav_events_empty);

        favSolutionsList  = view.findViewById(R.id.fav_solutions_list);
        favSolutionsEmpty = view.findViewById(R.id.fav_solutions_empty);

        // Session info
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
        loggedUserId = prefs.getInt("userId", -1);

        // Args
        Bundle args = getArguments();
        if (args != null) {
            organizerId = args.getInt(ARG_ORGANIZER_ID, -1);
        }

        // Button visibility (only if logged in)
        boolean userLogged = (loggedUserId != -1);
        reportButton.setVisibility(userLogged ? View.VISIBLE : View.GONE);
        chatButton.setVisibility(userLogged ? View.VISIBLE : View.GONE);

        // Start loading
        setLoading(true);

        if (organizerId > 0) {
            fetchOrganizerData();
        } else {
            showError("Invalid organizer.");
        }

        // Actions not dependent on loaded profile
        reportButton.setOnClickListener(v -> {
            if (profileUser != null) showReportUserDialog(profileUser);
            else Toast.makeText(requireContext(), "Profile not loaded yet.", Toast.LENGTH_SHORT).show();
        });

        chatButton.setOnClickListener(v -> {
            if (organizerId <= 0) {
                Toast.makeText(requireContext(), "Invalid user.", Toast.LENGTH_SHORT).show();
                return;
            }
            ChatFragment chatFragment = ChatFragment.newInstance(organizerId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_page_fragment, chatFragment) // ensure container exists
                    .addToBackStack(null)
                    .commit();
        });

        // EO actions (only for own profile)
        editPhotoButton.setOnClickListener(v -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can edit only your own photo.", Toast.LENGTH_SHORT).show();
                return;
            }
            pickImage();
        });

        editInfoButton.setOnClickListener(v -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can edit only your own profile.", Toast.LENGTH_SHORT).show();
                return;
            }
            openEditDialog();
        });

        changePasswordButton.setOnClickListener(v -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can change only your own password.", Toast.LENGTH_SHORT).show();
                return;
            }
            openChangePasswordDialog();
        });

        deactivateButton.setOnClickListener(v -> {
            if (!isOwnProfile()) {
                Toast.makeText(requireContext(), "You can deactivate only your own account.", Toast.LENGTH_SHORT).show();
                return;
            }
            openDeactivateConfirm();
        });

        favEventsList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        favEventsAdapter = new FavoriteEventsAdapter(event -> {
            // open EventDetails
            Fragment details = com.example.eventplanner.ui.fragment.events.EventDetailsFragment
                    .newInstance(event.getId());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_page_fragment, details)
                    .addToBackStack(null)
                    .commit();
        });
        favEventsList.setAdapter(favEventsAdapter);

        // Grid looks nice for products/services
        favSolutionsList.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));
        favSolutionsAdapter = new FavoriteSolutionAdapter(item -> {
            // TODO: Open solution details if you have a screen for it
            // e.g., SolutionDetailsFragment.newInstance(item.id)
        });
        favSolutionsList.setAdapter(favSolutionsAdapter);
    }

    private boolean isOwnProfile() {
        return profileUser != null && loggedUserId > 0 && loggedUserId == profileUser.getId();
    }

    private void setSelfActionsVisibility() {
        int vis = isOwnProfile() ? View.VISIBLE : View.GONE;
        editPhotoButton.setVisibility(vis);
        editInfoButton.setVisibility(vis);
        changePasswordButton.setVisibility(vis);
        deactivateButton.setVisibility(vis);
    }

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

    private void fetchOrganizerData() {
        OrganizerService service = ClientUtils.organizerService;
        service.getOrganizerById(organizerId).enqueue(new Callback<OrganizerModel>() {
            @Override
            public void onResponse(Call<OrganizerModel> call, Response<OrganizerModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                    setLoading(false);
                } else {
                    showError("Failed to load organizer.");
                }
            }

            @Override
            public void onFailure(Call<OrganizerModel> call, Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void bindData(OrganizerModel user) {
        profileUser = user;

        setSelfActionsVisibility();

        loadFavoriteEvents(user.getId());

        String fullName = safe(user.getName()) + " " + safe(user.getSurname());
        fullName = fullName.trim().isEmpty() ? "User Name" : fullName.trim();
        fullNameText.setText(fullName);

        emailText.setText("Email: "   + showOrFallback(user.getEmail(), "No email"));
        addressText.setText("Address: " + showOrFallback(user.getAddress(), "No address"));
        cityText.setText("City: "     + showOrFallback(user.getCity(), "No city"));

        String phone = user.getPhoneNumber();
        if (!TextUtils.isEmpty(phone) && !phone.startsWith("0")) phone = "0" + phone;
        phoneText.setText("Phone: " + (TextUtils.isEmpty(phone) ? "No phone" : phone));

        String photo = user.getProfilePhoto();
        String finalUrl = null;
        if (!TextUtils.isEmpty(photo)) {
            finalUrl = photo.startsWith("http") ? photo
                    : (BASE_URL + (photo.startsWith("/") ? photo.substring(1) : photo));
        }

        Glide.with(this)
                .load(TextUtils.isEmpty(finalUrl) ? R.drawable.profile_placeholder : finalUrl)
                .centerCrop()
                .placeholder(R.drawable.profile_placeholder)
                .into(profileImage);
    }

    private void showReportUserDialog(OrganizerModel reportedUser) {
        if (loggedUserId == -1) {
            Toast.makeText(requireContext(), "You must be logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_report_user, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        TextView reportingUser = dialogView.findViewById(R.id.reporting_user);
        EditText reasonEditText = dialogView.findViewById(R.id.report_reason);
        Button submitButton = dialogView.findViewById(R.id.submit_report);
        Button cancelButton = dialogView.findViewById(R.id.cancel_report);

        String fn = (safe(reportedUser.getName()) + " " + safe(reportedUser.getSurname())).trim();
        reportingUser.setText("Reporting: " + (fn.isEmpty() ? "User" : fn));

        submitButton.setOnClickListener(v -> {
            String reason = reasonEditText.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a reason for the report.", Toast.LENGTH_SHORT).show();
                return;
            }
            ReportModel report = new ReportModel(loggedUserId, reportedUser.getId(), reason);
            submitReport(report);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void submitReport(ReportModel report) {
        ClientUtils.reportUserService.reportUser(report).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(requireContext(),
                        response.isSuccessful() ? "Report submitted successfully!" : "Error submitting report.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ====== EO actions (photo / edit / password / deactivate) ======

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
        if (selectedPhotoUri == null || profileUser == null) return;
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

            // PUT /api/organizers/update-photo/{id}
            ClientUtils.organizerService.updatePhoto(profileUser.getId(), photoPart)
                    .enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                            setLoading(false);
                            if (resp.isSuccessful()) {
                                Toast.makeText(requireContext(), "Photo updated", Toast.LENGTH_SHORT).show();
                                Glide.with(ViewOrganizerProfileFragment.this)
                                        .load(BASE_URL + "api/organizers/get-photo/" + profileUser.getId() + "?t=" + System.currentTimeMillis())
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
            e.printStackTrace();
            Toast.makeText(requireContext(), "Cannot read image", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEditDialog() {
        if (profileUser == null) return;

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        final EditText etName    = makeEt("Name",      profileUser.getName());
        final EditText etSurname = makeEt("Surname",   profileUser.getSurname());
        final EditText etAddress = makeEt("Address",   profileUser.getAddress());
        final EditText etCity    = makeEt("City",      profileUser.getCity());
        final EditText etPhone   = makeEt("Phone",     profileUser.getPhoneNumber());

        root.addView(etName);
        root.addView(etSurname);
        root.addView(etAddress);
        root.addView(etCity);
        root.addView(etPhone);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit profile")
                .setView(root)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (d, w) -> {
                    // Body WITH ID (server expects id in body)
                    UpdateEoRequest body = new UpdateEoRequest(
                            profileUser.getId(),
                            etName.getText().toString().trim(),
                            etSurname.getText().toString().trim(),
                            etAddress.getText().toString().trim(),
                            etCity.getText().toString().trim(),
                            etPhone.getText().toString().trim()
                    );
                    setLoading(true);

                    // PUT /api/organizers/update
                    ClientUtils.organizerService.updateOrganizer(body)
                            .enqueue(new Callback<OrganizerModel>() {
                                @Override public void onResponse(Call<OrganizerModel> call, Response<OrganizerModel> response) {
                                    setLoading(false);
                                    if (response.isSuccessful() && response.body() != null) {
                                        OrganizerModel updated = response.body();

                                        // keep the id stable if backend didn't send it
                                        if (updated.getId() == 0) {
                                            updated.setId(profileUser.getId());
                                        }
                                        profileUser = updated; // or just copy fields onto existing profileUser

                                        String newFull = (safe(updated.getName()) + " " + safe(updated.getSurname())).trim();
                                        fullNameText.setText(newFull.isEmpty() ? "User Name" : newFull);
                                        addressText.setText("Address: " + showOrFallback(updated.getAddress(), "No address"));
                                        cityText.setText("City: " + showOrFallback(updated.getCity(), "No city"));
                                        String phone = updated.getPhoneNumber();
                                        if (!TextUtils.isEmpty(phone) && !phone.startsWith("0")) phone = "0" + phone;
                                        phoneText.setText("Phone: " + (TextUtils.isEmpty(phone) ? "No phone" : phone));

                                        // re-evaluate action visibility using organizerId vs loggedUserId
                                        setSelfActionsVisibility();

                                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override public void onFailure(Call<OrganizerModel> call, Throwable t) {
                                    setLoading(false);
                                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    private void openChangePasswordDialog() {
        if (profileUser == null) return;

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
        if (profileUser == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Deactivate account")
                .setMessage("Are you sure? This action cannot be undone.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (d, w) -> {
                    setLoading(true);
                    // PUT /api/organizers/deactivate/{id}
                    ClientUtils.organizerService.deactivate(profileUser.getId())
                            .enqueue(new Callback<Void>() {
                                @Override public void onResponse(Call<Void> call, Response<Void> response) {
                                    setLoading(false);
                                    if (response.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Account deactivated", Toast.LENGTH_SHORT).show();
                                        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
                                        prefs.edit().clear().apply();
                                        // Back to home
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

    // Helpers
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

    private String showOrFallback(String s, String fallback) {
        return TextUtils.isEmpty(s) ? fallback : s;
    }

    private String safe(String s) { return s == null ? "" : s; }

    private void loadFavoriteEvents(int userId) {
        // Optionally show a small spinner, or just proceed
        ClientUtils.organizerService.getFavoriteEvents(userId)
                .enqueue(new retrofit2.Callback<java.util.List<com.example.eventplanner.data.model.events.EventModel>>() {
                    @Override
                    public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            List<EventModel> data = response.body();

                            // Update adapter
                            favEventsAdapter.submit(data);

                            // Toggle empty state
                            boolean empty = data.isEmpty();
                            favEventsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                            favEventsList.setVisibility(empty ? View.GONE : View.VISIBLE);
                        } else {
                            // Treat as empty if server returned 404/empty
                            favEventsAdapter.submit(java.util.Collections.emptyList());
                            favEventsEmpty.setVisibility(View.VISIBLE);
                            favEventsList.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EventModel>> call, Throwable t) {
                        if (!isAdded()) return;
                        // On failure, also show the empty message
                        favEventsAdapter.submit(java.util.Collections.emptyList());
                        favEventsEmpty.setVisibility(View.VISIBLE);
                        favEventsList.setVisibility(View.GONE);
                    }
                });
    }

    private void loadFavoriteSolutions(int userId) {
        if (!isAdded() || favSolutionsAdapter == null) return;

        // Always show empty for now
        favSolutionsAdapter.submit(
                java.util.Collections.<FavoriteSolutionAdapter.SolutionItem>emptyList()
        );

        // Toggle empty state UI
        if (favSolutionsEmpty != null) favSolutionsEmpty.setVisibility(View.VISIBLE);
        if (favSolutionsList  != null) favSolutionsList.setVisibility(View.GONE);
    }


}
