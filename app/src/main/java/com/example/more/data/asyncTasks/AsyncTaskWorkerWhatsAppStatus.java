package com.example.more.data.asyncTasks;


import android.content.Context;
import android.os.Environment;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.data.Resource;
import com.example.more.data.Status;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.model.WhatsAppStatus;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.utills.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Class use to perform background task for Whatsapp status functionality
 * {@link com.example.more.ui.fragment.WhatsappStatusFragment}
 * One of the first things we do in the Repository class.
 * That class is based on LiveData but here we are
 * using Observable from RxJava.
 * is to make it a Singleton.
 * */

@Singleton
public class AsyncTaskWorkerWhatsAppStatus {

    private ContentDao contentDao;

    // Default location of whatsapp status fragment
    public static final String WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses";

    // Default location for saving app's content
    File directory = new File(Environment.getExternalStorageDirectory().toString() + "/" + AppController.getInstance().getString(R.string.app_name));

    public AsyncTaskWorkerWhatsAppStatus(ContentDao contentDao, ContentApiService contentApiServic) {
        this.contentDao = contentDao;
    }


    /*
     * We are using this method to fetch the list of whatsapp status media paths
     * So basically we fetch paths from whatsapp folder, update the UI withObservable.fromCallable(
     * this data.
     */
    public Observable<Resource<List<WhatsAppStatus>>> getFilePaths() {
        Observable<Resource<List<WhatsAppStatus>>> result;
        Observable<Resource<List<WhatsAppStatus>>> source = Observable.fromCallable(() -> getPaths()).subscribeOn(Schedulers.io())
                .flatMap(apiResponse -> Observable.just(processResponse(apiResponse)).map(Resource::success))
                .onErrorResumeNext(t -> {
                    return handleError()
                            .toObservable()
                            .map(strings -> Resource.error(t.getMessage(), strings));
                })
                .observeOn(AndroidSchedulers.mainThread());

        result = Observable.concat(
                handleError()
                        .toObservable()
                        .map(Resource::loading).take(1)
                ,
                source
        );

        return result;
    }

    /*Basically it wraps the request type into response type */
    List<WhatsAppStatus> processResponse(ArrayList<WhatsAppStatus> response) {
        return response;
    }

    /*it return empty response when an error occurred*/
    Flowable<List<WhatsAppStatus>> handleError() {
        return Flowable.just(new ArrayList<WhatsAppStatus>());
    }

    /*
     * We are using this method to copy whatsapp status media to App default folder from given whatsapp media file path.
     * So basically we copy files from whatsapp folder, update the UI withObservable.fromCallable(
     * this data.
     */
    public Observable<Resource<WhatsAppStatus>> copyFile(final ArrayList<WhatsAppStatus> filePath, Context context) {
        Observable<Resource<WhatsAppStatus>> result;
        Observable<Resource<WhatsAppStatus>> source = Observable.fromIterable(filePath).map(path -> copyFile(path, context)).subscribeOn(Schedulers.io())
                .flatMap(s -> Observable.just(s).map(Resource::success))
                .onErrorResumeNext(t -> {
                    return Observable.just(new WhatsAppStatus("", 0, "", Status.ERROR))
                            .map(data -> Resource.error(t.getMessage(), data));

                })
                .observeOn(AndroidSchedulers.mainThread());
        filePath.get(0).status = Status.LOADING;
        result = Observable.concat(Observable.just(filePath.get(0))
                        .map((Resource::loading)).take(1)
                ,
                source
        );
        return result;
    }

    /*
    *   Method to get paths oa all files from whatsapp status folder
    *
    */
    ArrayList<WhatsAppStatus> getPaths() {
        File parentDir = new File(Environment.getExternalStorageDirectory().toString() + WHATSAPP_STATUSES_LOCATION);
        ArrayList<WhatsAppStatus> inFiles = new ArrayList<>();
        File[] files;
        files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {

                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".gif") || file.getName().endsWith(".mp4")) {

                    if (!inFiles.contains(file)) {
                        boolean existed = new File(directory, file.getName()).exists();
                        if (file.getName().endsWith(".mp4")) {
                            inFiles.add(new WhatsAppStatus(existed ? new File(directory, file.getName()).getAbsolutePath() : file.getAbsolutePath(), ".mp4", Status.SUCCESS, existed));
                        } else {
                            inFiles.add(new WhatsAppStatus(existed ? new File(directory, file.getName()).getAbsolutePath() : file.getAbsolutePath(), ".img", Status.SUCCESS, existed));
                        }
                    }
                }
            }
        }
        return inFiles;
    }

    /**
     * copy file to destination.
     *
     * @throws IOException
     */
    public WhatsAppStatus copyFile(WhatsAppStatus path, Context context) throws IOException {
        File sourceFile = new File(path.path);
        File destFile = new File(directory, new File(path.path).getName());
        if (!directory.exists())
            directory.mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        path.isDownloaded = true;
        path.status = Status.SUCCESS;
        Utils.sendFileToScan(destFile);
        path.path = destFile.getPath();
        return path;
    }

}
