package com.softwareengineering.restaurant.CustomerPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.StaffPackage.StaffsAccountActivity;
import com.softwareengineering.restaurant.StaffPackage.StaffsCustomersActivity;
import com.softwareengineering.restaurant.StaffPackage.StaffsMenuActivity;
import com.softwareengineering.restaurant.StaffPackage.StaffsPaymentActivity;
import com.softwareengineering.restaurant.StaffPackage.StaffsReportsActivity;
import com.softwareengineering.restaurant.StaffPackage.StaffsTablesActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomersMenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg, userAvatar;
    private TextView topMenuName, userName;
    private RelativeLayout menu, tables, review, account, logout;
    private GridLayout list_menu;
    private List<Food> foodList = new ArrayList<>();
    private FoodAdapter foodAdapter;
    private GridView gridView;
    private LinearLayout salad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_menu);

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
        gridView = findViewById(R.id.list_menu);

        foodAdapter = new FoodAdapter(this, foodList);
        gridView.setAdapter(foodAdapter);
        getFoodListFromFirestore();

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

        setItemBackgroundColors(menu);

        topMenuImg.setImageResource(R.drawable.topmenu);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText(R.string.menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                recreate();
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(CustomersMenuActivity.this, CustomersTablesActivity.class);
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(review);
                redirectActivity(CustomersMenuActivity.this, CustomersReviewActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(CustomersMenuActivity.this, CustomersAccountActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(CustomersMenuActivity.this, LoginActivity.class);
            }
        });

    }

    private void getFoodListFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference foodCollection = db.collection("food");

        foodCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        if (doc.getBoolean("state") == null) Log.d("null_Long", doc.getId().toString());
                        Food food = new Food(
                                doc.getString("imageRef"),
                                doc.getString("imageURL"),
                                doc.getString("name"),
                                doc.getLong("price"),
                                Boolean.TRUE.equals(doc.getBoolean("state")),
                                doc.getString("type")
                        );
                        foodList.add(food);
                    }
                    foodAdapter.notifyDataSetChanged();
                }
                else {
                    Log.e("CustomersMenuActivity", "Error getting documents: ", task.getException());
                }
            }
        });
//        foodCollection.whereEqualTo("type","burger").get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                            Food foodItem = document.toObject(Food.class);
//                            foodItem.setState(Boolean.TRUE.equals(document.getBoolean("state")));
//                            foodList.add(foodItem);
//                        }
//
//                    }
//                });
    }

    private void setupGridLayout() {
        GridView gridView = findViewById(R.id.list_menu);
        FoodAdapter foodAdapter = new FoodAdapter(this, foodList);
        gridView.setAdapter(foodAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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