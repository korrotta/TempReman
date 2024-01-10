package com.softwareengineering.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TablesModel implements Parcelable {
    String id;
    int image;

    public TablesModel(String id, int image) {
        this.id = id;
        this.image = image;
    }

    public TablesModel(Parcel in) {
        id = in.readString();
    }

    public static final Creator<TablesModel> CREATOR = new Creator<TablesModel>() {
        @Override
        public TablesModel createFromParcel(Parcel in) {
            return new TablesModel(in);
        }

        @Override
        public TablesModel[] newArray(int size) {
            return new TablesModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
    }
}
