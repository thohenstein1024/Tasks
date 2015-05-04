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
public class New extends ActionBarActivity {

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private EditText taskName;
    private Button dateSelect;
    private Button timeSelect;
    private EditText location;
    private EditText notes;

    public static int dpToPixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        calendar = GregorianCalendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, MMMM d, y");
        timeFormat = new SimpleDateFormat("h:mm a");

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        taskName = (EditText) findViewById(R.id.input_task_name);
        dateSelect = (Button) findViewById(R.id.input_date_select);
        timeSelect = (Button) findViewById(R.id.input_time_select);
        location = (EditText) findViewById(R.id.input_location);
        notes = (EditText) findViewById(R.id.input_notes);

        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
        timeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

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
            task.dueDate = calendar.getTimeInMillis();
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

    private void confirmDiscardTask() {
        //if nothing has been entered, exit without prompt
        if (TextUtils.isEmpty(taskName.getText()) &&
                TextUtils.equals(dateSelect.getText(), getResources().getText(R.string.due_date)) &&
                TextUtils.equals(timeSelect.getText(), getResources().getText(R.string.time)) &&
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

    private void selectDate() {
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

    private void selectTime() {
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
