package com.luckyba.myfile.externalstorage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.animations.AVLoadingIndicatorView;
import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.common.CommonFunctionInterface;
import com.luckyba.myfile.common.viewer.FullImageViewActivity;
import com.luckyba.myfile.common.viewer.TextFileViewActivity;
import com.luckyba.myfile.data.model.ExternalStorageFilesModel;
import com.luckyba.myfile.helper.DividerItemDecoration;
import com.luckyba.myfile.helper.StorageHelper;

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
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExternalStorageViewManager implements CommonFunctionInterface {
    private View mRootView;
    private ExternalStorageViewModel externalStorageViewModel;
    private ExternalStorageListAdapter externalStorageListAdapter;
    private Activity activity;

    private LinearLayout fileCopyLayout, fileMoveLayout;
    private RecyclerView recyclerView;
    private LinearLayout noMediaLayout, noMemoryCard;
    private ArrayList<ExternalStorageFilesModel> externalStorageFilesModelArrayList;
    private String rootPath;
    private String fileExtension;
    private RelativeLayout footerAudioPlayer;
    private MediaPlayer mediaPlayer;
    private RelativeLayout footerLayout;
    private TextView tvFilePath;
    private ArrayList<String> arrayListFilePaths;
    private int selectedFilePosition;
    private final HashMap selectedFileHashMap = new HashMap();
    private boolean isCheckboxVisible = false;
    private AVLoadingIndicatorView progressBar;
    private TextView tvCopyFile, tvCopyCancel, tvMoveFile, tvMoveCancel;
    private ImageView imgDelete, imgFileCopy,  imgMenu;

    public ExternalStorageViewManager(View mRootView, ExternalStorageViewModel externalStorageViewModel
            , ExternalStorageListAdapter externalStorageListAdapter, Activity activity) {
        this.mRootView = mRootView;
        this.externalStorageViewModel = externalStorageViewModel;
        this.externalStorageListAdapter = externalStorageListAdapter;
        this.activity = activity;
        init();
    }

    private void init() {
        
        initView();
        initListener();
    }
    
    private void initView() {
        progressBar = (AVLoadingIndicatorView) mRootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        noMediaLayout = (LinearLayout) mRootView.findViewById(R.id.noMediaLayout);
        noMemoryCard = (LinearLayout) mRootView.findViewById(R.id.noMemoryCard);
        footerLayout = (RelativeLayout) mRootView.findViewById(R.id.id_layout_footer);
        fileCopyLayout = (LinearLayout) mRootView.findViewById(R.id.fileCopyLayout);
        fileMoveLayout = (LinearLayout) mRootView.findViewById(R.id.fileMoveLayout);
        tvMoveFile = (TextView) mRootView.findViewById(R.id.id_move);
        tvMoveCancel = (TextView) mRootView.findViewById(R.id.id_move_cancel);
        tvCopyCancel = (TextView) mRootView.findViewById(R.id.id_copy_cancel);
        tvCopyFile = (TextView) mRootView.findViewById(R.id.id_copy);
        tvFilePath = (TextView) mRootView.findViewById(R.id.id_file_path);
        imgDelete = (ImageView) mRootView.findViewById(R.id.id_delete);
        imgFileCopy = (ImageView) mRootView.findViewById(R.id.id_copy_file);
        imgMenu = (ImageView) mRootView.findViewById(R.id.id_menu);
        arrayListFilePaths = new ArrayList<>();
        externalStorageFilesModelArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getInstance().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MyApplication.getInstance().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(externalStorageListAdapter);
        if (StorageHelper.isExternalStorageReadable()) {
            rootPath = System.getenv("SECONDARY_STORAGE");
            if (rootPath != null) {
                arrayListFilePaths.add(rootPath);
                getFilesList(rootPath);
            } else {
                recyclerView.setVisibility(View.GONE);
                noMediaLayout.setVisibility(View.GONE);
                noMemoryCard.setVisibility(View.VISIBLE);
            }
        } else {
            recyclerView.setVisibility(View.GONE);
            noMediaLayout.setVisibility(View.VISIBLE);
        }

    }
    
    private void initListener () {
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFile();
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

        tvMoveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedFileHashMap.clear();
                isCheckboxVisible = false;
                fileMoveLayout.setVisibility(View.GONE);
            }
        });

        tvMoveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveFile(tvFilePath.getText().toString());
            }
        });
        tvCopyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedFileHashMap.clear();
                isCheckboxVisible = false;
                fileCopyLayout.setVisibility(View.GONE);
            }
        });

        tvCopyFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyFile(tvFilePath.getText().toString());
            }
        });


        imgFileCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                footerLayout.setVisibility(View.GONE);
                fileCopyLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < externalStorageFilesModelArrayList.size(); i++) {
                    ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelArrayList.get(i);
                    externalStorageFilesModel.setCheckboxVisible(false);
                }
                externalStorageListAdapter.notifyDataSetChanged();
                isCheckboxVisible = false;
            }
        });
    }

    private void getFilesList(String filePath) {
        rootPath = filePath;
        tvFilePath.setText(filePath);
        externalStorageFilesModelArrayList = externalStorageViewModel.getAllFileExternal(filePath);
        if (externalStorageFilesModelArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noMediaLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMediaLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void createNewFile() {
        if (noMemoryCard.getVisibility() != View.VISIBLE) {
            if (!isCheckboxVisible) {
                final Dialog dialogNewFile = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                dialogNewFile.setContentView(R.layout.custom_new_file_dialog);
                dialogNewFile.show();
                final EditText txtNewFile = (EditText) dialogNewFile.findViewById(R.id.txt_new_folder);
                Button btnCreate = (Button) dialogNewFile.findViewById(R.id.btn_create);
                Button btnCancel = (Button) dialogNewFile.findViewById(R.id.btn_cancel);
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fileName = txtNewFile.getText().toString().trim();
                        if (fileName.length() == 0) {//if file name is empty
                            fileName = "NewFile";
                        }
                        try {
                            File file = new File(rootPath + "/" + fileName + ".txt");
                            if (file.exists()) {
                                Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.msg_prompt_file_already_exits), Toast.LENGTH_SHORT).show();
                            } else {
                                boolean isCreated = file.createNewFile();
                                if (isCreated) {
                                    ExternalStorageFilesModel model = new ExternalStorageFilesModel(fileName + ".txt", file.getPath(), false, false, false);
                                    externalStorageFilesModelArrayList.add(model);
                                    externalStorageListAdapter.notifyDataSetChanged();
                                    Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.msg_prompt_file_created), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.msg_prompt_file_not_created), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                        }
                        dialogNewFile.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtNewFile.setText("");
                        dialogNewFile.dismiss();
                    }
                });
            }
        }
    }

    @Override
    public void createNewFolder() {
        if (noMemoryCard.getVisibility() != View.VISIBLE) {
            if (!isCheckboxVisible) {
                final Dialog dialogNewFolder = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                dialogNewFolder.setContentView(R.layout.custom_new_folder_dialog);
                dialogNewFolder.show();
                final EditText txtNewFolder = (EditText) dialogNewFolder.findViewById(R.id.txt_new_folder);
                Button btnCreate = (Button) dialogNewFolder.findViewById(R.id.btn_create);
                Button btnCancel = (Button) dialogNewFolder.findViewById(R.id.btn_cancel);
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String folderName = txtNewFolder.getText().toString().trim();
                        if (folderName.length() == 0) {//if user not enter text file name
                            folderName = "NewFolder";
                        }
                        try {
                            File file = new File(rootPath + "/" + folderName);
                            if (file.exists()) {
                                Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.msg_prompt_folder_already_exits), Toast.LENGTH_SHORT).show();
                            } else {
                                boolean isFolderCreated = file.mkdir();
                                if (isFolderCreated) {
                                    ExternalStorageFilesModel model = new ExternalStorageFilesModel(folderName, rootPath + "/" + folderName, true, false, false);
                                    externalStorageFilesModelArrayList.add(model);
                                    externalStorageListAdapter.notifyDataSetChanged();
                                    Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msg_prompt_folder_created), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msg_prompt_folder_not_created_you_dont_have_permission_to_create), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            MyApplication.getInstance().trackException(e);
                            e.printStackTrace();
                        }
                        dialogNewFolder.cancel();
                    }

                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtNewFolder.setText("");
                        dialogNewFolder.dismiss();
                    }
                });
            }
        }

    }

    @Override
    public void deleteFile() {
        final Dialog dialogDeleteFile = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialogDeleteFile.setContentView(R.layout.custom_delete_file_dialog);
        dialogDeleteFile.show();
        Button btnOkay = (Button) dialogDeleteFile.findViewById(R.id.btn_okay);
        Button btnCancel = (Button) dialogDeleteFile.findViewById(R.id.btn_cancel);
        TextView lblDeleteFile = (TextView) dialogDeleteFile.findViewById(R.id.id_lbl_delete_files);
        if (selectedFileHashMap.size() == 1) {
            lblDeleteFile.setText(MyApplication.getInstance().getApplicationContext().getResources().getString(R.string.lbl_delete_single_file));
        } else {
            lblDeleteFile.setText(MyApplication.getInstance().getApplicationContext().getResources().getString(R.string.lbl_delete_multiple_files));
        }
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Set set = selectedFileHashMap.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext()) {
                        int i = Integer.parseInt(itr.next().toString());
                        File deleteFile = new File((String) selectedFileHashMap.get(i));//create file for selected file
                        boolean isDeleteFile = deleteFile.delete();//delete the file from memory
                        if (isDeleteFile) {
                            selectedFileHashMap.remove(i);
                            ExternalStorageFilesModel model = externalStorageFilesModelArrayList.get(i);
                            externalStorageFilesModelArrayList.remove(model);//remove file from listview
                            externalStorageListAdapter.notifyDataSetChanged();//refresh the adapter
                            selectedFileHashMap.remove(selectedFilePosition);
                        }
                    }
                    dialogDeleteFile.dismiss();
                    footerLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    MyApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDeleteFile.dismiss();
            }
        });
    }

    @Override
    public void openFile(String fileName, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {//check if selected item is directory
            if (file.canRead()) {//if directory is readable
                externalStorageFilesModelArrayList.clear();
                arrayListFilePaths.add(filePath);
                getFilesList(filePath);
                externalStorageListAdapter.notifyDataSetChanged();
            } else {//Toast to your not openable type
                Toast.makeText(MyApplication.getInstance().getApplicationContext(), "Folder can't be read!", Toast.LENGTH_SHORT).show();
            }
            //if file is not directory open a application for file type
        } else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {
            Intent imageIntent = new Intent(MyApplication.getInstance().getApplicationContext(), FullImageViewActivity.class);
            imageIntent.putExtra("imagePath", filePath);
            activity.startActivity(imageIntent);
        } else if (fileExtension.equals("mp3")) {
            showAudioPlayer(fileName, filePath);
        } else if (fileExtension.equals("txt") || fileExtension.equals("html") || fileExtension.equals("xml")) {
            Intent txtIntent = new Intent(MyApplication.getInstance().getApplicationContext(), TextFileViewActivity.class);
            txtIntent.putExtra("filePath", filePath);
            txtIntent.putExtra("fileName", fileName);
            activity.startActivity(txtIntent);
        } else if (fileExtension.equals("zip") || fileExtension.equals("rar")) {
            extractZip(fileName, filePath);
        } else if (fileExtension.equals("pdf")) {
            File pdfFile = new File(filePath);
            PackageManager packageManager = activity.getPackageManager();
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            testIntent.setType("application/pdf");
            List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0 && pdfFile.isFile()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity.getApplicationContext(), "There is no app to handle this type of file", Toast.LENGTH_SHORT).show();
            }
        } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv")) {
            Uri fileUri = Uri.fromFile(new File(filePath));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(fileUri, "video/*");
            activity.startActivity(intent);
        }
    }

    private void showMenu() {
        final Dialog menuDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        menuDialog.setContentView(R.layout.custom_menu_dialog);
        TextView lblRenameFile = (TextView) menuDialog.findViewById(R.id.id_rename);
        TextView lblFileDetails = (TextView) menuDialog.findViewById(R.id.id_file_details);
        TextView lblFileMove = (TextView) menuDialog.findViewById(R.id.id_move);
        if (selectedFileHashMap.size() == 1) {
            lblRenameFile.setClickable(true);
            lblRenameFile.setFocusable(true);
            lblFileMove.setClickable(true);
            lblFileMove.setFocusable(true);
            lblFileDetails.setFocusable(true);
            lblFileDetails.setClickable(true);
            lblRenameFile.setTextColor(ContextCompat.getColor(MyApplication.getInstance().getApplicationContext(), R.color.color_text_selected));
            lblFileMove.setTextColor(ContextCompat.getColor(MyApplication.getInstance().getApplicationContext(), R.color.color_text_selected));
            lblFileDetails.setTextColor(ContextCompat.getColor(MyApplication.getInstance().getApplicationContext(), R.color.color_text_selected));
        } else {
            lblRenameFile.setClickable(false);
            lblRenameFile.setFocusable(false);
            lblFileMove.setClickable(false);
            lblFileMove.setFocusable(false);
            lblFileDetails.setFocusable(false);
            lblFileDetails.setClickable(false);
            lblFileDetails.setTextColor(ContextCompat.getColor(MyApplication.getInstance().getApplicationContext(), R.color.color_text_unselected));
            lblRenameFile.setTextColor(ContextCompat.getColor(MyApplication.getInstance().getApplicationContext(), R.color.color_text_unselected));
            lblFileMove.setTextColor(ContextCompat.getColor(MyApplication.getInstance().getApplicationContext(), R.color.color_text_unselected));
        }

        lblFileMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
                footerLayout.setVisibility(View.GONE);
                fileMoveLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < externalStorageFilesModelArrayList.size(); i++) {
                    ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelArrayList.get(i);
                    externalStorageFilesModel.setCheckboxVisible(false);
                }
                externalStorageListAdapter.notifyDataSetChanged();
                isCheckboxVisible = false;
            }
        });
        lblRenameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelArrayList.get(selectedFilePosition);
                renameFile(menuDialog, externalStorageFilesModel.getFileName(), externalStorageFilesModel.getFilePath(), selectedFilePosition);
            }
        });
        lblFileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
                ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelArrayList.get(selectedFilePosition);
                showFileDetails(externalStorageFilesModel.getFileName(), externalStorageFilesModel.getFilePath());
            }
        });
        menuDialog.show();
    }

    @Override
    public void moveFile(String outputPath) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Set set = selectedFileHashMap.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                int i = Integer.parseInt(itr.next().toString());
                File file = new File((String) selectedFileHashMap.get(i));
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
                ExternalStorageFilesModel model = new ExternalStorageFilesModel();
                model.setSelected(false);
                model.setFilePath(outputPath + "/" + file.getName());
                model.setFileName(file.getName());
                if (new File(outputPath + "/" + file.getName()).isDirectory()) {
                    model.setIsDir(true);
                } else {
                    model.setIsDir(false);
                }
                externalStorageFilesModelArrayList.add(model);
            }
            externalStorageListAdapter.notifyDataSetChanged();//refresh the adapter
            selectedFileHashMap.clear();
            footerLayout.setVisibility(View.GONE);
            fileMoveLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "unable to process this action", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void copyFile(String outputPath) {
        progressBar.setVisibility(View.VISIBLE);
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
                ExternalStorageFilesModel model = new ExternalStorageFilesModel();
                model.setSelected(false);
                model.setFilePath(outputPath + "/" + file.getName());
                model.setFileName(file.getName());
                if (new File(outputPath + "/" + file.getName()).isDirectory()) {
                    model.setIsDir(true);
                } else {
                    model.setIsDir(false);
                }
                externalStorageFilesModelArrayList.add(model);
            }
            externalStorageListAdapter.notifyDataSetChanged();//refresh the adapter
            selectedFileHashMap.clear();
            footerLayout.setVisibility(View.GONE);
            fileCopyLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "unable to process this action", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void extractZip(String fileName, final String filePath) {
        final Dialog extractZipDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        extractZipDialog.setContentView(R.layout.custom_extract_zip_dialog);
        Button btnOkay = (Button) extractZipDialog.findViewById(R.id.btn_okay);
        Button btnCancel = (Button) extractZipDialog.findViewById(R.id.btn_cancel);
        final TextView lblFileName = (TextView) extractZipDialog.findViewById(R.id.id_file_name);
        lblFileName.setText("Are you sure you want to extract " + fileName);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractZipDialog.dismiss();
                lblFileName.setText("");
            }
        });
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractZipDialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                byte[] buffer = new byte[1024];
                try {
                    File folder = new File(rootPath);//create output directory is not exists
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    ZipInputStream zis =
                            new ZipInputStream(new FileInputStream(filePath));//get the zip file content
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
                    progressBar.setVisibility(View.GONE);
                } catch (IOException ex) {
                    MyApplication.getInstance().trackException(ex);
                    progressBar.setVisibility(View.GONE);
                    ex.printStackTrace();
                    extractZipDialog.dismiss();
                }
            }
        });

        extractZipDialog.show();

    }

    @Override
    public void renameFile(final Dialog menuDialog, String fileName, final String filePath, final int selectedFilePosition) {
        final Dialog dialogRenameFile = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialogRenameFile.setContentView(R.layout.custom_rename_file_dialog);
        dialogRenameFile.show();
        final EditText txtRenameFile = (EditText) dialogRenameFile.findViewById(R.id.txt_file_name);
        Button btnRename = (Button) dialogRenameFile.findViewById(R.id.btn_rename);
        Button btnCancel = (Button) dialogRenameFile.findViewById(R.id.btn_cancel);
        txtRenameFile.setText(fileName);
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtRenameFile.getText().toString().trim().length() == 0) {
                    Toast.makeText(MyApplication.getInstance().getApplicationContext(), "Please enter file name", Toast.LENGTH_SHORT).show();
                } else {
                    File renamedFile = new File(filePath.substring(0, filePath.lastIndexOf('/') + 1) + txtRenameFile.getText().toString());
                    if (renamedFile.exists()) {
                        Toast.makeText(MyApplication.getInstance().getApplicationContext(), "File already exits,choose another name", Toast.LENGTH_SHORT).show();
                    } else {
                        final File oldFile = new File(filePath);//create file with old name
                        boolean isRenamed = oldFile.renameTo(renamedFile);
                        if (isRenamed) {
                            ExternalStorageFilesModel model = externalStorageFilesModelArrayList.get(selectedFilePosition);
                            model.setFileName(txtRenameFile.getText().toString());
                            model.setFilePath(renamedFile.getPath());
                            if (renamedFile.isDirectory()) {
                                model.setIsDir(true);
                            } else {
                                model.setIsDir(false);
                            }
                            model.setSelected(false);
                            externalStorageFilesModelArrayList.remove(selectedFilePosition);
                            externalStorageFilesModelArrayList.add(selectedFilePosition, model);
                            externalStorageListAdapter.notifyDataSetChanged();
                            dialogRenameFile.dismiss();
                            menuDialog.dismiss();
                            footerLayout.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(MyApplication.getInstance().getApplicationContext(), MyApplication.getInstance().getApplicationContext().getString(R.string.msg_prompt_not_renamed_you_dont_have_permission_to_rename), Toast.LENGTH_SHORT).show();
                            dialogRenameFile.dismiss();
                            menuDialog.dismiss();
                            footerLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRenameFile.setText("");
                dialogRenameFile.dismiss();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showFileDetails(String fileName, String filePath) {
        final Dialog fileDetailsDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        fileDetailsDialog.setContentView(R.layout.custom_file_details_dialog);
        final TextView lblFileName = (TextView) fileDetailsDialog.findViewById(R.id.id_name);
        final TextView lblFilePath = (TextView) fileDetailsDialog.findViewById(R.id.id_path);
        final TextView lblSize = (TextView) fileDetailsDialog.findViewById(R.id.id_size);
        final TextView lblCreateAt = (TextView) fileDetailsDialog.findViewById(R.id.id_create_at);
        lblFileName.setText("Name :" + fileName);
        lblFilePath.setText("Path :" + filePath);
        File file = new File(filePath);
        if (file.isDirectory()) {
            int subFolders = file.list().length;
            lblSize.setText("items :" + subFolders);
        } else {
            long length = file.length();
            length = length / 1024;
            if (length >= 1024) {
                length = length / 1024;
                lblSize.setText("Size :" + length + " MB");
            } else {
                lblSize.setText("Size :" + length + " KB");
            }
        }
        Date lastModDate = new Date(file.lastModified());
        lblCreateAt.setText("Created on :" + lastModDate.toString());
        Button btnOkay = (Button) fileDetailsDialog.findViewById(R.id.btn_okay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lblFileName.setText("");
                lblFilePath.setText("");
                lblSize.setText("");
                lblCreateAt.setText("");
                fileDetailsDialog.dismiss();
            }
        });
        fileDetailsDialog.show();
    }

    private void showAudioPlayer(String fileName, String filePath) {
        final Dialog audioPlayerDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        audioPlayerDialog.setContentView(R.layout.custom_audio_player_dialog);
        footerAudioPlayer = (RelativeLayout) audioPlayerDialog.findViewById(R.id.id_layout_audio_player);
        TextView lblAudioFileName = (TextView) audioPlayerDialog.findViewById(R.id.ic_audio_file_name);
        ToggleButton toggleBtnPlayPause = (ToggleButton) audioPlayerDialog.findViewById(R.id.id_play_pause);
        toggleBtnPlayPause.setChecked(true);
        lblAudioFileName.setText(fileName);
        audioPlayerDialog.show();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        mediaPlayer.start();
        footerAudioPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                audioPlayerDialog.dismiss();
            }
        });
        toggleBtnPlayPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                }
            }
        });
        audioPlayerDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    audioPlayerDialog.dismiss();
                }
                return true;
            }
        });
    }

    public void onBackPressed (int navItemIndex) {
        if (selectedFileHashMap.size() == 0)
            if (footerLayout.getVisibility() != View.GONE) {
                Animation topToBottom = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                        R.anim.top_bottom);
                footerLayout.startAnimation(topToBottom);
                footerLayout.setVisibility(View.GONE);
            } else {
                if (isCheckboxVisible) {
                    for (int i = 0; i < externalStorageFilesModelArrayList.size(); i++) {
                        ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelArrayList.get(i);
                        externalStorageFilesModel.setCheckboxVisible(false);
                    }
                    externalStorageListAdapter.notifyDataSetChanged();
                    isCheckboxVisible = false;
                } else {
                    if (navItemIndex == 1) {
                        if (arrayListFilePaths.size() == 1) {
                            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                        }
                        if (arrayListFilePaths.size() != 0) {
                            if (arrayListFilePaths.size() >= 2) {
                                externalStorageFilesModelArrayList.clear();
                                getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 2));
                                externalStorageListAdapter.notifyDataSetChanged();
                            }
                            arrayListFilePaths.remove(arrayListFilePaths.size() - 1);
                        } else {
                            activity.finish();
                            System.exit(0);
                        }
                    }
                }
            }
    }

    public void onItemClick (View view, int position) {
        ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelArrayList.get(position);
        if (externalStorageFilesModel.isCheckboxVisible()) {//if list item selected
            if (externalStorageFilesModel.isSelected()) {
                externalStorageFilesModel.setSelected(false);
                externalStorageFilesModelArrayList.remove(position);
                externalStorageFilesModelArrayList.add(position, externalStorageFilesModel);
                externalStorageListAdapter.notifyDataSetChanged();
                selectedFileHashMap.remove(position);
            } else {
                selectedFileHashMap.put(position, externalStorageFilesModel.getFilePath());
                externalStorageFilesModel.setSelected(true);
                selectedFilePosition = position;
                externalStorageFilesModelArrayList.remove(position);
                externalStorageFilesModelArrayList.add(position, externalStorageFilesModel);
                externalStorageListAdapter.notifyDataSetChanged();
            }
        } else {
            fileExtension = externalStorageFilesModel.getFileName().substring(externalStorageFilesModel.getFileName().lastIndexOf(".") + 1);//file extension (.mp3,.png,.pdf)
            openFile(externalStorageFilesModel.getFileName(), externalStorageFilesModel.getFilePath());
        }
        if (selectedFileHashMap.isEmpty()) {
            if (footerLayout.getVisibility() != View.GONE) {
                Animation topToBottom = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                        R.anim.top_bottom);
                footerLayout.startAnimation(topToBottom);
                footerLayout.setVisibility(View.GONE);
            }
        } else {
            if (footerLayout.getVisibility() != View.VISIBLE) {
                Animation bottomToTop = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                        R.anim.bottom_top);
                footerLayout.startAnimation(bottomToTop);
                footerLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onItemLongClick (View view, int position) {
        if (footerLayout.getVisibility() != View.VISIBLE) {
            Animation bottomToTop = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                    R.anim.bottom_top);
            footerLayout.startAnimation(bottomToTop);
            footerLayout.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < externalStorageFilesModelArrayList.size(); i++) {
            ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelArrayList.get(i);
            externalStorageFilesModel.setCheckboxVisible(true);
            isCheckboxVisible = true;
            if (position == i) {
                externalStorageFilesModel.setSelected(true);
                selectedFileHashMap.put(position, externalStorageFilesModel.getFilePath());
                selectedFilePosition = position;
            }
        }
        externalStorageListAdapter.notifyDataSetChanged();
    }
}
