package ru.steganography.tasks;

import net.vrallev.android.task.Task;

import ru.steganography.engine.Steganography;

public class FindTask extends Task<Boolean> {

    private String mPathToImage;
    private String mPathToSave;
    private String mPassword;

    public FindTask(String pathToImage, String pathToSave, String password) {
        super();
        mPathToImage = pathToImage;
        mPathToSave = pathToSave;
        mPassword = password;
    }

    @Override
    protected Boolean execute() {
        try {
            Steganography.withInput(mPathToImage)
                    .withPassword(mPassword)
                    .decode()
                    .intoFile(mPathToSave);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}