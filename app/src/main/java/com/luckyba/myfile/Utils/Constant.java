package com.luckyba.myfile.Utils;


import android.provider.MediaStore;

import com.luckyba.myfile.data.model.DictionaryModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constant {
    public static final List listAudioType = Collections.unmodifiableList(
            Arrays.asList("mp3", "wav", "ogg"));

    public static final List listVideoType = Collections.unmodifiableList(
            Arrays.asList("mp4", "3gp", "wmv", "avi", "mov", "mp4", "wmv"));

    public static final List listDocsType = Collections.unmodifiableList(
            Arrays.asList("txt", "html", "xml", "pdf", "xlsx", "ppt", "pptx", "odt", "ods", "odp", "rtf", "doc"));

    public static final List listImageType = Collections.unmodifiableList(
            Arrays.asList("png", "jpeg", "jpg"));

    public static final List listExtractType = Collections.unmodifiableList(
            Arrays.asList("zip", "rar"));

    public static final DictionaryModel AudioDir = new DictionaryModel(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            , new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
            "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

    public static final DictionaryModel ImageDir = new DictionaryModel(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            new String[]{MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                    }, null, null,
            "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC");

    public static final DictionaryModel VideoDir = new DictionaryModel(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            new String[]{MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA}, null, null,
            "LOWER(" + MediaStore.Video.Media.TITLE + ") ASC");

}
