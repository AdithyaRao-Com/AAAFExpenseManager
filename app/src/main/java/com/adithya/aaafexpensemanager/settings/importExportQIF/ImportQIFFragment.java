package com.adithya.aaafexpensemanager.settings.importExportQIF;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adithya.aaafexpensemanager.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImportQIFFragment extends Fragment {

    private Button selectFileButton;
    private TextView fileSelectedTextView;
    private Button importButton;
    private TextView importStatusTextView;
    private ActivityResultLauncher<Intent> pickFileLauncher;
    private Uri importUri;
    private QIFImporter qifImporter;
    private ProgressBar circularProgress;
    private ExecutorService executorService;
    private Handler mainHandler;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qifImporter = new QIFImporter((Application) requireContext().getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        pickFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        importUri = result.getData().getData();
                        if (importUri != null) {
                            String filePath = importUri.toString();
                            if (filePath.toLowerCase().endsWith(".qif")) {
                                fileSelectedTextView.setText("File Selected: " + importUri);
                            } else {
                                Snackbar.make(requireView(), "Selected file is not a QIF file.", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            showSnackbar("QIF file selection failed.");
                        }
                    } else {
                        showSnackbar("QIF file selection cancelled.");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_import_qif, container, false);
        circularProgress = view.findViewById(R.id.progress_circular);

        selectFileButton = view.findViewById(R.id.selectFileButton);
        fileSelectedTextView = view.findViewById(R.id.fileSelectedTextView);
        importButton = view.findViewById(R.id.importButton);
        importStatusTextView = view.findViewById(R.id.importStatusTextView);

        selectFileButton.setOnClickListener(v -> selectImportFile());
        importButton.setOnClickListener(v -> importQIF());

        return view;
    }

    private void selectImportFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        pickFileLauncher.launch(intent);
    }

    @SuppressLint("SetTextI18n")
    private void importQIF() {
        if (importUri == null) {
            showSnackbar("Please select a QIF file first.");
            return;
        }

        circularProgress.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            try {
                boolean success = qifImporter.importQIF(importUri);
                mainHandler.post(() -> {
                    circularProgress.setVisibility(View.GONE);
                    if (success) {
                        importStatusTextView.setText("QIF import completed successfully.");
                        showSnackbar("QIF file imported successfully.");
                    } else {
                        importStatusTextView.setText("QIF import failed.");
                        showSnackbar("QIF file import failed.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    circularProgress.setVisibility(View.GONE);
                    importStatusTextView.setText("QIF import failed: " + e.getMessage());
                    showSnackbar("QIF file import failed: " + e.getMessage());
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