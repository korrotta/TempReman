package com.softwareengineering.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwareengineering.restaurant.databinding.ActivityAddStaffsBinding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AddStaffsActivity extends AppCompatActivity {

    private ImageView topMenuImg;
    private TextView topMenuName;
    private FirebaseAuth mAuth;
    private Staffs newStaffs;
    private RadioGroup addStaffsGender;
    private EditText nameET, emailET, phoneET, roleET, usernameET, passwordET;
    private String name, email, phone, role, username, password, gender;
    private Button btnAddStaffDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staffs);

        mAuth = FirebaseAuth.getInstance();

        addStaffsGender = findViewById(R.id.addStaffsGender);
        nameET = findViewById(R.id.addStaffsName);
        emailET = findViewById(R.id.addStaffsEmail);
        phoneET = findViewById(R.id.addStaffsPhone);
        roleET = findViewById(R.id.addStaffsRole);
        usernameET = findViewById(R.id.addStaffsUsername);
        passwordET = findViewById(R.id.addStaffsPassword);
        btnAddStaffDone = findViewById(R.id.btn_add_staff_done);

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        topMenuImg.setImageResource(R.drawable.back);
        topMenuImg.setColorFilter(ContextCompat.getColor(this, R.color.white));
        topMenuName.setText(R.string.new_account);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddStaffDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioButtonId = addStaffsGender.getCheckedRadioButtonId();

                if (selectedRadioButtonId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                    gender = String.valueOf(selectedRadioButton.getText());
                }
                else {
                    Toast.makeText(AddStaffsActivity.this, "Select Staff's Gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                name = String.valueOf(nameET.getText());
                email = String.valueOf(emailET.getText());
                phone = String.valueOf(phoneET.getText());
                role = String.valueOf(roleET.getText());
                username = String.valueOf(usernameET.getText());
                password = String.valueOf(passwordET.getText());

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddStaffsActivity.this, "Enter Staff's Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AddStaffsActivity.this, "Enter Staff's Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(AddStaffsActivity.this, "Enter Staff's Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(AddStaffsActivity.this, "Select Staff's Gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(role)) {
                    Toast.makeText(AddStaffsActivity.this, "Select Staff's Role", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(AddStaffsActivity.this, "Enter Staff's Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(AddStaffsActivity.this, "Enter Staff's Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        currentUser.sendEmailVerification();
                                        Toast.makeText(AddStaffsActivity.this, "Confirmation link has been sent to staff's registered email", Toast.LENGTH_SHORT).show();
                                        addStaffToDatabase(currentUser.getUid(), name, email, gender, phone, username, role);
                                        newStaffs = new Staffs(name, email, phone, gender, role, username);
                                    }
                                } else {
                                    Toast.makeText(AddStaffsActivity.this, "Add staff failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }

    private void addStaffToDatabase(String uid, String name, String email, String gender, String phone, String username, String role) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("gender", gender);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("username", username);
        userData.put("role", role);

        userRef.set(userData)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(AddStaffsActivity.this, StaffsActivity.class);
                    intent.putExtra("newStaffs", newStaffs);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e("Error adding staff to Firestore", "Error ", e));
    }
}