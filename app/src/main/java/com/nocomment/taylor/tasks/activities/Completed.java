package com.nocomment.taylor.tasks.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.adapters.TaskAdapter;
import com.nocomment.taylor.tasks.models.Task;
import com.nocomment.taylor.tasks.storage.TaskDbHelper;
import com.nocomment.taylor.tasks.storage.TaskSettings;

import java.util.List;


@SuppressWarnings("ALL")
public class Completed extends ActionBarActivity implements ListView.OnItemClickListener {

    private static final int TASK_DETAILS_CODE = 200;

    private TaskAdapter taskAdapter;
    private TaskDbHelper dbHelper;
    private TaskSettings settings;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            Task task = taskAdapter.getItem(position);

            task.completed = isChecked;
            taskAdapter.toggleCompleted(task.id, task.completed);

            if (!task.completed && task.cleared) {
                dbHelper.setNotCleared(task.id);
                taskAdapter.remove(position);
            } else if (!task.completed) {
                taskAdapter.remove(position);
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
        setContentView(R.layout.activity_completed);

        taskAdapter = new TaskAdapter(this);
        dbHelper = new TaskDbHelper(this);
        settings = new TaskSettings(this);

        ListView taskList = (ListView) findViewById(R.id.completed_task_list);

        taskList.setAdapter(taskAdapter);
        taskList.setOnItemClickListener(this);
        taskAdapter.setOnCheckedChangedListener(checkedChangeListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_completed);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.setDrawerListener(drawerToggle);

        TextView selectedMenuItem = (TextView) findViewById(R.id.side_menu_completed_tasks);
        selectedMenuItem.setBackgroundColor(getResources().getColor(R.color.md_grey_200));

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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed, menu);
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
            case R.id.action_sort_by_name:
                sortByName();
                return true;

            case R.id.action_sort_by_due_date:
                sortByDueDate();
                return true;

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

    private void sortByName() {
        if (settings.getCompletedSortOrder() != 0) {
            settings.setCompletedSortOrder(0);
            List<Task> completedTasks = dbHelper.getAllCompletedTasks();
            taskAdapter.swapTasks(completedTasks);
        }
    }

    private void sortByDueDate() {
        if (settings.getCompletedSortOrder() != 1) {
            settings.setCompletedSortOrder(1);
            List<Task> completedTasks = dbHelper.getAllCompletedTasks();
            taskAdapter.swapTasks(completedTasks);
        }
    }

    private void deleteAllCompleted() {
        int deletedRowID = dbHelper.softDeleteAllCompleted();

        if (deletedRowID > 0) {
            List<Task> completedTasks = dbHelper.getAllCompletedTasks();
            taskAdapter.swapTasks(completedTasks);

            String feedback = getResources().getString(R.string.all_completed_tasks_moved_to_trash);
            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
