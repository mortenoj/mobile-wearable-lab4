package com.example.morten.lab4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Users extends AppCompatActivity {

    ArrayList<String> userArray = new ArrayList<>();

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        addListenerOnDataBase();

    }

    private void addListenerOnDataBase() {
        // Read from the database
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> users = dataSnapshot.getChildren();

                for (DataSnapshot user : users) {
                    userArray.add(user.getKey());
                }

                updateListView();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Users.this, MainActivity.class));
    }


    private void updateListView() {
        ListView listView = findViewById(R.id.listView);


        listView.setAdapter(new userAdapter(this, userArray));
     }
}
