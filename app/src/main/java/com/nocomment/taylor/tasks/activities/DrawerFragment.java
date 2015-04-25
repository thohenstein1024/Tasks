package com.nocomment.taylor.tasks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nocomment.taylor.tasks.R;


public class DrawerFragment extends Fragment implements View.OnClickListener {

    public static final String ACTION_CLOSE_DRAWER = "com.nocomment.taylor.tasks.CLOSE_DRAWER";

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tasks = (TextView) view.findViewById(R.id.menu_item_tasks);
        TextView completed = (TextView) view.findViewById(R.id.menu_item_completed);
        TextView trash = (TextView) view.findViewById(R.id.menu_item_trash);

        tasks.setOnClickListener(this);
        completed.setOnClickListener(this);
        trash.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.menu_item_tasks:  //TODO check which activity is inflating the fragment
                closeDrawer();
                switchActivity(HomeScreen.class);
                break;
            case R.id.menu_item_completed:
                closeDrawer();
                switchActivity(Completed.class);
                break;
            case R.id.menu_item_trash:
                closeDrawer();
                switchActivity(Trash.class);
                break;
        }
    }

    private void closeDrawer() {
        Intent intent = new Intent(ACTION_CLOSE_DRAWER);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void switchActivity(final Class<?> cls) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), cls);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 260);
    }
}
