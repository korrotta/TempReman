package com.softwareengineering.restaurant.CustomerPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.softwareengineering.restaurant.R;

public class AddReviewActivity extends AppCompatActivity {
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(view -> onBackPressed());
    }
}