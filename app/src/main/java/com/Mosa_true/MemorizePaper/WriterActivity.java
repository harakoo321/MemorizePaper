package com.Mosa_true.MemorizePaper;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.luben.zstd.Zstd;

import org.apache.commons.compress.utils.IOUtils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
        private QRCodeHandler qrCodeHandler = new QRCodeHandler();
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
                    }
                }
        );

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
            Log.i("ClickedPos", "" + pos);
            if (pos == 0){
                //filePickerLauncher.launch("*/*");
                //while (new FileInputStream().read(filebytes, 0, 8191) != -1) {

                //}
                String hoge = "https://fast.com";
                byte[] filebytes = hoge.getBytes();

                int fileSize = filebytes.length;
                Log.i("Writer", "originalSize:" + fileSize);
                byte[] originalSize = ByteBuffer.allocate(4).putInt(fileSize).array();
                byte[] compressedBytes = Zstd.compress(filebytes, 1);
                int len = compressedBytes.length;
                byte[] compressedBytes2 = new byte[len + 4];
                System.arraycopy(originalSize, 0, compressedBytes2, 0, 4);
                System.arraycopy(compressedBytes, 0, compressedBytes2, 4, len);
                List<Mat> matList = new ArrayList<>();
                int len2 = compressedBytes2.length;
                int qrNum = len2 / 270;
                int qrLast = len2 % 270;
                if (qrLast != 0) qrNum++;
                if (!(qrNum > 16)) {
                    for (int i = 0; i < qrNum; i++) {
                        byte[] divBytes;
                        if(qrLast != 0 && i == qrNum - 1) {
                            divBytes = new byte[qrLast + 1];
                            byte[] header = new byte[1];
                            header[0] = (byte) (qrNum - 1 << 4 | qrNum - 1);
                            Log.i("header", "header:" + header[0]);
                            System.arraycopy(header, 0, divBytes, 0, 1);
                            System.arraycopy(compressedBytes2, i * 271, divBytes, 1, qrLast);
                        }else {
                            divBytes = new byte[271];
                            byte[] header = new byte[1];
                            header[0] = (byte) (i << 4 | qrNum - 1);
                            Log.i("header", "header:" + header[0]);
                            System.arraycopy(header, 0, divBytes, 0, 1);
                            System.arraycopy(compressedBytes2, i * 271, divBytes, 1, 270);
                        }
                        String qrString = new String(divBytes);
                        matList.add(qrCodeHandler.encodeBytesToQRMat(qrString.getBytes()));
                    }
                }
                Mat mat = qrCodeHandler.mergeMatList(matList);
                Bitmap bitmap = qrCodeHandler.convertMatToBitmap(mat);
                PrintHelper photoPrinter = new PrintHelper(getBaseContext());
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                photoPrinter.printBitmap("test print", bitmap);
            }else {
                Toast.makeText(WriterActivity.this, "ファイルが大きすぎ", Toast.LENGTH_LONG).show();
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