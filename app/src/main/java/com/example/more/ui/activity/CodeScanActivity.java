package com.example.more.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.more.R;
import com.example.more.factory.ViewModelFactory;
import com.example.more.ui.fragment.WhatsappStatusFragment;
import com.example.more.utills.Parser;
import com.example.more.utills.Utils;
import com.example.more.viewmodel.ImageFirebaseViewModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Class use to scan barcodes, qrcodes and extract text from a photo saved in device
 */
public class CodeScanActivity extends AppCompatActivity {
    public int TASK_CHOICE = 0;

    // use this class to scan Barcodes and QRcodes
    public static final int TOOL_INTENT_TYPE_SCAN = 0;
    //use this class to extract text from a photo
    public static final int TOOL_INTENT_TYPE_PICTURE = 1;
    public static final int PICK_IMAGE = 101;
    public static final String TOOL_INTENT_TYPE = "tool_intent_type";
    final static int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    /*
     * we need to
     * inject the ViewModelFactory. The ViewModelFactory class
     * has a list of ViewModels and will provide
     * the corresponding ViewModel in this activity
     * */
    @Inject
    ViewModelFactory viewModelFactory;

    /*
     * This is our ViewModel class
     * */
    private ImageFirebaseViewModel imageFirebaseViewModel;

    /*
     * Use to scan qrcodes and barcode provided by xzing library
     * provided by ToolsModule
     */

    @Inject
    IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Remember in our {@link com.example.more.di.module.ActivityModule}, we
         * defined {@link CodeScanActivity} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity.
         * */
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);
        getSupportActionBar().hide();
        TASK_CHOICE = getIntent().getIntExtra(TOOL_INTENT_TYPE, TOOL_INTENT_TYPE_SCAN);
        initialiseViewModel();
        if (TASK_CHOICE == TOOL_INTENT_TYPE_SCAN) {
            intentIntegrator.initiateScan();
        } else {
            // request permission for external storage as we need to get image from storage for extracting text from image
            Utils.PermissionStatus status = Utils.checkPermissionsCamera(this, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
            if (status == Utils.PermissionStatus.SUCCESS) pickImage();
            else if (status == Utils.PermissionStatus.ERROR) finish();
        }
    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            // Extracting text from image
            if (requestCode == PICK_IMAGE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                ArrayList<String> picturePaths = new ArrayList<>();
                picturePaths.add(cursor.getString(columnIndex));
                cursor.close();
                imageFirebaseViewModel.detectImagesText(picturePaths, CodeScanActivity.this);
            }
            // receiving data from scanned QRcode or Barcode
            else if (requestCode == intentIntegrator.REQUEST_CODE) {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {

                    //if qrcode has nothing in it
                    if (result.getContents() == null) {
                        Toast.makeText(this, "Unable to read", Toast.LENGTH_SHORT).show();
                    } else {

                        setResult(RESULT_OK, new Intent().putExtra("message", Parser.parseJsonAll(result.getContents())));
                    }

                }
                finish();
            }
        } else {
            Toast.makeText(this, "Unable to read", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
            finish();
        }
    }

    /**
     * Method to pick an image from gallery
     */
    void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    /*
     * Initialising the ViewModel class here.
     * We are adding the ViewModelFactory class here.
     * We are observing the LiveData\
     * Basically here we receive result from the process of extracting text from image
     * */
    private void initialiseViewModel() {
        imageFirebaseViewModel = ViewModelProviders.of(this, viewModelFactory).get(ImageFirebaseViewModel.class);
        imageFirebaseViewModel.getTextLiveData().observe(this, resource -> {
            if (resource.isLoading()) {

            } else if (!TextUtils.isEmpty(resource.data)) {
                setResult(RESULT_OK, new Intent().putExtra("message", resource.data));
            } else {
                Toast.makeText(this, "Unable to read", Toast.LENGTH_SHORT).show();
            }
            finish();
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else finish();
        }
    }

    /**
     * Use to provide intent for this activity
     *
     * @param ctx
     * @param type type of operation eg. Scn Barcode, QRcode or Scan image to extract text
     * @return
     */
    public static Intent getIntent(Context ctx, int type) {
        Intent intent = new Intent(ctx, CodeScanActivity.class);
        intent.putExtra(TOOL_INTENT_TYPE, type);
        return intent;
    }


}
