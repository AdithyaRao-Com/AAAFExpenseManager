package com.adithya.aaafexpensemanager.settings.importDatabase;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
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
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class ImportDatabaseFragment extends Fragment {

    private static final String EXPORT_DIRECTORY_KEY = "Export Directory";

    private TextInputLayout directoryInputLayout;
    private TextInputEditText directoryEditText;
    private MaterialButton importButton;
    private ActivityResultLauncher<Intent> openDocumentLauncher;
    private SettingsRepository settingsRepository;
    private File databaseFile;
    private Application application;
    private File zipFilePath1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.application = (Application) requireContext().getApplicationContext();
        settingsRepository = new SettingsRepository(this.application);

        openDocumentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            directoryEditText.setText(uri.toString());
                            importDatabaseFromUri(uri);
                        } else {
                            showSnackbar("Import failed: No URI received.");
                        }
                    } else {
                        showSnackbar("Import cancelled.");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_import_database, container, false);

        directoryInputLayout = view.findViewById(R.id.directoryInputLayout);
        directoryEditText = view.findViewById(R.id.directoryEditText);
        importButton = view.findViewById(R.id.importButton);

        directoryInputLayout.setEndIconOnClickListener(v -> {
            Log.d("ImportFragment", "End icon clicked, opening file picker");
            openFilePicker();
        });

        importButton.setOnClickListener(v -> {
            Log.d("ImportFragment", "Import button clicked, importing database");
            importDatabase();
        });

        populateExportDirectory();

        return view;
    }

    private void openFilePicker() {
        Log.d("ImportFragment", "openFilePicker() called");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");

        Uri startDir = getStartDirectoryUri();
        if (startDir != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, startDir);
        }

        openDocumentLauncher.launch(intent);
        Log.d("ImportFragment", "openDocumentLauncher.launch() called");
    }

    private Uri getStartDirectoryUri() {
        String directory = getExportDirectory();
        if (directory != null && !directory.isEmpty()) {
            File dir = new File(directory);
            if (dir.exists() && dir.isDirectory()) {
                return Uri.fromFile(dir);
            }
        }
        return MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
    }

    private void importDatabase() {
        String zipFilePath = directoryEditText.getText().toString();
        zipFilePath1 = new File(zipFilePath);
        if (zipFilePath.isEmpty()) {
            showSnackbar("Please select a zip file to import.");
            return;
        }

        Uri zipFileUri = Uri.parse(zipFilePath);
        importDatabaseFromUri(zipFileUri);
    }

    private void importDatabaseFromUri(Uri zipFileUri) {
        try {
            File extractedDbFile = extractDatabaseFromZip(zipFileUri, "SQLite");
            replaceDatabase(extractedDbFile);
            showSnackbar("Database imported successfully.");
        } catch (ZipException e) {
            e.printStackTrace();
            showSnackbar("Zip error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showSnackbar("Database import failed: " + e.getMessage());
        }
    }

    private File extractDatabaseFromZip(Uri zipFileUri, String password) throws IOException, ZipException {
        File extractedDbFile = new File(requireContext().getCacheDir(), AppConstants.DATABASE_NAME);
        File zipFile = getFileFromUri(zipFileUri);
        try{
            File parentDir = new File(extractedDbFile.getParent());
            File[] files = parentDir.listFiles();
            for(File file: files){
                if(file.getName().endsWith(".db")){
                    file.delete();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ZipFile zip = new ZipFile(zipFile, password.toCharArray());
        try {
            zip.extractAll(extractedDbFile.getParent());
        } catch (ZipException e) {
            Log.e("ImportFragment", "Zip extraction failed: " + e.getMessage());
            throw e;
        }
        try{
            File parentDir = new File(extractedDbFile.getParent());
            File[] files = parentDir.listFiles();
            for(File file: files){
                if(file.getName().endsWith(".db")){
                    extractedDbFile = file;
                    break;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.d("ImportFragment", "Extracted file path: " + extractedDbFile.getAbsolutePath());
        if (extractedDbFile.exists()) {
            Log.d("ImportFragment", "Extracted file Exists");
        } else {
            Log.d("ImportFragment", "Extracted file Does not Exist");
        }
        return extractedDbFile;
    }

    private File getFileFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = requireContext().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (displayNameIndex != -1) {
                String displayName = cursor.getString(displayNameIndex);
                File cacheFile = new File(requireContext().getCacheDir(), displayName);
                try (InputStream inputStream = contentResolver.openInputStream(uri);
                     FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                cursor.close();
                return cacheFile;
            } else {
                cursor.close();
                throw new IOException("Could not get display name from uri");
            }
        } else {
            throw new IOException("Could not query uri");
        }
    }

    private void replaceDatabase(File extractedDbFile) throws IOException {
        Log.d("ImportFragment", "replaceDatabase() called, extractedDbFile: " + extractedDbFile.getAbsolutePath());

        databaseFile = settingsRepository.getDatabaseFile();
        if (databaseFile == null || !databaseFile.exists()) {
            showSnackbar("Database file not found.");
            return;
        }
        if(!extractedDbFile.exists()){
            showSnackbar("The extracted file not found");
        }
        try{
            LockedFileReplace.replaceLockedFile(extractedDbFile.toPath(),databaseFile.toPath());
//            Files.copy(extractedDbFile.toPath(), databaseFile.toPath());
        }
        catch (Exception e){
            e.printStackTrace();
        }
//        new ImportDatabaseRepository(application,extractedDbFile);
    }
    private void restartActivity() {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

    private void populateExportDirectory() {
        String directory = getExportDirectory();
        if (directory != null) {
            directoryEditText.setText(directory);
        }
    }

    private String getExportDirectory() {
        return settingsRepository.getSetting(EXPORT_DIRECTORY_KEY);
    }

    private void showSnackbar(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}