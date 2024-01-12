package com.softwareengineering.restaurant.CustomerPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
        // Format giá tiền với dấu chấm sau mỗi 3 chữ số
        String formattedPrice = formatPrice(food.getPrice());
        priceTextView.setText(formattedPrice);


        // Kích thước cố định (ví dụ: 200x200 pixels)
        int targetWidth = 200;
        int targetHeight = 200;

        int radius = 10;

        RequestOptions requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(radius))
                .override(targetWidth, targetHeight);

        Glide.with(context)
                .load(food.getImageUrl())
                .apply(requestOptions)
                .into(imageView);

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Giả sử isStatusTrue là biến boolean kiểm tra điều kiện
        boolean isStatusTrue = food.getStatus();
        statusTextView.setText(isStatusTrue ? "Sale" : "Stop sale");

        TextView statusButton = itemView.findViewById(R.id.status);

        if (isStatusTrue) {
            // Nếu là true, đặt màu nền xanh lá cây
            statusButton.setBackgroundResource(R.drawable.custom_button_status_green);
        } else {
            // Nếu là false, đặt màu nền đỏ
            statusButton.setBackgroundResource(R.drawable.custom_button_status_red);
        }

        nameTextView.setText(food.getName());
        statusButton.setText(isStatusTrue ? "Sale" : "Stop sale");

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi item được nhấn
                // Ví dụ: Mở một activity khác để hiển thị chi tiết món ăn
            }
        });
    }

    private String formatPrice(double price) {
        // Sử dụng NumberFormat để định dạng số theo định dạng tiền tệ
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price);
    }
}
