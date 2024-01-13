package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.softwareengineering.restaurant.R;

public class StaffBillActivity extends AppCompatActivity {
    private TextView topMenuName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_bill);

        topMenuName = findViewById(R.id.topMenuName);

        topMenuName.setText("Bill");
    }
}