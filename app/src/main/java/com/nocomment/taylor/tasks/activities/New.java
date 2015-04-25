package com.nocomment.taylor.tasks.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.database.TaskDbHelper;
import com.nocomment.taylor.tasks.models.Task;


public class New extends ActionBarActivity {

    private EditText taskName;
    private EditText dueDate;
    private EditText location;
    private EditText notes;

    public static int dpToPixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskName = (EditText) findViewById(R.id.input_task_name);
        dueDate = (EditText) findViewById(R.id.input_due_date);
        location = (EditText) findViewById(R.id.input_location);
        notes = (EditText) findViewById(R.id.input_notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save_task:
                saveTask();
                return true;
            case R.id.action_discard_task:
                discardTask();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveTask() {
        if (taskName.getText().length() != 0) {
            TaskDbHelper dbHelper = new TaskDbHelper(this);
            Task task = new Task();

            task.taskName = taskName.getText().toString();
            task.dueDate = dueDate.getText().toString();
            task.location = location.getText().toString();
            task.notes = notes.getText().toString();

            long newRowID = dbHelper.insertTask(task);
            String feedback;

            if (newRowID != -1) {
                setResult(RESULT_OK);
                feedback = getResources().getString(R.string.task_saved);
            } else {
                setResult(RESULT_CANCELED);
                feedback = getResources().getString(R.string.error_saving_task);
            }

            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else {
            String feedback = getResources().getString(R.string.task_needs_name);

            int[] coordinates = {0, 0};
            taskName.getLocationOnScreen(coordinates);
            int taskNameYPos = coordinates[1];

            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.START, taskName.getRight() + dpToPixels(this, 13), taskNameYPos - dpToPixels(this, 27));
            toast.show();
            taskName.requestFocus();
        }
    }

    private void discardTask() {
        if (taskName.getText().length() == 0 &&
                dueDate.getText().length() == 0 &&
                location.getText().length() == 0 &&
                notes.getText().length() == 0) {
            String feedback = getResources().getString(R.string.task_discarded);

            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.confirm_discard_task, Toast.LENGTH_LONG);
            toast.show();  //for testing
        }
    }
}
