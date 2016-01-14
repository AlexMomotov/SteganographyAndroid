package ru.steganography.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.reflect.Field;

import carbon.widget.Button;
import ru.steganography.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_WRITE = 3;
    private static final int PERMISSION_REQUEST_READ = 4;

    private Toolbar mToolbar;
    private Button mCreateButton, mFindButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        makeToolbarLookGood(mToolbar);

        // Init buttons
        mCreateButton = (Button) findViewById(R.id.main_button_create);
        mCreateButton.setOnClickListener(this);
        mFindButton = (Button) findViewById(R.id.main_button_find);
        mFindButton.setOnClickListener(this);
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

    /* Handles toolbar menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Handles toolbar menu clicks */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                makeAboutDialog();
                return true;
            case android.R.id.home:
                return true;
            default:
                return false;
        }

    }

    /* About dialog */
    private void makeAboutDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_about_title)
                .content(R.string.dialog_about_content)
                .negativeText(R.string.dialog_about_close)
                .show();
    }

    /* Handles button clicks */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_button_create:
                startCreateActivity();
                break;
            case R.id.main_button_find:
                startFindActivity();
                break;
            default:
                break;
        }
    }

    /* Start create */
    private void startCreateActivity() {
        if ((checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
            startActivity(intent);
        }
    }

    /* Start find */
    private void startFindActivity() {
        if ((checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            Intent intent2 = new Intent(MainActivity.this, FindActivity.class);
            startActivity(intent2);
        }
    }

    /* Check permission */
    private boolean checkPermission(String permission) {
        boolean permissionIsGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!permissionIsGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, getResources().getString(R.string.toast_permission_description), Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_WRITE);
        }
        return permissionIsGranted;
    }

    /* Get permission */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE:
                if (!((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))) {
                    Toast.makeText(this, getResources().getString(R.string.toast_permission_no), Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_READ:
                if (!((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))) {
                    Toast.makeText(this, getResources().getString(R.string.toast_permission_no), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}