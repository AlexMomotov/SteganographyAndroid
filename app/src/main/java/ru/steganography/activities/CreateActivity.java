package ru.steganography.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;

import net.vrallev.android.task.Task;
import net.vrallev.android.task.TaskExecutor;
import net.vrallev.android.task.TaskResult;

import java.io.File;
import java.lang.reflect.Field;

import carbon.widget.ImageView;
import carbon.widget.ScrollView;
import carbon.widget.TextView;
import ru.steganography.R;
import ru.steganography.tasks.CreateTask;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener, FileChooserDialog.FileCallback {

    private Toolbar mToolbar;
    private ImageView mButtonPicture, mButtonFile, mButtonPassword, mButtonShare;
    private TextView mTextPicture, mTextFile, mTextPassword;
    private ScrollView mScroll;
    private TextView mTitlePicture, mTitleFile, mTitlePassword;

    private String mPathToImage;
    private String mPathToFile;
    private String mPathToSave;
    private String mPassword;

    private boolean mIsTaskShouldRunning = false;

    private MaterialDialog mLoadDialog;
    private FileChooserDialog mChooseImageDialog;
    private FileChooserDialog mChooseFileDialog;
    private MaterialDialog mPasswordDialog;
    private MaterialDialog mErrorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Init toolbar
        mToolbar = (Toolbar) findViewById(R.id.create_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_toolbar_close);
        mToolbar.setContentInsetsAbsolute((int) getResources().getDimension(R.dimen.second_keyline), 0);
        makeToolbarLookGood(mToolbar);

        // Init buttons
        mButtonPicture = (ImageView) findViewById(R.id.create_picture);
        mButtonPicture.setOnClickListener(this);
        mButtonFile = (ImageView) findViewById(R.id.create_file);
        mButtonFile.setOnClickListener(this);
        mButtonPassword = (ImageView) findViewById(R.id.create_password);
        mButtonPassword.setOnClickListener(this);
        mButtonShare = (ImageView) findViewById(R.id.create_share);
        mButtonShare.setOnClickListener(this);

        // Init text
        mTextPicture = (TextView) findViewById(R.id.create_picture_text);
        mTextPicture.setOnClickListener(this);
        mTextFile = (TextView) findViewById(R.id.create_file_text);
        mTextFile.setOnClickListener(this);
        mTextPassword = (TextView) findViewById(R.id.create_password_text);
        mTextPassword.setOnClickListener(this);

        // Init titles
        mTitlePicture = (TextView) findViewById(R.id.create_picture_title);
        mTitleFile = (TextView) findViewById(R.id.create_file_title);
        mTitlePassword = (TextView) findViewById(R.id.create_password_title);

        // Init scroll
        mScroll = (ScrollView) findViewById(R.id.create_scroll);

        // Init save path
        mPathToSave = Environment.getExternalStorageDirectory() + File.separator + "steganography_secret_image.png";

        if (savedInstanceState != null) {
            mIsTaskShouldRunning = savedInstanceState.getBoolean("mIsTaskShouldRunning");
            mPathToImage = savedInstanceState.getString("mPathToImage");
            mPathToFile = savedInstanceState.getString("mPathToFile");
            mPassword = savedInstanceState.getString("mPassword");
        }

        if (mIsTaskShouldRunning == true) {
            makeLoadDialog(true);
        }

        refreshStep();
    }

    void startCreateTask() {
        mIsTaskShouldRunning = true;
        Task task = new CreateTask(mPathToImage, mPathToFile, mPathToSave, mPassword);
        TaskExecutor.getInstance().execute(task, this);
    }

    @TaskResult
    public void onResult(Boolean isOk) {
        mIsTaskShouldRunning = false;
        makeLoadDialog(false);
        if (!isOk) {
            makeErrorDialog();
        } else {
            shareFile(mPathToSave);
        }
        refreshStep();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* Save states and values to bundle when recreate */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mIsTaskShouldRunning", mIsTaskShouldRunning);
        outState.putString("mPathToImage", mPathToImage);
        outState.putString("mPathToFile", mPathToFile);
        outState.putString("mPassword", mPassword);
    }

    /* Handles toolbar menu clicks */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return false;
        }
        return false;
    }

    /* Change toolbar title typeface */
    private void makeToolbarLookGood(Toolbar toolbar) {
        android.widget.TextView titleTextView = null;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (android.widget.TextView) f.get(toolbar);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        titleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf"));
        titleTextView.setAllCaps(true);
    }

    /* Handles views clicks */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_picture:
                makeChooseImageDialog();
                break;
            case R.id.create_file:
                makeChooseFileDialog();
                break;
            case R.id.create_password:
                makePasswordDialog();
                break;
            case R.id.create_share:
                makeLoadDialog(true);
                startCreateTask();
                mPathToFile = mPathToImage = mPassword = null;
                break;
            default:
                break;
        }
    }

    /* Handle file selections form dialogs */
    @Override
    public void onFileSelection(@NonNull File file) {
        if (mPathToImage == null) {
            mPathToImage = file.getAbsolutePath();
            refreshStep();
        } else {
            mPathToFile = file.getAbsolutePath();
            refreshStep();
        }
    }

    private void refreshStep() {
        if ((mPathToImage != null) && (mPathToFile == null) && (mPassword == null)) {
            makeBlockUsed(mButtonPicture, mTextPicture, getFileExtension(mPathToImage), mTitlePicture, getFileName(mPathToImage));
            makeBlockDeactivate(mButtonPicture);
            makeBlockActive(mButtonFile);
            makeBlockDeactivate(mButtonPassword);
            makeBlockDeactivate(mButtonShare);
        }
        if ((mPathToImage != null) && (mPathToFile != null) && (mPassword == null)) {
            makeBlockUsed(mButtonPicture, mTextPicture, getFileExtension(mPathToImage), mTitlePicture, getFileName(mPathToImage));
            makeBlockUsed(mButtonFile, mTextFile, getFileExtension(mPathToFile), mTitleFile, getFileName(mPathToFile));
            makeBlockDeactivate(mButtonFile);
            makeBlockDeactivate(mButtonPicture);
            makeBlockActive(mButtonPassword);
            makeBlockDeactivate(mButtonShare);
            scrollToEnd();
        }
        if ((mPathToImage != null) && (mPathToFile != null) && (mPassword != null)) {
            makeBlockUsed(mButtonPicture, mTextPicture, getFileExtension(mPathToImage), mTitlePicture, getFileName(mPathToImage));
            makeBlockUsed(mButtonFile, mTextFile, getFileExtension(mPathToFile), mTitleFile, getFileName(mPathToFile));
            makeBlockUsed(mButtonPassword, mTextPassword, mPassword, mTitlePassword, mPassword);
            makeBlockDeactivate(mButtonFile);
            makeBlockDeactivate(mButtonPicture);
            makeBlockDeactivate(mButtonPassword);
            makeBlockActive(mButtonShare);
            scrollToEnd();
        }
        if ((mPathToImage == null) && (mPathToFile == null) && (mPassword == null)) {
            makeBlockUnused(mButtonPicture, mTextPicture, mTitlePicture, getResources().getString(R.string.create_step_picture_title));
            makeBlockUnused(mButtonFile, mTextFile, mTitleFile, getResources().getString(R.string.create_step_file_title));
            makeBlockUnused(mButtonPassword, mTextPassword, mTitlePassword, getResources().getString(R.string.create_step_password_title));
            makeBlockActive(mButtonPicture);
            makeBlockDeactivate(mButtonFile);
            makeBlockDeactivate(mButtonPassword);
            makeBlockDeactivate(mButtonShare);
            scrollToStart();
        }
    }

    private void makeBlockUsed(ImageView button, TextView textView, String text, TextView titleView, String title) {
        button.setVisibility(View.GONE);
        button.setClickable(false);
        button.setBackgroundColor(getResources().getColor(R.color.night_normal));
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        titleView.setText(title);
    }

    private void makeBlockUnused(ImageView button, TextView textView, TextView titleView, String title) {
        makeBlockActive(button);
        textView.setText("");
        textView.setVisibility(View.GONE);
        titleView.setText(title);
    }

    private void makeBlockActive(ImageView button) {
        button.setVisibility(View.VISIBLE);
        button.setClickable(true);
        button.setBackgroundColor(getResources().getColor(R.color.rose_normal));
        button.setElevation(20);
    }

    private void makeBlockDeactivate(ImageView button) {
        button.setVisibility(View.VISIBLE);
        button.setClickable(false);
        button.setBackgroundColor(getResources().getColor(R.color.night_normal));
        button.setElevation(0);
    }

    /* Display or hide loading dialog */
    private void makeLoadDialog(boolean shouldShow) {
        if (shouldShow) {
            mLoadDialog = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_load_title)
                    .content(R.string.dialog_load_please_wait)
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
        } else {
            if (mLoadDialog.isShowing()) {
                mLoadDialog.cancel();
            }
        }
    }

    /* Display dialog to choose image */
    private void makeChooseImageDialog() {
        mChooseImageDialog = new FileChooserDialog.Builder(this)
                .cancelButton(R.string.dialog_about_close)
                .initialPath("/sdcard")
                .mimeType("image/*")
                .show();
    }

    /* Display dialog to choose file to hide */
    private void makeChooseFileDialog() {
        mChooseFileDialog = new FileChooserDialog.Builder(this)
                .cancelButton(R.string.dialog_about_close)
                .initialPath("/sdcard")
                .show();
    }

    /* Display dialog to input password */
    private void makePasswordDialog() {
        mPasswordDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_password_title)
                .inputRangeRes(2, 4, R.color.rose_normal)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(getResources().getString(R.string.dialog_password_input), "1234", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if ((input != null) && (input.length() > 0)) {
                            mPassword = String.valueOf(input);
                            refreshStep();
                        }

                    }
                }).show();
    }

    /* Display error dialog if something gone wrong */
    private void makeErrorDialog() {
        mErrorDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_error_small_image_title)
                .content(R.string.dialog_error_small_image_content)
                .negativeText(R.string.dialog_error_small_image_close)
                .cancelable(true)
                .show();
    }

    private void scrollToEnd() {
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void scrollToStart() {
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        String extension = null;
        if (dotIndex != -1) {
            if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
                extension = filePath.substring(dotIndex + 1).toUpperCase();
            }
        } else {
            extension = ".DAT";
        }
        return extension;
    }

    private String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    private void shareFile(String path) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(path);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("*/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
            startActivity(Intent.createChooser(intentShareFile, getResources().getString(R.string.menu_share)));
        }
    }
}