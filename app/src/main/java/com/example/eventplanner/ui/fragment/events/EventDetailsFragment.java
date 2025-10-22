package com.example.eventplanner.ui.fragment.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.model.events.ToggleFavoriteResponse;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.events.EventService;
import com.example.eventplanner.ui.adapter.ActivitiesAdapter;
import com.example.eventplanner.ui.fragment.budget.EventBudgetFragment;
import com.example.eventplanner.ui.fragment.chat.ChatFragment;
import com.example.eventplanner.ui.fragment.profiles.ViewOrganizerProfileFragment;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {

    public static EventDetailsFragment newInstance(int eventId) {
        Bundle b = new Bundle();
        b.putInt("eventId", eventId);
        EventDetailsFragment f = new EventDetailsFragment();
        f.setArguments(b);
        return f;
    }

    // Views
    private TextView nameText, locationText, dateText, descriptionText, organizerText;
    private ImageView eventImage;
    private ImageButton favoriteButton;
    private Button chatButton;

    // State
    private int eventId;
    private int loggedUserId;
    private String loggedUserRole; // ← added
    private EventModel event;
    private boolean isFavorite = false;

    private androidx.recyclerview.widget.RecyclerView agendaList;
    private TextView agendaEmpty;
    private Button addActivityBtn, exportPdfBtn; // agenda controls (EO owner only)
    private ActivitiesAdapter activitiesAdapter;

    private Button eventPdfBtn; // event details PDF (visible to everyone)

    private static final String BASE = "http://10.0.2.2:8080/api/";

    public EventDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        nameText = root.findViewById(R.id.event_name);
        locationText = root.findViewById(R.id.event_location);
        dateText = root.findViewById(R.id.event_date);
        descriptionText = root.findViewById(R.id.event_description);
        organizerText = root.findViewById(R.id.event_organizer);

        eventImage = root.findViewById(R.id.event_image);
        favoriteButton = root.findViewById(R.id.favorite_button);
        chatButton = root.findViewById(R.id.chat_button);

        agendaList = root.findViewById(R.id.agenda_list);
        agendaEmpty = root.findViewById(R.id.agenda_empty);
        addActivityBtn = root.findViewById(R.id.add_activity_btn);
        exportPdfBtn = root.findViewById(R.id.export_pdf_btn);

        // Hide agenda editor buttons by default; we'll enable them after we know role & owner
        if (addActivityBtn != null) addActivityBtn.setVisibility(View.GONE);

        if (getArguments() != null) {
            eventId = getArguments().getInt("eventId", -1);
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        loggedUserId = prefs.getInt("userId", -1);
        loggedUserRole = prefs.getString("role", null); // ← added

        if (eventId != -1) {
            fetchEventDetails(eventId);

            // Child budget fragment
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.budget_layout, EventBudgetFragment.newInstance(eventId))
                    .commit();
        }

        // Favorite toggle
        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(btn -> {
                if (loggedUserId <= 0 || eventId <= 0) {
                    Toast.makeText(requireContext(), "Please log in to favorite events.", Toast.LENGTH_SHORT).show();
                    return;
                }
                toggleFavorite();
            });
        }

        // Chat with organizer
        if (chatButton != null) {
            chatButton.setOnClickListener(btn -> {
                if (event != null && event.getOrganizerId() > 0) {
                    ChatFragment chat = ChatFragment.newInstance(event.getOrganizerId());
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.home_page_fragment, chat)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(requireContext(), "Organizer not available.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        agendaList.setLayoutManager(new LinearLayoutManager(requireContext()));
        activitiesAdapter = new ActivitiesAdapter();
        agendaList.setAdapter(activitiesAdapter);
        toggleAgendaEmpty(); // show "No activities yet." initially

        if (addActivityBtn != null) addActivityBtn.setOnClickListener(btn -> showAddActivityDialog());
        if (exportPdfBtn != null) exportPdfBtn.setOnClickListener(btn -> exportAgendaToPdf());

        eventPdfBtn = root.findViewById(R.id.event_pdf_btn);
        if (eventPdfBtn != null) eventPdfBtn.setOnClickListener(btn -> exportEventDetailsToPdf());
    }

    private void fetchEventDetails(int id) {
        EventService eventApi = ClientUtils.eventService;
        eventApi.getEventById(id).enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event = response.body();
                    displayEvent(event);
                    if (loggedUserId > 0) checkIsFavorite();
                } else {
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEvent(EventModel e) {
        nameText.setText(nullTo(e.getName(), "Event"));
        locationText.setText("Location: " + nullTo(e.getLocation(), "Unknown"));
        dateText.setText("From " + nullTo(e.getStartDate(), "-") + " to " + nullTo(e.getEndDate(), "-"));
        descriptionText.setText(nullTo(e.getDescription(), "No description"));

        // Organizer text + link to profile (if not me)
        String fn = safe(e.getOrganizerFirstName());
        String ln = safe(e.getOrganizerLastName());
        String fullName = (fn + " " + ln).trim();
        fullName = TextUtils.isEmpty(fullName) ? "Organizer" : fullName;

        if (e.getOrganizerId() == loggedUserId) {
            organizerText.setText(fullName);
            organizerText.setOnClickListener(null);
        } else {
            organizerText.setText(Html.fromHtml("<u>" + fullName + "</u>"));
            organizerText.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putInt("organizerId", e.getOrganizerId());

                ViewOrganizerProfileFragment fragment = new ViewOrganizerProfileFragment();
                fragment.setArguments(bundle);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        // Event image
        if (eventImage != null) {
            String url = BASE + "events/get-photo/" + e.getId();
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.profile_placeholder)
                    .centerCrop()
                    .into(eventImage);
        }

        renderFavoriteIcon();         // reflect current favorite state
        updateAgendaEditorVisibility(); // ← toggle Add/Export buttons now that we know the event owner
    }

    /** Show Add/Export agenda buttons only if logged user is the EO AND owns this event */
    private void updateAgendaEditorVisibility() {
        if (addActivityBtn == null) return;

        boolean isEO     = "EventOrganizer".equalsIgnoreCase(loggedUserRole);
        boolean isOwner  = (event != null && event.getOrganizerId() == loggedUserId);

        // Show Add Activity only if the logged user is the EO who owns this event
        addActivityBtn.setVisibility((isEO && isOwner) ? View.VISIBLE : View.GONE);

        // Do NOT touch exportPdfBtn here — it remains visible to everyone
    }


    /** Query server favorites to set the initial star state */
    private void checkIsFavorite() {
        if (loggedUserId <= 0 || event == null) return;
        ClientUtils.organizerService.getFavoriteEvents(loggedUserId)
                .enqueue(new Callback<List<EventModel>>() {
                    @Override public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            isFavorite = false;
                            for (EventModel m : resp.body()) {
                                if (m.getId() == eventId) { isFavorite = true; break; }
                            }
                            renderFavoriteIcon();
                        }
                    }
                    @Override public void onFailure(Call<List<EventModel>> call, Throwable t) {
                        // ignore
                    }
                });
    }

    /** POST toggle on server, then update icon */
    private void toggleFavorite() {
        ClientUtils.organizerService.toggleFavoriteEvent(loggedUserId, eventId)
                .enqueue(new Callback<ToggleFavoriteResponse>() {
                    @Override public void onResponse(Call<ToggleFavoriteResponse> call, Response<ToggleFavoriteResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            isFavorite = resp.body().isFavorite;
                            renderFavoriteIcon();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<ToggleFavoriteResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderFavoriteIcon() {
        if (favoriteButton == null) return;
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        favoriteButton.setContentDescription(isFavorite ? "Unfavorite" : "Favorite");
    }

    // helpers
    private static String nullTo(String s, String fb) { return s == null ? fb : s; }
    private static String safe(String s) { return s == null ? "" : s; }

    private void toggleAgendaEmpty() {
        boolean empty = activitiesAdapter.getItems().isEmpty();
        if (agendaEmpty != null) agendaEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (agendaList  != null) agendaList.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showAddActivityDialog() {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        final EditText etName  = makeEt("Name *");
        final EditText etDesc  = makeEt("Description");
        final EditText etStart = makeEt("Start Time (e.g. 2025-10-20 14:00)");
        final EditText etEnd   = makeEt("End Time (e.g. 2025-10-20 16:00)");
        final EditText etLoc   = makeEt("Location");

        // Force picker, block keyboard
        etStart.setFocusable(false);
        etStart.setClickable(true);
        etEnd.setFocusable(false);
        etEnd.setClickable(true);

        // Open pickers on tap
        etStart.setOnClickListener(view -> pickDateTimeInto(etStart, null, false));
        etEnd.setOnClickListener(view -> pickDateTimeInto(etEnd, etStart, true));

        root.addView(etName);
        root.addView(etDesc);
        root.addView(etStart);
        root.addView(etEnd);
        root.addView(etLoc);

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Add Activity")
                .setView(root)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", (d, w) -> {
                    String name  = etName.getText().toString().trim();
                    String start = etStart.getText().toString().trim();
                    String end   = etEnd.getText().toString().trim();

                    if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Name, Start and End are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ActivitiesAdapter.ActivityItem item = new ActivitiesAdapter.ActivityItem(
                            name,
                            etDesc.getText().toString().trim(),
                            start,
                            end,
                            etLoc.getText().toString().trim()
                    );
                    activitiesAdapter.add(item);
                    toggleAgendaEmpty();
                })
                .show();
    }

    private static final SimpleDateFormat AGENDA_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    /**
     * Opens Date+Time pickers and writes the result into `target`.
     * - Blocks picking dates in the past.
     * - If picking END time, it validates it is not before the given `startField`.
     */
    private void pickDateTimeInto(@NonNull EditText target,
                                  @Nullable EditText startField,
                                  boolean isEndField) {

        final Calendar cal = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> {
                    cal.set(Calendar.YEAR, y);
                    cal.set(Calendar.MONTH, m);
                    cal.set(Calendar.DAY_OF_MONTH, d);

                    TimePickerDialog tp = new TimePickerDialog(
                            requireContext(),
                            (timeView, hour, minute) -> {
                                cal.set(Calendar.HOUR_OF_DAY, hour);
                                cal.set(Calendar.MINUTE, minute);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);

                                if (isEndField && startField != null) {
                                    long startMs = parseMillisSafe(startField.getText().toString());
                                    long endMs   = cal.getTimeInMillis();
                                    if (startMs > 0 && endMs < startMs) {
                                        Toast.makeText(requireContext(),
                                                "End time can’t be before start time.",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                target.setText(AGENDA_FMT.format(cal.getTime()));
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                    );
                    tp.show();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        // Block past days
        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.show();
    }

    private long parseMillisSafe(String s) {
        try {
            java.util.Date d = AGENDA_FMT.parse(s);
            return (d != null) ? d.getTime() : -1L;
        } catch (Exception ignore) {
            return -1L;
        }
    }

    private EditText makeEt(String hint) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return et;
    }

    private void exportAgendaToPdf() {
        List<ActivitiesAdapter.ActivityItem> data = activitiesAdapter.getItems();
        if (data.isEmpty()) {
            Toast.makeText(requireContext(), "No activities to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument doc = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595;   // ~A4 width
        int pageHeight = 842;  // ~A4 height
        int y = 40;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(16f);
        paint.setFakeBoldText(true);
        canvas.drawText("Event Activities", 30, y, paint);
        y += 24;

        paint.setTextSize(12f);
        paint.setFakeBoldText(false);

        for (int i = 0; i < data.size(); i++) {
            ActivitiesAdapter.ActivityItem a = data.get(i);

            if (y > pageHeight - 60) {
                doc.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, doc.getPages().size() + 1).create();
                page = doc.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 40;
                paint.setTextSize(12f);
                paint.setFakeBoldText(false);
            }

            canvas.drawText("• " + a.name, 30, y, paint); y += 16;
            canvas.drawText("   Description: " + (TextUtils.isEmpty(a.description) ? "-" : a.description), 30, y, paint); y += 16;
            canvas.drawText("   Start: " + a.startTime + "   End: " + a.endTime, 30, y, paint); y += 16;
            canvas.drawText("   Location: " + (TextUtils.isEmpty(a.location) ? "-" : a.location), 30, y, paint); y += 20;
        }

        doc.finishPage(page);

        String fileName = "event_agenda.pdf";
        try {
            Uri savedUri = savePdfToDownloads(doc, fileName);
            if (savedUri != null) {
                Toast.makeText(requireContext(), "Saved to Downloads as " + fileName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(requireContext(), "Save failed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "PDF export failed.", Toast.LENGTH_SHORT).show();
        } finally {
            doc.close();
        }
    }

    /** Saves the PdfDocument to the user-visible Downloads folder. */
    private @Nullable Uri savePdfToDownloads(PdfDocument doc, String fileName) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ → MediaStore, no permission needed
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            Uri itemUri = requireContext().getContentResolver().insert(collection, values);
            if (itemUri == null) return null;

            try (OutputStream os = requireContext().getContentResolver().openOutputStream(itemUri)) {
                if (os == null) return null;
                doc.writeTo(os);
            }

            values.clear();
            values.put(MediaStore.Downloads.IS_PENDING, 0);
            requireContext().getContentResolver().update(itemUri, values, null, null);
            return itemUri;
        } else {
            // Android 9 and below → write to public Downloads (needs WRITE_EXTERNAL_STORAGE on old targets)
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloads.exists()) downloads.mkdirs();
            File outFile = new File(downloads, fileName);
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                doc.writeTo(fos);
            }
            return null; // we still toast success above when needed
        }
    }

    private void exportEventDetailsToPdf() {
        if (event == null) {
            Toast.makeText(requireContext(), "Event not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument doc = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595;   // A4-ish width
        int pageHeight = 842;  // A4-ish height
        int margin = 30;
        int y = margin + 10;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Title
        paint.setTextSize(18f);
        paint.setFakeBoldText(true);
        canvas.drawText("Event Details", margin, y, paint);
        y += 24;

        // Body text
        paint.setTextSize(12f);
        paint.setFakeBoldText(false);

        int maxWidth = pageWidth - (margin * 2);
        android.text.TextPaint tp = new android.text.TextPaint(paint);

        y = drawLabelAndValue(canvas, tp, "Name: ", nullTo(event.getName(), "Event"), margin, y, maxWidth);
        y = drawLabelAndValue(canvas, tp, "Location: ", nullTo(event.getLocation(), "Unknown"), margin, y, maxWidth);

        String dates = "From " + nullTo(event.getStartDate(), "-") + " to " + nullTo(event.getEndDate(), "-");
        y = drawLabelAndValue(canvas, tp, "Date: ", dates, margin, y, maxWidth);

        String organizerFull = ((safe(event.getOrganizerFirstName()) + " " + safe(event.getOrganizerLastName())).trim());
        if (organizerFull.isEmpty()) organizerFull = "Organizer";
        y = drawLabelAndValue(canvas, tp, "Organizer: ", organizerFull, margin, y, maxWidth);

        y = drawLabelAndValue(canvas, tp, "Description: ", nullTo(event.getDescription(), "No description"), margin, y, maxWidth);

        doc.finishPage(page);

        String fileName = "event_details.pdf";
        try {
            Uri savedUri = savePdfToDownloads(doc, fileName);
            if (savedUri != null) {
                Toast.makeText(requireContext(), "Saved to Downloads as " + fileName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(requireContext(), "Save failed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "PDF export failed.", Toast.LENGTH_SHORT).show();
        } finally {
            doc.close();
        }
    }

    /** Simple label/value writer with wrapping. Returns next Y position. */
    private int drawLabelAndValue(Canvas canvas, android.text.TextPaint paint, String label,
                                  String value, int x, int y, int maxWidth) {
        // Label
        Paint bold = new Paint(paint);
        bold.setFakeBoldText(true);
        canvas.drawText(label, x, y, bold);

        // Value (wrapped)
        float labelWidth = bold.measureText(label);
        int textX = (int) (x + labelWidth);
        int available = maxWidth - (int) labelWidth;

        if (available <= 0) {
            y += 16;
            textX = x;
            available = maxWidth;
        }

        android.text.StaticLayout layout = android.text.StaticLayout.Builder
                .obtain(value == null ? "" : value, 0, (value == null ? 0 : value.length()), paint, available)
                .build();

        canvas.save();
        canvas.translate(textX, y - (int) paint.getTextSize());
        layout.draw(canvas);
        canvas.restore();

        return y + layout.getHeight() + 12;
    }
}
