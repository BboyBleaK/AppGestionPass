package com.example.appgestionpass.models;

public class Password {

    private String id;
    private String site;
    private String username;
    private String password;

    // Constructor vacío
    public Password() {}

    public Password(String id, String site, String username, String password) {
        this.id = id;
        this.site = site;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
