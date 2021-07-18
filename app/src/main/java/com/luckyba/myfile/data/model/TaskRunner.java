package com.luckyba.myfile.data.model;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.luckyba.myfile.data.reponsitory.FileRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final  Executor executor = Executors.newScheduledThreadPool(8); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());
    @SuppressLint("StaticFieldLeak")
    private  static FileRepository fileRepository;

    public TaskRunner(FileRepository fileRepository) {
        TaskRunner.fileRepository = fileRepository;
    }

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        executor.execute(() -> {
            try {
                final R result = callable.call();
                handler.post(() -> {
                    callback.onComplete(result);
                });
            } catch (Exception e) {
                Log.d("fdsafs", "can't excuse this function ");
                e.printStackTrace();
            }

        });
    }

    public static class LoadData implements Callable<ArrayList<StorageFilesModel>> {
        private final String pathName;

        public LoadData(String pathName) {
            this.pathName = pathName;
        }

        @Override
        public ArrayList<StorageFilesModel> call() {
            return fileRepository.getAllFile(pathName);
        }
    }

    public static class CopyData implements Callable<ArrayList<StorageFilesModel>> {
        private final String pathName;
        private final HashMap selectedMap;

        public CopyData(String pathName, HashMap selectedMap) {
            this.pathName = pathName;
            this.selectedMap = selectedMap;
        }

        @Override
        public ArrayList<StorageFilesModel> call() {
            return fileRepository.copy(pathName, selectedMap);
        }
    }

    public static class MoveData implements Callable<ArrayList<StorageFilesModel>> {
        private final String pathName;
        private final HashMap selectedMap;

        public MoveData(String pathName, HashMap selectedMap) {
            this.pathName = pathName;
            this.selectedMap = selectedMap;
        }

        @Override
        public ArrayList<StorageFilesModel> call() {
            return fileRepository.move(pathName, selectedMap);
        }
    }

    public static class DeleteData implements Callable<List<Integer>> {
        private final HashMap listSelected;

        public DeleteData(HashMap listSelected) {
            this.listSelected = listSelected;
        }

        @Override
        public List<Integer> call() {
            return fileRepository.delete(listSelected);
        }
    }

    public static class ExtractData implements Callable<Boolean> {
        private final String rootPath;
        private final String fileName;
        private final String pathName;

        public ExtractData (String rootPath, String fileName, String pathName) {
            this.rootPath = rootPath;
            this.fileName = fileName;
            this.pathName = pathName;
        }

        @Override
        public Boolean call() {
            return fileRepository.extract(rootPath, fileName, pathName);
        }
    }

    public static class CreateFolderData implements Callable<StorageFilesModel> {
        private final String rootPath;
        private final String folderName;
        private final String defaultName;

        public CreateFolderData(String rootPath, String folderName, String defaultName) {
            this.rootPath = rootPath;
            this.folderName = folderName;
            this.defaultName = defaultName;
        }

        @Override
        public StorageFilesModel call() {
            return fileRepository.createFolder(rootPath, folderName, defaultName);
        }
    }

    public static class RenameData implements Callable<StorageFilesModel> {
        private final String rootPath;
        private final String oldName;
        private final String newName;
        private final String filePath;

        public RenameData(String rootPath, String oldName, String newName, String filePath) {
            this.rootPath = rootPath;
            this.oldName = oldName;
            this.newName = newName;
            this.filePath = filePath;
        }

        @Override
        public StorageFilesModel call() {
            return fileRepository.reName(rootPath, oldName, newName, filePath);
        }
    }

    public static class CreateFileData implements Callable<StorageFilesModel> {
        private final String rootPath;
        private final String fileName;
        private final String defaultName;

        public CreateFileData(String rootPath, String fileName, String defaultName) {
            this.rootPath = rootPath;
            this.fileName = fileName;
            this.defaultName = defaultName;
        }

        @Override
        public StorageFilesModel call() {
            return fileRepository.createFile(rootPath, fileName, defaultName);
        }
    }

    public static class LoadAllMedia implements Callable<ArrayList<MediaFileListModel>> {
        private final DictionaryModel dictionaryModel;

        public LoadAllMedia(DictionaryModel dictionaryModel) {
            this.dictionaryModel = dictionaryModel;
        }

        @Override
        public ArrayList<MediaFileListModel> call() {
            return fileRepository.getAllMedia(dictionaryModel);
        }
    }


}
