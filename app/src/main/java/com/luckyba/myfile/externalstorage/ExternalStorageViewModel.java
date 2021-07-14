package com.luckyba.myfile.externalstorage;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.ExternalStorageFilesModel;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;

public class ExternalStorageViewModel extends ViewModel {
    private Application application;
    private FileRepository repository;


    public ExternalStorageViewModel(Application application, FileRepository repository) {
        this.repository = repository;
        this.application = application;
    }

    public ArrayList<ExternalStorageFilesModel> getAllFileExternal (String filePath) {
        return repository.getAllExternalFile(filePath);
    }
}
