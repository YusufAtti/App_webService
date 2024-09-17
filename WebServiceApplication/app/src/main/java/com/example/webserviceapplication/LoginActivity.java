package com.example.webserviceapplication;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;
import android.content.Intent;
import java.io.StringReader;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Web servise istek gönder
            sendLoginRequest(username, password);
        });
    }

    private void sendLoginRequest(String username, String password) {
        String url = "http://ID_portName/service.asmx/UserLogin"; // Web servis adresiniz

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LoginResponse", response); // Sunucudan gelen yanıtı loglayalım

                        try {
                            // XML yanıtını JSON string olarak parse edelim
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = factory.newPullParser();
                            parser.setInput(new StringReader(response));

                            int eventType = parser.getEventType();
                            String jsonString = null;

                            // XML içindeki JSON stringini bul
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG && parser.getName().equalsIgnoreCase("string")) {
                                    parser.next();
                                    jsonString = parser.getText();
                                    break;
                                }
                                eventType = parser.next();
                            }

                            if (jsonString != null) {
                                // JSON stringini işleyelim
                                JSONObject jsonObject = new JSONObject(jsonString);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {
                                    // Sunucudan dönen ID, name ve surname bilgilerini al
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("name");
                                    String surname = jsonObject.getString("surname");

                                    // Giriş başarılı, UserDetailActivity'ye ID, name ve surname ile geçiş yap
                                    Intent intent = new Intent(LoginActivity.this, UserDetailActivity.class);
                                    intent.putExtra("id", id);
                                    intent.putExtra("name", name);
                                    intent.putExtra("surname", surname);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Kullanıcı adı veya şifre hatalı!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Hataları daha ayrıntılı loglayalım
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("LoginError", "Hata Kodu: " + networkResponse.statusCode);
                }
                if (error.getMessage() != null) {
                    Log.e("LoginError", "Hata Mesajı: " + error.getMessage());
                }
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    Log.e("LoginError", "Hata Detayı: " + new String(error.networkResponse.data));
                }

                Toast.makeText(LoginActivity.this, "Sunucuya bağlanılamadı! " + error.toString(), Toast.LENGTH_LONG).show();
            }
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
