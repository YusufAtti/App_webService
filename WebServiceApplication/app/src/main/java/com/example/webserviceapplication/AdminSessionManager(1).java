package com.example.webserviceapplication;

public class AdminSessionManager {

    private static AdminSessionManager instance;
    private boolean isAdminLoggedIn;  // Admin'in oturum durumu
    private int adminId;  // Admin ID'si

    // Singleton sınıfı
    private AdminSessionManager() {
        isAdminLoggedIn = false; // Başlangıçta oturum kapalı
        adminId = -1; // Başlangıçta adminId tanımsız (-1)
    }

    // Singleton instance'ı
    public static synchronized AdminSessionManager getInstance() {
        if (instance == null) {
            instance = new AdminSessionManager();
        }
        return instance;
    }

    // Admin giriş yaptı mı kontrolü
    public boolean isAdminLoggedIn() {
        return isAdminLoggedIn;
    }

    // Admin'in giriş yaptığını işaretle ve adminId'yi ayarla
    public void login(int adminId) {
        this.adminId = adminId;
        isAdminLoggedIn = true;
    }

    // Admin'in çıkış yaptığını işaretle ve adminId'yi sıfırla
    public void logout() {
        isAdminLoggedIn = false;
        adminId = -1;
    }

    // Admin ID'sini almak için
    public int getAdminId() {
        return adminId;
    }
}
