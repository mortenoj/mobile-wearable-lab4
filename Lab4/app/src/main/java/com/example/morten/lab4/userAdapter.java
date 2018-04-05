package com.example.morten.lab4;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class userAdapter extends BaseAdapter {

    ArrayList<String> users = null;
    private Context ctx;

    public userAdapter(Context c, ArrayList<String> userList) {
        ctx = c; users = userList;
    }

    @Override
    public View getItem(int id) {
        return null;
    }

    @Override
    public long getItemId(int id) {
        return 0;
    }

    @Override
    public int getCount() {
        return users.size();
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {
        // Inflate the layout according to the view type
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.userlistitem, parent, false);

        Switch muted = v.findViewById(R.id.mute);



        final String user = users.get(position);


        TextView uname = v.findViewById(R.id.username);

        uname.setText(user);

        // Get shared prefs
        SharedPreferences sharedPref = ctx.getSharedPreferences("FileName",0);

        if (sharedPref.getBoolean("MUTE" + user, false)) {
            muted.setChecked(true);
        } else {
            muted.setChecked(false);
        }

        muted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Get shared prefs
                SharedPreferences sharedPref = ctx.getSharedPreferences("FileName",0);

                // Save values to shared preferences
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putBoolean("MUTE" + user, isChecked);

                prefEditor.apply();
            }
        });

        return v;
    }

}