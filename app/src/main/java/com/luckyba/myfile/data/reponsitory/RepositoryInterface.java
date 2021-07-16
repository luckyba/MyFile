package com.luckyba.myfile.data.reponsitory;

import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.data.model.StorageFilesModel;

import java.util.ArrayList;
import java.util.HashMap;

public interface RepositoryInterface {


    ArrayList<MediaFileListModel> getAllMedia (DictionaryModel dir);

//    ArrayList<ImageFolder> getAllFolderPicture (DictionaryProvider dir);

    ArrayList<StorageFilesModel> getAllFile (String filePath);

    ArrayList<StorageFilesModel> move (String outputPath, HashMap selectedFileHashMap);

    ArrayList<StorageFilesModel> copy (String outputPath, HashMap selectedFileHashMap);

    boolean delete (String root, String fileName, String pathName);

    StorageFilesModel createFolder (String root, String folderName, String defaultNameFolder);

    StorageFilesModel createFile (String root, String fileName, String defaultName);

    StorageFilesModel reName (String root, String oldName, String newName, String pathName);

    boolean extract (String root, String fileName, String pathName);
}
