package com.example.rs.Model;

public class PassengerModel {
    private String firstName, lastName, birthDate, note;

    public PassengerModel(String firstName, String lastName, String birthDate, String note) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.note = note;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
