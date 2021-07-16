package com.luckyba.myfile.storage;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.StorageFilesModel;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class StorageViewModel extends ViewModel {
    private FileRepository repository;
    public StorageViewModel(Application application, FileRepository repository) {
        this.repository = repository;
    }

    public ArrayList<StorageFilesModel> getAllInternal (String filePath) {
        return repository.getAllFile(filePath);
    }

    public ArrayList<StorageFilesModel> move (String outPath, HashMap selectedFileHashMap) {
        return repository.move(outPath, selectedFileHashMap);
    }

    public ArrayList<StorageFilesModel> copy (String outPath, HashMap selectedFileHashMap) {
        return repository.copy(outPath, selectedFileHashMap);
    }

    public boolean delete (String root, String fileName, String pathName) {
        return repository.delete(root, fileName, pathName);
    }

    public boolean extract (String root, String fileName, String pathName) {
        return repository.extract(root, fileName, pathName);
    }

    public StorageFilesModel createFolder (String rootPath, String folderName, String defaultNameFolder) {
        return repository.createFolder(rootPath, folderName, defaultNameFolder);
    }

    public StorageFilesModel createFile (String rootPath, String fileName, String defaultName) {
        return repository.createFile(rootPath, fileName, defaultName);
    }

    public StorageFilesModel reName (String root,String oldName, String newName, String pathName) {
        return repository.reName(root, oldName, newName, pathName);
    }
}
