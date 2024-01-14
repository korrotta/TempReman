package com.softwareengineering.restaurant.StaffPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.StaffOrderItem;
import com.softwareengineering.restaurant.R;

import java.util.List;

public class StaffOrderAdapter extends ArrayAdapter<StaffOrderItem> {

    public StaffOrderAdapter(Context context, List<StaffOrderItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StaffOrderItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.staff_list_item_order, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image_food);
        TextView foodNameTextView = convertView.findViewById(R.id.foodName);
        TextView priceTextView = convertView.findViewById(R.id.price);
        TextView quantityTextView = convertView.findViewById(R.id.quantity);

        imageView.setImageResource(item.getImageResourceId()); // Thay thế bằng phương thức thích hợp
        foodNameTextView.setText(item.getFoodName());
        priceTextView.setText(item.getPrice());
        quantityTextView.setText(item.getQuantity());

        return convertView;
    }
}