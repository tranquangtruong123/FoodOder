package com.example.tqtruongcnpmct2.model;

public class OptionProfile {
    private int img;
    private String nameoption;

    private int imgarrow;

    public OptionProfile() {
    }

    public OptionProfile(int img, String nameoption, int imgarrow) {
        this.img = img;
        this.nameoption = nameoption;
        this.imgarrow = imgarrow;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getNameoption() {
        return nameoption;
    }

    public void setNameoption(String nameoption) {
        this.nameoption = nameoption;
    }

    public int getImgarrow() {
        return imgarrow;
    }

    public void setImgarrow(int imgarrow) {
        this.imgarrow = imgarrow;
    }
}
