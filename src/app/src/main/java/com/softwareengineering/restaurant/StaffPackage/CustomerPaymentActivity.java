package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.PaymentFood;
import com.softwareengineering.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerPaymentActivity extends AppCompatActivity {

    private TextView topMenuName;
    private ListView listView;
    private ImageView btn_back, topMenuImg;

    private Button btn_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment);

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        listView = findViewById(R.id.listView);
        btn_payment = findViewById(R.id.btn_payment);

        topMenuImg.setImageResource(R.drawable.back);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topMenuName.setText(R.string.payment);

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
                // Bill Details
//                Intent intent = new Intent(CustomerPaymentActivity.this, StaffBillActivity.class);
//
//                // intent.putExtra("key", value);
//                startActivity(intent);
            }
        });
    }
}