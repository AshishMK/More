package com.example.more.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.net.Uri;

import com.example.more.data.Resource;
import com.example.more.utills.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

/**
 * {@link com.example.more.ui.activity.CodeScanActivity}
 */
public class ImageFirebaseViewModel extends ViewModel {


    /* We are using LiveData to update the UI with the data changes.
     */
    private MutableLiveData<Resource<String>> textLiveData = new MutableLiveData<>();

    /*
     * We are injecting the ContentDao class
     * and the ContentApiService class to the ViewModel.
     * */

    @Inject
    public ImageFirebaseViewModel() {
    }

    /*
     * Method called by UI to fetch movies list
     * */
    ArrayList<FirebaseVisionText> fText = new ArrayList<>();

    public void detectImagesText(ArrayList<String> imagePathList, Context ctx) {


        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(ctx, Uri.fromFile( new File(imagePathList.get(0))));
        } catch (IOException e) {
            System.out.println("fgg "+e.getLocalizedMessage());
        }

        if (image == null) {
            return;
        }
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText texts) {
                fText.add(texts);
                imagePathList.remove(0);
                System.out.println("fgg "+texts.toString());
                if (imagePathList.size() == 0) {

                    textLiveData.setValue(Resource.success(FirebaseUtils.processExtractedText(fText)));
                } else {
                    detectImagesText(imagePathList, ctx);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure
                    (@NonNull Exception exception) {
                imagePathList.remove(0);
                if (imagePathList.size() == 0) {
                    textLiveData.setValue(Resource.error("Error in processing image", null));
                } else
                    detectImagesText(imagePathList, ctx);

            }
        });
    }


    /*
     * LiveData observed by the UI
     * */
    public MutableLiveData<Resource<String>> getTextLiveData() {
        return textLiveData;
    }
}
