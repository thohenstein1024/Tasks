package com.nocomment.taylor.tasks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nocomment.taylor.tasks.R;
import com.nocomment.taylor.tasks.database.TaskDbHelper;
import com.nocomment.taylor.tasks.models.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskAdapter extends BaseAdapter {

    private Context context;
    private List<Task> tasks = new ArrayList<Task>();
    private LayoutInflater inflater;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    public TaskAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Task task = tasks.get(position);

        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.list_item_task, parent, false);
        }

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.display_checkbox);
        TextView taskName = (TextView) view.findViewById(R.id.display_list_task_name);
        TextView dueDate = (TextView) view.findViewById(R.id.display_list_due_date);

        if (checkedChangeListener != null) {
            checkBox.setOnCheckedChangeListener(checkedChangeListener);
        }

        checkBox.setTag(position);
        checkBox.setChecked(task.completed);
        taskName.setText(task.taskName);
        dueDate.setText(task.dueDate);

        return view;
    }

    public void setOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }

    public void remove(int position) {
        tasks.remove(position);
        notifyDataSetChanged();
    }

    public void toggleCompleted(int id, boolean isCompleted) {
        TaskDbHelper dbHelper = new TaskDbHelper(context);
        int completed = (isCompleted ? 1 : 0);
        dbHelper.toggleCompleted(id, completed);
    }

    public void swapTasks(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }
}
