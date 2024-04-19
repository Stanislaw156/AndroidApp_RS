package com.example.rs.Model;

public class DriverModel {
    private String firstName, lastName, birthDate, vehicle, registrationNumber, note;

    public DriverModel(String firstName, String lastName, String birthDate, String vehicle, String registrationNumber, String note) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.vehicle = vehicle;
        this.registrationNumber = registrationNumber;
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

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
