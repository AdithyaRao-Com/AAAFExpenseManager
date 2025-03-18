package com.adithya.aaafexpensemanager.settings.importCSV;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.importdata.ImportCSVParser;
import com.adithya.aaafexpensemanager.util.CsvFileTypeDetector;
import com.adithya.aaafexpensemanager.util.ExcelToCsvConverter;
import com.google.android.material.snackbar.Snackbar;

public class UploadCSVFragment extends Fragment {
    private TextView fileSelectedTextView;
    private Button uploadButton;
    private Uri selectedFileUri;
    private Context context;
    private ViewGroup viewGroup;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_upload_csv, container, false);
        this.viewGroup = container;
        this.context = getContext();
        fileSelectedTextView = view.findViewById(R.id.fileSelectedTextView);
        uploadButton = view.findViewById(R.id.uploadButton);
        Button selectFileButton = view.findViewById(R.id.selectFileButton);

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
        if(CsvFileTypeDetector.isLikelyCsv(this.context, fileUri)){
            ImportCSVParser.parseTransactions(this.context, fileUri);
            Snackbar.make(viewGroup.getRootView(),"Upload Successful", Snackbar.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this.context, "File is not a CSV", Toast.LENGTH_SHORT).show();
            ExcelToCsvConverter convertor = new ExcelToCsvConverter(this.context, new ExcelToCsvConverter.ConversionListener() {
                @Override
                public void onConversionComplete(Uri csvFileUri) {
                    ImportCSVParser.parseTransactions(context, csvFileUri);
                }

                @Override
                public void onConversionFailed(String errorMessage) {
                    Snackbar.make(viewGroup.getRootView(), "Conversion failed: " + errorMessage, Snackbar.LENGTH_LONG).show();
                    Log.e("UploadCSVFragment", "Conversion failed: " + errorMessage);
                }
            });
            try {
                convertor.convertExcelToCsv(fileUri);
            }
            catch (Exception e){
                Snackbar.make(viewGroup.getRootView(), "Conversion failed 2nd Attempt: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                Log.e("UploadCSVFragment", "Conversion failed: " + e.getMessage());
            }
        }
    }
}