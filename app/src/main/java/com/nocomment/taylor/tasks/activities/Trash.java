package com.nocomment.taylor.tasks.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.adapters.TaskAdapter;
import com.nocomment.taylor.tasks.models.Task;
import com.nocomment.taylor.tasks.storage.TaskDbHelper;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public class Trash extends ActionBarActivity implements AdapterView.OnItemLongClickListener, AbsListView.MultiChoiceModeListener {

    private TaskAdapter taskAdapter;
    private TaskDbHelper dbHelper;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @SuppressWarnings("unused")
    private android.support.v7.view.ActionMode actionMode;
    @SuppressWarnings("unused")
    private android.support.v7.view.ActionMode.Callback callback;

    private ArrayList<Integer> selectedTaskIDs = new ArrayList<>();

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            Task task = taskAdapter.getItem(position);

            task.completed = isChecked;
            taskAdapter.toggleCompleted(task.id, task.completed);

            if (!task.completed && task.cleared) {
                dbHelper.setNotCleared(task.id);
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawerLayout.closeDrawers();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        taskAdapter = new TaskAdapter(this);
        dbHelper = new TaskDbHelper(this);

        ListView taskList = (ListView) findViewById(R.id.deleted_task_list);

        taskList.setAdapter(taskAdapter);
        taskList.setOnItemLongClickListener(this);
        taskList.setMultiChoiceModeListener(this);
        taskList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        taskAdapter.setOnCheckedChangedListener(checkedChangeListener);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_trash);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.setDrawerListener(drawerToggle);

        IntentFilter intentFilter = new IntentFilter(DrawerFragment.ACTION_CLOSE_DRAWER);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Task> deletedTasks = dbHelper.getAllDeletedTasks();
        taskAdapter.swapTasks(deletedTasks);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trash, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        switch (id) {
            case R.id.action_empty_trash:
                confirmEmptyTrash();
                return true;

            case R.id.action_restore_all:
                restoreAll();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        actionMode = startSupportActionMode(callback);
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Task task = taskAdapter.getItem(position);

        if (checked) {
            selectedTaskIDs.add(task.id);
        } else {
            selectedTaskIDs.remove(selectedTaskIDs.indexOf(task.id));
        }

        mode.setTitle(selectedTaskIDs.size() + "  Selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.alt_menu_trash, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_selected:
                deleteSelected();
                mode.finish();
                return true;

            case R.id.action_restore_selected:
                restoreSelected();
                mode.finish();
                return true;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectedTaskIDs.clear();
        actionMode = null;
    }

    private void deleteAll() {
        int deletedRowID = dbHelper.hardDeleteAllTrash();
        if (deletedRowID > 0) {
            List<Task> deletedTasks = dbHelper.getAllDeletedTasks();
            taskAdapter.swapTasks(deletedTasks);
        }
    }

    private void restoreAll() {
        int restoredRowID = dbHelper.restoreAllTrash();

        if (restoredRowID > 0) {
            List<Task> deletedTasks = dbHelper.getAllDeletedTasks();
            taskAdapter.swapTasks(deletedTasks);

            String feedback = getResources().getString(R.string.all_tasks_restored);
            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void deleteSelected() {
        String ids = "(" + TextUtils.join(",", selectedTaskIDs) + ")";

        int deletedRowID = dbHelper.hardDeleteSelection(ids);
        String feedback;

        if (deletedRowID > 0) {
            List<Task> deletedTasks = dbHelper.getAllDeletedTasks();
            taskAdapter.swapTasks(deletedTasks);
            feedback = getResources().getString(R.string.selected_tasks_deleted);
        } else {
            feedback = getResources().getString(R.string.error_deleting_selected_tasks);
        }

        Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void restoreSelected() {
        String ids = "(" + TextUtils.join(",", selectedTaskIDs) + ")";

        int restoredRowID = dbHelper.restoreSelection(ids);
        String feedback;

        if (restoredRowID > 0) {
            List<Task> deletedTasks = dbHelper.getAllDeletedTasks();
            taskAdapter.swapTasks(deletedTasks);
            feedback = getResources().getString(R.string.selected_tasks_restored);
        } else {
            feedback = getResources().getString(R.string.error_restoring_selected_tasks);
        }

        Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void confirmEmptyTrash() {
        AppCompatDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirm_empty_trash_title)
                .setMessage(R.string.confirm_empty_trash)
                .setPositiveButton(R.string.empty_trash, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAll();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog = builder.create();
        dialog.show();
    }
}
