package com.softwareengineering.restaurant.ItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Reports implements Parcelable, Comparable<Reports> {
    String title, sender, content;
    Date date;
    boolean isRead;

    public Reports(String title, String sender, String content, Date date, boolean isRead) {
        this.title = title;
        this.sender = sender;
        this.content = content;
        this.date = date;
        this.isRead = isRead;
    }

    protected Reports(Parcel in) {
        title = in.readString();
        sender = in.readString();
        content = in.readString();
        date = new Date(in.readLong());
        isRead = in.readByte() != 0;
    }

    public static final Creator<Reports> CREATOR = new Creator<Reports>() {
        @Override
        public Reports createFromParcel(Parcel in) {
            return new Reports(in);
        }

        @Override
        public Reports[] newArray(int size) {
            return new Reports[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(sender);
        dest.writeString(content);
        dest.writeLong(date.getTime());
        dest.writeByte((byte) (isRead ? 1 : 0));
    }

    @Override
    public int compareTo(Reports other) {
        return this.date.compareTo(other.date);
    }
}
