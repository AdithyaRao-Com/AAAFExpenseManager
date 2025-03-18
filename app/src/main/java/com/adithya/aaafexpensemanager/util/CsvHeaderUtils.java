package com.adithya.aaafexpensemanager.util;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvHeaderUtils {

    public static List<String> getCsvHeaders(Context context, Uri fileUri) {
        List<String> headers = new ArrayList<>();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                //noinspection deprecation
                CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
                CSVParser parser = CSVParser.parse(reader, format);

                Map<String, Integer> headerMap = parser.getHeaderMap();
                if (headerMap != null) {
                    headers.addAll(headerMap.keySet());
                }

                parser.close();
                reader.close();
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to get headers from CSV");
        }
        return headers;
    }

    public static List<String> getCsvHeadersWithoutHeaderRow(Context context, Uri fileUri) {
        List<String> headers = new ArrayList<>();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                CSVFormat format = CSVFormat.DEFAULT; //No first record as header.
                CSVParser parser = CSVParser.parse(reader, format);

                CSVRecord firstRecord = parser.iterator().next();
                for(int i = 0; i < firstRecord.size(); i++){
                    headers.add("Column " + (i + 1));
                }

                parser.close();
                reader.close();
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e("CsvHeaderUtils", "Error getting CSV headers: " + e.getMessage());
        }
        return headers;
    }
}