package com.luckyba.myfile.data.reponsitory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.data.model.DictionaryModel;
import com.luckyba.myfile.data.model.ExternalStorageFilesModel;
import com.luckyba.myfile.data.model.InternalStorageFilesModel;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.utils.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public ArrayList<ExternalStorageFilesModel> getAllExternalFile(String filePath) {
        ArrayList<ExternalStorageFilesModel> externalStorageFilesModelArrayList = new ArrayList<>();

        try {
            File f = new File(filePath);
            File[] files = f.listFiles();

            if (files.length != 0) {
                for (File file : files) {
                    ExternalStorageFilesModel model = new ExternalStorageFilesModel();
                    model.setFileName(file.getName());
                    model.setFilePath(file.getPath());
                    model.setCheckboxVisible(false);
                    model.setSelected(false);
                    if (file.isDirectory()) {
                        model.setDir(true);
                    } else {
                        model.setDir(false);
                    }

                    externalStorageFilesModelArrayList.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return externalStorageFilesModelArrayList;
    }

    @Override
    public ArrayList<InternalStorageFilesModel> getAllInternalFile(String filePath) {
        ArrayList<InternalStorageFilesModel> internalStorageFilesModelList = new ArrayList<>();

        try {
            File f = new File(filePath);
            File[] files = f.listFiles();

            if (files != null && files.length != 0) {
                for (File file : files) {
                    InternalStorageFilesModel model = new InternalStorageFilesModel
                            .Builder(file.getName(), file.getPath())
                            .setDir(file.isDirectory() ? true : false)
                            .setCheckBoxVisible(false)
                            .setSelected(false)
                            .setType(checkType(file))
                            .build();
                    internalStorageFilesModelList.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return internalStorageFilesModelList;
    }

    @Override
    public ArrayList<InternalStorageFilesModel> move(String outputPath, HashMap selectedFileHashMap) {
        ArrayList<InternalStorageFilesModel> internalStorageFilesModels = new ArrayList<>();

        try {
            Set set = selectedFileHashMap.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                int i = Integer.parseInt(itr.next().toString());
                File file = new File((String) Objects.requireNonNull(selectedFileHashMap.get(i)));
                InputStream in = null;
                OutputStream out = null;
                try {
                    //create output directory if it doesn't exist
                    File dir = new File(outputPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    in = new FileInputStream((String) selectedFileHashMap.get(i));
                    out = new FileOutputStream(outputPath + "/" + file.getName());
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;
                    // write the output file
                    out.flush();
                    out.close();
                    out = null;
                    // delete the original file
                    new File((String) selectedFileHashMap.get(i)).delete();
                } catch (Exception e) {
                    MyApplication.getInstance().trackException(e);
                    Log.e("tag", e.getMessage());
                }

                InternalStorageFilesModel model = new InternalStorageFilesModel
                        .Builder(outputPath + "/" + file.getName(), file.getName())
                        .setSelected(false)
                        .setCheckBoxVisible(false)
                        .setDir(new File(outputPath + "/" + file.getName()).isDirectory())
                        .setType(checkType(file))
                        .build();

                internalStorageFilesModels.add(model);
            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "unable to process this action", Toast.LENGTH_SHORT).show();
        }

        return internalStorageFilesModels;
    }

    @Override
    public ArrayList<InternalStorageFilesModel> copy(String outputPath, HashMap selectedFileHashMap) {
        ArrayList<InternalStorageFilesModel> internalStorageFilesModels = new ArrayList<>();
        try {
            Set set = selectedFileHashMap.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                int i = Integer.parseInt(itr.next().toString());
                File file = new File((String) selectedFileHashMap.get(i));
                InputStream in = new FileInputStream((String) selectedFileHashMap.get(i));
                OutputStream out = new FileOutputStream(outputPath + "/" + file.getName());
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                InternalStorageFilesModel model = new InternalStorageFilesModel
                        .Builder(file.getName(), outputPath + "/" + file.getName())
                        .setCheckBoxVisible(false)
                        .setSelected(false)
                        .setDir(new File(outputPath + "/" + file.getName()).isDirectory())
                        .setType(checkType(file))
                        .build();
                internalStorageFilesModels.add(model);
            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "unable to process this action" + e, Toast.LENGTH_SHORT).show();
        }

        return null;
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
