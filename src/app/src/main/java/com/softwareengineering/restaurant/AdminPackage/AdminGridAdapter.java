package com.softwareengineering.restaurant.AdminPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.R;

public class AdminGridAdapter extends BaseAdapter {

    Context context;
    int[] itemImage;
    String[] itemName;
    LayoutInflater layoutInflater;

    public AdminGridAdapter(Context context, int[] itemImage, String[] itemName) {
        this.context = context;
        this.itemImage = itemImage;
        this.itemName = itemName;
    }

    @Override
    public int getCount() {
        return itemName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.admin_grid_items, null);
        }

        ImageView imageView = convertView.findViewById(R.id.gridItemImage);
        TextView textView = convertView.findViewById(R.id.gridItemName);

        imageView.setImageResource(itemImage[position]);
        textView.setText(itemName[position]);

        return convertView;
    }
}
