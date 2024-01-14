package com.softwareengineering.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.softwareengineering.restaurant.ItemClasses.Reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReportsAdapter extends ArrayAdapter<Reports> {
    public ReportsAdapter(@NonNull Context context, ArrayList<Reports> reportsArrayList) {
        super(context, R.layout.admin_reports_list_item, reportsArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Reports reports = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_reports_list_item, parent, false);
        }

        TextView reportsName = convertView.findViewById(R.id.adminReportsName);
        TextView reportsDate = convertView.findViewById(R.id.adminReportsDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        reportsName.setText(reports.getTitle());
        reportsDate.setText(dateFormat.format(reports.getDate()));

//        if (!reports.isRead()) {
//            reportsName.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
//            reportsDate.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
//        }
//        else {
//            reportsName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//            reportsDate.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//        }

        return convertView;
    }
}
