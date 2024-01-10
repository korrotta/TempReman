package com.softwareengineering.restaurant.ItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Customers implements Parcelable {
    private String name, email, username, phone, gender;

    public Customers(String name, String email, String username, String phone, String gender) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.gender = gender;
    }

    protected Customers(Parcel in) {
        name = in.readString();
        email = in.readString();
        username = in.readString();
        phone = in.readString();
        gender = in.readString();
    }

    public static final Creator<Customers> CREATOR = new Creator<Customers>() {
        @Override
        public Customers createFromParcel(Parcel in) {
            return new Customers(in);
        }

        @Override
        public Customers[] newArray(int size) {
            return new Customers[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(gender);
    }
}
