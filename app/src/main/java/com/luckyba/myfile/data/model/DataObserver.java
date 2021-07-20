package com.luckyba.myfile.data.model;

import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luckyba.myfile.storage.StorageViewModel;

import java.io.File;

public class DataObserver extends FileObserver {
    private static StorageViewModel storageViewModel;
    private static DataObserver mInstance;
    private String rootPath;
    private static final int MASK = FileObserver.DELETE | FileObserver.DELETE_SELF | FileObserver.MOVE_SELF
            | FileObserver.MOVED_FROM | FileObserver.MOVED_TO | FileObserver.CREATE;

    public DataObserver(@NonNull File file) {
        super(file, MASK);
        rootPath = file.getAbsolutePath();
    }

    public static DataObserver getInstance(File file) {
        if (mInstance == null) {
            synchronized (DataObserver.class) {
                if (mInstance == null) {
                    mInstance = new DataObserver(file);
                }
            }
        }
        return mInstance;
    }

    public void setStorageViewModel (StorageViewModel storageViewModel) {
        this.storageViewModel = storageViewModel;
    }


    @Override
    public void onEvent(int event, @Nullable String path) {
        int value = event & MASK;
        Log.d("fdsafsa", " onEvent " + value +" event "+ event);

        if (value >0 && path != null) {
            Log.d("fdsafsa", "onChange "+ path);
            storageViewModel.getAllInternal(rootPath);
        } else {
            Log.d("fdsafsa", "onEvent "+ event + " is ignored");
        }
    }
}
