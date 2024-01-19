package com.softwareengineering.restaurant.CustomerPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.ims.ImsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.R;

import org.w3c.dom.Text;

public class CustomersAccountEditActivity extends AppCompatActivity {
    private ImageView topMenuImg;
    private TextView topMenuName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_account_edit);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        initToolBar();
    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.back);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topMenuName.setText("");
    }
}