package com.example.more.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2rx.RxFetch;

import io.reactivex.functions.Consumer;

/**
 * Receiver to cancel facebook downloads @see kink #FileDownloadNotificationListener
 */
public class CancelReceiver extends BroadcastReceiver {
    public static final String ACTION = "cancel_download";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        int id = intent.getIntExtra("id", -1);
        Toast.makeText(context, ""+id, Toast.LENGTH_SHORT).show();
        if (id != -1) {
            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context).build();
            RxFetch rxFetch = RxFetch.Impl.getRxInstance(fetchConfiguration);
            rxFetch.cancel(id).asFlowable().subscribe(new Consumer<Download>() {
                @Override
                public void accept(Download download) throws Exception {
                    Toast.makeText(context, "canceled " + download.getStatus(), Toast.LENGTH_SHORT).show();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(context, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
