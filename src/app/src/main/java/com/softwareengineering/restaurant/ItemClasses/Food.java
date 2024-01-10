package com.softwareengineering.restaurant.ItemClasses;

public class Food {
    private String name;
    private String imageReference;
    private String imageUrl;
    private boolean isOnSale;
    private String type;
    private long price;

    public Food(String name, String imageReference, String imageUrl, boolean isOnSale, String type, long price){
        this.imageReference = imageReference;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isOnSale = isOnSale;
        this.type = type;
        this.price = price;
    }

}
