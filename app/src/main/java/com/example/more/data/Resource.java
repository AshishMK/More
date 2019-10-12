package com.example.more.data;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.more.data.Status.ERROR;
import static com.example.more.data.Status.LOADING;
import static com.example.more.data.Status.SUCCESS;


public class Resource<T> {
    @NonNull
    public Status getStatus() {
        return status;
    }

    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable public final String message;
    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null);
    }

    public boolean isSuccess() {
        return status == SUCCESS && data != null;
    }

    public boolean isLoading() {
        return status == LOADING;
    }

    public boolean isLoaded() {
        return status != LOADING;
    }
}