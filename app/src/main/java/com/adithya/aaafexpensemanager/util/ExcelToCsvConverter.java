package com.adithya.aaafexpensemanager.util;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** @noinspection unused*/
public class ExcelToCsvConverter {

    private final Context context;
    private final ConversionListener listener;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Single thread for sequential tasks

    public interface ConversionListener {
        void onConversionComplete(Uri csvFileUri);

        void onConversionFailed(String errorMessage);
    }

    public ExcelToCsvConverter(Context context, ConversionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void convertExcelToCsv(Uri excelFileUri) {
        executorService.execute(() -> {
            Uri result = doInBackground(excelFileUri);
            if (result != null) {
                listener.onConversionComplete(result);
                // Switch to main thread for UI updates
                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                mainHandler.post(() -> Toast.makeText(context, "Conversion successful. CSV saved to Downloads.", Toast.LENGTH_SHORT).show());
            } else {
                listener.onConversionFailed(errorMessage);
                // Switch to main thread for UI updates
                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                mainHandler.post(() -> Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show());
            }
        });
    }

    private String errorMessage;

    private Uri doInBackground(@NonNull Uri excelFileUri) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter writer = null;

        try {
            inputStream = context.getContentResolver().openInputStream(excelFileUri);
            assert inputStream != null;
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the first sheet

            String csvFileName = "converted_" + System.currentTimeMillis() + ".csv";
            File csvFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), csvFileName);
            fileOutputStream = new FileOutputStream(csvFile);
            writer = new OutputStreamWriter(fileOutputStream);

            for (Row row : sheet) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (i > 0) {
                        line.append(",");
                    }
                    switch (cell.getCellType()) {
                        case STRING:
                            line.append("\"").append(cell.getStringCellValue().replace("\"", "\"\"")).append("\"");
                            break;
                        case NUMERIC:
                            line.append(cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            line.append(cell.getBooleanCellValue());
                            break;
                        case FORMULA:
                            try {
                                line.append(cell.getNumericCellValue());
                            } catch (IllegalStateException e) {
                                line.append("\"").append(cell.getStringCellValue().replace("\"", "\"\"")).append("\"");
                            }
                            break;
                        case BLANK:
                            // Handle blank cells
                            break;
                        default:
                            line.append("\"").append(cell.toString().replace("\"", "\"\"")).append("\"");
                            break;
                    }
                }
                writer.write(line.toString());
                writer.write("\n");
            }
            return Uri.fromFile(csvFile);

        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            errorMessage = "Conversion failed: " + e.getMessage();
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (writer != null) writer.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }
}