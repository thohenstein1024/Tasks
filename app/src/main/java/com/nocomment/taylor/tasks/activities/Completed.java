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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.adapters.TaskAdapter;
import com.nocomment.taylor.tasks.database.TaskDbHelper;
import com.nocomment.taylor.tasks.models.Task;

import java.util.List;


public class Completed extends ActionBarActivity implements ListView.OnItemClickListener {

    private static final int TASK_DETAILS_CODE = 200;

    private TaskAdapter taskAdapter;
    private TaskDbHelper dbHelper;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_completed);
            drawerLayout.closeDrawers();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        taskAdapter = new TaskAdapter(this);
        dbHelper = new TaskDbHelper(this);

        ListView taskList = (ListView) findViewById(R.id.completed_task_list);
        taskList.setAdapter(taskAdapter);
        taskList.setOnItemClickListener(this);

        IntentFilter intentFilter = new IntentFilter(DrawerFragment.ACTION_CLOSE_DRAWER);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Task> completedTasks = dbHelper.getAllCompletedTasks();
        taskAdapter.swapTasks(completedTasks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_all:
                deleteAllCompleted();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = (Task) parent.getItemAtPosition(position);

        Intent intent = new Intent(this, Details.class);
        intent.putExtra("task", task);
        startActivityForResult(intent, TASK_DETAILS_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteAllCompleted() {
        int deletedRowID = dbHelper.softDeleteAllCompleted();
        String feedback;

        if (deletedRowID > 0) {
            List<Task> completedTasks = dbHelper.getAllCompletedTasks();
            taskAdapter.swapTasks(completedTasks);
            feedback = getResources().getString(R.string.all_completed_tasks_deleted);
        } else {
            feedback = getResources().getString(R.string.no_completed_tasks_to_delete);
        }

        Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
        toast.show();
    }
}
