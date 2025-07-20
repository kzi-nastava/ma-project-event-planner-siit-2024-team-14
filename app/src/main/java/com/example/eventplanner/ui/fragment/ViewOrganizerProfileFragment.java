package com.example.eventplanner.ui.fragment;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.data.model.reports.ReportModel;
import com.example.eventplanner.data.network.ApiClient;
import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.users.OrganizerModel;
import com.example.eventplanner.data.network.services.profiles.OrganizerService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrganizerProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView fullNameText, emailText, addressText, cityText, phoneText;
    private Button reportButton, chatButton;
    private OrganizerModel profileUser;
    private int organizerId;
    String baseUrl = "http://10.0.2.2:8080/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_organizer_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        fullNameText = view.findViewById(R.id.full_name);
        emailText = view.findViewById(R.id.email);
        addressText = view.findViewById(R.id.address);
        cityText = view.findViewById(R.id.city);
        phoneText = view.findViewById(R.id.phone_number);
        reportButton = view.findViewById(R.id.report_button);
        chatButton = view.findViewById(R.id.chat_button);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
        int loggedUserId = prefs.getInt("userId", -1);

        if (loggedUserId == -1) {
            reportButton.setVisibility(View.GONE);
        } else {
            reportButton.setVisibility(View.VISIBLE);
        }


        Bundle args = getArguments();
        if (args != null) {
            organizerId = args.getInt("organizerId", -1);
        }

        if (organizerId != -1) {
            fetchOrganizerData();
        }

        reportButton.setOnClickListener(v -> {
            showReportUserDialog(profileUser);
        });

        chatButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Chat clicked", Toast.LENGTH_SHORT).show();
            // TODO: implement chat logic
        });

        return view;
    }

    private void fetchOrganizerData() {


        OrganizerService service = ApiClient.getOrganizerService();
        service.getOrganizerById(organizerId).enqueue(new Callback<OrganizerModel>() {
            @Override
            public void onResponse(Call<OrganizerModel> call, Response<OrganizerModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrganizerModel user = response.body();
                    bindData(user);
                }
            }

            @Override
            public void onFailure(Call<OrganizerModel> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load organizer", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(OrganizerModel user) {
        fullNameText.setText(user.getName() + " " + user.getSurname());
        emailText.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "No email"));
        addressText.setText("Address: " + (user.getAddress() != null ? user.getAddress() : "No address"));
        cityText.setText("City: " + (user.getCity() != null ? user.getCity() : "No city"));
        phoneText.setText("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "No phone"));

        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            Glide.with(this)
                    .load(baseUrl + user.getProfilePhoto())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(profileImage);
        }

        profileUser = new OrganizerModel();
        profileUser.setId(user.getId());
        profileUser.setName(user.getName());
        profileUser.setSurname(user.getSurname());
        profileUser.setEmail(user.getEmail());
        profileUser.setAddress(user.getAddress());
        profileUser.setCity(user.getCity());
        profileUser.setPhoneNumber(user.getPhoneNumber());

    }

    private void showReportUserDialog(OrganizerModel reportedUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_report_user, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        TextView title = dialogView.findViewById(R.id.report_title);
        TextView reportingUser = dialogView.findViewById(R.id.reporting_user);
        EditText reasonEditText = dialogView.findViewById(R.id.report_reason);
        Button submitButton = dialogView.findViewById(R.id.submit_report);
        Button cancelButton = dialogView.findViewById(R.id.cancel_report);

        String fullName = reportedUser.getName() + " " + reportedUser.getSurname();
        reportingUser.setText("Reporting: " + fullName);

        submitButton.setOnClickListener(v -> {
            String reason = reasonEditText.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a reason for the report.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
            Integer senderId = prefs.getInt("userId", -1);
            Integer reportedId = reportedUser.getId();

            ReportModel report = new ReportModel(senderId, reportedId, reason);
            submitReport(report);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void submitReport(ReportModel report) {
        ApiClient.getReportUserService().reportUser(report).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error submitting report.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
