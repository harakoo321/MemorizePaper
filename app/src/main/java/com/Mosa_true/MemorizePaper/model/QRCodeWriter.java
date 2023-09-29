package com.Mosa_true.MemorizePaper.model;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.objdetect.QRCodeEncoder;

import java.util.List;

public class QRCodeWriter {
    private final Bitmap _qrCode;

    public QRCodeWriter(String fileData) throws NullPointerException, IllegalArgumentException {
        Mat qrMat = encodeStringToQRMat(fileData);
        _qrCode = convertMatToBitmap(qrMat);
        qrMat.release();
    }

    private Mat encodeStringToQRMat(String data){
        if(data.getBytes().length > 960) {
            throw new IllegalArgumentException();
        }
        Mat qrMat = new Mat();
        QRCodeEncoder qrCodeEncoder = QRCodeEncoder.create();
        qrCodeEncoder.encode(data, qrMat);
        return qrMat;
    }

    private Bitmap convertMatToBitmap(Mat mat) throws IllegalArgumentException{
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private Mat mergeMatList(List<Mat> matList){
        int qrSize = matList.get(0).rows();
        int matSize = (qrSize + 20) * 4;
        Mat mat = new Mat(matSize, matSize, matList.get(0).type(), new Scalar(255, 255, 255));
        for(int i = 0; i < matList.size(); i++){
            matList.get(i).copyTo(mat.submat((i / 4) * (qrSize + 20), qrSize, i * (qrSize + 20), qrSize));
        }
        return mat;
    }

    public Bitmap getQRCode() {
        return _qrCode;
    }
}
