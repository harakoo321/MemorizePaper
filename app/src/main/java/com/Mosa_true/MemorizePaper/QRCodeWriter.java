package com.Mosa_true.MemorizePaper;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.objdetect.QRCodeEncoder;

import java.util.List;

public class QRCodeWriter {
    public Mat encodeStringToQRMat(String data){
        Mat qrMat = new Mat();
        QRCodeEncoder qrCodeEncoder = QRCodeEncoder.create();
        qrCodeEncoder.encode(data, qrMat);
        return qrMat;
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
        matList = null;
        return mat;
    }

    public Bitmap convertMatToBitmap(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        mat.release();
        return bitmap;
    }
}
