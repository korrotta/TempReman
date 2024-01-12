package com.softwareengineering.restaurant.CustomerPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.ims.ImsManager;
import android.widget.ImageView;

import com.softwareengineering.restaurant.R;

public class CustomersAccountEditActivity extends AppCompatActivity {
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_account_edit);

        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(view -> onBackPressed());
    }
}