package com.example.morten.lab4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class SignIn extends AppCompatActivity {

    DatabaseReference mDatabase;
    DataSnapshot snapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        getDbSnapshot();

        setOnClickOnButton();
    }

    private void writeUserToDb(String username) { mDatabase.child(username).setValue(""); }

    private void setOnClickOnButton() {
        Button button = findViewById(R.id.button_a2);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText username = findViewById(R.id.username);
                String nick = username.getText().toString();

                if (nick.isEmpty()) {
                    Toast.makeText(SignIn.this, "Invalid username", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("USER", "" + snapshot.child(nick).exists());
                    Log.e("USER", "" + snapshot.child(nick).getKey());
                    if (!snapshot.child(nick).exists()) {
                        writeUserToDb(nick);

                        // Creates shared preferences
                        createSharedPreferences(nick);

                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignIn.this, "Username already taken!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void createSharedPreferences(String nick) {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",0);

        // Save values to shared preferences
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("username", nick);

        prefEditor.apply();
    }


    private void getDbSnapshot() {

        DatabaseReference users = mDatabase.child("users");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                snapshot = s;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
