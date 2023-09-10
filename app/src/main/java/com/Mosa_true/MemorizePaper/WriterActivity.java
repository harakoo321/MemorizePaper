package com.Mosa_true.MemorizePaper;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.github.luben.zstd.Zstd;
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
        private QRCodeWriter qrCodeWriter = new QRCodeWriter();
        private Bitmap bitmap;
        private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        Log.i("FilePicker", "loaded! :" + result.toString());
                        TextView fileNameView = findViewById(R.id.file_name);
                        fileNameView.setText(readFileNameFromUri(result));
                        try {
                            Log.i("FilePicker", "data:" + readTextFromUri(result));
                        }catch (IOException e){
                            Log.e("FilePicker", "" + e);
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

        private String readTextFromUri(Uri uri) throws IOException {
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