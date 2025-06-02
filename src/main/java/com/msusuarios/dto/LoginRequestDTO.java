package com.msusuarios.dto;

public class LoginRequestDTO {

    private String username;
    private String password;

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters (si es necesario)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
