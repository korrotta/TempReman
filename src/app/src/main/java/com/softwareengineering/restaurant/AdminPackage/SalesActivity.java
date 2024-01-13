package com.softwareengineering.restaurant.AdminPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;

public class SalesActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private RelativeLayout staffs, customers, menu, tables, reports, sales, logout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        drawerLayout = findViewById(R.id.adminDrawerLayout);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        staffs = findViewById(R.id.staffsDrawer);
        customers = findViewById(R.id.customersDrawer);
        menu = findViewById(R.id.menuDrawer);
        tables = findViewById(R.id.tablesDrawer);
        reports = findViewById(R.id.reportsDrawer);
        sales = findViewById(R.id.salesDrawer);
        logout = findViewById(R.id.adminLogoutDrawer);

        setItemBackgroundColors(sales);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText("Sales");

        staffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(staffs);
                redirectActivity(SalesActivity.this, StaffsActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(SalesActivity.this, CustomersActivity.class);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(SalesActivity.this, MenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(SalesActivity.this, TablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(SalesActivity.this, ReportsActivity.class);
            }
        });

        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(sales);
                closeDrawer(drawerLayout);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(SalesActivity.this, LoginActivity.class);
            }
        });

    }

    private void setItemBackgroundColors(RelativeLayout selectedItem) {
        staffs.setBackgroundColor(selectedItem == staffs ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        customers.setBackgroundColor(selectedItem == customers ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        menu.setBackgroundColor(selectedItem == menu ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        tables.setBackgroundColor(selectedItem == tables ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        reports.setBackgroundColor(selectedItem == reports ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        sales.setBackgroundColor(selectedItem == sales ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        logout.setBackgroundColor(selectedItem == logout ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
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