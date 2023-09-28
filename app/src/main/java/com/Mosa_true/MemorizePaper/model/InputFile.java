package com.Mosa_true.MemorizePaper.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class InputFile {
    private final String _fileName;
    private final String _fileData;

    public InputFile(Uri inputFileUri, Context context) throws IOException, NullPointerException {
        if(inputFileUri == null || context == null) {
            throw new NullPointerException();
        }
        _fileName = setFileName(inputFileUri, context);
        _fileData = setFileData(inputFileUri, context);
    }

    private String setFileName(Uri inputFileUri, Context context){
        Cursor resultCursor = context.getContentResolver().query(inputFileUri, null, null, null, null);
        int nameIndex = resultCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        resultCursor.moveToFirst();
        String fileName = resultCursor.getString(nameIndex);
        resultCursor.close();
        return fileName;
    }

    private String setFileData(Uri inputFileUri, Context context) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     context.getContentResolver().openInputStream(inputFileUri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    public String getFileName() {
        return _fileName;
    }

    public String getFileData() {
        return _fileData;
    }
}
