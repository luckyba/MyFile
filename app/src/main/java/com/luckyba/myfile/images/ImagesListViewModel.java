package com.luckyba.myfile.images;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;

public class ImagesListViewModel extends ViewModel {
    private FileRepository repository;
    public ImagesListViewModel(Application application, FileRepository repository) {
        this.repository = repository;
    }

    public ArrayList<MediaFileListModel> getAllInternal (DictionaryModel dir) {
        return repository.getAllMedia(dir);
    }
}
