package com.adithya.aaafexpensemanager.settings.importCSV;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.adithya.aaafexpensemanager.R;

public class UploadCSVFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_upload_csv, container, false);

        fileSelectedTextView = view.findViewById(R.id.fileSelectedTextView);
        uploadButton = view.findViewById(R.id.uploadButton);
        Button selectFileButton = view.findViewById(R.id.selectFileButton);

        uploadButton.setEnabled(false);

        selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/csv"); // Filter for CSV files
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
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                List<String[]> data = new ArrayList<>(); // Store the CSV data

                while ((line = reader.readLine()) != null) {
                    String[] row = line.split(","); // Split by comma
                    data.add(row);
                }

                reader.close();
                inputStream.close();

                // Now 'data' contains the CSV data as a List of String arrays.
                // Process the data as needed (e.g., display in a RecyclerView, save to database, etc.)
                displayCsvData(data); // Call a method to display or handle the data.

            } else {
                Toast.makeText(getContext(), "Failed to open file", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            Toast.makeText(getContext(), "Error processing CSV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCsvData(List<String[]> data) {
        // Example: Display the first few rows in a TextView or log.
        StringBuilder stringBuilder = new StringBuilder();
        int rowsToDisplay = Math.min(10, data.size()); // Display up to 10 rows
        for (int i = 0; i < rowsToDisplay; i++) {
            String[] row = data.get(i);
            for (String cell : row) {
                stringBuilder.append(cell).append("\t"); // Use tab for separation
            }
            stringBuilder.append("\n");
        }
        //You can use a recycler view here instead.
        TextView displayTextView = requireView().findViewById(R.id.csvDataDisplayTextView);
        if(displayTextView != null){
            displayTextView.setText(stringBuilder.toString());
        }
        else{
            android.util.Log.d("CSV_DATA", stringBuilder.toString()); // Log the data
        }

    }
}