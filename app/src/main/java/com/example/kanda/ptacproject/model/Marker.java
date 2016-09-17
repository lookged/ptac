package com.example.kanda.ptacproject.model;

/**
 * Created by NamPeung on 04-Sep-16.
 */
public class Marker {
    private int accId;
    private String accTitle;
    private String accDescription;
    private double accLat;
    private double accLong;
    private String date;
    private int rateId;
    private String email;

    public String getAccTitle() {
        return accTitle;
    }

    public void setAccTitle(String accTitle) {
        this.accTitle = accTitle;
    }

    public int getAccId() {
        return accId;
    }

    public void setAccId(int accId) {
        this.accId = accId;
    }

    public String getAccDescription() {
        return accDescription;
    }

    public void setAccDescription(String accDescription) {
        this.accDescription = accDescription;
    }

    public double getAccLat() {
        return accLat;
    }

    public void setAccLat(double accLat) {
        this.accLat = accLat;
    }

    public double getAccLong() {
        return accLong;
    }

    public void setAccLong(double accLong) {
        this.accLong = accLong;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRateId() {
        return rateId;
    }

    public void setRateId(int rateId) {
        this.rateId = rateId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return this.accId + "  " +
                this.accTitle + "  " +
                this.accDescription + "  " +
                this.accLat + "  " +
                this.accLong + "  " +
                this.date + "  " +
                this.rateId + "  " +
                this.email;

    }
}
