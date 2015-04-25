package com.nocomment.taylor.tasks.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.adapters.TaskAdapter;
import com.nocomment.taylor.tasks.database.TaskDbHelper;
import com.nocomment.taylor.tasks.models.Task;

import java.util.List;


public class Trash extends ActionBarActivity {

    private TaskAdapter taskAdapter;
    private TaskDbHelper dbHelper;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_trash);
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
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_empty_trash:
                deleteAll();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        int deletedRowID = dbHelper.hardDeleteAllTrash();
        String feedback;

        if (deletedRowID > 0) {
            List<Task> deletedTasks = dbHelper.getAllDeletedTasks();
            taskAdapter.swapTasks(deletedTasks);
            feedback = getResources().getString(R.string.all_tasks_deleted);
        } else {
            feedback = getResources().getString(R.string.no_tasks_to_delete);
        }

        Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void deleteSelected() {
        //TODO create method body
    }

    private void restoreSelected() {
        //TODO create method body
    }
}
