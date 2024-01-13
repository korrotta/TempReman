package com.softwareengineering.restaurant.StaffPackage;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.softwareengineering.restaurant.R;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> customerNames;
    private final ArrayList<String> tableNumbers;

    public CustomListAdapter(Activity context, ArrayList<String> customerNames, ArrayList<String> tableNumbers) {
        super(context, R.layout.staff_list_item_customer_payment, customerNames);
        this.context = context;
        this.customerNames = customerNames;
        this.tableNumbers = tableNumbers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.staff_list_item_customer_payment, null, true);

        TextView customerName = rowView.findViewById(R.id.customerName);
        TextView numberTable = rowView.findViewById(R.id.numberTable);

        customerName.setText(customerNames.get(position));
        numberTable.setText(tableNumbers.get(position));

        return rowView;
    }
}
