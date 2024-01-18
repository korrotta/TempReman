package com.softwareengineering.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SalesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> dates;
    private ArrayList<Long> sales;

    public SalesAdapter(Context context, ArrayList<String> dates, ArrayList<Long> sales) {
        this.context = context;
        this.dates = dates;
        this.sales = sales;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public ArrayList<Long> getSales() {
        return sales;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        }

        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView salesTextView = convertView.findViewById(R.id.salesTextView);

        dateTextView.setText(dates.get(position));
        
        // Format the sales value with commas
        String formattedSales = formatNumberWithCommas(sales.get(position));
        salesTextView.setText(formattedSales);

        return convertView;
    }

    private String formatNumberWithCommas(long number) {
        // Format the number with commas using NumberFormat
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(number);
    }

}
