package com.softwareengineering.restaurant.AdminPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.Customers;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.databinding.ActivityCustomersDetailsBinding;

public class CustomersDetails extends AppCompatActivity {

    private ActivityCustomersDetailsBinding binding;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private LinearLayout removeCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomersDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        removeCustomers = findViewById(R.id.adminCustomersRemove);

        topMenuImg.setImageResource(R.drawable.back);
        topMenuImg.setColorFilter(ContextCompat.getColor(this, R.color.white));
        topMenuName.setText(R.string.customers);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Handle new created staff account
        Customers customers = getIntent().getParcelableExtra("customers");

        if (customers != null) {
            String name = customers.getName();
            String email = customers.getEmail();
            String phone = customers.getPhone();
            String gender = customers.getGender();

            binding.customersDetailName.setText(name);
            binding.customersDetailEmail.setText(email);
            binding.customersDetailGender.setText(gender);
            binding.customersDetailPhone.setText(phone);
        }

        // Handle Remove Staff (Also remove from database)
        removeCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}