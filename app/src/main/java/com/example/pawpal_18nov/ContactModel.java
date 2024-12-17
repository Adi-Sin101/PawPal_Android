package com.example.pawpal_18nov;
public class ContactModel {
    private String name;
    private String phone;
    private String gender;

    // Constructor
    public ContactModel(String name, String phone, String gender) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
    }

    // Getter and Setter Methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}



