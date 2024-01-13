package com.softwareengineering.restaurant.StaffPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.CustomerPackage.FoodAdapter;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffsMenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private CircleImageView userAvatar;
    private TextView topMenuName, userName;
    private RelativeLayout customers, menu, tables, reports, payment, account, logout;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private List<Food> foodList = new ArrayList<>();
    private FoodAdapter foodAdapter;
    private List<Food> foodListHolder = new ArrayList<>();
    private GridView gridView;

    private LinearLayout saladButton;
    private LinearLayout pizzaButton;
    private LinearLayout drinkButton;
    private LinearLayout dessertButton;
    private LinearLayout pastaButton;
    private LinearLayout burgerButton;

    private TextView statusClick;

    private FILTER_TYPE g1_filterType = FILTER_TYPE.FULL;

    public enum FILTER_TYPE{
        FULL,
        SALAD,
        PASTA,
        PIZZA,
        DESSERT,
        DRINK,
        BURGER,
        OTHERS,
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffs_menu);

        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.staffsDrawerLayout);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        customers = findViewById(R.id.staffsCustomersDrawer);
        menu = findViewById(R.id.staffsMenuDrawer);
        tables = findViewById(R.id.staffsTablesDrawer);
        reports = findViewById(R.id.staffsReportsDrawer);
        payment = findViewById(R.id.staffsPaymentDrawer);
        account = findViewById(R.id.staffsAccountDrawer);
        logout = findViewById(R.id.staffsLogoutDrawer);
        userAvatar = findViewById(R.id.staffsNavAvatar);
        userName = findViewById(R.id.staffsNavName);
        gridView = findViewById(R.id.list_menu);

        //Filter button setup here:
        saladButton = findViewById(R.id.saladFilter);
        drinkButton = findViewById(R.id.drinkFilter);
        dessertButton = findViewById(R.id.dessertFilter);
        pizzaButton = findViewById(R.id.pizzaFilter);
        pastaButton = findViewById(R.id.pastaFilter);
        burgerButton = findViewById(R.id.burgerFilter);

        statusClick = findViewById(R.id.status);

        //TODO: HANDLE ON CLICK OF ALL ABOVE LINEAR LAYOUT: SET BACKGROUND TO STRONGER COLOR OR SOMETHING TO EMPHASIS (?)

        //always showing by foodListHolder
        foodAdapter = new FoodAdapter(this, foodList);
        gridView.setAdapter(foodAdapter);

        //Synchronize by event listener
        realtimeUpdateMenu();

        // Get currentUser
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        Uri avatarPhotoUrl = currentUser.getPhotoUrl();
        // Avatar Image
        Picasso.get().load(avatarPhotoUrl).placeholder(R.drawable.default_user).into(userAvatar);

        // Get user info from firestore
        getUserInfoFirestore(currentUser.getUid());

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

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(StaffsMenuActivity.this, StaffsCustomersActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(StaffsMenuActivity.this, StaffsTablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(StaffsMenuActivity.this, StaffsReportsActivity.class);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(payment);
                redirectActivity(StaffsMenuActivity.this, StaffsPaymentActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(StaffsMenuActivity.this, StaffsAccountActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(StaffsMenuActivity.this, LoginActivity.class);
            }
        });

        //filter click
        saladButton.setOnClickListener(saladClickEvent);
        drinkButton.setOnClickListener(drinkClickEvent);
        dessertButton.setOnClickListener(dessertClickEvent);
        burgerButton.setOnClickListener(burgerClickEvent);
        pizzaButton.setOnClickListener(pizzaClickEvent);
        pastaButton.setOnClickListener(pastaClickEvent);
    }

    private void getUserInfoFirestore(String uid) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get user info
                    String name;
                    name = document.getString("name");

                    // Set user info
                    userName.setText(name);

                } else {
                    // User document not found
                    Log.d("Auth Firestore Database", "No such document");
                }
            } else {
                Log.d("Auth Firestore Database", "get failed with ", task.getException());
            }
        });

    }

    private void fetchFoodList() {
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
                    //changed UI
                    foodAdapter.notifyDataSetChanged();
                    foodListHolder.addAll(foodList);
                }
                else {
                    Log.e("CustomersMenuActivity", "Error getting documents: ", task.getException());
                }
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

    View.OnClickListener saladClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            g1_filterType = FILTER_TYPE.SALAD;
            filterClickedShowing("Salad");
        }
    };

    View.OnClickListener drinkClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            g1_filterType = FILTER_TYPE.DRINK;
            filterClickedShowing("Drink");
        }
    };
    View.OnClickListener dessertClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            g1_filterType = FILTER_TYPE.DESSERT;
            filterClickedShowing("Dessert");
        }
    };

    View.OnClickListener pastaClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            g1_filterType = FILTER_TYPE.PASTA;
            filterClickedShowing("Pasta");
        }
    };

    View.OnClickListener burgerClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            g1_filterType = FILTER_TYPE.BURGER;
            filterClickedShowing("Burger");
        }
    };

    View.OnClickListener pizzaClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            g1_filterType = FILTER_TYPE.PIZZA;
            filterClickedShowing("Pizza");
        }
    };

    private final String TAG = "UserChecker";
    //Filter handler:
    private void filterClickedShowing(String filterValue){

        //Known that foodList is fetched successfully
        ArrayList<Food> foodFilter = foodListHolder.stream()
                .filter(x -> x.getType().equals(filterValue))
                .collect(Collectors.toCollection(ArrayList::new));

        foodAdapter.updateData(foodFilter);
        foodAdapter.notifyDataSetChanged();
    }

    private void realtimeUpdateMenu(){
        firestore.collection("food").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()){
                    fetchFoodList();
                    switch (g1_filterType){
                        case DRINK: {
                            filterClickedShowing("Drink");
                            break;
                        }
                        case PASTA: {
                            filterClickedShowing("Pasta");
                            break;
                        }
                        case SALAD: {
                            filterClickedShowing("Salad");
                            break;
                        }
                        case PIZZA: {
                            filterClickedShowing("Pizza");
                            break;
                        }
                        case DESSERT: {
                            filterClickedShowing("Dessert");
                            break;
                        }
                        case BURGER: {
                            filterClickedShowing("Burger");
                            break;
                        }
                        case OTHERS: {
                            filterClickedShowing("Others");
                            break;
                        }
                        default: break;
                    }

                }
            }
        });
    }
}