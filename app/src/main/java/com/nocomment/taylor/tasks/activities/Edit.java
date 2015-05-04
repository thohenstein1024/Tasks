package com.nocomment.taylor.tasks.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.models.Task;
import com.nocomment.taylor.tasks.storage.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


@SuppressWarnings("ALL")
public class Edit extends ActionBarActivity {

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private EditText taskName;
    private Button dateSelect;
    private Button timeSelect;
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

        calendar = GregorianCalendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, MMMM d, y");
        timeFormat = new SimpleDateFormat("h:mm a");

        Bundle bundle = getIntent().getExtras();
        task = bundle.getParcelable("task");

        taskName = (EditText) findViewById(R.id.input_edit_task_name);
        dateSelect = (Button) findViewById(R.id.input_edit_date_select);
        timeSelect = (Button) findViewById(R.id.input_edit_time_select);
        location = (EditText) findViewById(R.id.input_edit_location);
        notes = (EditText) findViewById(R.id.input_edit_notes);

        populateFields(task);

        calendar.setTimeInMillis(task.dueDate);

        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDate();
            }
        });
        timeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTime();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        confirmDiscardChanges();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                confirmDiscardChanges();
                return true;

            case R.id.action_save_changes:
                saveChanges();
                return true;

            case R.id.action_discard_changes:
                confirmDiscardChanges();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateFields(Task task) {
        taskName.setText(task.taskName);
        dateSelect.setText(dateFormat.format(task.dueDate));
        timeSelect.setText(timeFormat.format(task.dueDate));
        location.setText(task.location);
        notes.setText(task.notes);
    }

    private void saveChanges() {
        if (!TextUtils.isEmpty(taskName.getText())) {
            TaskDbHelper dbHelper = new TaskDbHelper(this);

            task.taskName = taskName.getText().toString();
            task.dueDate = calendar.getTimeInMillis();
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
            //display the toast beside the name field
            int[] coordinates = {0, 0};
            taskName.getLocationOnScreen(coordinates);
            int taskNameYPos = coordinates[1];

            String feedback = getResources().getString(R.string.task_needs_name);
            Toast toast = Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.START, taskName.getRight() + dpToPixels(this, -225), taskNameYPos - dpToPixels(this, 27));
            toast.show();
            taskName.requestFocus();
        }
    }

    private void confirmDiscardChanges() {
        //if no changes have been made, exit without prompt
        if (TextUtils.equals(task.taskName, taskName.getText()) &&
                TextUtils.equals(dateSelect.getText(), dateFormat.format(task.dueDate)) &&
                TextUtils.equals(timeSelect.getText(), timeFormat.format(task.dueDate)) &&
                TextUtils.equals(task.location, location.getText()) &&
                TextUtils.equals(task.notes, notes.getText())) {
            finish();
        } else {
            AppCompatDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.confirm_discard_changes_title)
                    .setMessage(R.string.confirm_discard_changes)
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

    private void editDate() {
        CalendarDatePickerDialog datePickerDialog = new CalendarDatePickerDialog();

        CalendarDatePickerDialog.OnDateSetListener dateSetListener = new CalendarDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);

                dateSelect.setText(dateFormat.format(calendar.getTimeInMillis()));
            }
        };

        datePickerDialog.setOnDateSetListener(dateSetListener);
        datePickerDialog.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
    }

    private void editTime() {
        RadialTimePickerDialog timePickerDialog = new RadialTimePickerDialog();

        RadialTimePickerDialog.OnTimeSetListener timeSetListener = new RadialTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);

                timeSelect.setText(timeFormat.format(calendar.getTimeInMillis()));
            }
        };

        timePickerDialog.setOnTimeSetListener(timeSetListener);
        timePickerDialog.show(getSupportFragmentManager(), "TIME_PICKER_TAG");
    }
}
