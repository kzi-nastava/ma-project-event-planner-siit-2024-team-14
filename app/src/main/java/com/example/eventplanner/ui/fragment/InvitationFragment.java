package com.example.eventplanner.ui.fragment;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.invitations.InvitationRequestModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.SimpleTextWatcher;
import com.example.eventplanner.data.network.services.invitations.InvitationService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationFragment extends Fragment {

    private LinearLayout emailListContainer;
    private Button sendButton, laterButton;
    private List<String> emails = new ArrayList<>();
    private List<String> existingEmails = new ArrayList<>();
    private int maxGuests;
    private int eventId;

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_MAX_GUESTS = "max_guests";

    public static InvitationFragment newInstance(int eventId, int maxGuests, ArrayList<String> existingEmails) {
        InvitationFragment fragment = new InvitationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        args.putInt(ARG_MAX_GUESTS, maxGuests);
        args.putStringArrayList("existing_emails", existingEmails);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
            maxGuests = getArguments().getInt(ARG_MAX_GUESTS);
            existingEmails = getArguments().getStringArrayList("existing_emails");
            if (existingEmails == null) {
                existingEmails = new ArrayList<>();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitation, container, false);

        emailListContainer = view.findViewById(R.id.email_list_container);
        sendButton = view.findViewById(R.id.send_button);
        laterButton = view.findViewById(R.id.later_button);

        emails.add(""); // start with one empty input
        populateEmailFields();

        sendButton.setOnClickListener(v -> sendInvitations());
        laterButton.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void populateEmailFields() {
        emailListContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // existing emails
        for (String email : existingEmails) {
            View item = inflater.inflate(R.layout.item_existing_email, emailListContainer, false);
            EditText input = item.findViewById(R.id.email_input);
            input.setText(email);
            emailListContainer.addView(item);
        }

        // editable emails
        for (int i = 0; i < emails.size(); i++) {
            final int index = i;
            View item = inflater.inflate(R.layout.item_editable_email, emailListContainer, false);
            EditText input = item.findViewById(R.id.email_input);
            input.setText(emails.get(i));

            input.addTextChangedListener(new SimpleTextWatcher(s -> emails.set(index, s.toString())));

            item.findViewById(R.id.validate_btn).setOnClickListener(v -> validateEmail(index));
            item.findViewById(R.id.remove_btn).setOnClickListener(v -> {
                emails.remove(index);
                populateEmailFields();
            });

            if (i == emails.size() - 1 && totalCount() < maxGuests) {
                item.findViewById(R.id.add_btn).setVisibility(View.VISIBLE);
                item.findViewById(R.id.add_btn).setOnClickListener(v -> {
                    emails.add("");
                    populateEmailFields();
                });
            }

            emailListContainer.addView(item);
        }
    }

    private int totalCount() {
        return existingEmails.size() + emails.size();
    }

    private void validateEmail(int index) {
        String email = emails.get(index);
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid email at position " + (index + 1), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Valid email: " + email, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendInvitations() {
        List<String> validEmails = new ArrayList<>();
        for (String email : emails) {
            email = email.trim();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validEmails.add(email);
            }
        }

        if (validEmails.isEmpty()) {
            Toast.makeText(getContext(), "Please enter at least one valid email.", Toast.LENGTH_SHORT).show();
            return;
        }

        InvitationRequestModel dto = new InvitationRequestModel(eventId, validEmails);

        InvitationService service = ClientUtils.invitationService;
        Call<Void> call = service.sendInvitations(dto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Invitations sent successfully!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Failed to send invitations: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to send invitations: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

