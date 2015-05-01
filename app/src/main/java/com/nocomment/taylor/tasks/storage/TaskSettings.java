package com.nocomment.taylor.tasks.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class TaskSettings {

    private static final String SORT_SETTINGS = "SORT_SETTINGS";
    private static final String HOME_SORT = "homeScreenSortOrder";
    private static final String COMPLETED_SORT = "completedSortOrder";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public TaskSettings(Context context) {
        this.preferences = context.getSharedPreferences(SORT_SETTINGS, Context.MODE_PRIVATE);
        this.editor = preferences.edit();
    }

    public int getHomeScreenSortOrder() {
        return preferences.getInt(HOME_SORT, 0);
    }

    public void setHomeScreenSortOrder(int sortOrder) {
        editor.putInt(HOME_SORT, sortOrder).apply();
    }

    public int getCompletedSortOrder() {
        return preferences.getInt(COMPLETED_SORT, 0);
    }

    public void setCompletedSortOrder(int sortOrder) {
        editor.putInt(COMPLETED_SORT, sortOrder).apply();
    }
}
