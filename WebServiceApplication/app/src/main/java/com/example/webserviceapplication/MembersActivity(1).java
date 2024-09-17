package com.example.webserviceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.ArrayList;

public class MembersActivity extends AppCompatActivity {

    private static final String TAG = "MembersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        ScrollView scrollView = findViewById(R.id.members_scroll_view);
        LinearLayout membersLayout = new LinearLayout(this);
        membersLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(membersLayout);

        fetchMembers(membersLayout);
    }

    private void fetchMembers(LinearLayout membersLayout) {
        String url = "http://ID_portName/service.asmx/TumKayitlar"; // Web servis URL'i

        Log.d(TAG, "fetchMembers: URL'ye istek gönderiliyor: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: Sunucudan gelen yanıt: " + response);

                            // XML yanıtını parse etme
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = factory.newPullParser();
                            parser.setInput(new StringReader(response));

                            ArrayList<Member> members = new ArrayList<>();
                            Member currentMember = null;
                            int eventType = parser.getEventType();

                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                String tagName = parser.getName();
                                switch (eventType) {
                                    case XmlPullParser.START_TAG:
                                        Log.d(TAG, "onResponse: XML start tag bulundu: " + tagName);
                                        if ("Table".equals(tagName)) { // Veritabanı tablosunun XML'deki etiketi
                                            currentMember = new Member();
                                        } else if (currentMember != null) {
                                            if ("ID".equals(tagName)) {
                                                currentMember.setId(Integer.parseInt(parser.nextText()));
                                                Log.d(TAG, "onResponse: ID: " + currentMember.getId());
                                            } else if ("Name".equals(tagName)) {
                                                currentMember.setName(parser.nextText());
                                                Log.d(TAG, "onResponse: Name: " + currentMember.getName());
                                            } else if ("Surname".equals(tagName)) {
                                                currentMember.setSurname(parser.nextText());
                                                Log.d(TAG, "onResponse: Surname: " + currentMember.getSurname());
                                            }
                                        }
                                        break;
                                    case XmlPullParser.END_TAG:
                                        if ("Table".equals(tagName) && currentMember != null) {
                                            members.add(currentMember);
                                            Log.d(TAG, "onResponse: Üye eklendi: " + currentMember.getName() + " " + currentMember.getSurname());
                                        }
                                        break;
                                }
                                eventType = parser.next();
                            }

                            // Üyeleri ekranda göster
                            for (Member member : members) {
                                Button memberButton = new Button(MembersActivity.this);
                                memberButton.setText(member.getName() + " " + member.getSurname().charAt(0) + ".");

                                // Yuvarlak köşeli arka planı butona uyguluyoruz
                                memberButton.setBackground(getResources().getDrawable(R.drawable.rounded_scroll));

                                memberButton.setTextSize(18);  // 18sp olarak ayarladık
                                memberButton.setPadding(16, 16, 16, 16);  // Butonun içindeki yazıya padding ekledik

                                // Butonun LayoutParams'ına margin ekleyerek butonlar arası boşluk bırakıyoruz
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, // Buton genişliği
                                        LinearLayout.LayoutParams.WRAP_CONTENT  // Buton yüksekliği
                                );
                                params.setMargins(16, 16, 16, 16);  // Butonun her tarafına 16dp boşluk ekliyoruz
                                memberButton.setLayoutParams(params);

                                // Butona tıklayınca detayları yeni sayfada göster
                                memberButton.setOnClickListener(view -> {
                                    Intent intent = new Intent(MembersActivity.this, MemberDetailActivity.class);
                                    intent.putExtra("ID", member.getId());
                                    intent.putExtra("Name", member.getName());
                                    intent.putExtra("Surname", member.getSurname());
                                    startActivity(intent);
                                });

                                membersLayout.addView(memberButton);
                                Log.d(TAG, "onResponse: Buton eklendi: " + member.getName() + " " + member.getSurname());
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "XML Parsing error: " + e.getMessage(), e);
                            Toast.makeText(MembersActivity.this, "Veri çözümlemesi hatası!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error: " + error.toString());
                Toast.makeText(MembersActivity.this, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    // Member sınıfı
    class Member {
        private int id;
        private String name;
        private String surname;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
    }
}
