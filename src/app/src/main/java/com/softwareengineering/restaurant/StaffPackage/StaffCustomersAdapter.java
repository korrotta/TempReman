package com.softwareengineering.restaurant.StaffPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.StaffCustomerItem;
import com.softwareengineering.restaurant.R;

import java.util.List;

public class StaffCustomersAdapter extends ArrayAdapter<StaffCustomerItem> {

    public StaffCustomersAdapter(Context context, int resource, List<StaffCustomerItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.staff_list_item_customer, parent, false);
        }

        StaffCustomerItem item = getItem(position);

        ImageView userImage = convertView.findViewById(R.id.image_user);
        TextView customerName = convertView.findViewById(R.id.customerName);

        // Thiết lập dữ liệu cho các view
        userImage.setImageResource(item.getUserImage());
        customerName.setText(item.getCustomerName());

        return convertView;
    }
}
