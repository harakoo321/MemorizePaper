package com.Mosa_true.MemorizePaper;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class WriterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        Button fileSelectButton = findViewById(R.id.file_select_button);
        Button printButton = findViewById(R.id.print_button);
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        fileSelectButton.setOnClickListener(buttonClickListener);
        printButton.setOnClickListener(buttonClickListener);
    }

    private class ButtonClickListener implements View.OnClickListener{
        private QRCodeWriter qrCodeWriter = QRCodeWriter.getInstance();
        private Bitmap qrCode;
        private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            TextView fileNameView = findViewById(R.id.file_name);
                            fileNameView.setText(readFileNameFromUri(result));
                            try {
                                Mat qrMat = qrCodeWriter.encodeStringToQRMat(readDataFromUri(result));
                                qrCode = qrCodeWriter.convertMatToBitmap(qrMat);
                                ImageView imageView = findViewById(R.id.qrCodeView);
                                imageView.setImageBitmap(qrCode);
                                imageView.setVisibility(ImageView.VISIBLE);
                            }catch (IOException e){
                                Toast.makeText(
                                        WriterActivity.this,
                                        R.string.io_exception,
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                }
        );

        @Override
        public void onClick(View view){
            int id = view.getId();
            if(id == R.id.file_select_button){
                filePickerLauncher.launch("*/*");
            } else if (id == R.id.print_button) {
                PrintHelper photoPrinter = new PrintHelper(getBaseContext());
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                photoPrinter.printBitmap("test print", qrCode);
            }
        }

        private String readFileNameFromUri(Uri uri){
            Cursor resultCursor = getContentResolver().query(uri, null, null, null, null);
            int nameIndex = resultCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            resultCursor.moveToFirst();
            String fileName = resultCursor.getString(nameIndex);
            resultCursor.close();
            return fileName;
        }

        private String readDataFromUri(Uri uri) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream =
                         getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            return stringBuilder.toString();
        }
    }
}