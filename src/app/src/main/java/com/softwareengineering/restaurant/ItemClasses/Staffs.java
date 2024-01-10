package com.softwareengineering.restaurant.ItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Staffs implements Parcelable {

    private String name, email, phone, gender, role, username;

    public Staffs(String name, String email, String phone, String gender, String role, String username) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
        this.username = username;
    }

    protected Staffs(Parcel in) {
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        gender = in.readString();
        role = in.readString();
        username = in.readString();
    }

    public static final Creator<Staffs> CREATOR = new Creator<Staffs>() {
        @Override
        public Staffs createFromParcel(Parcel in) {
            return new Staffs(in);
        }

        @Override
        public Staffs[] newArray(int size) {
            return new Staffs[size];
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(role);
        dest.writeString(username);
    }
}
