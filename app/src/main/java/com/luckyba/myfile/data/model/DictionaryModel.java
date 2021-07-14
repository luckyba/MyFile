package com.luckyba.myfile.data.model;

import android.net.Uri;

public class DictionaryModel {
    Uri uri;
    String[] projection;
    String selectionClause;
    String[] selectionArgs;
    String sortOrder;

    public DictionaryModel() {}

    public DictionaryModel(Uri uri, String[] projection, String selectionClause, String[] selectionArgs, String sortOrder) {
        this.uri = uri;
        this.projection = projection;
        this.selectionClause = selectionClause;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String[] getProjection() {
        return projection;
    }

    public void setProjection(String[] projection) {
        this.projection = projection;
    }

    public String getSelectionClause() {
        return selectionClause;
    }

    public void setSelectionClause(String selectionClause) {
        this.selectionClause = selectionClause;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
