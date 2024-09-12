package com.example.webserviceapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private Button membersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        membersButton = findViewById(R.id.members_button); // Yeni buton tanımlaması

        // Giriş Yap butonuna tıklanınca LoginActivity'ye git
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Kayıt Ol butonuna tıklanınca RegisterActivity'ye git
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Kayıtlı Üyeler butonuna tıklanınca MembersActivity'ye git
        membersButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MembersActivity.class);
            startActivity(intent);
        });
    }
}
