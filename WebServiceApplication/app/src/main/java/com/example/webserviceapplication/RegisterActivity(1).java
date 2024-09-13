package com.example.webserviceapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText surnameEditText;
    private Button registerButton;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        surnameEditText = findViewById(R.id.surname);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(view -> {
            String name = usernameEditText.getText().toString();
            String surname = surnameEditText.getText().toString();

            if (!name.isEmpty() && !surname.isEmpty()) {
                // Web servisine kayıt ekleme isteği gönder
                sendRegisterRequest(name, surname);
            } else {
                Toast.makeText(RegisterActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRegisterRequest(String name, String surname) {
        String url = "http://10.6.40.154:8019/service.asmx/VeriEkle";  // Web servisin URL'si

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: " + response);
                        Toast.makeText(RegisterActivity.this, "Kayıt başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.toString());
                Toast.makeText(RegisterActivity.this, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Web servise gönderilecek parametreler
                Map<String, String> params = new HashMap<>();
                params.put("ad", name);
                params.put("soyad", surname);
                return params;
            }
        };

        try {
            // İsteği kuyruğa ekleyelim
            queue.add(stringRequest);
        } catch (Exception e) {
            Log.e(TAG, "Request failed: " + e.getMessage());
            Toast.makeText(this, "İstek gönderilemedi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
