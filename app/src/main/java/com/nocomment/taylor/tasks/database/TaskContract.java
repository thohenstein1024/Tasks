package com.nocomment.taylor.tasks.database;

import android.provider.BaseColumns;


public final class TaskContract {

    public TaskContract() {
    }

    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "Task";
        public static final String COLUMN_NAME_TASK_NAME = "task_name";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_LOCATION_REMINDER = "location_reminder";
        public static final String COLUMN_NAME_NOTES = "notes";
        public static final String COLUMN_NAME_COMPLETED = "completed";
        public static final String COLUMN_NAME_CLEARED = "cleared";
        public static final String COLUMN_NAME_DELETED = "deleted";
    }
}
