package com.softwareengineering.restaurant.AdminPackage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.ItemClasses.Staffs;
import com.softwareengineering.restaurant.StaffsAdapter;
import com.softwareengineering.restaurant.databinding.ActivityStaffsBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class StaffsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private RelativeLayout staffs, customers, menu, tables, reports, sales, logout;
    private ActivityStaffsBinding binding;
    private LinearLayout addStaffs;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<Staffs>staffsArrayList;
    private StaffsAdapter staffsAdapter;

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
        logout = findViewById(R.id.adminLogoutDrawer);
        addStaffs = findViewById(R.id.adminStaffsAdd);

        setItemBackgroundColors(staffs);

        // Set data for Staffs list
        // TODO: Need to get all staff with roles in firestore database
//        String[] staffsName = {
//                "Alpha", "Beta", "Charlie", "Delta"
//        };
//
//        String[] staffsRole = {
//                "Waiter", "Cook", "Cashier", "Janitor"
//        };
//
//        String[] staffsEmail = {
//                "alpha@12345.com", "beta@12345.com", "charlie@12345.com", "delta@12345.com"
//        };
//
//        String[] staffsGender = {
//                "Male", "Female"
//        };
//
//        String[] staffsPhone = {
//                "0123456789"
//        };
//
//        String[] staffsUsername = {
//                "Default"
//        };
//
//        // Initialize Staffs list
//        ArrayList<Staffs> staffsArrayList = new ArrayList<>();
//
//        for (int i = 0; i < staffsName.length; i++) {
//
//            Staffs tempStaff = new Staffs(staffsName[i], staffsEmail[i], staffsPhone[0], staffsGender[i % 2], staffsRole[i], staffsUsername[0]);
//            staffsArrayList.add(tempStaff);
//
//        }

        staffsArrayList = new ArrayList<Staffs>();
        staffsAdapter = new StaffsAdapter(StaffsActivity.this, staffsArrayList);

        rebindDataForAdapter();

        binding.staffsListView.setAdapter(staffsAdapter);
        binding.staffsListView.setClickable(true);
        binding.staffsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StaffsActivity.this, StaffsDetails.class);
                intent.putExtra("data", staffsAdapter.getItem(position));
                someActivityResultLauncher.launch(intent);
            }
        });

        menuItemClickEvent();

        // Handle Add Staffs
        addStaffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffsActivity.this, AddStaffsActivity.class);
                someActivityResultLauncher.launch(intent);
            }
        });

//        // Handle new created staff account
//        Staffs newStaffs = getIntent().getParcelableExtra("newStaffs");
//        if (newStaffs != null) {
//            staffsArrayList.add(newStaffs);
//            staffsAdapter.notifyDataSetChanged();
//        }
//        staffsAdapter.notifyDataSetChanged();
    }

    private void menuItemClickEvent() {
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
                closeDrawer(drawerLayout);
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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(StaffsActivity.this, LoginActivity.class);
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

    private void rebindDataForAdapter(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereNotIn("role", Arrays.asList("admin", "customer", "deleted"))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        staffsArrayList.clear();
                        for (QueryDocumentSnapshot doc: task.getResult()){
                            staffsArrayList.add(new Staffs(doc.getString("name"), doc.getString("email"), doc.getString("phone"),
                                    doc.getString("gender"), doc.getString("role")));
                        }
                        staffsAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.d("Intent received", getIntent().toString());
                    Intent resultIntent = result.getData();
                    String returnData = resultIntent.getStringExtra("data");
                    if (returnData.equals("rebind")) {
                        rebindDataForAdapter();
                        Log.d("Reach rebind", "YES");
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    //Do nothing
                }
            });
}