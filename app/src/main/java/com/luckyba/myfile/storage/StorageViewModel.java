package com.luckyba.myfile.storage;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.luckyba.myfile.data.model.StorageFilesModel;
import com.luckyba.myfile.data.model.TaskRunner;
import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StorageViewModel extends ViewModel {
    private FileRepository repository;
    private TaskRunner taskRunner;
    private MutableLiveData<ArrayList<StorageFilesModel>> loadAllData;
    private MutableLiveData<ArrayList<StorageFilesModel>> moveLiveData;
    private MutableLiveData<ArrayList<StorageFilesModel>> copyLiveData;
    private MutableLiveData<StorageFilesModel> createFolderLiveData;
    private MutableLiveData<StorageFilesModel> createFileLiveData;
    private MutableLiveData<StorageFilesModel> reNameLiveData;
    private MutableLiveData<List<Integer>> deleteNameLiveData;
    private MutableLiveData<Boolean> extractLiveData;

    public StorageViewModel(Application application, FileRepository repository) {
        this.repository = repository;

        taskRunner = new TaskRunner(repository);
        loadAllData = new MutableLiveData<ArrayList<StorageFilesModel>>();
        moveLiveData = new MutableLiveData<ArrayList<StorageFilesModel>>();
        copyLiveData = new MutableLiveData<ArrayList<StorageFilesModel>>();
        createFolderLiveData = new MutableLiveData<StorageFilesModel>();
        createFileLiveData = new MutableLiveData<StorageFilesModel>();
        reNameLiveData = new MutableLiveData<StorageFilesModel>();
        deleteNameLiveData = new MutableLiveData<List<Integer>>();
        extractLiveData = new MutableLiveData<Boolean>();
    }

    public MutableLiveData<ArrayList<StorageFilesModel>> getLoadAllData() {
        return loadAllData;
    }

    public MutableLiveData<ArrayList<StorageFilesModel>> getMoveLiveData() {
        return moveLiveData;
    }

    public MutableLiveData<ArrayList<StorageFilesModel>> getCopyLiveData() {
        return copyLiveData;
    }

    public MutableLiveData<StorageFilesModel> getCreateFolderLiveData() {
        return createFolderLiveData;
    }

    public MutableLiveData<StorageFilesModel> getCreateFileLiveData() {
        return createFileLiveData;
    }

    public MutableLiveData<StorageFilesModel> getReNameLiveData() {
        return reNameLiveData;
    }

    public MutableLiveData<List<Integer>> getDeleteNameLiveData() {
        return deleteNameLiveData;
    }

    public MutableLiveData<Boolean> getExtractLiveData() {
        return extractLiveData;
    }

    public void getAllInternal(String filePath) {
        taskRunner.executeAsync(new TaskRunner.LoadData(filePath), result -> {
            loadAllData.setValue(result);
        });
    }

    public void move(String outPath, HashMap selectedFileHashMap) {
        taskRunner.executeAsync(new TaskRunner.MoveData(outPath, selectedFileHashMap), result -> {
            moveLiveData.setValue(result);
        });
    }

    public void copy(String outPath, HashMap selectedFileHashMap) {
        taskRunner.executeAsync(new TaskRunner.CopyData(outPath, selectedFileHashMap), result -> {
            moveLiveData.setValue(result);
        });
    }

    public void delete(HashMap selectedPos) {
        taskRunner.executeAsync(new TaskRunner.DeleteData(selectedPos), result -> {
            deleteNameLiveData.setValue(result);
        });
    }

    public void extract(String root, String fileName, String pathName) {
        taskRunner.executeAsync(new TaskRunner.ExtractData(root, fileName, pathName), result -> {
            extractLiveData.setValue(result);
        });
    }

    public void createFolder(String rootPath, String folderName, String defaultNameFolder) {
        taskRunner.executeAsync(new TaskRunner.CreateFolderData(rootPath, folderName, defaultNameFolder), result -> {
            createFolderLiveData.setValue(result);
        });
    }

    public void createFile(String rootPath, String fileName, String defaultName) {
        taskRunner.executeAsync(new TaskRunner.CreateFileData(rootPath, fileName, defaultName), result -> {
            createFileLiveData.setValue(result);
        });
    }

    public void reName(String root, String oldName, String newName, String pathName) {
        taskRunner.executeAsync(new TaskRunner.RenameData(root, oldName, newName, pathName), result -> {
            reNameLiveData.setValue(result);
        });
    }
}
