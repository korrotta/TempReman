package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.R;

public class StaffBillActivity extends AppCompatActivity {

    private TextView topMenuName;
    private ImageView topMenuImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_bill);

        topMenuName = findViewById(R.id.topMenuName);
        topMenuImg = findViewById(R.id.topMenuImg);

        topMenuImg.setImageResource(R.drawable.back);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topMenuName.setText(R.string.bill);


    }
}