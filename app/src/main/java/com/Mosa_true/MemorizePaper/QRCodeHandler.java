package com.Mosa_true.MemorizePaper;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;
import org.opencv.objdetect.QRCodeEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QRCodeHandler {
    private List<String> detectedData = new ArrayList<>();

    public Mat detect(Mat mat) {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        Mat points = new Mat();
        if (qrCodeDetector.detectAndDecodeMulti(mat, detectedData, points)) {
            for (int i = 0; i < detectedData.size(); i++) {
                Log.i("QR", "Detected:" + detectedData.get(i));
            }
            Log.i("Points", "row:" + points.rows() + " col:" + points.cols() + " channels:" + points.channels());
            for (int i = 0; i < points.rows(); i++) {
                double[] posX = new double[4], posY = new double[4];
                for (int j = 0; j < 4; j++) {
                    posX[j] = points.get(i, j)[0];
                    posY[j] = points.get(i, j)[1];
                    Log.i("Points", j + ":" + Arrays.toString(posX) + "," + Arrays.toString(posY));
                }
                List<MatOfPoint> pos = new ArrayList<>();
                pos.add(new MatOfPoint(new Point(posX[0], posY[0]), new Point(posX[1], posY[1]), new Point(posX[2], posY[2]), new Point(posX[3], posY[3])));
                Imgproc.polylines(mat, pos, true, new Scalar(0, 255, 0), 3);
            }
        }
        points.release();
        return mat;
    }

    public List<Mat> encodeBytesToQRMatList(byte[] compressedBytes){
        List<Mat> qrMatList = new ArrayList<>();
        String compressedData = new String(compressedBytes);
        QRCodeEncoder qrCodeEncoder = QRCodeEncoder.create();
        qrCodeEncoder.encodeStructuredAppend(compressedData, qrMatList);
        return qrMatList;
    }

    public void joinMats(List<Mat> matList){
        for(int i = 0; i < matList.size(); i++){
            Mat mat = matList.get(i);
            Log.i("MatSize", "width:" + mat.width() + "height:" + mat.height());
        }
    }

    public Bitmap convertMatToBitmap(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        mat.release();
        return bitmap;
    }
}
