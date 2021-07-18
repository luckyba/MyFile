package com.luckyba.myfile.storage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.animations.AVLoadingIndicatorView;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.common.CommonFunctionInterface;
import com.luckyba.myfile.common.ListPathAdapter;
import com.luckyba.myfile.common.viewer.FullImageViewActivity;
import com.luckyba.myfile.common.viewer.TextFileViewActivity;
import com.luckyba.myfile.data.model.StorageFilesModel;
import com.luckyba.myfile.helper.StorageHelper;
import com.luckyba.myfile.utils.Constant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StorageViewManager {
    private View mRootView;
    private String mParam1;
    private StorageListAdapter storageListAdapter;
    private LifecycleOwner lifecycleOwner;
    private RecyclerView recyclerView;
    private LinearLayout noMediaLayout, noMemoryCard;

    private StorageFragment.OnFragmentInteractionListener mListener;
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
    private ArrayList<StorageFilesModel> storageFilesModelArrayList;
    private StorageViewModel storageViewModel;
    private String rootPath;
    private String fileExtension;
    private int selectedFilePosition;
    private final HashMap selectedFileHashMap = new HashMap();
    private boolean isCheckboxVisible = false;
    private boolean isVerticalList = true;
    private int numCol = 1;

    public StorageViewManager(View root, StorageViewModel viewModel
            , StorageListAdapter adapter, ListPathAdapter listPathAdapter, Activity activity, String[] agr
            , LifecycleOwner lifecycleOwner) {
        mRootView = root;
        storageListAdapter = adapter;
        storageViewModel = viewModel;
        this.listPathAdapter = listPathAdapter;
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        mParam1 = agr[0];
        init();
    }

    private void init() {
        storageFilesModelArrayList = new ArrayList<>();
        arrayListFilePaths = new ArrayList<>();
        arrayListFileNames = new ArrayList<>();
        storageListAdapter.setData(storageFilesModelArrayList);
        storageListAdapter.notifyDataSetChanged();
        initView();
        initListener();
        if (mParam1.equals(Constant.INTERNAL_STORAGE)) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            arrayListFilePaths.add(rootPath);
            arrayListFileNames.add(activity.getString(R.string.internal_storage));
            storageViewModel.getAllInternal(rootPath);
        } else {
            if (StorageHelper.isExternalStorageReadable()) {
                rootPath = System.getenv("SECONDARY_STORAGE");
                if (rootPath != null) {
                    arrayListFilePaths.add(rootPath);
                    arrayListFileNames.add(activity.getString(R.string.external_storage));
                    storageViewModel.getAllInternal(rootPath);
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
    }

    private void initView() {
        progressBar = (AVLoadingIndicatorView) mRootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        noMediaLayout = (LinearLayout) mRootView.findViewById(R.id.noMediaLayout);
        noMemoryCard = (LinearLayout) mRootView.findViewById(R.id.noMemoryCard);
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
        recyclerView.setAdapter(storageListAdapter);

    }

    private void initListener() {
        imgDelete.setOnClickListener(view -> {
            footerLayout.setVisibility(View.GONE);
            deleteFile();
        });

        imgMenu.setOnClickListener(view -> showMenu());

        tvMoveCancel.setOnClickListener(view -> {
            selectedFileHashMap.clear();
            isCheckboxVisible = false;
            footerLayout.setVisibility(View.GONE);
            fileMoveLayout.setVisibility(View.GONE);
        });

        tvMoveFile.setOnClickListener(view ->
        {
            progressBar.setVisibility(View.VISIBLE);
            footerLayout.setVisibility(View.GONE);
            fileMoveLayout.setVisibility(View.GONE);
            storageViewModel.move(arrayListFilePaths.get(arrayListFilePaths.size() - 1), selectedFileHashMap);
        });

        tvCopyCancel.setOnClickListener(view -> {
            selectedFileHashMap.clear();
            isCheckboxVisible = false;
            fileCopyLayout.setVisibility(View.GONE);
        });

        tvPasteFile.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            fileCopyLayout.setVisibility(View.GONE);
            storageViewModel.copy(arrayListFilePaths.get(arrayListFilePaths.size() - 1), selectedFileHashMap);
        });

        imgFileCopy.setOnClickListener(view -> {
            footerLayout.setVisibility(View.GONE);
            fileCopyLayout.setVisibility(View.VISIBLE);
            resetCheckBox();
        });

        viewBy.setOnClickListener(v -> viewBy(true));

        storageViewModel.getLoadAllData().observe(lifecycleOwner, this::updateFilesList);
        storageViewModel.getExtractLiveData().observe(lifecycleOwner, this::updateAfterExtract);
        storageViewModel.getMoveLiveData().observe(lifecycleOwner, this::updateAfterMove);
        storageViewModel.getCopyLiveData().observe(lifecycleOwner, this::updateAfterCopyFile);
        storageViewModel.getReNameLiveData().observe(lifecycleOwner, this::updateAfterRename);
        storageViewModel.getCreateFileLiveData().observe(lifecycleOwner, this::updateAfterCreateNewFile);
        storageViewModel.getCreateFolderLiveData().observe(lifecycleOwner, this::updateAfterCreateNewFolder);
        storageViewModel.getDeleteNameLiveData().observe(lifecycleOwner, this::updateAfterDelete);
    }

    private void updateFilesList(ArrayList<StorageFilesModel> data) {
        if (data.isEmpty()) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMediaLayout.setVisibility(View.GONE);
        }
        listPathAdapter.setData(arrayListFileNames);
        listPathAdapter.notifyDataSetChanged();
        storageFilesModelArrayList = data;
        storageListAdapter.setData(storageFilesModelArrayList);
        storageListAdapter.notifyDataSetChanged();
    }

    private void updateAfterExtract(Boolean result) {
        if (result) {
            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.successfully_extracted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.fail_extracted), Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void updateAfterMove (ArrayList<StorageFilesModel> storageFilesModels) {
        if (noMediaLayout.getVisibility() == View.VISIBLE) {
            noMediaLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (!storageFilesModels.isEmpty()) {
            storageFilesModelArrayList.addAll(storageFilesModels);
            selectedFileHashMap.clear();
        } else {
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), activity.getString(R.string.unable_to_process_this_action), Toast.LENGTH_SHORT).show();
        }
        resetCheckBox();
        progressBar.setVisibility(View.GONE);
    }

    private void updateAfterCopyFile (ArrayList<StorageFilesModel> storageFilesModels) {
        //if storageFilesModels large thread to handle it
        if (noMediaLayout.getVisibility() == View.VISIBLE) {
            noMediaLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (!storageFilesModels.isEmpty()) {
            storageFilesModelArrayList.addAll(storageFilesModels);
            selectedFileHashMap.clear();
        } else {
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), activity.getString(R.string.unable_to_process_this_action), Toast.LENGTH_SHORT).show();
        }
        resetCheckBox();
        progressBar.setVisibility(View.GONE);
    }

    private void updateAfterRename (StorageFilesModel storageFilesModel) {
        if (storageFilesModel != null) {
            storageFilesModel.setType(storageFilesModelArrayList.get(selectedFilePosition).getType());
            storageFilesModelArrayList.get(selectedFilePosition);
            storageFilesModelArrayList.remove(selectedFilePosition);
            storageFilesModelArrayList.add(selectedFilePosition, storageFilesModel);
            storageListAdapter.notifyDataSetChanged();
        } else{
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), MyApplication.getInstance().getApplicationContext().getString(R.string.msg_prompt_not_renamed_you_dont_have_permission_to_rename), Toast.LENGTH_SHORT).show();
        }

        resetCheckBox();
    }

    private void updateAfterCreateNewFile (StorageFilesModel storageFilesModel) {
        if (storageFilesModel != null) {
            if (noMediaLayout.getVisibility() == View.VISIBLE) {
                noMediaLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            storageFilesModelArrayList.add(storageFilesModel);
            storageListAdapter.notifyDataSetChanged();
            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_file_created), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_file_not_created_you_dont_have_permission_to_create_or_already_existed), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAfterCreateNewFolder (StorageFilesModel storageFilesModel) {
        if (storageFilesModel != null) {
            if (noMediaLayout.getVisibility() == View.VISIBLE) {
                noMediaLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            storageFilesModelArrayList.add(storageFilesModel);
            storageListAdapter.notifyDataSetChanged();
            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_folder_created), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyApplication.getInstance(), activity.getString(R.string.msg_prompt_folder_not_created_you_dont_have_permission_to_create), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAfterDelete (List<Integer> listDeleted) {
        selectedFileHashMap.clear();
        listDeleted.sort(Comparator.reverseOrder());
        for (Integer pos: listDeleted) {
            storageFilesModelArrayList.remove((int)pos);
        }
        if (storageFilesModelArrayList.isEmpty()) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        resetCheckBox();
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
                    for (int i = 0; i < storageFilesModelArrayList.size(); i++) {
                        StorageFilesModel storageFilesModel = storageFilesModelArrayList.get(i);
                        storageFilesModel.setCheckboxVisible(false);
                    }
                    storageListAdapter.notifyDataSetChanged();
                    isCheckboxVisible = false;
                } else {
                    if (navItemIndex == 3) {
                        if (arrayListFilePaths.size() == 1) {
                            Toast.makeText(MyApplication.getInstance().getApplicationContext(), activity.getString(R.string.please_click_back_again_to_exist), Toast.LENGTH_SHORT).show();
                        }
                        if (arrayListFilePaths.size() != 0) {
                            if (arrayListFilePaths.size() >= 2) {
                                storageFilesModelArrayList.clear();
//                                getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 2));
                                rootPath = arrayListFilePaths.get(arrayListFilePaths.size() - 2);
                                storageViewModel.getAllInternal(rootPath);

                                storageListAdapter.notifyDataSetChanged();
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
            StorageFilesModel storageFilesModel = storageFilesModelArrayList.get(position);
            if (storageFilesModel.isCheckboxVisible()) {//if list item selected
                if (storageFilesModel.isSelected()) {
                    storageFilesModel.setSelected(false);
                    storageFilesModelArrayList.remove(position);
                    storageFilesModelArrayList.add(position, storageFilesModel);
                    storageListAdapter.notifyDataSetChanged();
                    selectedFileHashMap.remove(position);
                } else {
                    selectedFileHashMap.put(position, storageFilesModel.getFilePath());
                    storageFilesModel.setSelected(true);
                    selectedFilePosition = position;
                    storageFilesModelArrayList.remove(position);
                    storageFilesModelArrayList.add(position, storageFilesModel);
                    storageListAdapter.notifyDataSetChanged();
                }
            } else {
                fileExtension = storageFilesModel.getFileName().substring(storageFilesModel.getFileName().lastIndexOf(".") + 1);//file extension (.mp3,.png,.pdf)
                openFile(storageFilesModel.getFileName(), storageFilesModel.getFilePath());
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
            for (int i = 0; i < storageFilesModelArrayList.size(); i++) {
                StorageFilesModel storageFilesModel = storageFilesModelArrayList.get(i);
                storageFilesModel.setCheckboxVisible(true);
                isCheckboxVisible = true;
                if (position == i) {
                    storageFilesModel.setSelected(true);
                    selectedFileHashMap.put(position, storageFilesModel.getFilePath());
                    selectedFilePosition = position;
                }
            }
            storageListAdapter.notifyDataSetChanged();
        }
    }

    public void openFile(String fileName, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {//check if selected item is directory
            if (file.canRead()) {//if directory is readable
                storageFilesModelArrayList.clear();
                arrayListFilePaths.add(filePath);

                rootPath = filePath;
                storageViewModel.getAllInternal(filePath);

                viewBy(false);
                storageListAdapter.notifyDataSetChanged();

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
        btnOkay.setOnClickListener(view -> {
            dialogDeleteFile.dismiss();
            storageViewModel.delete(selectedFileHashMap);
        });
        btnCancel.setOnClickListener(view -> dialogDeleteFile.dismiss());
    }

    public void extractZip(String fileName, String filePath) {
        final Dialog extractZipDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        extractZipDialog.setContentView(R.layout.custom_extract_zip_dialog);
        Button btnOkay = (Button) extractZipDialog.findViewById(R.id.btn_okay);
        Button btnCancel = (Button) extractZipDialog.findViewById(R.id.btn_cancel);
        final TextView tvFileName = (TextView) extractZipDialog.findViewById(R.id.id_file_name);
        tvFileName.setText("Are you sure you want to extract " + fileName);
        btnCancel.setOnClickListener(view -> {
            extractZipDialog.dismiss();
            tvFileName.setText("");
        });
        btnOkay.setOnClickListener(view -> {
            extractZipDialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);
            // need to handle thread here.
            storageViewModel.extract(rootPath, fileName, filePath);
        });

        extractZipDialog.show();

    }

    public void renameFile(String fileName, String filePath, int selectedFilePosition) {
        footerLayout.setVisibility(View.GONE);

        final Dialog dialogRenameFile = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialogRenameFile.setContentView(R.layout.custom_rename_file_dialog);
        dialogRenameFile.show();
        final EditText txtRenameFile = (EditText) dialogRenameFile.findViewById(R.id.txt_file_name);
        Button btnRename = (Button) dialogRenameFile.findViewById(R.id.btn_rename);
        Button btnCancel = (Button) dialogRenameFile.findViewById(R.id.btn_cancel);
        txtRenameFile.setText(fileName);
        btnRename.setOnClickListener(view -> {
            if (txtRenameFile.getText().toString().trim().length() == 0) {
                Toast.makeText(MyApplication.getInstance().getApplicationContext(), "Please enter file name", Toast.LENGTH_SHORT).show();
            }
            storageViewModel.reName(rootPath, fileName, txtRenameFile.getText().toString().trim(), filePath);
            dialogRenameFile.dismiss();
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRenameFile.setText("");
                dialogRenameFile.dismiss();
            }
        });
    }

    public void createNewFile() {
        if (!isCheckboxVisible) {
            final Dialog dialogNewFile = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialogNewFile.setContentView(R.layout.custom_new_file_dialog);
            dialogNewFile.show();
            final EditText txtNewFile = (EditText) dialogNewFile.findViewById(R.id.txt_new_folder);
            Button btnCreate = (Button) dialogNewFile.findViewById(R.id.btn_create);
            Button btnCancel = (Button) dialogNewFile.findViewById(R.id.btn_cancel);
            btnCreate.setOnClickListener(view -> {
                storageViewModel.createFile(rootPath, txtNewFile.getText().toString().trim(), activity.getString(R.string.new_file));
                dialogNewFile.dismiss();
            });
            btnCancel.setOnClickListener(view -> {
                txtNewFile.setText("");
                dialogNewFile.dismiss();
            });
        }
    }

    public void createNewFolder() {
        if (!isCheckboxVisible) {
            final Dialog dialogNewFolder = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialogNewFolder.setContentView(R.layout.custom_new_folder_dialog);
            dialogNewFolder.show();
            final EditText txtNewFolder = (EditText) dialogNewFolder.findViewById(R.id.txt_new_folder);
            Button btnCreate = (Button) dialogNewFolder.findViewById(R.id.btn_create);
            Button btnCancel = (Button) dialogNewFolder.findViewById(R.id.btn_cancel);
            btnCreate.setOnClickListener(view -> {
                storageViewModel.createFolder(rootPath, txtNewFolder.getText().toString().trim(), activity.getString(R.string.new_folder));
                dialogNewFolder.dismiss();
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

        lblFileMove.setOnClickListener(view -> {
            menuDialog.dismiss();
            footerLayout.setVisibility(View.GONE);
            fileMoveLayout.setVisibility(View.VISIBLE);
            resetCheckBox();
        });
        lblRenameFile.setOnClickListener(view -> {
            menuDialog.dismiss();
            StorageFilesModel storageFilesModel = storageFilesModelArrayList.get(selectedFilePosition);
            renameFile(storageFilesModel.getFileName(), storageFilesModel.getFilePath(), selectedFilePosition);
        });
        lblFileDetails.setOnClickListener(view -> {
            menuDialog.dismiss();
            StorageFilesModel storageFilesModel = storageFilesModelArrayList.get(selectedFilePosition);
            showFileDetails(storageFilesModel.getFileName(), storageFilesModel.getFilePath());
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
        btnOkay.setOnClickListener(view -> {
            tvFileName.setText("");
            tvFilePath.setText("");
            lblSize.setText("");
            lblCreateAt.setText("");
            fileDetailsDialog.dismiss();
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
        footerAudioPlayer.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.reset();
            audioPlayerDialog.dismiss();
        });
        toggleBtnPlayPause.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            } else {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
            }
        });
        audioPlayerDialog.setOnKeyListener((arg0, keyCode, event) -> {
            // TODO Auto-generated method stub
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                audioPlayerDialog.dismiss();
            }
            return true;
        });
    }

    private void viewBy(boolean click) {
        GridLayoutManager layoutManager;
        if (isVerticalList && click || !isVerticalList && !click) {
            layoutManager =
                    new GridLayoutManager(activity, 2);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            if (click) {
                storageListAdapter.setViewType(1);
                isVerticalList = false;
            }

        } else {
            layoutManager =
                    new GridLayoutManager(activity, 1);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            if (click) {
                isVerticalList = true;
                storageListAdapter.setViewType(0);
            }

        }

    }

    private void resetCheckBox () {
        for (StorageFilesModel model:storageFilesModelArrayList) {
            model.setCheckboxVisible(false);
        }
        storageListAdapter.notifyDataSetChanged();
        isCheckboxVisible = false;
    }
}
