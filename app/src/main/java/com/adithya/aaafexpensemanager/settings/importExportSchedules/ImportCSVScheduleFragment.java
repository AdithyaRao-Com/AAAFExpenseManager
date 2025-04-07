package com.adithya.aaafexpensemanager.settings.importExportSchedules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.util.CsvFileTypeDetector;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImportCSVScheduleFragment extends Fragment {
    private TextView fileSelectedTextView;
    private Button uploadButton;
    private Uri selectedFileUri;
    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<Intent> pickFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedFileUri = data.getData();
                        if (selectedFileUri != null) {
                            fileSelectedTextView.setText("Selected file: " + selectedFileUri.getLastPathSegment());
                            uploadButton.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    private ProgressBar circularProgress;
    private Context context;
    private ViewGroup viewGroup;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_upload_csv, container, false);
        this.viewGroup = container;
        this.context = getContext();
        circularProgress = view.findViewById(R.id.progress_circular);
        fileSelectedTextView = view.findViewById(R.id.fileSelectedTextView);
        uploadButton = view.findViewById(R.id.uploadButton);
        Button selectFileButton = view.findViewById(R.id.selectFileButton);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        uploadButton.setEnabled(false);

        selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            pickFileLauncher.launch(intent);
        });

        uploadButton.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                processCsvFile(selectedFileUri);
            }
        });

        return view;
    }

    private void processCsvFile(Uri fileUri) {
        circularProgress.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            try {
                if (CsvFileTypeDetector.isLikelyScheduleCsv(this.context, fileUri)) {
                    ImportScheduleCSVParser.parseTransactions(this.context, fileUri);
                    mainHandler.post(() -> {
                        Snackbar.make(viewGroup.getRootView(), "Schedules CSV Imported Successfully", Snackbar.LENGTH_LONG).show();
                        circularProgress.setVisibility(View.GONE);
                    });
                } else {
                    mainHandler.post(() -> {
                        Toast.makeText(this.context, "File is not a CSV", Toast.LENGTH_SHORT).show();
                        circularProgress.setVisibility(View.GONE);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    Toast.makeText(this.context, "File Import Failed", Toast.LENGTH_SHORT).show();
                    circularProgress.setVisibility(View.GONE);
                });
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}