package com.luckyba.myfile.data.model;


import com.luckyba.myfile.internalstorage.InternalStorageViewModel;

public class InternalStorageFilesModel {
    private String fileName;
    private String filePath;
    private boolean selected;
    private boolean isDir;
    private boolean isCheckboxVisible;
    private int type;


    public InternalStorageFilesModel() {}

    public InternalStorageFilesModel(String fileName, String filePath, boolean isDir, boolean isSelected, boolean isCheckboxVisible, int type) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.selected = isSelected;
        this.isCheckboxVisible=isCheckboxVisible;
        this.isDir = isDir;
        this.type = type;
    }

    public  InternalStorageFilesModel (InternalStorageFilesModel.Builder builder) {
        this.filePath = builder.filePath;
        this.fileName = builder.fileName;
        this.selected = builder.isSelected;
        this.isCheckboxVisible=builder.isCheckboxVisible;
        this.isDir = builder.isDir;
        this.type = builder.type;
    }

    public boolean isCheckboxVisible() {
        return isCheckboxVisible;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isDir() {
        return isDir;
    }

    public int getType() {
        return type;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setCheckboxVisible(boolean checkboxVisible) {
        isCheckboxVisible = checkboxVisible;
    }

    public static class Builder {
        private String fileName;
        private String filePath;
        private boolean isSelected;
        private boolean isDir;
        private boolean isCheckboxVisible;
        private int type;

        public Builder(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public Builder setSelected (boolean selected) {
            this.isSelected = selected;
            return this;
        }

        public Builder setDir (boolean isDir) {
            this.isDir = isDir;
            return this;
        }

        public Builder setCheckBoxVisible (boolean visible) {
            this.isCheckboxVisible = visible;
            return this;
        }

        public Builder setType (int type) {
            this.type = type;
            return  this;
        }

        public InternalStorageFilesModel build() {
            return new InternalStorageFilesModel(this);
        }

    }
}
