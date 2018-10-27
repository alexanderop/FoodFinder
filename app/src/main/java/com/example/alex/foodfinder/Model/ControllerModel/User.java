package com.example.alex.foodfinder.Model.ControllerModel;

import java.util.List;
import java.util.Map;


public class User {

    private String uid;
    private String username;
    private String email;
    private List<String> groups;

    public User() {
    }

    public User(String uid, String username, String email, List<String> groups) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.groups = groups;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
}
