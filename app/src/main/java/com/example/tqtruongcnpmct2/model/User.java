package com.example.tqtruongcnpmct2.model;

public class User {

    private String email;
    private String password;
    private String fullname;
    private String PhoneNumber;
    private String adress;
    private String pathImg;
    public User() {
    }

    public User( String email, String password, String fullname, String phoneNumber, String adress) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.PhoneNumber = phoneNumber;
        this.adress = adress;
    }

    public User(String email, String password, String fullname, String phoneNumber, String adress, String pathImg) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        PhoneNumber = phoneNumber;
        this.adress = adress;
        this.pathImg = pathImg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPathImg() {
        return pathImg;
    }

    public void setPathImg(String pathImg) {
        this.pathImg = pathImg;
    }
}
