package com.adithya.aaafexpensemanager.settings.exportDatabase;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.settings.SettingsRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExportDatabaseFragment extends Fragment {

    private static final String EXPORT_DIRECTORY_KEY = "Export Directory";

    private TextInputEditText directoryEditText;
    private ActivityResultLauncher<Intent> createWriteRequestLauncher;
    private SettingsRepository settingsRepository;
    private File databaseFile; // Private field to store the database File

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsRepository = new SettingsRepository((Application) requireContext().getApplicationContext());

        createWriteRequestLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // Extract the directory from the URI and save it
                            String directory = extractDirectoryFromUri(uri);
                            if (directory != null) {
                                saveExportDirectory(directory);
                            }
                            exportDatabaseToUri(uri);
                        } else {
                            showSnackbar("Export failed: No URI received.");
                        }
                    } else {
                        showSnackbar("Export cancelled.");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_export_database, container, false);

        TextInputLayout directoryInputLayout = view.findViewById(R.id.directoryInputLayout);
        directoryEditText = view.findViewById(R.id.directoryEditText);
        MaterialButton exportButton = view.findViewById(R.id.exportButton);

        databaseFile = settingsRepository.getDatabaseFile(); // Initialize the private field

        directoryInputLayout.setEndIconOnClickListener(v -> exportDatabase());
        exportButton.setOnClickListener(v -> exportDatabase());

        populateExportDirectory();

        return view;
    }

    private void exportDatabase() {
        if (databaseFile == null || !databaseFile.exists()) {
            showSnackbar("Database file not found");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String zipFileName = "AAAF_Expense_Manager_" + timestamp + ".zip";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, zipFileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = requireContext().getContentResolver().insert(collection, values);

        if (item != null) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");
            intent.putExtra(Intent.EXTRA_TITLE, zipFileName);
            createWriteRequestLauncher.launch(intent);
        } else {
            showSnackbar("Export failed: Could not create MediaStore entry.");
        }
    }

    private void exportDatabaseToUri(Uri uri) {
        if (databaseFile == null || !databaseFile.exists()) {
            showSnackbar("Database file not found");
            return;
        }

        try {
            File zipFile = new File(requireContext().getCacheDir(), "temp.zip");
            zipDatabase(databaseFile, zipFile, "SQLite");
            try (FileInputStream fis = new FileInputStream(zipFile);
                 OutputStream os = requireContext().getContentResolver().openOutputStream(uri)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
            zipFile.delete();
            populateExportDirectory();
            showSnackbar("Database exported successfully");
        } catch (ZipException e) {
            e.printStackTrace();
            showSnackbar("Zip error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showSnackbar("Database export failed: " + e.getMessage());
        }
    }

    private void zipDatabase(File databaseFile, File zipFile, String password) throws ZipException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

        ZipFile zip = new ZipFile(zipFile, password.toCharArray());
        zip.addFile(databaseFile, zipParameters);
    }

    private String getExportDirectory() {
        return settingsRepository.getSetting(EXPORT_DIRECTORY_KEY);
    }

    private boolean saveExportDirectory(String directory) {
        return settingsRepository.setSetting(EXPORT_DIRECTORY_KEY, directory);
    }

    private void showSnackbar(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void populateExportDirectory() {
        String directory = getExportDirectory();
        if (directory != null) {
            directoryEditText.setText(directory);
        } else {
            directoryEditText.setText(Environment.DIRECTORY_DOWNLOADS);
            saveExportDirectory(Environment.DIRECTORY_DOWNLOADS);
        }
    }
    private String extractDirectoryFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int lastSlashIndex = path.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                return path.substring(0, lastSlashIndex);
            }
        }
        return null;
    }
}