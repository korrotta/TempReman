package com.softwareengineering.restaurant.ItemClasses;

import java.util.Calendar;
import java.util.Date;

public class Review {
    private int userAvatar;
    private String cusName;
    private Date date;
    private String uid;
    private String reviewContent;
    private String rate;

    public Review(int userAvatar, String cusName, Date date, String reviewText, String rate) {
        this.userAvatar = userAvatar;
        this.cusName = cusName;
        this.date = date;
        this.reviewContent = reviewText;
        this.rate = rate;
    }

    public int getUserAvatar() {
        return userAvatar;
    }

    public String getCusName() {
        return cusName;
    }

    public Date getDate() {
        return date;
    }

    public String getReviewText() {
        return reviewContent;
    }

    public String getRate() {
        return rate;
    }
}
