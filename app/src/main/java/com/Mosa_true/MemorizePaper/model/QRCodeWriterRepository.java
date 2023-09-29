package com.Mosa_true.MemorizePaper.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;

public class QRCodeWriterRepository {
    private final InputFile _inputFile;
    private final QRCodeWriter _qrCodeWriter;

    public QRCodeWriterRepository(Uri inputFileUri, Context context)
            throws IOException, IllegalArgumentException, NullPointerException {
        _inputFile = new InputFile(inputFileUri, context);
        _qrCodeWriter = new QRCodeWriter(_inputFile.getFileData());
    }

    public String getFileName() {
        return _inputFile.getFileName();
    }

    public Bitmap getQRCode() {
        return _qrCodeWriter.getQRCode();
    }
}
