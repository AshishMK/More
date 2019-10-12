package com.example.more.data.local.model;

import com.example.more.data.Status;

public class WhatsAppStatus {
    public String path;
    public int position;
    public String type;
    public Status status;
    public boolean isDownloaded = false;

    public WhatsAppStatus(String path, String type, Status status, boolean isDownloaded) {
        this.path = path;
        this.status = status;
        this.type = type;
        this.isDownloaded = isDownloaded;
    }

    public WhatsAppStatus(String path, int position, String type, Status status) {
        this.path = path;
        this.status = status;
        this.type = type;
        this.position = position;
    }


}
