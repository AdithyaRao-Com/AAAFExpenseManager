package com.adithya.aaafexpensemanager.settings.importExportCSV;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExportCSVFragment extends Fragment {

    private Button selectFileButton;
    private TextView fileSelectedTextView;
    private Button exportButton;
    private TextView exportStatusTextView;
    private ActivityResultLauncher<Intent> createWriteRequestLauncher;
    private SettingsRepository settingsRepository;
    private Uri exportUri;
    private Application application;
    private ProgressBar circularProgress;
    private ExecutorService executorService;
    private Handler mainHandler;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsRepository = new SettingsRepository((Application) requireContext().getApplicationContext());
        application = (Application) requireContext().getApplicationContext();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

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
        circularProgress = view.findViewById(R.id.progress_circular);
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
        String csvFileName = "AAAF_Expense_Manager_" + timestamp + ".csv";

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

        circularProgress.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            try {
                ExportCSVGenerator.generateCSV(application, exportUri);
                mainHandler.post(() -> {
                    circularProgress.setVisibility(View.GONE);
                    exportStatusTextView.setText("CSV export completed.");
                    showSnackbar("CSV file exported successfully.");
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    circularProgress.setVisibility(View.GONE);
                    exportStatusTextView.setText("CSV file export failed.");
                    showSnackbar("CSV file export failed.");
                });
            }
        });
    }

    private void showSnackbar(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}