package com.softwareengineering.restaurant.CustomerPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.StaffPackage.StaffsAccountActivity;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Map;

public class CustomersAccountActivity extends AppCompatActivity {

    private final String TAG = "CAA Error";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg, userAvatar;
    private TextView userNameView, userEmail, topMenuName;
    private EditText userNameET, phoneET, genderET;
    private Button editAccountBtn, resetPassBtn;

    private RelativeLayout menu, tables, review, account, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_account);

        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.customersDrawerLayout);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        menu = findViewById(R.id.customersMenuDrawer);
        tables = findViewById(R.id.customersTablesDrawer);
        review = findViewById(R.id.customersReviewDrawer);
        account = findViewById(R.id.customersAccountDrawer);
        logout = findViewById(R.id.customersLogoutDrawer);
        userAvatar = findViewById(R.id.customersNavAvatar);
        userNameView = findViewById(R.id.customersNavName);
        userEmail = findViewById(R.id.customer_emailView);
        userNameET = findViewById(R.id.customer_name);
        phoneET = findViewById(R.id.customer_phone);
        genderET = findViewById(R.id.customer_gender);
        editAccountBtn = findViewById(R.id.customerAccountEditBtn);
        resetPassBtn = findViewById(R.id.customerAccountResetPassBtn);

        initToolBar();
        initMenuBar();
        initTextView();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        Uri avatarPhotoUrl = currentUser.getPhotoUrl();
        Log.d("PhotoURL", String.valueOf(currentUser.getPhotoUrl()));
        Picasso.get().load(avatarPhotoUrl).placeholder(R.drawable.default_user).into(userAvatar);


        //Set name for user:
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    userNameView.setText(task.getResult().getString("name"));
                    userNameET.setText(task.getResult().getString("name"));
                    phoneET.setText(task.getResult().getString("phone"));
                    userEmail.setText(task.getResult().getString("email"));
                    genderET.setText(task.getResult().getString("gender"));
                }
                else {
                    Log.e(TAG, "onComplete: " + task.getException());
                }
            }
        });

        // Change state of the edit button when clicked
        editAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If in View mode => Edit mode
                if (editAccountBtn.getText().toString().equals("Edit")) {
                    editAccountBtn.setText(R.string.save_changes);
                    // Set all Editable Text now editable
                    userNameET.setEnabled(true);
                    userNameET.setFocusable(true);
                    userNameET.setFocusableInTouchMode(true);
                    userNameET.setTextColor(Color.BLACK);
                    userNameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pen, 0);
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
                    editAccountBtn.setText(R.string.edit);

                    // Set all edit text field non editable and drawable invisible
                    userNameET.setEnabled(false);
                    userNameET.setFocusable(false);
                    userNameET.setFocusableInTouchMode(false);
                    userNameET.setTextColor(Color.GRAY);
                    userNameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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
                    userInfo.put("name", userNameET.getText().toString());
                    userInfo.put("phone", phoneET.getText().toString());
                    userInfo.put("gender", genderET.getText().toString());

                    userRef.update(userInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(CustomersAccountActivity.this, "Successfully updated info", Toast.LENGTH_SHORT).show();
                                    recreate();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CustomersAccountActivity.this, "Failed to update info", Toast.LENGTH_SHORT).show();
                                    recreate();
                                }
                            });
                }
            }
        });

        // Send reset Password link
        resetPassBtn.setEnabled(true);
        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassBtn.setEnabled(false);
                mAuth.sendPasswordResetEmail(userEmail.toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CustomersAccountActivity.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CustomersAccountActivity.this, "Error: -" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                resetPassBtn.setEnabled(true);
                            }
                        });
            }
        });
    }

    private void initTextView() {
        // Set all edit text field non editable and drawable invisible unless press Edit Btn
        userNameET.setEnabled(false);
        userNameET.setFocusable(false);
        userNameET.setFocusableInTouchMode(false);
        userNameET.setTextColor(Color.GRAY);
        userNameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        userEmail.setEnabled(false);
        userEmail.setFocusable(false);
        userEmail.setFocusableInTouchMode(false);
        userEmail.setTextColor(Color.GRAY);
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
    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.topmenu);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText(R.string.account);
    }

    private void initMenuBar() {
        setItemBackgroundColors(account);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(CustomersAccountActivity.this, CustomersMenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(CustomersAccountActivity.this, CustomersTablesActivity.class);
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(review);
                redirectActivity(CustomersAccountActivity.this, CustomersReviewActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                recreate();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(CustomersAccountActivity.this, LoginActivity.class);
            }
        });

    }

    private void setItemBackgroundColors(RelativeLayout selectedItem) {
        menu.setBackgroundColor(selectedItem == menu ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        tables.setBackgroundColor(selectedItem == tables ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        review.setBackgroundColor(selectedItem == review ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        account.setBackgroundColor(selectedItem == account ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
    }

    public static void openDrawer (DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer (DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity (Activity activity, Class secondActivity) {
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