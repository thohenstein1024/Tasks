package com.nocomment.taylor.tasks.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
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

        taskName = (EditText) findViewById(R.id.input_task_name);
        dueDate = (EditText) findViewById(R.id.input_due_date);
        location = (EditText) findViewById(R.id.input_location);
        notes = (EditText) findViewById(R.id.input_notes);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        confirmDiscardTask();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                confirmDiscardTask();
                return true;

            case R.id.action_save_task:
                saveTask();
                return true;

            case R.id.action_discard_task:
                confirmDiscardTask();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveTask() {
        if (!TextUtils.isEmpty(taskName.getText())) {
            TaskDbHelper dbHelper = new TaskDbHelper(this);
            Task task = new Task();

            task.taskName = taskName.getText().toString();
            task.dueDate = dueDate.getText().toString();
            task.location = location.getText().toString();
            task.notes = notes.getText().toString();

            long newRowID = dbHelper.insertTask(task);

            if (newRowID != -1) {
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED);
                String feedback = getResources().getString(R.string.error_saving_task);
                Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_SHORT);
                toast.show();
            }

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

    private void confirmDiscardTask() {
        if (TextUtils.isEmpty(taskName.getText()) &&
                TextUtils.isEmpty(dueDate.getText()) &&
                TextUtils.isEmpty(location.getText()) &&
                TextUtils.isEmpty(notes.getText())) {
            finish();
        } else {
            AppCompatDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.confirm_discard_task_title)
                    .setMessage(R.string.confirm_discard_task)
                    .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
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
}
