package com.luckyba.myfile.internalstorage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.animations.AVLoadingIndicatorView;
import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.common.CommonFunctionInterface;
import com.luckyba.myfile.common.ListPathAdapter;
import com.luckyba.myfile.common.viewer.FullImageViewActivity;
import com.luckyba.myfile.common.viewer.TextFileViewActivity;
import com.luckyba.myfile.data.model.InternalStorageFilesModel;
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
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InternalStorageViewManager implements CommonFunctionInterface {
    private View mRootView;
    private InternalStorageListAdapter internalStorageListAdapter;
    private RecyclerView recyclerView;
    private LinearLayout noMediaLayout;

    private InternalStorageFragment.OnFragmentInteractionListener mListener;
    private RelativeLayout footerAudioPlayer;
    private LinearLayout fileCopyLayout, fileMoveLayout;
    private MediaPlayer mediaPlayer;
    private RelativeLayout footerLayout;
    private TextView imgDelete, imgFileCopy;
    private ImageView imgMenu;
    private AVLoadingIndicatorView progressBar;
    private TextView tvPasteFile, tvCopyCancel, tvMoveFile, tvMoveCancel;
    private View viewBy;
    private Activity activity;
    private RecyclerView lvPathName;

    private ListPathAdapter listPathAdapter;
    private ArrayList<String> arrayListFilePaths;
    private ArrayList<String> arrayListFileNames;
    private ArrayList<InternalStorageFilesModel> internalStorageFilesModelArrayList;
    private InternalStorageViewModel internalStorageViewModel;
    private String rootPath;
    private String fileExtension;
    private int selectedFilePosition;
    private final HashMap selectedFileHashMap = new HashMap();
    private boolean isCheckboxVisible = false;
    private boolean isVerticalList = true;
    private int numCol = 1;

    private boolean isCopy = false;


    public InternalStorageViewManager(View root, InternalStorageViewModel viewModel
            , InternalStorageListAdapter adapter, ListPathAdapter listPathAdapter, Activity activity) {
        mRootView = root;
        internalStorageListAdapter = adapter;
        internalStorageViewModel = viewModel;
        this.listPathAdapter = listPathAdapter;
        this.activity = activity;

        init();
    }

    private void init() {
        internalStorageFilesModelArrayList = new ArrayList<>();
        arrayListFilePaths = new ArrayList<>();
        arrayListFileNames = new ArrayList<>();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        internalStorageListAdapter.setData(internalStorageFilesModelArrayList);

        initView();
        initListener();

        arrayListFilePaths.add(rootPath);
        arrayListFileNames.add(activity.getString(R.string.internal_storage));
        getFilesList(rootPath);

        startWatchingExternalStorage();
    }

    private void initView() {
        progressBar = (AVLoadingIndicatorView) mRootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        noMediaLayout = (LinearLayout) mRootView.findViewById(R.id.noMediaLayout);
        footerLayout = (RelativeLayout) mRootView.findViewById(R.id.id_layout_footer);
        imgDelete = (TextView) mRootView.findViewById(R.id.id_delete);
        imgFileCopy = (TextView) mRootView.findViewById(R.id.id_copy_file);
        fileCopyLayout = (LinearLayout) mRootView.findViewById(R.id.fileCopyLayout);
        fileMoveLayout = (LinearLayout) mRootView.findViewById(R.id.fileMoveLayout);
        imgMenu = (ImageView) mRootView.findViewById(R.id.id_menu);
        tvMoveFile = (TextView) mRootView.findViewById(R.id.id_move);
        tvMoveCancel = (TextView) mRootView.findViewById(R.id.id_move_cancel);
        tvCopyCancel = (TextView) mRootView.findViewById(R.id.id_copy_cancel);
        tvPasteFile = (TextView) mRootView.findViewById(R.id.id_paste);
        viewBy = mRootView.findViewById(R.id.view_by);

        lvPathName = (RecyclerView) mRootView.findViewById(R.id.lv_path_name);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        lvPathName.setLayoutManager(layoutManager);
        lvPathName.setItemAnimator(new DefaultItemAnimator());
        lvPathName.setAdapter(listPathAdapter);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(internalStorageListAdapter);

    }

    private BroadcastReceiver mExternalStorageReceiver;

    void startWatchingExternalStorage() {
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("fdsfsf", "Storage: " + intent.getData());

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        activity.registerReceiver(mExternalStorageReceiver, filter);

    }

    private void initListener() {
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
                moveFile(arrayListFilePaths.get(arrayListFilePaths.size()-1));
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

        tvPasteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyFile(arrayListFilePaths.get(arrayListFilePaths.size()-1));
            }
        });

        imgFileCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                footerLayout.setVisibility(View.GONE);
                fileCopyLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < internalStorageFilesModelArrayList.size(); i++) {
                    InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(i);
                    internalStorageFilesModel.setCheckboxVisible(false);
                }
                internalStorageListAdapter.notifyDataSetChanged();
                isCheckboxVisible = false;
            }
        });

        viewBy.setOnClickListener(v -> viewBy(true));
    }

    private void getFilesList(String filePath) {
        rootPath = filePath;

        internalStorageFilesModelArrayList = internalStorageViewModel.getAllInternal(filePath);
        if (internalStorageFilesModelArrayList.isEmpty()) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMediaLayout.setVisibility(View.GONE);
        }
        listPathAdapter.setData(arrayListFileNames);
        listPathAdapter.notifyDataSetChanged();

        internalStorageListAdapter.setData(internalStorageFilesModelArrayList);
        internalStorageListAdapter.notifyDataSetChanged();
    }

    public void onBackPressed (int navItemIndex, Activity activity) {
        if (selectedFileHashMap.size() == 0)
            if (footerLayout.getVisibility() != View.GONE) {
                Animation topToBottom = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                        R.anim.top_bottom);
                footerLayout.startAnimation(topToBottom);
                footerLayout.setVisibility(View.GONE);
            } else {
                if (isCheckboxVisible) {
                    for (int i = 0; i < internalStorageFilesModelArrayList.size(); i++) {
                        InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(i);
                        internalStorageFilesModel.setCheckboxVisible(false);
                    }
                    internalStorageListAdapter.notifyDataSetChanged();
                    isCheckboxVisible = false;
                } else {
                    if (navItemIndex == 3) {
                        if (arrayListFilePaths.size() == 1) {
                            Toast.makeText(MyApplication.getInstance().getApplicationContext(), activity.getString(R.string.please_click_back_again_to_exist), Toast.LENGTH_SHORT).show();
                        }
                        if (arrayListFilePaths.size() != 0) {
                            if (arrayListFilePaths.size() >= 2) {
                                internalStorageFilesModelArrayList.clear();
                                getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 2));
                                internalStorageListAdapter.notifyDataSetChanged();
                            }
                            arrayListFilePaths.remove(arrayListFilePaths.size() - 1);

                            arrayListFileNames.remove(arrayListFileNames.size() -1);
                            viewBy(false);
                            listPathAdapter.setData(arrayListFileNames);
                            listPathAdapter.notifyDataSetChanged();
                        } else {
                            activity.finish();
                            System.exit(0);
                        }
                    }
                }
            }
    }

    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.path_item_layout) {
            if (position != arrayListFileNames.size() -1) {
                openFile(arrayListFileNames.get(position), arrayListFilePaths.get(position));
                int leng = arrayListFileNames.size();
                for (int i = leng-1; i > position ; i--) {
                    arrayListFileNames.remove(i);
                    arrayListFilePaths.remove(i);
                }
                viewBy(false);
                listPathAdapter.setData(arrayListFileNames);
                listPathAdapter.notifyDataSetChanged();
            }
        } else {
            InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(position);
            if (internalStorageFilesModel.isCheckboxVisible()) {//if list item selected
                if (internalStorageFilesModel.isSelected()) {
                    internalStorageFilesModel.setSelected(false);
                    internalStorageFilesModelArrayList.remove(position);
                    internalStorageFilesModelArrayList.add(position, internalStorageFilesModel);
                    internalStorageListAdapter.notifyDataSetChanged();
                    selectedFileHashMap.remove(position);
                } else {
                    selectedFileHashMap.put(position, internalStorageFilesModel.getFilePath());
                    internalStorageFilesModel.setSelected(true);
                    selectedFilePosition = position;
                    internalStorageFilesModelArrayList.remove(position);
                    internalStorageFilesModelArrayList.add(position, internalStorageFilesModel);
                    internalStorageListAdapter.notifyDataSetChanged();
                }
            } else {
                fileExtension = internalStorageFilesModel.getFileName().substring(internalStorageFilesModel.getFileName().lastIndexOf(".") + 1);//file extension (.mp3,.png,.pdf)
                openFile(internalStorageFilesModel.getFileName(), internalStorageFilesModel.getFilePath());
            }
            if (selectedFileHashMap.isEmpty()) {
                if (footerLayout.getVisibility() != View.GONE) {
                    Animation topToBottom = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                            R.anim.top_bottom);
                    footerLayout.startAnimation(topToBottom);
                    footerLayout.setVisibility(View.GONE);
                }
            } else {
                if (footerLayout.getVisibility() != View.VISIBLE
                        && fileCopyLayout.getVisibility() == View.GONE
                        && fileMoveLayout.getVisibility() == View.GONE) {
                    Animation bottomToTop = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                            R.anim.bottom_top);
                    footerLayout.startAnimation(bottomToTop);
                    footerLayout.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public void onItemLongClick(View view, int position) {
        if (view.getId() != R.id.path_item_layout) {
            if (footerLayout.getVisibility() != View.VISIBLE) {
                Animation bottomToTop = AnimationUtils.loadAnimation(MyApplication.getInstance().getApplicationContext(),
                        R.anim.bottom_top);
                footerLayout.startAnimation(bottomToTop);
                footerLayout.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < internalStorageFilesModelArrayList.size(); i++) {
                InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(i);
                internalStorageFilesModel.setCheckboxVisible(true);
                isCheckboxVisible = true;
                if (position == i) {
                    internalStorageFilesModel.setSelected(true);
                    selectedFileHashMap.put(position, internalStorageFilesModel.getFilePath());
                    selectedFilePosition = position;
                }
            }
            internalStorageListAdapter.notifyDataSetChanged();
        }
    }

    public void setDataChange(InternalStorageFilesModel data) {
        internalStorageFilesModelArrayList.add(data);
        internalStorageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void openFile(String fileName, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {//check if selected item is directory
            if (file.canRead()) {//if directory is readable
                internalStorageFilesModelArrayList.clear();
                arrayListFilePaths.add(filePath);
                getFilesList(filePath);

                viewBy(false);
                internalStorageListAdapter.notifyDataSetChanged();

                arrayListFileNames.add(fileName);
                listPathAdapter.setData(arrayListFileNames);
                listPathAdapter.notifyDataSetChanged();
            } else {//Toast to your not openable type
                Toast.makeText(MyApplication.getInstance().getApplicationContext(), "Folder can't be read!", Toast.LENGTH_SHORT).show();
            }
            //if file is not directory open a application for file type
        } else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {
            Intent imageIntent = new Intent(MyApplication.getInstance(), FullImageViewActivity.class);
            imageIntent.putExtra("imagePath", filePath);
            activity.startActivity(imageIntent);
        } else if (fileExtension.equals("mp3")) {
            showAudioPlayer(fileName, filePath);
        } else if (fileExtension.equals("txt") || fileExtension.equals("html") || fileExtension.equals("xml")) {
            Intent txtIntent = new Intent(MyApplication.getInstance(), TextFileViewActivity.class);
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
                Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.there_is_no_app_to_handle_this_type_of_file), Toast.LENGTH_SHORT).show();
            }
        } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv")) {
            Uri photoURI = FileProvider.getUriForFile(activity, MyApplication.getInstance() + ".provider"
                    , new File(filePath));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(photoURI, "video/*");
            activity.startActivity(intent);
        } else if (fileExtension.equals("apk")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
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
                            InternalStorageFilesModel model = internalStorageFilesModelArrayList.get(i);
                            internalStorageFilesModelArrayList.remove(model);//remove file from listview
                            internalStorageListAdapter.notifyDataSetChanged();//refresh the adapter
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
    public void extractZip(String fileName, String filePath) {
        final Dialog extractZipDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        extractZipDialog.setContentView(R.layout.custom_extract_zip_dialog);
        Button btnOkay = (Button) extractZipDialog.findViewById(R.id.btn_okay);
        Button btnCancel = (Button) extractZipDialog.findViewById(R.id.btn_cancel);
        final TextView tvFileName = (TextView) extractZipDialog.findViewById(R.id.id_file_name);
        tvFileName.setText("Are you sure you want to extract " + fileName);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractZipDialog.dismiss();
                tvFileName.setText("");
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
    public void moveFile(String outputPath) {
        progressBar.setVisibility(View.VISIBLE);
        internalStorageFilesModelArrayList.addAll(internalStorageViewModel.move(outputPath, selectedFileHashMap));
        internalStorageListAdapter.notifyDataSetChanged();//refresh the adapter
        selectedFileHashMap.clear();
        footerLayout.setVisibility(View.GONE);
        fileMoveLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    // only copy file
    @Override
    public void copyFile(String outputPath) {
        progressBar.setVisibility(View.VISIBLE);

        internalStorageFilesModelArrayList.addAll(internalStorageViewModel.copy(outputPath, selectedFileHashMap));

        internalStorageListAdapter.setData(internalStorageFilesModelArrayList);
        internalStorageListAdapter.notifyDataSetChanged();//refresh the adapter

        selectedFileHashMap.clear();
        footerLayout.setVisibility(View.GONE);
        fileCopyLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void renameFile(Dialog menuDialog, String fileName, String filePath, int selectedFilePosition) {
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
                            InternalStorageFilesModel model = new InternalStorageFilesModel
                                    .Builder(txtRenameFile.getText().toString(), renamedFile.getPath())
                                    .setSelected(false)
                                    .setDir(renamedFile.isDirectory())
                                    .setType(internalStorageFilesModelArrayList.get(selectedFilePosition).getType())
                                    .setCheckBoxVisible(false)
                                    .build();
                                    internalStorageFilesModelArrayList.get(selectedFilePosition)
                            ;
                            internalStorageFilesModelArrayList.remove(selectedFilePosition);
                            internalStorageFilesModelArrayList.add(selectedFilePosition, model);
                            internalStorageListAdapter.notifyDataSetChanged();
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

    @Override
    public void createNewFile() {
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
                            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_file_already_exits), Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isCreated = file.createNewFile();
                            if (isCreated) {
                                InternalStorageFilesModel model = new InternalStorageFilesModel
                                        .Builder(fileName + ".txt", file.getPath())
                                        .setDir(false)
                                        .setSelected(false)
                                        .setCheckBoxVisible(false)
                                        .setType(Constant.DOCUMENT_TYPE)
                                        .build();
                                internalStorageFilesModelArrayList.add(model);
                                internalStorageListAdapter.notifyDataSetChanged();
                                Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_file_created), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_file_not_created), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        MyApplication.getInstance().trackException(e);
                        e.printStackTrace();
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

    @Override
    public void createNewFolder() {
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
                            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_folder_already_exits), Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isFolderCreated = file.mkdir();
                            if (isFolderCreated) {
                                InternalStorageFilesModel model = new InternalStorageFilesModel
                                        .Builder(folderName, rootPath + "/" + folderName)
                                        .setSelected(false)
                                        .setCheckBoxVisible(false)
                                        .setDir(true)
                                        .setType(Constant.FOLDER_TYPE)
                                        .build();

                                internalStorageFilesModelArrayList.add(model);
                                internalStorageListAdapter.notifyDataSetChanged();
                                Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_folder_created), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_folder_not_created_you_dont_have_permission_to_create), Toast.LENGTH_SHORT).show();
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
                for (int i = 0; i < internalStorageFilesModelArrayList.size(); i++) {
                    InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(i);
                    internalStorageFilesModel.setCheckboxVisible(false);
                }
                internalStorageListAdapter.notifyDataSetChanged();
                isCheckboxVisible = false;
            }
        });
        lblRenameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(selectedFilePosition);
                renameFile(menuDialog, internalStorageFilesModel.getFileName(), internalStorageFilesModel.getFilePath(), selectedFilePosition);
            }
        });
        lblFileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
                InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(selectedFilePosition);
                showFileDetails(internalStorageFilesModel.getFileName(), internalStorageFilesModel.getFilePath());
            }
        });
        menuDialog.show();
    }


    @SuppressLint("SetTextI18n")
    private void showFileDetails(String fileName, String filePath) {
        final Dialog fileDetailsDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        fileDetailsDialog.setContentView(R.layout.custom_file_details_dialog);
        final TextView tvFileName = (TextView) fileDetailsDialog.findViewById(R.id.id_name);
        final TextView tvFilePath = (TextView) fileDetailsDialog.findViewById(R.id.id_path);
        final TextView lblSize = (TextView) fileDetailsDialog.findViewById(R.id.id_size);
        final TextView lblCreateAt = (TextView) fileDetailsDialog.findViewById(R.id.id_create_at);
        tvFileName.setText("Name :" + fileName);
        tvFilePath.setText("Path :" + filePath);
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
                tvFileName.setText("");
                tvFilePath.setText("");
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
        TextView tvAudioFileName = (TextView) audioPlayerDialog.findViewById(R.id.ic_audio_file_name);
        ToggleButton toggleBtnPlayPause = (ToggleButton) audioPlayerDialog.findViewById(R.id.id_play_pause);
        toggleBtnPlayPause.setChecked(true);
        tvAudioFileName.setText(fileName);
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

    private void viewBy(boolean click) {
        int tmp;
        if (internalStorageFilesModelArrayList.size() % 2 == 0) {
            tmp = internalStorageFilesModelArrayList.size() / 2;
        } else {
            tmp = internalStorageFilesModelArrayList.size() / 2 + 1;
        }
        if (isVerticalList && click || !isVerticalList && !click) {
            numCol = tmp;
            GridLayoutManager layoutManager;
            if (numCol > 0 ){
                layoutManager =
                        new GridLayoutManager(activity, numCol);
            } else {
                layoutManager =
                        new GridLayoutManager(activity, 10);
            }
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            if (click)
                isVerticalList = false;
        } else {
            numCol = 1;
            GridLayoutManager layoutManager =
                    new GridLayoutManager(activity, numCol);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            if (click)
                isVerticalList = true;
        }

    }

}
