package com.luckyba.myfile.common;

public interface CommonFunctionInterface {
    void createNewFile();
    void createNewFolder();
    void openFile (String fileName, String filePath);
    void deleteFile();
    void extractZip(String fileName, final String filePath);
    void renameFile(String fileName, final String filePath, final int selectedFilePosition);
}
