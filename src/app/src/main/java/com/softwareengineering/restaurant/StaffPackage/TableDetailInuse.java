package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.softwareengineering.restaurant.R;

public class TableDetailInuse extends AppCompatActivity {
    Button btn_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_detail_inuse);

        btn_order = findViewById(R.id.btn_order);

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TableDetailInuse.this, StaffOrderActivity.class);
                //Truyen tên khách, số bàn, số lượng khách qua nha...
                startActivity(intent);
            }
        });
    }
}