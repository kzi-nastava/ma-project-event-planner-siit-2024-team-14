package com.example.eventplanner.ui.fragment.profiles;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.users.ProviderModel;
import com.example.eventplanner.data.model.reports.ReportModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.profiles.ProviderService;
import com.example.eventplanner.ui.fragment.chat.ChatFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProviderProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView companyNameText, emailText, descriptionText, addressText, cityText, phoneText;
    private Button reportButton, chatButton;
    private ProviderModel profileUser;
    private int providerId;
    String baseUrl = "http://10.0.2.2:8080/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_provider_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        companyNameText = view.findViewById(R.id.company_name);
        emailText = view.findViewById(R.id.email);
        descriptionText = view.findViewById(R.id.description);
        addressText = view.findViewById(R.id.address);
        cityText = view.findViewById(R.id.city);
        phoneText = view.findViewById(R.id.phone_number);
        reportButton = view.findViewById(R.id.report_button);
        chatButton = view.findViewById(R.id.chat_button);

        // Sakrij dugme ako nije ulogovan korisnik
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
        int loggedUserId = prefs.getInt("userId", -1);
        if (loggedUserId == -1) {
            reportButton.setVisibility(View.GONE);
        } else {
            reportButton.setVisibility(View.VISIBLE);
        }

        Bundle args = getArguments();
        if (args != null) {
            providerId = args.getInt("providerId", -1);
        }

        if (providerId != -1) {
            fetchProviderData();
        }

        reportButton.setOnClickListener(v -> {
            showReportUserDialog(profileUser);
        });

        chatButton.setOnClickListener(v -> {
            ChatFragment chatFragment = ChatFragment.newInstance(providerId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_page_fragment, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchProviderData() {
        ProviderService service = ClientUtils.providerService;
        service.getProviderById(providerId).enqueue(new Callback<ProviderModel>() {
            @Override
            public void onResponse(Call<ProviderModel> call, Response<ProviderModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProviderModel user = response.body();
                    bindData(user);
                }
            }

            @Override
            public void onFailure(Call<ProviderModel> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load provider", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(ProviderModel user) {
        companyNameText.setText(user.getCompanyName());
        emailText.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "No email"));
        descriptionText.setText("Description: " + (user.getDescription() != null ? user.getDescription() : "No description"));
        addressText.setText("Address: " + (user.getAddress() != null ? user.getAddress() : "No address"));
        cityText.setText("City: " + (user.getCity() != null ? user.getCity() : "No city"));
        phoneText.setText("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "No phone"));

        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            Glide.with(this)
                    .load(baseUrl + user.getProfilePhoto())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(profileImage);
        }

        profileUser = user;
    }

    private void showReportUserDialog(ProviderModel reportedUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_report_user, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        TextView reportingUser = dialogView.findViewById(R.id.reporting_user);
        EditText reasonEditText = dialogView.findViewById(R.id.report_reason);
        Button submitButton = dialogView.findViewById(R.id.submit_report);
        Button cancelButton = dialogView.findViewById(R.id.cancel_report);

        reportingUser.setText("Reporting: " + reportedUser.getCompanyName());

        submitButton.setOnClickListener(v -> {
            String reason = reasonEditText.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a reason for the report.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
            int senderId = prefs.getInt("userId", -1);
            int reportedId = reportedUser.getId();

            ReportModel report = new ReportModel(senderId, reportedId, reason);
            submitReport(report);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void submitReport(ReportModel report) {
        ClientUtils.reportUserService.reportUser(report).enqueue(new Callback<Void>() {
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
