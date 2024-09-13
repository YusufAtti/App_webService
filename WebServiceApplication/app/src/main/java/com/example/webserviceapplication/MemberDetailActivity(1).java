package com.example.webserviceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MemberDetailActivity extends AppCompatActivity {

    private TextView memberDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        memberDetailTextView = findViewById(R.id.memberDetailTextView);

        Intent intent = getIntent();
        int id = intent.getIntExtra("ID", -1);
        String name = intent.getStringExtra("Name");
        String surname = intent.getStringExtra("Surname");

        memberDetailTextView.setText("ID: " + id + "\nName: " + name + "\nSurname: " + surname);
    }
}
