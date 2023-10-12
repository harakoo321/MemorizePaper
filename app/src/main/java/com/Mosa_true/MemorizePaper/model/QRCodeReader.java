package com.Mosa_true.MemorizePaper.model;

import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;

public class QRCodeReader {
    QRCodeDetector qrCodeDetector = new QRCodeDetector();
    public String detectAndDecodeQR(Mat qrCode) {
        return qrCodeDetector.detectAndDecode(qrCode);
    }
}
