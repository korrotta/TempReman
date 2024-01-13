package com.softwareengineering.restaurant.StaffPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;

public class StaffNewReportActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView topMenuName, title, date, content;
    private ImageView topMenuImg;
    private DrawerLayout drawerLayout;
    private RelativeLayout customers, menu, tables, reports, payment, account, logout;
    private Button confirm, save;

    private String g1_sender;
    private String g1_currentReportId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_new_report);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        topMenuImg = findViewById(R.id.topMenuImg);
        drawerLayout = findViewById(R.id.staffsDrawerLayout);
        customers = findViewById(R.id.staffsCustomersDrawer);
        menu = findViewById(R.id.staffsMenuDrawer);
        tables = findViewById(R.id.staffsTablesDrawer);
        reports = findViewById(R.id.staffsReportsDrawer);
        payment = findViewById(R.id.staffsPaymentDrawer);
        account = findViewById(R.id.staffsAccountDrawer);
        logout = findViewById(R.id.staffsLogoutDrawer);
        topMenuName = findViewById(R.id.topMenuName);
        title = findViewById(R.id.title);
      
        content = findViewById(R.id.content);

        topMenuImg.setImageResource(R.drawable.back);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirm = (Button) findViewById(R.id.btn_cancel);
        save = (Button)findViewById(R.id.btn_save);

        topMenuName.setText("Write a report");

        menuBarItemClick();

        confirm.setOnClickListener(confirmButtonClickEvent);

    }
    //TODO: Remove isRead in Reports parcel, add id as a String
    View.OnClickListener confirmButtonClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: Avoid empty title
            if (title == null || title.equals("")){
                return;
            }

            //Obviously level 1 callback
            Log.d("Sender_check", g1_sender);

            if (content.getText() == null || content.getText().equals("")) {
                return;
            }

            //Upload to firebase storage
            uploadReportFile();

            finish();
        }
    };

    private void uploadReportFile(){
        FirebaseStorage.getInstance().getReference().child("reports/"+mAuth.getCurrentUser().getUid() + "/" );
    }

    private void fetchName(){
        FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        g1_sender = task.getResult().getString("name");
                    }
                });
    }

    private void fetchCurrentReportId(){
        FirebaseFirestore.getInstance().collection("reports");
    }
    private void menuBarItemClick() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(StaffNewReportActivity.this, StaffsMenuActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(StaffNewReportActivity.this, StaffsCustomersActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(StaffNewReportActivity.this, StaffsTablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                recreate();
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(payment);
                redirectActivity(StaffNewReportActivity.this, StaffsPaymentActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(StaffNewReportActivity.this, StaffsAccountActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(StaffNewReportActivity.this, LoginActivity.class);
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