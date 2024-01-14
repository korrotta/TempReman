package com.softwareengineering.restaurant.ItemClasses;

public class StaffOrderItem {
    private int imageResourceId;
    private String foodName;
    private String price;
    private String quantity;

    public StaffOrderItem(int imageResourceId, String foodName, String price, String quantity) {
        this.imageResourceId = imageResourceId;
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }
}

