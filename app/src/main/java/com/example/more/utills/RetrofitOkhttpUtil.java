package com.example.more.utills;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RetrofitOkhttpUtil {
    public static RequestBody getRequestBodyText(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }

    public static RequestBody getRequestBodyImage(File param) {
        return RequestBody.create(MediaType.parse("image/*"), param);
    }

    public static RequestBody getRequestBodyVideo(File param) {
        return RequestBody.create(MediaType.parse("video/*"), param);
    }

    public static ArrayList<RequestBody> getRequestList(ArrayList<String> list, boolean includeDummyIndex) {
        ArrayList<RequestBody> tmp = new ArrayList<>();

        for (String param : list) {
            tmp.add(getRequestBodyText(param));
        }
        if (tmp.size()>0 && includeDummyIndex){
                tmp.add(0,RetrofitOkhttpUtil.getRequestBodyText("dummy data as atleast size of arraylist must be 2"));
        }

        return tmp;
    }
}
