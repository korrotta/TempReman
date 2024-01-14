package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.StaffOrderItem;
import com.softwareengineering.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class StaffOrderActivity extends AppCompatActivity {
    private TextView topMenuName;
    private Button btn_add;
    private ListView listView;
    private StaffOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_order);

        topMenuName = findViewById(R.id.topMenuName);
        btn_add = findViewById(R.id.btn_add);
        listView = findViewById(R.id.listView);

        topMenuName.setText("Order");

        // Khởi tạo danh sách mẫu (bạn có thể thay thế bằng dữ liệu thực tế)
        List<StaffOrderItem> orderItems = new ArrayList<>();
        orderItems.add(new StaffOrderItem(R.drawable.salad, "Summer Salad", "20.000", "1"));
        orderItems.add(new StaffOrderItem(R.drawable.pasta, "Pasta", "25.000", "2"));

        // Khởi tạo Adapter
        adapter = new StaffOrderAdapter(this, orderItems);

        // Kết nối ListView với Adapter
        listView.setAdapter(adapter);



        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffOrderActivity.this, StaffOrderAddActivity.class);
                startActivity(intent);
            }
        });
    }
}