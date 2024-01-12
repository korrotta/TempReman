package com.softwareengineering.restaurant.CustomerPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.softwareengineering.restaurant.R;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailFoodActivity extends AppCompatActivity {
    ImageView btn_back, image;
    TextView name, status, price, type, des, ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);

        name = findViewById(R.id.name_food);
        image = findViewById(R.id.image_food);
        type = findViewById(R.id.type_food);
        price = findViewById(R.id.price);
//        des = findViewById(des);
//        ingredients = findViewById(ingredients);
        btn_back = findViewById(R.id.btn_back);
        status = findViewById(R.id.status);

        btn_back.setOnClickListener(view -> onBackPressed());

        Intent intent = getIntent();
        if (intent != null) {
            String foodName = intent.getStringExtra("foodName");
            String foodImageUrl = intent.getStringExtra("foodImageUrl");
            boolean foodStatus = intent.getBooleanExtra("foodStatus", false);  // default value is false
            String foodPriceString = intent.getStringExtra("foodPrice");
            double foodPrice = Double.parseDouble(foodPriceString);
            String foodType = intent.getStringExtra("foodType");

            if (foodStatus) {
                // Nếu là true, đặt màu nền xanh lá cây
                status.setBackgroundResource(R.drawable.custom_button_status_green);
            } else {
                // Nếu là false, đặt màu nền đỏ
                status.setBackgroundResource(R.drawable.custom_button_status_red);
            }
            name.setText(foodName);
            status.setText(foodStatus ? "Sale" : "Stop sale");
            price.setText(formatPrice(foodPrice));
            type.setText(foodType);

            int targetWidth = 380;
            int targetHeight = 380;
            int radius = 10;

            RequestOptions requestOptions = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(radius))
                    .override(targetWidth, targetHeight);

            Glide.with(this)
                    .load(foodImageUrl)
                    .apply(requestOptions)
                    .into(image);
        }
    }

    private String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price) + " VND";
    }
}