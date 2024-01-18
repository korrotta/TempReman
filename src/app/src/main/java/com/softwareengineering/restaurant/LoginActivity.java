package com.softwareengineering.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwareengineering.restaurant.AdminPackage.AdminMainActivity;
import com.softwareengineering.restaurant.CustomerPackage.CustomersMenuActivity;
import com.softwareengineering.restaurant.StaffPackage.StaffsMenuActivity;
import com.softwareengineering.restaurant.databinding.ActivityAddFoodBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textView, txtForgotPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonLogin.setOnClickListener(v -> attemptLogin());

        txtForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        textView.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            getUserRoleFromFirestore(currentUser.getUid());
        }
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        textView = findViewById(R.id.registerNow);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        handler = new Handler();
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToast("Enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Enter password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulLogin();
                    } else {
                        showToast("Authentication failed");
                        showFailPopup();
                    }
                });
    }

    private void handleSuccessfulLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            showToast("Login successfully");
            showSuccessPopup(() -> getUserRoleFromFirestore(currentUser.getUid()));
        } else {
            showToast("Your email has not been verified yet");
            showFailPopup();
        }
    }

    private void showSuccessPopup(Runnable onDismissAction) {
        Dialog successDialog = new Dialog(this);
        successDialog.setContentView(R.layout.success_dialog);
        successDialog.setCancelable(true);
        successDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        successDialog.setOnDismissListener(dialog -> onDismissAction.run());
        successDialog.show();
        // Auto-dismiss after 5 seconds
        handler.postDelayed(successDialog::dismiss, 2000);
    }

    private void showFailPopup() {
        Dialog failDialog = new Dialog(this);
        failDialog.setContentView(R.layout.fail_dialog);
        failDialog.setCancelable(true);
        failDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        failDialog.show();
        // Auto-dismiss after 5 seconds
        handler.postDelayed(failDialog::dismiss, 2000);
    }

    private void getUserRoleFromFirestore(String uid) {
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String userRole = document.getString("role");
                    redirectToUserMenu(userRole);
                } else {
                    Log.d("Auth Firestore Database", "No such document");
                }
            } else {
                Log.d("Auth Firestore Database", "get failed with ", task.getException());
            }
        });
    }

    private void redirectToUserMenu(String userRole) {
        Intent intent;
        switch (userRole) {
            case "admin":
                intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                break;
            case "customer":
                intent = new Intent(LoginActivity.this, CustomersMenuActivity.class);
                break;
            default:
                intent = new Intent(LoginActivity.this, StaffsMenuActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}