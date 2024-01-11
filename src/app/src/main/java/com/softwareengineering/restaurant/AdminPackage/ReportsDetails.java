package com.softwareengineering.restaurant.AdminPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.Reports;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.databinding.ActivityReportsDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ReportsDetails extends AppCompatActivity {

    private ActivityReportsDetailsBinding binding;
    private ImageView topMenuImg;
    private TextView topMenuName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        topMenuImg.setImageResource(R.drawable.back);
        topMenuImg.setColorFilter(ContextCompat.getColor(this, R.color.white));
        topMenuName.setText(R.string.reports);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Handle new reports
        Reports reports = getIntent().getParcelableExtra("reports");

        if (reports != null) {
            String title = reports.getTitle();
            String sender = reports.getSender();
            Date date = reports.getDate();
            String content = reports.getContent();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

            binding.adminReportsDetailsTitle.setText(title);
            binding.adminReportsDetailsSender.setText(sender);
            binding.adminReportsDetailsDate.setText(dateFormat.format(date));
            binding.adminReportsDetailsContent.setText(content);
        }

    }
}