package com.luckyba.myfile.audios;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.Utils.Constant;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.io.IOException;
import java.util.ArrayList;

public class AudiosViewManager {
    private View mRootView;
    private AudiosViewModel audiosViewModel;
    private AudiosListAdapter audiosListAdapter;
    private Activity activity;

    private RecyclerView recyclerView;
    private ArrayList<MediaFileListModel> mediaFileListModels;
    private LinearLayout noMediaLayout;
    private MediaPlayer mediaPlayer;

    public AudiosViewManager(View mRootView, AudiosViewModel audiosViewModel, AudiosListAdapter audiosListAdapter, Activity activity) {
        this.mRootView = mRootView;
        this.audiosViewModel = audiosViewModel;
        this.audiosListAdapter = audiosListAdapter;
        this.activity = activity;
        init();
    }

    private void init() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_audios_list);
        noMediaLayout = (LinearLayout) mRootView.findViewById(R.id.noMediaLayout);

        mediaFileListModels = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getInstance().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(audiosListAdapter);

        getMusicList();

    }


    private void showAudioPlayer(String fileName, String filePath) {
        final Dialog audioPlayerDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        audioPlayerDialog.setContentView(R.layout.custom_audio_player_dialog);
        RelativeLayout footerAudioPlayer = (RelativeLayout) audioPlayerDialog.findViewById(R.id.id_layout_audio_player);
        TextView lblAudioFileName = (TextView) audioPlayerDialog.findViewById(R.id.ic_audio_file_name);
        ToggleButton toggleBtnPlayPause = (ToggleButton) audioPlayerDialog.findViewById(R.id.id_play_pause);
        toggleBtnPlayPause.setChecked(true);
        lblAudioFileName.setText(fileName);
        audioPlayerDialog.show();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

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
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    audioPlayerDialog.dismiss();
                }
                return true;
            }
        });

    }

    private void getMusicList() {
        mediaFileListModels = audiosViewModel.getALLAudios(Constant.AudioDir);
        if (mediaFileListModels.isEmpty()) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMediaLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        Log.d("fdsafsaf", mediaFileListModels.toString());
        audiosListAdapter.setData(mediaFileListModels);
        audiosListAdapter.notifyDataSetChanged();
    }

    public void onItemClick(View view, int pos) {
        MyApplication.getInstance().trackEvent(activity.getString(R.string.play_audio), activity.getString(R.string.play_audio), activity.getString(R.string.my_file));
        MediaFileListModel mediaFileListModel = mediaFileListModels.get(pos);
        showAudioPlayer(mediaFileListModel.getFileName(), mediaFileListModel.getFilePath());
    }

    public void onItemLongClick(View view, int pos) {

    }
}
