package com.softwareengineering.restaurant.AdminPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.softwareengineering.restaurant.ItemClasses.Reports;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.databinding.ActivityReportsDetailsBinding;

import java.nio.charset.Charset;
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
            Date date = reports.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            StorageReference storage = FirebaseStorage.getInstance().getReference();

            firestore.collection("users").document(reports.getSender()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String name = task.getResult().getString("name");
                    binding.adminReportsDetailsTitle.setText(title);
                    binding.adminReportsDetailsSender.setText(name);
                    binding.adminReportsDetailsDate.setText(dateFormat.format(date));
                }
            });

            StorageReference ref2=  storage.child("reports/" + reports.getSender() + "/" + reports.getId() + ".txt");
            ref2.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    String fileContent = new String(bytes, Charset.defaultCharset());
                    binding.adminReportsDetailsContent.setText(fileContent);
                    Log.d("Dir", ref2.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error text file", e.toString() + " ref: " + ref2.toString());
                }
            });
        }

    }
}