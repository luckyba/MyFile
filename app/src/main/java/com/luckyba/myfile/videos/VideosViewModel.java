package com.luckyba.myfile.videos;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;

public class VideosViewModel extends ViewModel {
    private Application application;
    private FileRepository repository;

    public VideosViewModel( Application application, FileRepository repository) {
        this.repository = repository;
        this.application = application;
    }

    public ArrayList<MediaFileListModel> getAllVideos (DictionaryModel dir) {
        return repository.getAllMedia(dir);
    }
}
