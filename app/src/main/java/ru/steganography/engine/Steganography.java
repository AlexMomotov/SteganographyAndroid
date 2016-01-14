package ru.steganography.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Steganography {

    private String mKey = null;
    private Bitmap mInBitmap = null;

    /* Constructors */
    public static Steganography withInput(@NonNull String filePath) {
        Steganography steganography = new Steganography();
        steganography.setInputBitmap(BitmapFactory.decodeFile(filePath));
        return steganography;
    }

    public static Steganography withInput(@NonNull File file) {
        Steganography steganography = new Steganography();
        steganography.setInputBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        return steganography;
    }

    public static Steganography withInput(@NonNull Bitmap bitmap) {
        Steganography steganography = new Steganography();
        steganography.setInputBitmap(bitmap);
        return steganography;
    }

    public Steganography withPassword(@NonNull String key) {
        this.mKey = key;
        return this;
    }

    /* Set bitmap */
    private void setInputBitmap(@NonNull Bitmap bitmap) {
        this.mInBitmap = bitmap;
    }

    /* Check key existence and decode it */
    public DecodedObject decode() throws Exception {
        byte[] bytes = BitmapEncoder.decode(mInBitmap).clone();
        byte[] data;
        if (mKey != null) {
            data = AESCrypt.decrypt(mKey, bytes).clone();
        } else {
            data = bytes.clone();
        }
        return new DecodedObject(data);
    }

    /* Encode it */
    public EncodedObject encode(@NonNull File file) throws Exception {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encode(bytes);
    }

    /* Encode it */
    public EncodedObject encode(@NonNull String filePath) throws Exception {
        return encode(new File(filePath));
    }

    /* Check key existence and bitmap hide capacity. Then encode it */
    public EncodedObject encode(@NonNull byte[] bytes) throws Exception {
        byte[] data;
        if (mKey != null) {
            data = AESCrypt.encrypt(mKey, bytes).clone();
        } else {
            data = bytes.clone();
        }
        if (data.length > bytesAvaliableInBitmap()) {
            throw new IllegalArgumentException("Not enough space in bitmap to hold data (max:" + bytesAvaliableInBitmap() + ")");
        }
        return new EncodedObject(BitmapEncoder.encode(mInBitmap, data));
    }

    /* Check bitmap hide capacity */
    private int bytesAvaliableInBitmap() {
        if (mInBitmap == null) return 0;
        return (mInBitmap.getWidth() * mInBitmap.getHeight()) * 3 / 8 - BitmapEncoder.HEADER_SIZE;
    }
}