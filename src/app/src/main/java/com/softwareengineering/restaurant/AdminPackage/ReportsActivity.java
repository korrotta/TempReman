package com.softwareengineering.restaurant.AdminPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.softwareengineering.restaurant.ItemClasses.Reports;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.ReportsAdapter;
import com.softwareengineering.restaurant.databinding.ActivityReportsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ReportsActivity extends AppCompatActivity {

    private ActivityReportsBinding binding;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private RelativeLayout staffs, customers, menu, tables, reports, sales, logout;

    private ArrayList<Reports> reportsArrayList;
    ReportsAdapter reportsAdapter;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        setItemBackgroundColors(reports);


        reportsArrayList = new ArrayList<>();
        reportsAdapter = new ReportsAdapter(ReportsActivity.this, reportsArrayList);

        firestore.collection("reports").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        String staffID = doc.getString("staffID");
                        String title = doc.getString("title");
                        Date date = doc.getDate("date");
                        String rpContent = doc.getId();
                        boolean isRead = Boolean.TRUE.equals(doc.getBoolean("isRead"));

                        Reports rp = new Reports(title, staffID, rpContent, date, isRead);
                        reportsArrayList.add(rp);
                    }
                    // Sort Reports by Date
                    Collections.sort(reportsArrayList);
                    reportsAdapter.notifyDataSetChanged();
                }
            }
        });

        binding.adminReportsListView.setAdapter(reportsAdapter);
        binding.adminReportsListView.setClickable(true);
        binding.adminReportsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReportsActivity.this, ReportsDetails.class);
                intent.putExtra("reports", reportsArrayList.get(position));
                startActivity(intent);
                reportsArrayList.get(position).setRead(true);
                reportsAdapter.notifyDataSetChanged();
            }
        });

        menuBarClickEvent();

    }

    private void menuBarClickEvent() {
        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText("Reports");

        staffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(staffs);
                redirectActivity(ReportsActivity.this, StaffsActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(ReportsActivity.this, CustomersActivity.class);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(ReportsActivity.this, MenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(ReportsActivity.this, TablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                recreate();
            }
        });

        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(sales);
                redirectActivity(ReportsActivity.this, SalesActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(ReportsActivity.this, LoginActivity.class);
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