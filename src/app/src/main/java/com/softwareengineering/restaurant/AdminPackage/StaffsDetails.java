package com.softwareengineering.restaurant.AdminPackage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.util.ArrayList;

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
        Log.d("Marker", staffs.getEmail());

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
                someActivityResultLauncher.launch(intent);
            }
        });

        // Handle Remove Staff (Also remove from database)
        removeStaffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("users").whereEqualTo("email", staffs.getEmail()).get().addOnCompleteListener(task ->{
                    if (task.isSuccessful()) {
                        ArrayList<Staffs> st = new ArrayList<Staffs>();
                        for (QueryDocumentSnapshot doc: task.getResult()){
                            firestore.collection("users").document(doc.getId()).update("role", "customer").addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("Success re-declared", doc.getId());
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("data", "rebind"); // Thay "resultKey" và "resultValue" bằng dữ liệu bạn muốn trả về
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }
                            });
                        }

                    }
                });
            }
        });

        // OpenActivity for Result

    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.d("Intent received", getIntent().toString());
                    Intent resultIntent = result.getData();
                    Staffs returnData = resultIntent.getParcelableExtra("data");
                    if (returnData!=null) {
                        Log.d("New Data", "YES");
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    //Do nothing
                }
            });
}