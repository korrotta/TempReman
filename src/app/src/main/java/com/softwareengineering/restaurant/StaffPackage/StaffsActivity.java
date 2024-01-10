package com.softwareengineering.restaurant.StaffPackage;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softwareengineering.restaurant.AdminPackage.AccountActivity;
import com.softwareengineering.restaurant.AdminPackage.AddStaffsActivity;
import com.softwareengineering.restaurant.AdminPackage.CustomersActivity;
import com.softwareengineering.restaurant.AdminPackage.MenuActivity;
import com.softwareengineering.restaurant.AdminPackage.ReportsActivity;
import com.softwareengineering.restaurant.AdminPackage.SalesActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.Staffs;
import com.softwareengineering.restaurant.StaffsAdapter;
import com.softwareengineering.restaurant.AdminPackage.StaffsDetails;
import com.softwareengineering.restaurant.AdminPackage.TablesActivity;
import com.softwareengineering.restaurant.databinding.ActivityStaffsBinding;

import java.util.ArrayList;

public class StaffsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private RelativeLayout staffs, customers, menu, tables, reports, sales, account;
    private ActivityStaffsBinding binding;
    private LinearLayout addStaffs;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffsBinding.inflate(getLayoutInflater());
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
        account = findViewById(R.id.accountDrawer);
        addStaffs = findViewById(R.id.adminStaffsAdd);

        setItemBackgroundColors(staffs);

        // Set data for Staffs list
        // TODO: Need to get all staff with roles in firestore database
        String[] staffsName = {
                "Alpha", "Beta", "Charlie", "Delta"
        };

        String[] staffsRole = {
                "Waiter", "Cook", "Cashier", "Janitor"
        };

        String[] staffsEmail = {
                "alpha@12345.com", "beta@12345.com", "charlie@12345.com", "delta@12345.com"
        };

        String[] staffsGender = {
                "Male", "Female"
        };

        String[] staffsPhone = {
                "0123456789"
        };

        String[] staffsUsername = {
                "Default"
        };

        // Initialize Staffs list
        ArrayList<Staffs> staffsArrayList = new ArrayList<>();

        for (int i = 0; i < staffsName.length; i++) {

            Staffs tempStaff = new Staffs(staffsName[i], staffsEmail[i], staffsPhone[0], staffsGender[i % 2], staffsRole[i], staffsUsername[0]);
            staffsArrayList.add(tempStaff);

        }

        StaffsAdapter staffsAdapter = new StaffsAdapter(StaffsActivity.this, staffsArrayList);

        binding.staffsListView.setAdapter(staffsAdapter);
        binding.staffsListView.setClickable(true);
        binding.staffsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StaffsActivity.this, StaffsDetails.class);
                intent.putExtra("staffs", staffsArrayList.get(position));
                startActivity(intent);
            }
        });

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText("Staffs List");

        staffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(staffs);
                recreate();
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(StaffsActivity.this, CustomersActivity.class);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(StaffsActivity.this, MenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(StaffsActivity.this, TablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(StaffsActivity.this, ReportsActivity.class);
            }
        });

        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(sales);
                redirectActivity(StaffsActivity.this, SalesActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(StaffsActivity.this, AccountActivity.class);
            }
        });

        // Handle Add Staffs
        addStaffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffsActivity.this, AddStaffsActivity.class);
                startActivity(intent);
            }
        });

        // Handle new created staff account
        Staffs newStaffs = getIntent().getParcelableExtra("newStaffs");
        if (newStaffs != null) {
            staffsArrayList.add(newStaffs);
            staffsAdapter.notifyDataSetChanged();
            binding.staffsListView.setAdapter(staffsAdapter);
            binding.staffsListView.setClickable(true);
        }

        staffsAdapter.notifyDataSetChanged();
    }

    private void setItemBackgroundColors(RelativeLayout selectedItem) {
        staffs.setBackgroundColor(selectedItem == staffs ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        customers.setBackgroundColor(selectedItem == customers ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        menu.setBackgroundColor(selectedItem == menu ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        tables.setBackgroundColor(selectedItem == tables ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        reports.setBackgroundColor(selectedItem == reports ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        sales.setBackgroundColor(selectedItem == sales ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        account.setBackgroundColor(selectedItem == account ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
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