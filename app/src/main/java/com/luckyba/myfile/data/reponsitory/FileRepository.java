package com.luckyba.myfile.data.reponsitory;

import android.content.Context;
import android.database.Cursor;

import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.data.model.StorageFilesModel;
import com.luckyba.myfile.utils.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileRepository implements RepositoryInterface {

    private Context mContext;

    public FileRepository(Context context) {
        mContext = context;
    }

    @Override
    public ArrayList<MediaFileListModel> getAllMedia(DictionaryModel dir) {
        ArrayList<MediaFileListModel> mediaFileListModels = new ArrayList<>();
        final Cursor mCursor = mContext.getContentResolver().query(
                dir.getUri(),
                dir.getProjection(), dir.getSelectionClause(), dir.getSelectionArgs(),
                dir.getSortOrder());
        if (mCursor != null) {
            if (mCursor.getCount() != 0) {
                if (mCursor.moveToFirst()) {
                    do {
                        MediaFileListModel mediaFileListModel = new MediaFileListModel();
                        mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(dir.getProjection()[0])));
                        mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(dir.getProjection()[1])));

                        try {
                            File file = new File(mCursor.getString(mCursor.getColumnIndexOrThrow(dir.getProjection()[1])));
                            long length = file.length();
                            length = length / 1024;
                            if (length >= 1024) {
                                length = length / 1024;
                                mediaFileListModel.setFileSize(length + " MB");
                            } else {
                                mediaFileListModel.setFileSize(length + " KB");
                            }
                            Date lastModDate = new Date(file.lastModified());
                            mediaFileListModel.setFileCreatedTime(lastModDate.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            mediaFileListModel.setFileSize("unknown");
                        }
                        mediaFileListModels.add(mediaFileListModel);
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
            }

        }
        return mediaFileListModels;
    }

    @Override
    public ArrayList<StorageFilesModel> getAllFile(String filePath) {
        ArrayList<StorageFilesModel> storageFilesModelList = new ArrayList<>();

        try {
            File f = new File(filePath);
            File[] files = f.listFiles();

            if (files != null && files.length != 0) {
                for (File file : files) {
                    StorageFilesModel model = new StorageFilesModel
                            .Builder(file.getName(), file.getPath())
                            .setDir(file.isDirectory() ? true : false)
                            .setCheckBoxVisible(false)
                            .setSelected(false)
                            .setType(checkType(file))
                            .build();
                    storageFilesModelList.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return storageFilesModelList;
    }

    @Override
    public ArrayList<StorageFilesModel> move(String outputPath, HashMap selectedFileHashMap) {
        ArrayList<StorageFilesModel> storageFilesModels = new ArrayList<>();
        try {
            Set set = selectedFileHashMap.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                int i = Integer.parseInt(itr.next().toString());
                File file = new File((String) Objects.requireNonNull(selectedFileHashMap.get(i)));
                copyFileOrDirectory((String) Objects.requireNonNull(selectedFileHashMap.get(i)), outputPath);
                deleteFileOrDirectory((String) Objects.requireNonNull(selectedFileHashMap.get(i)));

                StorageFilesModel model = new StorageFilesModel
                        .Builder(file.getName(), outputPath + "/" + file.getName())
                        .setSelected(false)
                        .setCheckBoxVisible(false)
                        .setDir(new File(outputPath + "/" + file.getName()).isDirectory())
                        .setType(checkType(file))
                        .build();

                storageFilesModels.add(model);
            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        return storageFilesModels;
    }

    @Override
    public ArrayList<StorageFilesModel> copy(String outputPath, HashMap selectedFileHashMap) {
        ArrayList<StorageFilesModel> storageFilesModels = new ArrayList<>();
        try {
            Set set = selectedFileHashMap.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                int i = Integer.parseInt(itr.next().toString());
                File file = new File((String) selectedFileHashMap.get(i));


                copyFileOrDirectory((String) selectedFileHashMap.get(i), outputPath);

                StorageFilesModel model = new StorageFilesModel
                        .Builder(file.getName(), outputPath + "/" + file.getName())
                        .setCheckBoxVisible(false)
                        .setSelected(false)
                        .setDir(new File(outputPath + "/" + file.getName()).isDirectory())
                        .setType(checkType(file))
                        .build();
                storageFilesModels.add(model);

            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        return storageFilesModels;
    }

    public void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    @Override
    public List<Integer> delete(HashMap selectedFileHashMap) {
        List<Integer> listDeleted = new ArrayList<>();
        try {
            Set set = selectedFileHashMap.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                int i = Integer.parseInt(itr.next().toString());
                File file = new File((String) selectedFileHashMap.get(i));
                deleteFileOrDirectory((String) selectedFileHashMap.get(i));
                file.delete();
                listDeleted.add(i);
            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        return listDeleted;
    }

    private void deleteFileOrDirectory(String srcDir) {

        try {
            File deleteFile = new File(srcDir);

            if (deleteFile.isDirectory()) {

                String files[] = deleteFile.list();
                int filesLength = deleteFile.list().length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(deleteFile, files[i]).getPath());
                    deleteFileOrDirectory(src1);

                }
                deleteFile.delete();
            } else {
                deleteFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public StorageFilesModel createFolder(String rootPath, String folderName, String defaultNameFolder) {
        StorageFilesModel model = null;
        if (folderName.length() == 0) {//if user not enter text file name
            folderName = defaultNameFolder;
        }
        try {
            File file = new File(rootPath + "/" + folderName);
            if (!file.exists()) {
                boolean isFolderCreated = file.mkdir();
                if (isFolderCreated) {
                    model = new StorageFilesModel
                            .Builder(folderName, rootPath + "/" + folderName)
                            .setSelected(false)
                            .setCheckBoxVisible(false)
                            .setDir(true)
                            .setType(Constant.FOLDER_TYPE)
                            .build();

                }
            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        return model;
    }

    @Override
    public StorageFilesModel createFile(String rootPath, String fileName, String defaultName) {
        StorageFilesModel model = null;
        if (fileName.length() == 0) {//if file name is empty
            fileName = "NewFile";
        }
        try {
            File file = new File(rootPath + "/" + fileName + ".txt");
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (isCreated) {
                    model = new StorageFilesModel
                            .Builder(fileName + ".txt", file.getPath())
                            .setDir(false)
                            .setSelected(false)
                            .setCheckBoxVisible(false)
                            .setType(Constant.DOCUMENT_TYPE)
                            .build();
                }
            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        return model;
    }

    @Override
    public StorageFilesModel reName(String root, String oldName, String newName, String filePath) {
        StorageFilesModel model = null;

        try {
            File renamedFile = new File(filePath.substring(0, filePath.lastIndexOf('/') + 1) + newName);

            final File oldFile = new File(filePath);//create file with old name
            boolean isRenamed = oldFile.renameTo(renamedFile);
            if (isRenamed) {
                model = new StorageFilesModel
                        .Builder(newName, renamedFile.getPath())
                        .setSelected(false)
                        .setDir(renamedFile.isDirectory())
                        .setCheckBoxVisible(false)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }

    @Override
    public boolean extract(String rootPath, String fileName, String pathName) {

        byte[] buffer = new byte[1024];
        try {
            File folder = new File(rootPath);//create output directory is not exists
            if (!folder.exists()) {
                folder.mkdir();
            }
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(pathName));//get the zip file content
            ZipEntry ze = zis.getNextEntry(); //get the zipped file list entry
            while (ze != null) {
                String unzipFileName = ze.getName();
                File newFile = new File(rootPath + File.separator + unzipFileName);
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            return true;
        } catch (IOException ex) {
            MyApplication.getInstance().trackException(ex);
            ex.printStackTrace();
            return false;
        }
    }

    private int checkType(File file) {
        String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (file.isDirectory()) {//if list item folder the set icon
            return Constant.FOLDER_TYPE;
        } else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {//if list item any image then
            return Constant.IMAGE_TYPE;
        } else if (fileExtension.equals("pdf")) {
            return Constant.PDF_TYPE;
        } else if (fileExtension.equals("mp3")) {
            return Constant.AUDIO_TYPE;
        } else if (fileExtension.equals("txt")) {
            return Constant.TXT_TYPE;
        } else if (fileExtension.equals("zip") || fileExtension.equals("rar")) {
            return Constant.EXTRACT_TYPE;
        } else if (fileExtension.equals("html") || fileExtension.equals("xml")) {
            return Constant.DOCUMENT_TYPE;
        } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv") || fileExtension.equals("avi")) {
            return Constant.VIDEO_TYPE;
        } else if (fileExtension.equals("apk")) {
            return Constant.INSTALL_TYPE;
        } else {
            return Constant.UNKNOW_TYPE;
        }

    }
}
