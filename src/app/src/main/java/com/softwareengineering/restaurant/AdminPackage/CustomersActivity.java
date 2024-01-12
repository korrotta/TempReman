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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.softwareengineering.restaurant.ItemClasses.Customers;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.databinding.ActivityCustomersBinding;

import java.util.ArrayList;

public class CustomersActivity extends AppCompatActivity {

    private ActivityCustomersBinding binding;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private RelativeLayout staffs, customers, menu, tables, reports, sales, logout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ArrayList<Customers> customersArrayList;
    private CustomersAdapter customersAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomersBinding.inflate(getLayoutInflater());
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

        setItemBackgroundColors(customers);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText("Customers List");

        // Set data for Customers list
        // Need to get all customers with roles in firestore database
//        String[] customersName = {
//                "Alpha", "Beta", "Charlie", "Delta"
//        };
//
//        String[] customersEmail = {
//                "alpha@12345.com", "beta@12345.com", "charlie@12345.com", "delta@12345.com"
//        };
//
//        String[] customersGender = {
//                "Male", "Female"
//        };
//
//        String[] customersPhone = {
//                "0123456789"
//        };
//
//        String[] customersUsername = {
//                "Customer"
//        };

        // Initialize Customers list
//        ArrayList<Customers> customersArrayList = new ArrayList<>();
//
//        for (int i = 0; i < customersName.length; i++) {
//
//            Customers tempCustomer = new Customers(customersName[i], customersEmail[i], customersUsername[0], customersPhone[0], customersGender[i % 2]);
//            customersArrayList.add(tempCustomer);
//
//        }
//
//        CustomersAdapter customersAdapter = new CustomersAdapter(CustomersActivity.this, customersArrayList);

        // TODO: Get all customers registered in database

        customersArrayList = new ArrayList<Customers>();

        customersAdapter = new CustomersAdapter(this, customersArrayList);

        binding.customersListView.setAdapter(customersAdapter);
        binding.customersListView.setClickable(true);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("role", "customer").get().addOnCompleteListener(task->{
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc: task.getResult()){
                    addCustomerToList(customersArrayList, doc);
                }
                customersAdapter.notifyDataSetChanged();
            }
        });
//        getCustomerFromFirestore();
        binding.customersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CustomersActivity.this, CustomersDetails.class);
                intent.putExtra("customers", customersArrayList.get(position));
                startActivity(intent);
            }
        });

        staffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(staffs);
                redirectActivity(CustomersActivity.this, StaffsActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                recreate();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(CustomersActivity.this, MenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(CustomersActivity.this, TablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(CustomersActivity.this, ReportsActivity.class);
            }
        });

        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(sales);
                redirectActivity(CustomersActivity.this, SalesActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(CustomersActivity.this, LoginActivity.class);
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

    private void addCustomerToList(ArrayList<Customers> list, QueryDocumentSnapshot c){
        list.add(new Customers(
                c.getString("name"), c.getString("email"), c.getString("username"),
                c.getString("phone"), c.getString("gender")
        ));
    }

}