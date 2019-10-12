package com.example.more.utills;

import android.text.TextUtils;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.ArrayList;

/**
 * class use to provide firebase api functionalities
 * {@link FirebaseVisionText}
 */
public class FirebaseUtils {
    /**
     * method to use extract text from image using {@link FirebaseVisionText} results
     *
     * @param fText
     * @return
     */
    public static String processExtractedText(ArrayList<FirebaseVisionText> fText) {
        String toTrans = "";
        int i = 0;
        for (FirebaseVisionText firebaseVisionText : fText) {
            if (firebaseVisionText.getTextBlocks().size() == 0) {
                continue;
            }

            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                toTrans += ((i == 0 ? "" : "\n") + block.getText());
                i++;
            }
            toTrans += (i == 0 ? "" : "\n\n");

        }
        if (!TextUtils.isEmpty(toTrans)) {
            return toTrans;
        } else {
            return null;
        }
    }
}
