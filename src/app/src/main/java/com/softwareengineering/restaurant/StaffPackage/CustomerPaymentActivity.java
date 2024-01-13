package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.softwareengineering.restaurant.ItemClasses.PaymentFood;
import com.softwareengineering.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerPaymentActivity extends AppCompatActivity {
    ListView listView;
    ImageView btn_back;

    Button btn_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment);

        listView = findViewById(R.id.listView);
        btn_back = findViewById(R.id.btn_back);
        btn_payment = findViewById(R.id.btn_payment);

        btn_back.setOnClickListener(view -> onBackPressed());

        List<PaymentFood> data = new ArrayList<>();
        // Thêm dữ liệu vào danh sách
        data.add(new PaymentFood("Summer Salad", 1, 20000));
        data.add(new PaymentFood("Pasta", 2, 35000));
        // Thêm nhiều mục khác nếu cần

        PaymentAdapter adapter = new PaymentAdapter(this, data);
        listView.setAdapter(adapter);

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerPaymentActivity.this, StaffBillActivity.class);

                // intent.putExtra("key", value);
                startActivity(intent);
            }
        });
    }
}