package com.softwareengineering.restaurant.CustomerPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.StaffPackage.StaffsTablesActivity;
import com.softwareengineering.restaurant.TablesAdapter;
import com.softwareengineering.restaurant.TablesModel;
import com.softwareengineering.restaurant.databinding.ActivityCustomersTablesBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class CustomersTablesActivity extends AppCompatActivity {

    private final int idleTableImg = R.drawable.table_top_view;
    private final int inuseTableImg = R.drawable.table_top_view_inuse;
    private final int bookedTableImg = R.drawable.table_top_view_booked;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg, userAvatar;
    private TextView topMenuName, userName;
    private RelativeLayout menu, tables, review, account, logout;
    private ActivityCustomersTablesBinding binding;
    private ArrayList<TablesModel> tablesModelArrayList;
    private ArrayAdapter<TablesModel> tablesModelArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomersTablesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        userName = findViewById(R.id.customersNavName);

        initCurrentUser();
        initToolBar();
        initNavBar();

        // Initialize Tables Layout
        tablesModelArrayList = new ArrayList<>();
        tablesModelArrayAdapter = new TablesAdapter(this, tablesModelArrayList);
        binding.customersTableLayoutGridView.setAdapter(tablesModelArrayAdapter);
        // showTable Function with state

        // Set Click Listener For Table Layout
        binding.customersTableLayoutGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CustomersTablesActivity.this, "Table No. " + (position + 1), Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(CustomersTablesActivity.this, TablesDetails.class);
            }
        });
        realtimeUpdateTableList();
    }

    private void realtimeUpdateTableList(){

        firestore.collection("table").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("Staff table event", "onEvent: " + error.toString());
                    return;
                }
                if (value!=null && !value.isEmpty()){
                    fetchTableList();
                }
            }
        });
    }


    private void fetchTableList(){
        firestore.collection("table").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    tablesModelArrayList.clear();
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        String state = doc.getString("state");
                        int tableImg = declareTableImage(state);

                        if (tableImg == -1) continue; //not showing deleted table

                        tablesModelArrayList.add(new TablesModel(
                                doc.getId(),
                                tableImg
                        ));
                    }
                    tablesModelArrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private int declareTableImage(String state){
        switch (state){
            case "idle": return idleTableImg;
            case "booked": return bookedTableImg;
            case "inuse": return inuseTableImg;
            case "deleted": return -1;
            default: return -1; //as deleted
        }
    }
    private void initNavBar() {
        setItemBackgroundColors(menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(CustomersTablesActivity.this, CustomersMenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                recreate();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(review);
                redirectActivity(CustomersTablesActivity.this, CustomersReviewActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(CustomersTablesActivity.this, CustomersAccountActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(CustomersTablesActivity.this, LoginActivity.class);
            }
        });
    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.topmenu);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText(R.string.tables);
    }

    private void initCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String avatarPhotoUrl = String.valueOf(currentUser.getPhotoUrl());

        Picasso.get().load(avatarPhotoUrl).placeholder(R.drawable.default_user).into(userAvatar);

        if (currentUser.getDisplayName() != null) {
            userName.setText(currentUser.getDisplayName());
        }
        else {
            userName.setText(R.string.name);
        }
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