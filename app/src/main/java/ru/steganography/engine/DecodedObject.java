package ru.steganography.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DecodedObject {

    private final byte[] mBytes;

    public DecodedObject(byte[] bytes) {
        this.mBytes = bytes;
    }

    public byte[] intoByteArray() {
        return mBytes;
    }

    public File intoFile(String path) throws IOException {
        return intoFile(new File(path));
    }

    public File intoFile(File file) throws IOException {
        FileOutputStream fo = new FileOutputStream(file);
        file.createNewFile();
        fo.write(mBytes);
        fo.close();
        return file;
    }
}