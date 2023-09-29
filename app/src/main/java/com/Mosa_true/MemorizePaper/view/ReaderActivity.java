package com.Mosa_true.MemorizePaper.view;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.Mosa_true.MemorizePaper.model.QRCodeReader;
import com.Mosa_true.MemorizePaper.R;
import com.github.luben.zstd.Zstd;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        mOpenCvCameraView = findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();
        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new ShotButtonClickListener());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mMat = new Mat(height, width, CvType.CV_8UC4);
        Log.i("Started", "Started!");
    }

    public void onCameraViewStopped() {
        mMat.release();
    }

    private Mat mMat;
    private QRCodeReader qrCodeHandler = new QRCodeReader();

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        synchronized (mMat){
            mMat = inputFrame.rgba();    //color
        }
        Log.i("OnCameraFrame", "Got!:" + mMat.width() + ", " + mMat.height());
        //return qrCodeHandler.detectQRMulti(mMat);
        return mMat;
    }

    private class ShotButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Mat mat;
            synchronized (mMat){
                mat = mMat.clone();
            }
            Log.i("shot", "width:" + mat.width() + " height:" + mat.height());
            QRCodeReader qrCodeHandler = new QRCodeReader();
            List<String> readData = qrCodeHandler.detectAndDecodeQRMulti(mat);
            int originalSize = 0;
            int qrSum = (int)(readData.get(0).getBytes()[0] & (byte)15) + 1;
            int qrReadNum = readData.size();
            Log.i("Reader", "qrSum:" + qrSum + " qrReadNum" + qrReadNum);
            if (qrSum == qrReadNum) {
                Log.i("Reader", "Success! All QRCodes were read!");
                List<byte[]> sortedData = new ArrayList<>(qrSum);

                Map<Integer, byte[]> map = new HashMap<>();
                for (int i = 0; i < qrReadNum; i++) {
                    byte[] divBytes = readData.get(i).getBytes();
                    int qrNum = (int) ((divBytes[0] & ~(byte)15) >>> 4);
                    Log.i("Reader", "qrNum:" + qrNum);
                    if (qrNum == 0) {
                        originalSize = ByteBuffer.wrap(Arrays.copyOfRange(divBytes, 1, 5)).getInt();
                        Log.i("Reader", "originalSize:" + originalSize);
                        byte[] tmp = new byte[divBytes.length - 5];
                        System.arraycopy(divBytes, 5, tmp, 0, divBytes.length - 5);
                        map.put(0, tmp);
                        //sortedData.set(0, Arrays.copyOfRange(divBytes, 5, divBytes.length));
                    } else {
                        byte[] tmp = new byte[divBytes.length - 1];
                        System.arraycopy(divBytes, 1, tmp, 0, divBytes.length - 1);
                        map.put(qrNum, tmp);
                        //sortedData.set(qrNum, Arrays.copyOfRange(divBytes, 1, divBytes.length));
                    }
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                for (int i = 0; i < map.size(); i++) {
                    try {
                        outputStream.write(map.get(i));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                byte[] decompressedBytes = Zstd.decompress(outputStream.toByteArray(), originalSize);
                Log.i("Reader", decompressedBytes.length == originalSize ? "Decompress succeeded!" : "Decompress failed!");
            }else {
                Log.e("Reader", "Fail to read all QRCodes!");
            }
            mat.release();
            finish();
        }
    }
}