package com.example.more.utills;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

public class Parser {

    /**
     * Method to pass result of BarCode and QrCode
     * Based on String representation eg. {@link JSONObject}
     * @param jsonString
     * @return
     */
    public static String parseJsonAll(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            //setting values to textviews
            StringBuilder sb = new StringBuilder();
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = json.get(key);
                sb.append(key).append(" : ").append(value).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return parseResult(jsonString);
        }
    }

    /**
     * Method to pass result of BarCode and QrCode
     * Based on String representation eg. {@link XmlPullParser}
     * @param parseTo
     * @return
     */
    public static String parseResult(String parseTo) {
        boolean hast_text = false;
        String firstStartTag = null;

        StringBuilder sb = new StringBuilder();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();


            xpp.setInput(new StringReader(parseTo));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    //handels Start Tag
                    if (TextUtils.isEmpty(firstStartTag)) {
                        firstStartTag = xpp.getName();
                        sb.append(firstStartTag);
                    } else
                        sb.append(xpp.getName());

                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                    sb.append(" : ").append(xpp.getText());
                    hast_text = true;
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            Log.d("XMLpp", e.getMessage());
        } catch (IOException e) {
            Log.d("XMLpp", e.getMessage());
        }
        return hast_text ? sb.toString() : TextUtils.isEmpty(firstStartTag) ? parseTo : parseTo.replace(firstStartTag, "").replaceAll("[<>/\\[\\],-]", "").replace("?xml version=\"1.0\" encoding=\"utf-8\"","");

    }


}
