package com.example.morten.lab4;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAnalytics mFirebaseAnalytics;

    DatabaseReference mDatabase;
    DatabaseReference userDB;

    String savedUsername = "";

    // Array of strings containing values for the list
    ArrayList<HashMap<String, String>> messageArr= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserPreference();

        setContentView(R.layout.activity_main);

        TextInputLayout text = findViewById(R.id.textInputLayout);
        text.bringToFront();


        if (savedUsername.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
        }

        // Starts service
        if (!isMyServiceRunning()) {
            Intent serviceIntent = new Intent(this, MyService.class);
            this.startService(serviceIntent);
        }

        // Obtain the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("messages");
        userDB = FirebaseDatabase.getInstance().getReference("users");


        addListenerOnDataBase();

        setOnClickListenerOnButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.users:
                startActivity(new Intent(MainActivity.this, Users.class));
                return true;
            case R.id.logout:
                deleteUserFromDb();
                savedUsername = "";
                createSharedPreferences();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOnClickListenerOnButton() {
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.inputMessage);
                if (!editText.getText().toString().isEmpty()) {
                    Message msg = new Message(editText.getText().toString(), savedUsername);
                    writeMessageToDb(msg);
                    editText.setText("");
                }
            }
        });
    }

    private void updateListView(Message msg) {
        ListView listView = findViewById(R.id.listView);

        String message = msg.getMessageText();
        String author = msg.getMessageUser();

        if (!isMuted(msg.author)) {
            HashMap<String, String> map = new HashMap<>();

            map.put("rowid", message);
            map.put("col_1", author);

            messageArr.add(map);


            listView.setAdapter(new messageAdapter(this, savedUsername, messageArr));
        }
    }


    private boolean isMuted(String user) {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);

        // Get values from shared prefs
        return sharedPref.getBoolean("MUTE" + user, false);
    }

    private void getUserPreference() {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);

        // Get values from shared prefs
        savedUsername = sharedPref.getString("username", "");
    }

    private void createSharedPreferences() {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",0);

        // Save values to shared preferences
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("username", savedUsername);

        prefEditor.apply();
    }

    private void writeMessageToDb(Message msg) {
        mDatabase.child(msg.getMessageId()).child("message").setValue(msg);
    }

    private void deleteUserFromDb() {
        userDB.child(savedUsername).removeValue();
    }

    private void addListenerOnDataBase() {
        // Read from the database
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                HashMap<String, String> m = (HashMap<String, String>) map.get("message");

                String msgText = m.get("message");
                String author = m.get("author");

                if (!msgText.isEmpty() && !author.isEmpty()) {
                    updateListView(new Message(msgText, author));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "Failed to read value.", databaseError.toException());
            }
        });
    }

    /**
     * isMyServiceRunning checks if the service is running
     * **/
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
