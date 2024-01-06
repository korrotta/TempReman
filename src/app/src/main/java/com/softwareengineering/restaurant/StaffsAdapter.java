package com.softwareengineering.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StaffsAdapter extends ArrayAdapter<Staffs> {

    public StaffsAdapter(Context context, ArrayList<Staffs> staffsArrayList) {
        super(context, R.layout.admin_staffs_list_item, staffsArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Staffs staffs = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_staffs_list_item, parent, false);
        }

        TextView nameTV = convertView.findViewById(R.id.adminStaffsName);
        TextView roleTV = convertView.findViewById(R.id.adminStaffsRole);

        nameTV.setText(staffs.getName());
        roleTV.setText(staffs.getRole());

        return convertView;
    }
}
