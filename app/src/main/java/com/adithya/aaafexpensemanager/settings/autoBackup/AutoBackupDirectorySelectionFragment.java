package com.adithya.aaafexpensemanager.settings.autoBackup;

import com.adithya.aaafexpensemanager.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AutoBackupDirectorySelectionFragment extends Fragment {

    private TextView selectedDirectoryTextView;
    /** @noinspection FieldCanBeLocal*/
    private Button selectDirectoryButton;
    private Uri selectedDirectoryUri;
    private CheckBox autoBackupEnabledCheckBox;
    private AutoBackUpSharedPrefs autoBackUpSharedPrefs;

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<Intent> directoryPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedDirectoryUri = data.getData();
                        if (selectedDirectoryUri != null) {
                            selectedDirectoryTextView.setText("Selected directory: " + selectedDirectoryUri.getPath());
                            storeDirectoryInPreferences(selectedDirectoryUri);
                        }
                    } else {
                        Toast.makeText(getContext(), "No directory selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        autoBackUpSharedPrefs = new AutoBackUpSharedPrefs(context);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_autobackup_directory_selection, container, false);
        selectedDirectoryTextView = view.findViewById(R.id.selectedDirectoryTextView);
        selectDirectoryButton = view.findViewById(R.id.selectDirectoryButton);
        selectDirectoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            directoryPickerLauncher.launch(intent);
        });
        // Load previously saved directory
        String savedDirectoryUriString = autoBackUpSharedPrefs.getAutoBackupDirectory();
        if (savedDirectoryUriString != null) {
            selectedDirectoryUri = Uri.parse(savedDirectoryUriString);
            selectedDirectoryTextView.setText("Selected directory: " + selectedDirectoryUri.getPath());
        }
        autoBackupEnabledCheckBox = view.findViewById(R.id.autoBackup_enabled);
        autoBackupEnabledCheckBox.setChecked(autoBackUpSharedPrefs.getKeyIsAutoBackupEnabled());
        autoBackupEnabledCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            autoBackUpSharedPrefs.setKeyIsAutoBackupEnabled(isChecked);
        });
        return view;
    }
    private void storeDirectoryInPreferences(Uri directoryUri) {
        String directoryPath = directoryUri.toString(); // Store URI as String
        autoBackUpSharedPrefs.setAutoBackupDirectory(directoryPath);
        Toast.makeText(getContext(), "Directory stored successfully", Toast.LENGTH_SHORT).show();
    }
}