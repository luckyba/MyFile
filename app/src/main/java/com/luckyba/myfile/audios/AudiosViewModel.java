package com.luckyba.myfile.audios;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.data.model.TaskRunner;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;

public class AudiosViewModel extends ViewModel {
    private final FileRepository repository;
    private TaskRunner taskRunner;
    private final MutableLiveData<ArrayList<MediaFileListModel>> listMutableLiveData;

    public AudiosViewModel(Application application, FileRepository repository) {
        this.repository = repository;
        taskRunner = new TaskRunner(repository);
        listMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<MediaFileListModel>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void getALLAudios(DictionaryModel dir) {
        taskRunner.executeAsync(new TaskRunner.LoadAllMedia(dir), listMutableLiveData::setValue);
    }
}
