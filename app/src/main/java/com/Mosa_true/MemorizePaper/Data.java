package com.Mosa_true.MemorizePaper;

import android.app.Application;
import android.graphics.Bitmap;

public class Data extends Application {
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

