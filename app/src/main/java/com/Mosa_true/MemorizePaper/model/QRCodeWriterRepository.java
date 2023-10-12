package com.Mosa_true.MemorizePaper.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;

public class QRCodeWriterRepository {
    private final InputFile inputFile;
    private final QRCodeWriter qrCodeWriter;

    public QRCodeWriterRepository(Uri inputFileUri, Context context)
            throws IOException, IllegalArgumentException, NullPointerException {
        inputFile = new InputFile(inputFileUri, context);
        qrCodeWriter = new QRCodeWriter(inputFile.getFileData());
    }

    public String getFileName() {
        return inputFile.getFileName();
    }

    public Bitmap getQRCode() {
        return qrCodeWriter.getQRCode();
    }
}
