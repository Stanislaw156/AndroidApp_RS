package com.example.rs.UtilsService;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceClass {
    private static final String USER_PREF = "user_rs";
    private SharedPreferences appShared;
    private SharedPreferences.Editor prefsEditor;

    public SharedPreferenceClass(Context context){
        this.appShared = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        this.prefsEditor = appShared.edit();
    }

    //int
    public int getValue_int(String key){
        return appShared.getInt(key, 0);
    }

    public void setValue_int(String key, int value){
        prefsEditor.putInt(key, value).commit();
    }

    //string
    public String getValue_string(String key){
        return appShared.getString(key, "");
    }

    public void setValue_string(String key, String value){
        prefsEditor.putString(key, value).commit();
    }

    //boolean
    public boolean getValue_boolean(String key){
        return appShared.getBoolean(key, false);
    }

    public void setValue_boolean(String key, boolean value){
        prefsEditor.putBoolean(key, value).commit();
    }

    public void clear(){
        prefsEditor.clear().commit();
    }

   //----------------------------------------------------------------------------------------------------------------------------------------

    public void saveToken(String token) {
        setValue_string("authToken", token);
    }

    public String loadToken() {
        return getValue_string("authToken");
    }

    // User Role
    public void saveUserRole(String role) {
        setValue_string("userRole", role);
    }

    public String loadUserRole() {
        return getValue_string("userRole");
    }

    public void saveUserProfile(String userId, String firstName, String lastName, String birthDate, String vehicle, String registrationNumber, String note) {
        setValue_string("userId", userId);
        setValue_string("firstName", firstName);
        setValue_string("lastName", lastName);
        setValue_string("birthDate", birthDate);
        setValue_string("vehicle", vehicle);
        setValue_string("registrationNumber", registrationNumber);
        setValue_string("note", note);
        prefsEditor.apply();
    }

    public UserProfile loadUserProfile() {
        UserProfile profile = new UserProfile(
                getValue_string("userId"),
                getValue_string("firstName"),
                getValue_string("lastName"),
                getValue_string("birthDate"),
                getValue_string("vehicle"),
                getValue_string("registrationNumber"),
                getValue_string("note")
        );
        return profile;
    }

    //Clear user data
    public void clearUserProfile() {
        prefsEditor.clear().apply();
    }

    public static class UserProfile {
        public String userId, firstName, lastName, birthDate, vehicle, registrationNumber, note;

        public UserProfile(String userId, String firstName, String lastName, String birthDate, String vehicle, String registrationNumber, String note) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.vehicle = vehicle;
            this.registrationNumber = registrationNumber;
            this.note = note;
        }
    }

    // Uloženie userId
    public void saveUserId(String userId) {
        setValue_string("userId", userId);
    }

    // Načítanie userId
    public String loadUserId() {
        return getValue_string("userId");
    }

    public void saveDriverId(String id) {
        prefsEditor.putString("driverId", id).commit();
    }

    public String loadDriverId() {
        return appShared.getString("driverId", "");
    }

    public void savePassengerId(String id) {
        prefsEditor.putString("passengerId", id).commit();
    }

    public String loadPassengerId() {
        return appShared.getString("passengerId", "");
    }

    public void setSubmittedBefore(String role, boolean hasSubmitted) {
        setValue_boolean(role + "_submitted", hasSubmitted);
    }

    public boolean hasSubmittedBefore(String role) {
        return getValue_boolean(role + "_submitted");
    }
}
