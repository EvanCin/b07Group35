package com.example.planetze35;

import androidx.annotation.NonNull;

public class UserInfo {

    // TODO: add required fields
    private String firstName;
    private String lastName;

    public UserInfo() {
    }

    public UserInfo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NonNull
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
