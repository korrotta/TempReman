package com.softwareengineering.restaurant.StaffPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.softwareengineering.restaurant.ItemClasses.MenuItem;
import com.softwareengineering.restaurant.ItemClasses.OrderItem;
import com.softwareengineering.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<MenuItem> menuItems;
    private List<OrderItem> selectedItems;

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
        this.selectedItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.staff_items_menu, parent, false);
            holder = new ViewHolder();
            holder.imageFood = convertView.findViewById(R.id.image_food);
            holder.nameFood = convertView.findViewById(R.id.name_food);
            holder.price = convertView.findViewById(R.id.price);
            holder.btnAdd = convertView.findViewById(R.id.btn_add);
            holder.container = convertView.findViewById(R.id.container);
            holder.btnPlus = convertView.findViewById(R.id.btn_plus);
            holder.btnMinus = convertView.findViewById(R.id.btn_minus);
            holder.quanlity = convertView.findViewById(R.id.quanlity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MenuItem menuItem = menuItems.get(position);

        // Sử dụng Glide để tải ảnh và cấu hình kích thước
        RequestOptions requestOptions = new RequestOptions()
                .override(200, 200) // Kích thước mong muốn
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(menuItem.getImageResource())
                .apply(requestOptions)
                .into(holder.imageFood);

        holder.nameFood.setText(menuItem.getName());
        holder.price.setText(menuItem.getFormattedPrice());
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị layout mới và ẩn nút btn_add
                showNewLayout(holder, menuItem);
            }
        });

        return convertView;
    }



    private static class ViewHolder {
        ImageView imageFood;
        TextView nameFood;
        TextView price;
        ImageView btnAdd;
        FrameLayout container;
        ImageView btnPlus;
        ImageView btnMinus;
        TextView quanlity;
    }

    private void showNewLayout(ViewHolder holder, MenuItem menuItem) {
        // Ẩn nút btn_add
        holder.btnAdd.setVisibility(View.GONE);

        // Hiển thị layout mới
        holder.container.setVisibility(View.VISIBLE);

        // Xử lý sự kiện cho các nút trong layout mới
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tăng giá trị quanlity
                int newQuanlity = Integer.parseInt(holder.quanlity.getText().toString()) + 1;
                holder.quanlity.setText(String.valueOf(newQuanlity));
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Giảm giá trị quanlity, đảm bảo giá trị không âm
                int newQuanlity = Math.max(Integer.parseInt(holder.quanlity.getText().toString()) - 1, 0);
                holder.quanlity.setText(String.valueOf(newQuanlity));

                // Nếu quanlity giảm xuống 0, ẩn layout mới và hiển thị lại nút btn_add
                if (newQuanlity == 0) {
                    hideNewLayout(holder);
                }
            }
        });

        // Lưu thông tin món ăn đã chọn vào danh sách
        saveSelectedItem(menuItem, Long.parseLong(holder.quanlity.getText().toString()));
    }

    private void saveSelectedItem(MenuItem menuItem, Long quantity) {
        OrderItem orderItem = new OrderItem(menuItem.getName(), menuItem.getPrice(), quantity);
        selectedItems.add(orderItem);
    }

    // Thêm phương thức hideNewLayout để ẩn layout mới và hiển thị lại nút btn_add
    private void hideNewLayout(ViewHolder holder) {
        // Ẩn layout mới
        holder.container.setVisibility(View.GONE);

        // Hiển thị lại nút btn_add
        holder.btnAdd.setVisibility(View.VISIBLE);
    }

    public List<OrderItem> getSelectedItems() {
        return selectedItems;
    }

}


