package com.softwareengineering.restaurant.StaffPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
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

    private final boolean[] final_isCustomer = new boolean[1];
    private final String[] final_id = new String[1];
    private final String[] final_tableID = new String[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTableDetailInuseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        initToolBar();

        setDateToCurrentTime();
        getDataFromPreviousIntent();


        // Order Button
        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(TableDetailInuse.this, StaffOrderActivity.class);

                                    String[] data = new String[2];

                                    data[0] = task.getResult().getString("userinuse");
                                    data[1] = final_tableID[0];

                                    intent.putExtra("data", data);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

        // Cancel Button
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void getDataFromPreviousIntent() {
        String data = getIntent().getStringExtra("id");
        if (data != null) {
            final_tableID[0] = data;
            Log.d("", "getDataFromPreviousIntent: " + final_tableID[0]);

            FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("Inuse", task.getResult().getString("userinuse"));
                                final_id[0] = task.getResult().getString("userinuse");
                                fetchUserRole();
                            }
                            else Log.e("", task.getException().toString());
                        }
                    });
        }
    }


    private void fetchUsingUserRole() {

    }
    private void fetchUserRole(){
//        Log.d("Inuse2", final_id[0]);
        FirebaseFirestore.getInstance().collection("users").document(final_id[0])
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) final_isCustomer[0] = true;
                            else final_isCustomer[0] = false;

                            fetchUserData();
                        }
                    }
                });
    }
    private void fetchUserData(){
        if (final_isCustomer[0]){
            FirebaseFirestore.getInstance().collection("users").document(final_id[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        setTextForTextView(final_tableID[0], task.getResult().getString("name"), task.getResult().getString("phone"));
                    }
                }
            });

        }
        else {
            setTextForTextView(final_tableID[0], "Anonymous", final_id[0]);
        }
    }

    private void setTextForTextView(String tableId, String name, String phone){
        binding.tableDetailsInuseNumberTable.setText(tableId);
        binding.tableDetailInuseTableID.setText(tableId);
        binding.tableDetailsInuseCustomerName.setText(name);
        binding.tableDetailsInuseCustomerPhoneNumber.setText(phone);
    }
}