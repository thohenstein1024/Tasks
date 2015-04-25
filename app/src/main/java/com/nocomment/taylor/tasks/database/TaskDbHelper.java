package com.nocomment.taylor.tasks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nocomment.taylor.tasks.models.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Task.db";

    private static final String TEXT_TYPE = " TEXT ";
    private static final String INT_TYPE = " INTEGER ";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME +
                    " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME + TEXT_TYPE + COMMA_SEP +
                    TaskContract.TaskEntry.COLUMN_NAME_DUE_DATE + TEXT_TYPE + COMMA_SEP +
                    TaskContract.TaskEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    TaskContract.TaskEntry.COLUMN_NAME_LOCATION_REMINDER + INT_TYPE + COMMA_SEP +
                    TaskContract.TaskEntry.COLUMN_NAME_NOTES + TEXT_TYPE + COMMA_SEP +
                    TaskContract.TaskEntry.COLUMN_NAME_COMPLETED + INT_TYPE + " DEFAULT 0 " + COMMA_SEP +
                    TaskContract.TaskEntry.COLUMN_NAME_CLEARED + INT_TYPE + " DEFAULT 0 " + COMMA_SEP +
                    TaskContract.TaskEntry.COLUMN_NAME_DELETED + INT_TYPE + " DEFAULT 0 " +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    private static final String SQL_SELECT_ALL_CURRENT =
            "SELECT * FROM " + TaskContract.TaskEntry.TABLE_NAME +
                    " WHERE " + TaskContract.TaskEntry.COLUMN_NAME_CLEARED + " = 0 AND " +
                    TaskContract.TaskEntry.COLUMN_NAME_DELETED + " = 0";

    private static final String SQL_SELECT_ALL_COMPLETED =
            "SELECT * FROM " + TaskContract.TaskEntry.TABLE_NAME +
                    " WHERE " + TaskContract.TaskEntry.COLUMN_NAME_COMPLETED + " = 1 AND " +
                    TaskContract.TaskEntry.COLUMN_NAME_DELETED + " = 0";

    private static final String SQL_SELECT_ALL_DELETED =
            "SELECT * FROM " + TaskContract.TaskEntry.TABLE_NAME +
                    " WHERE " + TaskContract.TaskEntry.COLUMN_NAME_DELETED + " = 1";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME, task.taskName);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DUE_DATE, task.dueDate);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_LOCATION, task.location);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_NOTES, task.notes);

        return db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
    }

    public int updateTask(Task task, int id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME, task.taskName);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DUE_DATE, task.dueDate);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_LOCATION, task.location);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_NOTES, task.notes);

        return db.update(TaskContract.TaskEntry.TABLE_NAME, values,
                TaskContract.TaskEntry._ID + " = " + id, null);
    }

    public int toggleCompleted(int id, int completed) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_COMPLETED, completed);

        return db.update(TaskContract.TaskEntry.TABLE_NAME, values,
                TaskContract.TaskEntry._ID + " = " + id, null);
    }

    public int setNotCleared(int id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_CLEARED, 0);

        return db.update(TaskContract.TaskEntry.TABLE_NAME, values,
                TaskContract.TaskEntry._ID + " = " + id, null);
    }

    public int clearCompleted() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_CLEARED, 1);

        return db.update(TaskContract.TaskEntry.TABLE_NAME, values,
                TaskContract.TaskEntry.COLUMN_NAME_COMPLETED + " = 1 AND " +
                        TaskContract.TaskEntry.COLUMN_NAME_CLEARED + " = 0 AND " +
                        TaskContract.TaskEntry.COLUMN_NAME_DELETED + " = 0", null);
    }

    public int softDeleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DELETED, 1);

        return db.update(TaskContract.TaskEntry.TABLE_NAME, values,
                TaskContract.TaskEntry._ID + " = " + id, null);
    }

    public int softDeleteAllCompleted() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DELETED, 1);

        return db.update(TaskContract.TaskEntry.TABLE_NAME, values,
                TaskContract.TaskEntry.COLUMN_NAME_COMPLETED + " = 1 AND " +
                        TaskContract.TaskEntry.COLUMN_NAME_DELETED + " = 0", null);
    }

    public int hardDeleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TaskContract.TaskEntry.TABLE_NAME,
                TaskContract.TaskEntry._ID + " = " + id, null);
    }

    public int hardDeleteAllTrash() {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TaskContract.TaskEntry.TABLE_NAME,
                TaskContract.TaskEntry.COLUMN_NAME_DELETED + " = 1", null);
    }

    public List<Task> getAllCurrentTasks() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_CURRENT, null);
        List<Task> list = new ArrayList<Task>();

        while (cursor.moveToNext()) {
            Task task = new Task();

            task.id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
            task.taskName = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME));
            task.dueDate = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DUE_DATE));
            task.location = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_LOCATION));
            task.locationReminder = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_LOCATION_REMINDER)) != 0);
            task.notes = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_NOTES));
            task.completed = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_COMPLETED)) != 0);
            task.cleared = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_CLEARED)) != 0);
            task.deleted = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DELETED)) != 0);

            list.add(task);
        }

        cursor.close();
        return list;
    }

    public List<Task> getAllCompletedTasks() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_COMPLETED, null);
        List<Task> list = new ArrayList<Task>();

        while (cursor.moveToNext()) {
            Task task = new Task();

            task.id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
            task.taskName = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME));
            task.dueDate = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DUE_DATE));
            task.location = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_LOCATION));
            task.locationReminder = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_LOCATION_REMINDER)) != 0);
            task.notes = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_NOTES));
            task.completed = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_COMPLETED)) != 0);
            task.cleared = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_CLEARED)) != 0);
            task.deleted = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DELETED)) != 0);

            list.add(task);
        }

        cursor.close();
        return list;
    }

    public List<Task> getAllDeletedTasks() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_DELETED, null);
        List<Task> list = new ArrayList<Task>();

        while (cursor.moveToNext()) {
            Task task = new Task();

            task.id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
            task.taskName = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME));
            task.dueDate = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DUE_DATE));
            task.location = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_LOCATION));
            task.locationReminder = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_LOCATION_REMINDER)) != 0);
            task.notes = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_NOTES));
            task.completed = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_COMPLETED)) != 0);
            task.cleared = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_CLEARED)) != 0);
            task.deleted = (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DELETED)) != 0);

            list.add(task);
        }

        cursor.close();
        return list;
    }
}
