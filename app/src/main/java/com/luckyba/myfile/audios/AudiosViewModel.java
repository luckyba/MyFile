package com.luckyba.myfile.audios;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;

public class AudiosViewModel extends ViewModel {
    private final FileRepository repository;

    public AudiosViewModel(Application application, FileRepository repository) {
        this.repository = repository;
    }

    public ArrayList<MediaFileListModel> getALLAudios(DictionaryModel dir) {
        return repository.getAllMedia(dir);
    }
}
