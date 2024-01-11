package com.softwareengineering.restaurant.CustomerPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends BaseAdapter {
    private Context context;
    private List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.customers_items_menu, parent, false);
        }

        Food food = (Food) getItem(position);
        populateItemView(itemView, food);

        return itemView;
    }

    private void populateItemView(View itemView, Food food) {
        // Thiết lập dữ liệu cho mỗi ItemView
        ImageView imageView = itemView.findViewById(R.id.image_food);
        TextView nameTextView = itemView.findViewById(R.id.name_food);
        TextView statusTextView = itemView.findViewById(R.id.status);
        TextView priceTextView = itemView.findViewById(R.id.price);

        // Kích thước cố định (ví dụ: 200x200 pixels)
        int targetWidth = 200;
        int targetHeight = 200;

        Picasso.get()
                .load(food.getImageUrl())
                .resize(targetWidth, targetHeight)
                .centerCrop()
                .into(imageView);

        // Set scaleType của ImageView để ảnh sát vào khung mà không cắt bớt
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Giả sử isStatusTrue là biến boolean kiểm tra điều kiện
        boolean isStatusTrue = food.getStatus();
        statusTextView.setText(isStatusTrue ? "Sale" : "Stop sale");

        Button statusButton = itemView.findViewById(R.id.status);

        if (isStatusTrue) {
            // Nếu là true, đặt màu nền xanh lá cây
            statusButton.setBackgroundResource(R.drawable.custom_button_status_green);
        } else {
            // Nếu là false, đặt màu nền đỏ
            statusButton.setBackgroundResource(R.drawable.custom_button_status_red);
        }

        nameTextView.setText(food.getName());
        //statusTextView.setText(String.valueOf(food.getStatus()));
        priceTextView.setText(String.valueOf(food.getPrice()));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi item được nhấn
                // Ví dụ: Mở một activity khác để hiển thị chi tiết món ăn
            }
        });
    }
}
