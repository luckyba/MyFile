package com.luckyba.myfile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.audios.AudiosViewModel;
import com.luckyba.myfile.data.reponsitory.FileRepository;
import com.luckyba.myfile.externalstorage.ExternalStorageViewModel;
import com.luckyba.myfile.images.ImagesListViewModel;
import com.luckyba.myfile.internalstorage.InternalStorageViewModel;
import com.luckyba.myfile.videos.VideosViewModel;

public class FactoryViewModel implements ViewModelProvider.Factory {

    private static FactoryViewModel mInstance;
    private final FileRepository fileRepository;
    private final Application application;

    public FactoryViewModel() {
        application = MyApplication.getInstance();
        fileRepository = MyApplication.getInstance().getRepository();
    }

    public static FactoryViewModel getInstance() {
        if (mInstance == null) {
            synchronized (FactoryViewModel.class) {
                if (mInstance == null)
                    mInstance =  new FactoryViewModel();
            }
        }
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(InternalStorageViewModel.class)) {
            return (T) new InternalStorageViewModel(application, fileRepository);
        } else if (modelClass.isAssignableFrom(ImagesListViewModel.class)) {
            return (T) new ImagesListViewModel(application, fileRepository);
        } else if (modelClass.isAssignableFrom(VideosViewModel.class)) {
            return (T) new VideosViewModel(application, fileRepository);
        } else if (modelClass.isAssignableFrom(AudiosViewModel.class)) {
            return (T) new AudiosViewModel(application, fileRepository);
        } else if (modelClass.isAssignableFrom(ExternalStorageViewModel.class)) {
            return (T) new ExternalStorageViewModel(application, fileRepository);
        }
        throw new IllegalArgumentException("Unable to construct ViewModel");
    }
}
