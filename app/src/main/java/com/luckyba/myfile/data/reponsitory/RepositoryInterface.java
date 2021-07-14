package com.luckyba.myfile.data.reponsitory;

import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.ExternalStorageFilesModel;
import com.luckyba.myfile.data.model.InternalStorageFilesModel;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.util.ArrayList;
import java.util.HashMap;

public interface RepositoryInterface {


    ArrayList<MediaFileListModel> getAllMedia (DictionaryModel dir);

//    ArrayList<ImageFolder> getAllFolderPicture (DictionaryProvider dir);

    ArrayList<ExternalStorageFilesModel> getAllExternalFile (String filePath);

    ArrayList<InternalStorageFilesModel> getAllInternalFile (String filePath);

    ArrayList<InternalStorageFilesModel> move (String outputPath, HashMap selectedFileHashMap);

    ArrayList<InternalStorageFilesModel> copy (String outputPath, HashMap selectedFileHashMap);
}
