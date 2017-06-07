package com.example.bianguojian.project;


import java.util.Date;

public class AccountItem {
    Date date;
    String type;
    String remarks;
    String image;
    float number;
    int color;
    int ID;
    public AccountItem(Date date ,String image, String type, String remarks, float number, int color, int ID) {
        this.date= date;
        this.type= type;
        this.image= image;
        this.remarks= remarks;
        this.number= number;
        this.color= color;
        this.ID= ID;
    }
}