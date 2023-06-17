package com.Mosa_true.MemorizePaper;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.luben.zstd.Zstd;

import org.apache.commons.compress.utils.IOUtils;
import org.opencv.objdetect.QRCodeEncoder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class WriterActivity extends AppCompatActivity {
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        ListView listView = findViewById(R.id.fileListView);
        arrayAdapter = new ArrayAdapter<>(WriterActivity.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add(getString(R.string.add_file));
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new ListItemClickListener());
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        Log.i("FilePicker", "loaded! :" + result.toString());
                        Cursor resultCursor = getContentResolver().query(result, null, null, null, null);
                        int nameIndex = resultCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        resultCursor.moveToFirst();
                        Log.i("FilePicker", "FileName:" + resultCursor.getString(nameIndex));
                        arrayAdapter.add(resultCursor.getString(nameIndex));
                        arrayAdapter.notifyDataSetChanged();
                        resultCursor.close();

                        Log.i("FilePicker", "loaded! :" + result.toString());
                        /*
                        String path = getFilePath(WriterActivity.this, result);
                        Log.i("Path","" + path);
                        try (FileInputStream fileInputStream = new FileInputStream(path)){
                            byte[] bytedata = IOUtils.toByteArray(fileInputStream);
                            byte[] compressedBytes = Zstd.compress(bytedata, 16);
                            QRCodeHandler qrCodeHandler = new QRCodeHandler();
                            qrCodeHandler.joinMats(qrCodeHandler.encodeBytesToQRMatList(compressedBytes));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        */
                        byte[] bytedata;
                        try {
                            bytedata = readFileBytesFromUri(result);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        byte[] compressedBytes = Zstd.compress(bytedata, 16);
                        QRCodeHandler qrCodeHandler = new QRCodeHandler();
                        qrCodeHandler.joinMats(qrCodeHandler.encodeBytesToQRMatList(compressedBytes));
                    }
                }
        );

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
            Log.i("ClickedPos", "" + pos);
            if (pos == 0){
                filePickerLauncher.launch("*/*");
            }
        }

        private byte[] readFileBytesFromUri(Uri uri) throws IOException {
            ContentResolver cr = getContentResolver();
            InputStream is = cr.openInputStream(uri);
            return IOUtils.toByteArray(is);
        }

        private String getFilePath(Context context, Uri uri) {
            String result = null;
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        result = cursor.getString(nameIndex);
                    }
                }
            }
            if (result == null) {
                result = uri.getLastPathSegment();
            }
            return result;
        }
    }
}