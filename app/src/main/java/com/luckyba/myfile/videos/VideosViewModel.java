package com.luckyba.myfile.videos;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.data.model.TaskRunner;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;

public class VideosViewModel extends ViewModel {
    private Application application;
    private FileRepository repository;
    private TaskRunner taskRunner;
    private MutableLiveData<ArrayList<MediaFileListModel>> listMutableLiveData;
    public VideosViewModel( Application application, FileRepository repository) {
        this.repository = repository;
        this.application = application;
        taskRunner = new TaskRunner(repository);
        listMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<MediaFileListModel>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void getAllVideos (DictionaryModel dir) {
        taskRunner.executeAsync(new TaskRunner.LoadAllMedia(dir), result -> {
            listMutableLiveData.setValue(result);
        });
    }
}
