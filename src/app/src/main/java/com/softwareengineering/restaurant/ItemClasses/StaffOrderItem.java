package com.softwareengineering.restaurant.ItemClasses;

public class StaffOrderItem {
    private int imageResourceId;
    private String foodName;
    private Long price;
    private Integer quantity;

    public StaffOrderItem(int imageResourceId, String foodName, Long price, Integer quantity) {
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

    public Long getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

