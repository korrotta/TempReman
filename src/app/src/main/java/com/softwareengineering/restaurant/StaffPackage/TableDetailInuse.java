package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.databinding.ActivityTableDetailInuseBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TableDetailInuse extends AppCompatActivity {

    private ImageView topMenuImg;
    private TextView topMenuName;
    private ActivityTableDetailInuseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTableDetailInuseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        initToolBar();

        // Big table ID Display
        binding.tableDetailInuseTableID.setText("1");

        // Info box
        binding.tableDetailsInuseCustomerImg.setImageResource(R.drawable.default_user);
        binding.tableDetailsInuseCustomerName.setText("Name");
        binding.tableDetailsInuseNumberTable.setText("1");
        binding.tableDetailsInuseTime.setText("20:00");

        // Order Button
        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TableDetailInuse.this, StaffOrderActivity.class);
                //Truyen tên khách, số bàn, số lượng khách qua nha...
                startActivity(intent);
            }
        });

        // Cancel Button
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.back);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topMenuName.setText(R.string.table_detail);
    }

    private void setDateToCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date timeNow = Calendar.getInstance().getTime();
        binding.tableDetailsInuseDate.setText(dateFormat.format(timeNow));
    }

    private void getDataFromPreviousIntent(){
        String[] datas = getIntent().getStringArrayExtra("data");
        if (datas!= null) {
            Log.d("", "getDataFromPreviousIntent: " + datas[0] + " " + datas[1] + " " + datas[2]);
        }
    }

}