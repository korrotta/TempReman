package com.softwareengineering.restaurant.ItemClasses;

public class Food {
    private String name;
    private String imageReference;
    private String imageURL;
    private boolean state;
    private String type;
    private long price;

    // Constructor mặc định
    public Food() {
        // Cần có constructor mặc định rỗng cho Firestore
    }

    public Food(String imageReference, String imageUrl, String name, long price, boolean isOnSale, String type){
        this.imageReference = imageReference;
        this.name = name;
        this.imageURL = imageUrl;
        this.state = isOnSale;
        this.type = type;
        this.price = price;
    }

    public String getImageUrl() {return imageURL;}

    public String getName() {return name;}
    public boolean getStatus() {return state;}
    public long getPrice() {return price;}
    public String getType() {return type;}
}
