package com.softwareengineering.restaurant.ItemClasses;

import java.text.NumberFormat;
import java.util.Locale;

public class MenuItem {
    private int imageResource;
    private String name;
    private long price;

    public MenuItem(int imageResource, String name, long price) {
        this.imageResource = imageResource;
        this.name = name;
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    // Phương thức để định dạng giá thành chuỗi dạng "20.000"
    public String getFormattedPrice() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price);
    }
}
