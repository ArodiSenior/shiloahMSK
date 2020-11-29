package com.techsavanna.shiloahmsk.models;

public class UserModel {
    public String password;
    public String user_id;
    public String username;

    public UserModel(String password, String user_id, String username) {
        this.password = password;
        this.user_id = user_id;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }
}
