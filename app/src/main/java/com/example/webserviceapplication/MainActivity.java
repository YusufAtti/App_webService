package com.example.webserviceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private Button membersButton;
    private Button logoutButton;  // Çıkış butonu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Admin girişinden sonra gelen ana içerik sayfası

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        membersButton = findViewById(R.id.members_button);
        logoutButton = findViewById(R.id.logout_button); // Çıkış yap butonu

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

        // Çıkış butonuna tıklanınca
        logoutButton.setOnClickListener(view -> {
            logoutFromServer();
        });
    }

    private void logoutFromServer() {
        String url = "http://ID_portName/service.asmx/AdminLogout"; // Web servis çıkış URL'i

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Yanıtı loglayalım
                        Log.d("AdminLogoutResponse", response);

                        // JSON stringini parse edelim
                        String jsonString = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                        JSONObject jsonObject = new JSONObject(jsonString);

                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            AdminSessionManager.getInstance().logout(); // Oturumu kapat
                            Toast.makeText(MainActivity.this, "Çıkış yapıldı!", Toast.LENGTH_SHORT).show();

                            // Admin giriş ekranına dön
                            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                            startActivity(intent);
                            finish(); // Ana ekranı kapat
                        } else {
                            Toast.makeText(MainActivity.this, "Çıkış yapılamadı, tekrar deneyin!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Veri çözümleme hatası!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Hata durumu için eklenen kod bloğu
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        Log.e("LogoutError", "Hata Kodu: " + networkResponse.statusCode);
                    }
                    if (error.getMessage() != null) {
                        Log.e("LogoutError", "Hata Mesajı: " + error.getMessage());
                    }
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        Log.e("LogoutError", "Hata Detayı: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(MainActivity.this, "Sunucuya bağlanılamadı! " + error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                int adminId = AdminSessionManager.getInstance().getAdminId();
                // Log ile adminId'yi kontrol edelim
                Log.d("AdminLogout", "Gönderilen adminId: " + adminId);
                params.put("adminId", String.valueOf(adminId)); // adminId'yi gönder
                return params;
            }
        };

        queue.add(stringRequest);
    }

}
