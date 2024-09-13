package com.example.webserviceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

public class AdminLoginActivity extends AppCompatActivity {

    private EditText adminUsername;
    private EditText adminPassword;
    private Button adminLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminUsername = findViewById(R.id.admin_username);
        adminPassword = findViewById(R.id.admin_password);
        adminLoginButton = findViewById(R.id.admin_login_button);

        adminLoginButton.setOnClickListener(view -> {
            String username = adminUsername.getText().toString();
            String password = adminPassword.getText().toString();

            // Web servis ile oturum açık mı kontrol edelim
            checkAdminSession(username, password);
        });
    }

    // Admin oturumu olup olmadığını kontrol eden fonksiyon
    private void checkAdminSession(String username, String password) {
        String url = "http://10.6.40.154:8019/service.asmx/IsSessionActive"; // Web servis URL'i

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // XML yanıtını parse edelim
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = factory.newPullParser();
                        parser.setInput(new StringReader(response));

                        int eventType = parser.getEventType();
                        String sessionActive = null;

                        // XML'den sessionActive değerini bulalım
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG && parser.getName().equalsIgnoreCase("boolean")) {
                                parser.next();
                                sessionActive = parser.getText(); // sessionActive bilgisini alalım
                                break;
                            }
                            eventType = parser.next();
                        }

                        // sessionActive değeri true mu kontrol edelim
                        if (sessionActive != null && sessionActive.equals("true")) {
                            Toast.makeText(AdminLoginActivity.this, "Zaten bir admin giriş yaptı!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Oturum açık değilse giriş yap
                            sendAdminLoginRequest(username, password);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("SessionCheckError", "Oturum kontrol edilirken hata oluştu: " + e.getMessage());
                        Toast.makeText(AdminLoginActivity.this, "Oturum kontrolünde hata oluştu!", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e("AdminLoginError", "Hata: " + error.toString());
            Toast.makeText(AdminLoginActivity.this, "Sunucuya bağlanılamadı!", Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }


    // Admin girişi yapmak için web servise istek gönderiyoruz
    private void sendAdminLoginRequest(String username, String password) {
        String url = "http://10.6.40.154:8019/service.asmx/AdminLogin"; // Web servis URL'i

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("AdminLoginResponse", response); // Yanıtı logluyoruz

                    try {
                        // XML yanıtını parse edelim
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = factory.newPullParser();
                        parser.setInput(new StringReader(response));

                        int eventType = parser.getEventType();
                        String jsonString = null;

                        // XML'deki string etiketini bulalım
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG && parser.getName().equalsIgnoreCase("string")) {
                                parser.next();
                                jsonString = parser.getText(); // XML'deki JSON stringini alıyoruz
                                break;
                            }
                            eventType = parser.next();
                        }

                        if (jsonString != null) {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                // Giriş yanıtından adminId'yi al
                                int adminId = jsonObject.getInt("adminId");

                                // AdminSessionManager'a adminId'yi göndererek login yap
                                AdminSessionManager.getInstance().login(adminId);

                                Toast.makeText(AdminLoginActivity.this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show();
                                // Giriş başarılıysa, MainActivity'ye yönlendirme yap
                                Intent intent = new Intent(AdminLoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Admin giriş ekranını kapat

                                // Giriş işlemi başarılı olduğunda veritabanı durumu loglanır
                                Log.d("AdminLoginResponse", "Giriş başarılı, veritabanı durumu güncellendi.");
                            } else {
                                Toast.makeText(AdminLoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminLoginActivity.this, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("AdminLoginParseError", "ADMIN111 Veri çözümleme hatası: " + e.getMessage());
                        Toast.makeText(AdminLoginActivity.this, "ADMIN222 Veri çözümleme hatası!", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e("AdminLoginError", "Hata: " + error.toString());
            Toast.makeText(AdminLoginActivity.this, "Sunucuya bağlanılamadı!", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        queue.add(stringRequest);
    }



}