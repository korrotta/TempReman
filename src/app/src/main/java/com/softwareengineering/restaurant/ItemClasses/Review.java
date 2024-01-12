package com.softwareengineering.restaurant.ItemClasses;

public class Review {
    private int userAvatar;
    private String cusName;
    private String date;
    private String reviewText;
    private String rate;

    public Review(int userAvatar, String cusName, String date, String reviewText, String rate) {
        this.userAvatar = userAvatar;
        this.cusName = cusName;
        this.date = date;
        this.reviewText = reviewText;
        this.rate = rate;
    }

    public int getUserAvatar() {
        return userAvatar;
    }

    public String getCusName() {
        return cusName;
    }

    public String getDate() {
        return date;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getRate() {
        return rate;
    }
}
