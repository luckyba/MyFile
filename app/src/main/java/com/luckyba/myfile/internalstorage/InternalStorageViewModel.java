package com.luckyba.myfile.internalstorage;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.InternalStorageFilesModel;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;

public class InternalStorageViewModel extends ViewModel {
    private FileRepository repository;
    public InternalStorageViewModel(Application application, FileRepository repository) {
        this.repository = repository;
    }

    public ArrayList<InternalStorageFilesModel> getAllInternal (String filePath) {
        return repository.getAllInternalFile(filePath);
    }

}
