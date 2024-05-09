package com.uablis.easyfitness;

public class UsuarioActual {
    private static UsuarioActual instance = null;
    private String userId;
    private String email;

    private UsuarioActual() {}

    public static UsuarioActual getInstance() {
        if (instance == null) {
            instance = new UsuarioActual();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
