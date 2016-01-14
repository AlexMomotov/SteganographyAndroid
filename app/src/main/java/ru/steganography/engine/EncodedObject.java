package ru.steganography.engine;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class EncodedObject {

    private final Bitmap mBitmap;

    public EncodedObject(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public Bitmap intoBitmap() {
        return mBitmap;
    }

    public File intoFile(String path) throws IOException {
        return intoFile(new File(path));
    }

    public File intoFile(File file) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        file.createNewFile();
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();
        return file;
    }
}