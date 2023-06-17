package com.Mosa_true.MemorizePaper;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;

public class ReaderActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        mOpenCvCameraView = findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();
        ImageButton imageButton = findViewById(R.id.imageButton);
        //imageButton.setOnClickListener(new ShotButtonClickListener());
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

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        synchronized (mMat){
            mMat = inputFrame.rgba();    //color
        }
        //mMat= inputFrame.gray();    //grayscale
        //Core.bitwise_not(inputFrame.rgba(), mMat); //reversed
        //Core.bitwise_not(inputFrame.gray(), mMat); //grayscale reversed
        //Imgproc.Canny(inputFrame.gray(), mMat, 100, 200); //grayscale canny filtering
        //Imgproc.threshold(inputFrame.gray(), mMat, 0.0, 255.0, Imgproc.THRESH_OTSU); //grayscale binarization with Ohtsu
        //Core.rotate(mMat, mMat, Core.ROTATE_90_CLOCKWISE);
        Log.i("OnCameraFrame", "Got!:" + mMat.width() + ", " + mMat.height());
        QRCodeHandler qrCodeHandler = new QRCodeHandler();
        return qrCodeHandler.detect(mMat);
    }

    private class ShotButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Mat mat;
            synchronized (mMat){
                mat = mMat.clone();
            }
            Log.i("shot", "width:" + mat.width() + " height:" + mat.height());
            QRCodeHandler qrCodeHandler = new QRCodeHandler();
            Mat detectedMat = qrCodeHandler.detect(mat);
            if(detectedMat != null) Log.i("QRCode", "Detected!");
            mat.release();
            finish();
        }
    }
}