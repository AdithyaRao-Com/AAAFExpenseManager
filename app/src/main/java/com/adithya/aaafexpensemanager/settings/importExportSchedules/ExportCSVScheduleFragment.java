package com.adithya.aaafexpensemanager.settings.importExportSchedules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.settings.SettingsRepository;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExportCSVScheduleFragment extends Fragment {

    private Button selectFileButton;
    private TextView fileSelectedTextView;
    private Button exportButton;
    private TextView exportStatusTextView;
    private ActivityResultLauncher<Intent> createWriteRequestLauncher;
    private SettingsRepository settingsRepository;
    private Uri exportUri;
    private Application application;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsRepository = new SettingsRepository((Application) requireContext().getApplicationContext());
        application = (Application) requireContext().getApplicationContext();

        createWriteRequestLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        exportUri = result.getData().getData();
                        if (exportUri != null) {
                            fileSelectedTextView.setText("Location Selected: " + exportUri);
                        } else {
                            showSnackbar("Export location selection failed.");
                        }
                    } else {
                        showSnackbar("Export location selection cancelled.");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_export_csv, container, false);

        selectFileButton = view.findViewById(R.id.selectCSVFileButton);
        fileSelectedTextView = view.findViewById(R.id.csvFileSelectedTextView);
        exportButton = view.findViewById(R.id.exportCSVButton);
        exportStatusTextView = view.findViewById(R.id.exportCSVStatusTextView);
        selectFileButton.setOnClickListener(v -> selectExportLocation());
        exportButton.setOnClickListener(v -> exportCSV());

        return view;
    }

    private void selectExportLocation() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String csvFileName = "AAAF_Expense_Manager_Schedules_" + timestamp + ".csv";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, csvFileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = requireContext().getContentResolver().insert(collection, values);

        if (item != null) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, csvFileName);
            createWriteRequestLauncher.launch(intent);
        } else {
            showSnackbar("Failed to create MediaStore entry.");
        }
    }

    @SuppressLint("SetTextI18n")
    private void exportCSV() {
        if (exportUri == null) {
            showSnackbar("Please select an export location first.");
            return;
        }
        ExportScheduleCSVGenerator.generateCSV(application, exportUri);
        exportStatusTextView.setText("Schedules CSV export completed.");
        showSnackbar("Schedules CSV file exported successfully.");
    }

    private void showSnackbar(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}