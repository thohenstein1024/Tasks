package com.nocomment.taylor.tasks.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.models.Task;
import com.nocomment.taylor.tasks.storage.TaskDbHelper;


@SuppressWarnings("deprecation")
public class Edit extends ActionBarActivity {

    private EditText taskName;
    private EditText dueDate;
    private EditText location;
    private EditText notes;

    private Task task;

    public static int dpToPixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle bundle = getIntent().getExtras();
        task = bundle.getParcelable("task");

        taskName = (EditText) findViewById(R.id.input_edit_task_name);
        dueDate = (EditText) findViewById(R.id.input_edit_due_date);
        location = (EditText) findViewById(R.id.input_edit_location);
        notes = (EditText) findViewById(R.id.input_edit_notes);

        populateFields(task);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_save_changes:
                saveChanges();
                return true;

            case R.id.action_discard_changes:
                discardChanges();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateFields(Task task) {
        taskName.setText(task.taskName);
        dueDate.setText(task.dueDate);
        location.setText(task.location);
        notes.setText(task.notes);
    }

    private void saveChanges() {
        if (taskName.getText().length() != 0) {
            TaskDbHelper dbHelper = new TaskDbHelper(this);

            task.taskName = taskName.getText().toString();
            task.dueDate = dueDate.getText().toString();
            task.location = location.getText().toString();
            task.notes = notes.getText().toString();

            int updatedRowID = dbHelper.updateTask(task, task.id);
            Intent intent = new Intent();
            intent.putExtra("task", task);
            String feedback;

            if (updatedRowID == 1) {
                setResult(RESULT_OK, intent);
                feedback = getResources().getString(R.string.changes_saved);
            } else {
                setResult(RESULT_CANCELED);
                feedback = getResources().getString(R.string.error_saving_changes);
            }

            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else {
            int[] coordinates = {0, 0};
            taskName.getLocationOnScreen(coordinates);
            int taskNameYPos = coordinates[1];

            String feedback = getResources().getString(R.string.task_needs_name);
            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.START, taskName.getRight() + dpToPixels(this, 13), taskNameYPos - dpToPixels(this, 27));
            toast.show();
            taskName.requestFocus();
        }
    }

    private void discardChanges() {
        //TODO: confirmation dialogue
        finish();
    }
}
