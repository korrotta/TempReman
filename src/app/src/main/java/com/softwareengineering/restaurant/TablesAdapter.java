package com.softwareengineering.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TablesAdapter extends ArrayAdapter<TablesModel> {

    public TablesAdapter(Context context, ArrayList<TablesModel> tablesArrayList) {
        super(context, R.layout.table_grid_items, tablesArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TablesModel table = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.table_grid_items, parent, false);
        }

        ImageView tableImg = convertView.findViewById(R.id.tablesActiveImg);
        TextView tableID = convertView.findViewById(R.id.tablesID);

        Picasso.get().load(table.getImage()).placeholder(R.drawable.table_top_view).into(tableImg);
        tableID.setText(table.getId());

        return convertView;
    }

}
