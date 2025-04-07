package com.adithya.aaafexpensemanager.settings.importExportDatabase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;

/**
 * @noinspection FieldCanBeLocal
 */
public class ImportDatabaseFragment extends Fragment {

    private Button selectFileButton;
    private TextView fileSelectedTextView;
    private Button importButton;
    private TextView importStatusTextView;
    private ActivityResultLauncher<Intent> pickFileLauncher;
    private SettingsRepository settingsRepository;
    private File databaseFile;
    private Uri importUri;
    private DatabaseImporter databaseImporter;
    private ProgressBar circularProgress;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsRepository = new SettingsRepository((Application) requireContext().getApplicationContext());
        databaseImporter = new DatabaseImporter();

        pickFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        importUri = result.getData().getData();
                        if (importUri != null) {
                            fileSelectedTextView.setText("File Selected: " + importUri);
                        } else {
                            showSnackbar("Import file selection failed.");
                        }
                    } else {
                        showSnackbar("Import file selection cancelled.");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_import_database, container, false);
        circularProgress = view.findViewById(R.id.progress_circular);
        selectFileButton = view.findViewById(R.id.selectFileButton);
        fileSelectedTextView = view.findViewById(R.id.fileSelectedTextView);
        importButton = view.findViewById(R.id.importButton);
        importStatusTextView = view.findViewById(R.id.importStatusTextView);

        databaseFile = settingsRepository.getDatabaseFile();

        selectFileButton.setOnClickListener(v -> selectImportFile());
        importButton.setOnClickListener(v -> importDatabase());

        return view;
    }

    private void selectImportFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        pickFileLauncher.launch(intent);
    }

    @SuppressLint("SetTextI18n")
    private void importDatabase() {
        try {
            circularProgress.setVisibility(View.VISIBLE);
            if (importUri == null) {
                showSnackbar("Please select a database zip file first.");
                return;
            }

            boolean success = databaseImporter.importDatabase(requireContext(), importUri, databaseFile);
            if (success) {
                importStatusTextView.setText("Import completed successfully.");
                showSnackbar("Database imported successfully.");
            } else {
                importStatusTextView.setText("Import failed.");
                showSnackbar("Database import failed.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            showSnackbar("Database import failed");
        }
        finally {
            circularProgress.setVisibility(View.GONE);
        }
    }

    private void showSnackbar(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}