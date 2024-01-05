package com.softwareengineering.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPhone, editTextPassword, editTextRetypePassword;
    RadioGroup radioGroupGender;
    Button buttonReg;
    FirebaseAuth mAuth;
    TextView textViewSignIn;
    CheckBox checkBoxTOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPhone = findViewById(R.id.phone);
        radioGroupGender = findViewById(R.id.gender);
        editTextPassword = findViewById(R.id.password);
        editTextRetypePassword = findViewById(R.id.retypePassword);
        buttonReg = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        textViewSignIn = findViewById(R.id.loginNow);
        checkBoxTOS = findViewById(R.id.TOS);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, phone, gender, password, retypePassword;
                name = String.valueOf(editTextName.getText());
                email = String.valueOf(editTextEmail.getText());
                phone = String.valueOf(editTextPhone.getText());
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedGenderId);
                password = String.valueOf(editTextPassword.getText());
                retypePassword = String.valueOf(editTextRetypePassword.getText());

                gender = selectedRadioButton.getText().toString();

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(RegisterActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(gender)){
                    Toast.makeText(RegisterActivity.this, "Select gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(retypePassword)){
                    Toast.makeText(RegisterActivity.this, "Re-enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.equals(password, retypePassword)) {
                    Toast.makeText(RegisterActivity.this, "Password doesn't matched", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkBoxTOS.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "You must accept our Terms & Conditions in order to proceed", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null){
                                        currentUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Confirmation link has been sent to your registered email", Toast.LENGTH_SHORT).show();
                                        addUserToDatabase(currentUser.getUid(), name, email, gender, phone);
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addUserToDatabase(String uid, String name, String email, String gender, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("gender", gender);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("role", "customer");

        userRef.set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("User added to Firestore", "UID: " + uid);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e("Error adding user to Firestore", "Error ", e));
    }
}