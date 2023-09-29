package com.Mosa_true.MemorizePaper.model;

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
import java.util.List;

public class QRCodeReader {

    public Mat detectQRMulti(Mat mat) {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        Mat points = new Mat();
        if (qrCodeDetector.detectMulti(mat, points)) {
            Log.i("Points", "row:" + points.rows() + " col:" + points.cols() + " channels:" + points.channels());
            for (int i = 0; i < points.rows(); i++) {
                double[] posX = new double[4], posY = new double[4];
                for (int j = 0; j < 4; j++) {
                    posX[j] = points.get(i, j)[0];
                    posY[j] = points.get(i, j)[1];
                }
                List<MatOfPoint> pos = new ArrayList<>();
                pos.add(new MatOfPoint(new Point(posX[0], posY[0]), new Point(posX[1], posY[1]), new Point(posX[2], posY[2]), new Point(posX[3], posY[3])));
                Imgproc.polylines(mat, pos, true, new Scalar(0, 255, 0), 3);
            }
        }else {
            Log.i("QR", "Couldn't Detect!");
        }
        points.release();
        return mat;
    }

    public List<String> detectAndDecodeQRMulti(Mat mat){
        List<String> detectedData = new ArrayList<>();
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        if(qrCodeDetector.detectAndDecodeMulti(mat, detectedData)){
            return detectedData;
        }
        else return null;
    }

    public Mat encodeBytesToQRMat(byte[] compressedBytes){
        Mat qrMat = new Mat();
        String compressedData = new String(compressedBytes);
        QRCodeEncoder qrCodeEncoder = QRCodeEncoder.create();
        qrCodeEncoder.encode(compressedData, qrMat);
        return qrMat;
    }

    public Mat mergeMatList(List<Mat> matList){
        int qrSize = matList.get(0).rows();
        int matSize = (qrSize + 20) * 4;
        Mat mat = new Mat(matSize, matSize, matList.get(0).type(), new Scalar(255, 255, 255));
        for(int i = 0; i < matList.size(); i++){
            matList.get(i).copyTo(mat.submat((i / 4) * (qrSize + 20), qrSize, i * (qrSize + 20), qrSize));
        }
        return mat;
    }

    public Bitmap convertMatToBitmap(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        mat.release();
        return bitmap;
    }
}
