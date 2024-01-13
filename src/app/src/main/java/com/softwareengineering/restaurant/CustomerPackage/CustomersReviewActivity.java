package com.softwareengineering.restaurant.CustomerPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softwareengineering.restaurant.ItemClasses.Review;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomersReviewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg, userAvatar, add;
    private TextView topMenuName, userName;
    private ListView list_review;
    private RelativeLayout menu, tables, review, account, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_review);

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
        add = findViewById(R.id.add);
        userName = findViewById(R.id.customersNavName);
        list_review = findViewById(R.id.list_review);

        // Dữ liệu giả cho đánh giá (thay thế nó bằng dữ liệu đánh giá thực tế của bạn)
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(R.drawable.default_user, "Nguyễn Văn Mạnh", "Thursday, Aug 21", "Thức ăn tuyệt vời và tươi mới.", "5"));
        // Thêm nhiều đánh giá khác nếu cần...

        // Tạo adapter tùy chỉnh
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, R.layout.customers_list_item_review, reviews);

        // Thiết lập adapter cho ListView
        list_review.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

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

        topMenuName.setText("Review");

        menuBarItemsClick();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersReviewActivity.this, AddReviewActivity.class);
                startActivity(intent);
            }
        });



    }

    private void menuBarItemsClick() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(CustomersReviewActivity.this, CustomersMenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(CustomersReviewActivity.this, CustomersTablesActivity.class);
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(review);
                recreate();
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(CustomersReviewActivity.this, CustomersAccountActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(CustomersReviewActivity.this, LoginActivity.class);
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