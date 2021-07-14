package com.luckyba.myfile.data.model;

import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class DataObserver extends FileObserver {

    private static final int MASK = FileObserver.DELETE | FileObserver.DELETE_SELF | FileObserver.MOVE_SELF
            | FileObserver.MOVED_FROM | FileObserver.MOVED_TO | FileObserver.CREATE;
    public DataObserver(String path) {
        super(path, MASK);
    }

    public DataObserver(@NonNull File file) {
        super(file, MASK);
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        int value = event & MASK;
        Log.d("fdsafsa", " onEvent " + value +" event "+ event);

        if (value >0 && path != null) {
            Log.d("fdsafsa", "onChange");
        } else {
            Log.d("fdsafsa", "onEvent "+ event + " is ignored");
        }
    }
}
