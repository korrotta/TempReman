package com.softwareengineering.restaurant.StaffPackage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffsAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView userName;
    private RelativeLayout customers, menu, tables, reports, payment, account, logout;
    private EditText nameET, emailET, phoneET, genderET, roleET;
    private Button editBtn, resetPassBtn;
    private CircleImageView userAvatar, accountAvatar;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffs_account);

        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.staffsDrawerLayout);
        topMenuImg = findViewById(R.id.topMenuImg);
        customers = findViewById(R.id.staffsCustomersDrawer);
        menu = findViewById(R.id.staffsMenuDrawer);
        tables = findViewById(R.id.staffsTablesDrawer);
        reports = findViewById(R.id.staffsReportsDrawer);
        payment = findViewById(R.id.staffsPaymentDrawer);
        account = findViewById(R.id.staffsAccountDrawer);
        logout = findViewById(R.id.staffsLogoutDrawer);
        userAvatar = findViewById(R.id.staffsNavAvatar);
        userName = findViewById(R.id.staffsNavName);
        accountAvatar = findViewById(R.id.staffsAccountImage);
        nameET = findViewById(R.id.staffsAccountNameET);
        emailET = findViewById(R.id.staffsAccountEmailET);
        phoneET = findViewById(R.id.staffsAccountPhoneET);
        genderET = findViewById(R.id.staffsAccountGenderET);
        roleET = findViewById(R.id.staffsAccountRoleET);
        editBtn = findViewById(R.id.staffsAccountEditBtn);
        resetPassBtn = findViewById(R.id.staffsAccountResetPassBtn);

        // Get currentUser
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        Uri avatarPhotoUrl = currentUser.getPhotoUrl();
        // Avatar Image
        Picasso.get().load(avatarPhotoUrl).placeholder(R.drawable.default_user).into(userAvatar);
        Picasso.get().load(avatarPhotoUrl).placeholder(R.drawable.default_user).into(accountAvatar);

        // Get user info from firestore
        getUserInfoFirestore(currentUser.getUid());

        // Set all edit text field non editable and drawable invisible unless press Edit Btn
        nameET.setEnabled(false);
        nameET.setFocusable(false);
        nameET.setFocusableInTouchMode(false);
        nameET.setTextColor(Color.GRAY);
        nameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        emailET.setEnabled(false);
        emailET.setFocusable(false);
        emailET.setFocusableInTouchMode(false);
        emailET.setTextColor(Color.GRAY);
        phoneET.setEnabled(false);
        phoneET.setFocusable(false);
        phoneET.setFocusableInTouchMode(false);
        phoneET.setTextColor(Color.GRAY);
        phoneET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        genderET.setEnabled(false);
        genderET.setFocusable(false);
        genderET.setFocusableInTouchMode(false);
        genderET.setTextColor(Color.GRAY);
        genderET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        roleET.setEnabled(false);
        roleET.setFocusable(false);
        roleET.setFocusableInTouchMode(false);
        roleET.setTextColor(Color.GRAY);

        // Send reset Password link
        resetPassBtn.setEnabled(true);
        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassBtn.setEnabled(false);
                mAuth.sendPasswordResetEmail(emailET.toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(StaffsAccountActivity.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(StaffsAccountActivity.this, "Error: -" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                resetPassBtn.setEnabled(true);
                            }
                        });
            }
        });

        // Change state of the edit button when clicked
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If in View mode => Edit mode
                if (editBtn.getText().toString().equals("Edit")) {
                    editBtn.setText(R.string.save_changes);
                    // Set all Editable Text now editable
                    nameET.setEnabled(true);
                    nameET.setFocusable(true);
                    nameET.setFocusableInTouchMode(true);
                    nameET.setTextColor(Color.BLACK);
                    nameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pen, 0);
                    phoneET.setEnabled(true);
                    phoneET.setFocusable(true);
                    phoneET.setFocusableInTouchMode(true);
                    phoneET.setTextColor(Color.BLACK);
                    phoneET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pen, 0);
                    genderET.setEnabled(true);
                    genderET.setFocusable(true);
                    genderET.setFocusableInTouchMode(true);
                    genderET.setTextColor(Color.BLACK);
                    genderET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pen, 0);

                }
                // Else in Edit mode => View mode and save changes
                else {
                    editBtn.setText(R.string.edit);

                    // Set all edit text field non editable and drawable invisible
                    nameET.setEnabled(false);
                    nameET.setFocusable(false);
                    nameET.setFocusableInTouchMode(false);
                    nameET.setTextColor(Color.GRAY);
                    nameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    phoneET.setEnabled(false);
                    phoneET.setFocusable(false);
                    phoneET.setFocusableInTouchMode(false);
                    phoneET.setTextColor(Color.GRAY);
                    phoneET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    genderET.setEnabled(false);
                    genderET.setFocusable(false);
                    genderET.setFocusableInTouchMode(false);
                    genderET.setTextColor(Color.GRAY);
                    genderET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    // Update info onto database
                    DocumentReference userRef = firestore.collection("users").document(currentUser.getUid());
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("name", nameET.getText().toString());
                    userInfo.put("phone", phoneET.getText().toString());
                    userInfo.put("gender", genderET.getText().toString());

                    userRef.update(userInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(StaffsAccountActivity.this, "Successfully updated info", Toast.LENGTH_SHORT).show();
                                    recreate();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StaffsAccountActivity.this, "Failed to update info", Toast.LENGTH_SHORT).show();
                                    recreate();
                                }
                            });
                }
            }
        });

        setItemBackgroundColors(account);

        topMenuImg.setImageResource(R.drawable.topmenu);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(StaffsAccountActivity.this, StaffsMenuActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(StaffsAccountActivity.this, StaffsCustomersActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(StaffsAccountActivity.this, StaffsTablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(StaffsAccountActivity.this, StaffsReportsActivity.class);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(payment);
                redirectActivity(StaffsAccountActivity.this, StaffsPaymentActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(StaffsAccountActivity.this, LoginActivity.class);
            }
        });

    }

    private void getUserInfoFirestore(String uid) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get user info
                    String name, email, phone, gender, role;
                    name = document.getString("name");
                    email = document.getString("email");
                    phone = document.getString("phone");
                    gender = document.getString("gender");
                    role = document.getString("role");

                    // Set user info
                    userName.setText(name);
                    nameET.setText(name);
                    emailET.setText(email);
                    phoneET.setText(phone);
                    genderET.setText(gender);
                    roleET.setText(role);

                } else {
                    // User document not found
                    Log.d("Auth Firestore Database", "No such document");
                }
            } else {
                Log.d("Auth Firestore Database", "get failed with ", task.getException());
            }
        });

    }

    private void setItemBackgroundColors(RelativeLayout selectedItem) {
        customers.setBackgroundColor(selectedItem == customers ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        menu.setBackgroundColor(selectedItem == menu ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        tables.setBackgroundColor(selectedItem == tables ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        reports.setBackgroundColor(selectedItem == reports ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        payment.setBackgroundColor(selectedItem == payment ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        account.setBackgroundColor(selectedItem == account ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}