package com.softwareengineering.restaurant.ItemClasses;

public class StaffCustomerItem {
    private int userImage;
    private String customerName;

    public StaffCustomerItem(int userImage, String customerName) {
        this.userImage = userImage;
        this.customerName = customerName;
    }

    public int getUserImage() {
        return userImage;
    }

    public String getCustomerName() {
        return customerName;
    }
}