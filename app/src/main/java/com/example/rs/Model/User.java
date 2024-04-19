package com.example.rs.Model;

import org.json.JSONObject;

public class User {
    private String id;
    private String userName;
    private String email;
    private String phoneNumber;

    public User(String id, String userName, String email, String phoneNumber) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public static User fromJsonObject(JSONObject userObj) {
        if (userObj == null) {
            return new User("", "Unknown", "noemail@example.com", "No Phone Number");
        }

        String id = userObj.optString("_id", "");
        String userName = userObj.optString("userName", "");
        String email = userObj.optString("email", "");
        String phoneNumber = userObj.optString("phoneNumber", "");
        return new User(id, userName, email, phoneNumber);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}