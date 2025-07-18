package com.example.eventplanner.ui.fragment;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.eventplanner.data.network.ApiClient;
import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.OrganizerModel;
import com.example.eventplanner.data.network.services.profiles.OrganizerService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrganizerProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView fullNameText, emailText, addressText, cityText, phoneText;
    private Button reportButton, chatButton;

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

        Bundle args = getArguments();
        if (args != null) {
            organizerId = args.getInt("organizerId", -1);
        }

        if (organizerId != -1) {
            fetchOrganizerData();
        }

        reportButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Report clicked", Toast.LENGTH_SHORT).show();
            // TODO: implement report logic
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
    }
}
