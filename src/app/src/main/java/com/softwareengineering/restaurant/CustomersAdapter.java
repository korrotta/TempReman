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

public class CustomersAdapter extends ArrayAdapter<Customers> {


    public CustomersAdapter(Context context, ArrayList<Customers> customersArrayList) {
        super(context, R.layout.admin_customers_list_item, customersArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Customers customers = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_customers_list_item, parent, false);
        }

        TextView nameTV = convertView.findViewById(R.id.adminCustomersName);

        nameTV.setText(customers.getName());

        return convertView;
    }
}
