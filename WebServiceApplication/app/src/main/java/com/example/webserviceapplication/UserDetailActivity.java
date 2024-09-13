package com.example.webserviceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserDetailActivity extends AppCompatActivity {

    private TextView userDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userDetailsTextView = findViewById(R.id.userDetailsTextView);

        // Intent ile gelen verileri al
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);  // -1, default değer
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");

        // Gelen verileri TextView'e koy
        userDetailsTextView.setText("ID: " + id + "\nKullanıcı Adı: " + name + "\nSoyadı: " + surname);
    }
}
