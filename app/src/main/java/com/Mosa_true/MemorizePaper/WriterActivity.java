package com.Mosa_true.MemorizePaper;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Mosa_true.MemorizePaper.model.InputFile;
import com.Mosa_true.MemorizePaper.model.QRCodeWriter;

import java.io.IOException;

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
        private Bitmap qrCode;
        private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            TextView fileNameView = findViewById(R.id.file_name);
                            try {
                                InputFile inputFile = new InputFile(result, getApplicationContext());
                                fileNameView.setText(inputFile.getFileName());
                                QRCodeWriter qrCodeWriter = new QRCodeWriter(inputFile.getFileData());
                                qrCode = qrCodeWriter.getQRCode();
                                ImageView imageView = findViewById(R.id.qrCodeView);
                                imageView.setImageBitmap(qrCode);
                                imageView.setVisibility(ImageView.VISIBLE);
                            }
                            catch (NullPointerException | IOException | IllegalArgumentException e) {
                                Log.e("FilePicker", e.toString());
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
    }
}