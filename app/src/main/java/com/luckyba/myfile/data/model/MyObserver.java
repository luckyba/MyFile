package com.luckyba.myfile.data.model;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;

import static com.luckyba.myfile.utils.Constant.SCAN_DATA_CALLBACK;

public class MyObserver extends ContentObserver {
    private Handler mHandler;
    public MyObserver(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Message message = Message.obtain();
        message.what = SCAN_DATA_CALLBACK;
        message.obj = "Data has changed";
        mHandler.sendMessage(message);
        Log.d("dfsafsa", "MyContentObserver.onChange("+selfChange+")");
    }

    @Override
    public void onChange(boolean selfChange, @Nullable Uri uri) {
        super.onChange(selfChange, uri);
        Log.d("dfsafsa", "MyContentObserver.onChange("+selfChange+")");
    }

    @Override
    public void onChange(boolean selfChange, @Nullable Uri uri, int flags) {
        super.onChange(selfChange, uri, flags);
        Log.d("dfsafsa", "MyContentObserver.onChange("+selfChange+")");
    }

    @Override
    public void onChange(boolean selfChange, @NonNull Collection<Uri> uris, int flags) {
        super.onChange(selfChange, uris, flags);
        Log.d("dfsafsa", "MyContentObserver.onChange("+selfChange+")");

    }
}
