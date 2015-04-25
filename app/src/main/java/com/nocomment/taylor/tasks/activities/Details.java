package com.nocomment.taylor.tasks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.database.TaskDbHelper;
import com.nocomment.taylor.tasks.models.Task;


public class Details extends ActionBarActivity {

    private static final int EDIT_TASK_CODE = 300;

    private TextView taskName;
    private TextView dueDate;
    private TextView location;
    private TextView notes;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        task = bundle.getParcelable("task");

        taskName = (TextView) findViewById(R.id.display_details_task_name);
        dueDate = (TextView) findViewById(R.id.display_details_due_date);
        location = (TextView) findViewById(R.id.display_details_location);
        notes = (TextView) findViewById(R.id.display_details_notes);

        populateFields(task);
        setVisibility(taskName, dueDate, location, notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit_task:
                editTask();
                return true;
            case R.id.action_delete_task:
                deleteTask();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_TASK_CODE) {
            if (resultCode == RESULT_OK) {
                task = data.getParcelableExtra("task");
                populateFields(task);
                setVisibility(taskName, dueDate, location, notes);
            }
        }
    }

    private void populateFields(Task task) {
        taskName.setText(task.taskName);
        dueDate.setText(task.dueDate);
        location.setText(task.location);
        notes.setText(task.notes);
    }

    private void setVisibility(TextView taskName, TextView dueDate, TextView location, TextView notes) {
        if (TextUtils.isEmpty(taskName.getText())) {
            taskName.setVisibility(View.GONE);
        }
        else {
            taskName.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(dueDate.getText())) {
            dueDate.setVisibility(View.GONE);
        }
        else {
            dueDate.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(location.getText())) {
            location.setVisibility(View.GONE);
        }
        else {
            location.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(notes.getText())) {
            notes.setVisibility(View.GONE);
        }
        else {
            notes.setVisibility(View.VISIBLE);
        }
    }

    private void editTask() {
        Intent intent = new Intent(this, Edit.class);
        intent.putExtra("task", task);
        startActivityForResult(intent, EDIT_TASK_CODE);
    }

    private void deleteTask() {
        TaskDbHelper dbHelper = new TaskDbHelper(this);

        int deletedRowID = dbHelper.softDeleteTask(task.id);
        String feedback;

        if (deletedRowID == 1) {
            setResult(RESULT_OK);
            feedback = getResources().getString(R.string.task_deleted);
        } else {
            setResult(RESULT_CANCELED);
            feedback = getResources().getString(R.string.error_deleting_task);
        }

        Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }
}
