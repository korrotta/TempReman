package com.softwareengineering.restaurant.AdminPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.ItemClasses.Staffs;
import com.softwareengineering.restaurant.databinding.ActivityStaffsDetailsBinding;

public class StaffsDetails extends AppCompatActivity {

    private ActivityStaffsDetailsBinding binding;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private LinearLayout editStaffs, removeStaffs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        editStaffs = findViewById(R.id.adminStaffsEdit);
        removeStaffs = findViewById(R.id.adminStaffsRemove);

        topMenuImg.setImageResource(R.drawable.back);
        topMenuImg.setColorFilter(ContextCompat.getColor(this, R.color.white));
        topMenuName.setText(R.string.staffs);



        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Handle new created staff account
        Staffs staffs = getIntent().getParcelableExtra("data");


        if (staffs != null) {
            String name = staffs.getName();
            String email = staffs.getEmail();
            String phone = staffs.getPhone();
            String gender = staffs.getGender();
            String role = staffs.getRole();
            String username = staffs.getUsername();

            binding.staffsDetailName.setText(name);
            binding.staffsDetailEmail.setText(email);
            binding.staffsDetailGender.setText(gender);
            binding.staffsDetailPhone.setText(phone);
            binding.staffsDetailRole.setText(role);
            binding.staffsDetailUsername.setText(username);
        }

        // Handle Edit Existing Staff
        editStaffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffsDetails.this, EditStaffsActivity.class);
                if (staffs != null) {
                    intent.putExtra("existedStaffs", staffs);
                }
                startActivity(intent);
            }
        });

        // Handle Remove Staff (Also remove from database)
        removeStaffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("users").whereEqualTo("email", staffs.getEmail()).get().addOnCompleteListener(task ->{
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc: task.getResult()){
                            firestore.collection("users").document(doc.getId()).delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("Success delete", doc.getId());
                                    finish();
                                }
                            });
                        }

                    }
                });
            }
        });
        finish();
    }
}