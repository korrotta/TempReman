package com.softwareengineering.restaurant.CustomerPackage;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private TextView topMenuName, userName, customerBookedName, customerBookedTableID, customerBookedDate, customerBookedTime, customerBookedPhone;
    private RelativeLayout menu, tables, review, account, logout;
    private ActivityCustomersTablesBinding binding;
    private ArrayList<TablesModel> tablesModelArrayList;
    private ArrayAdapter<TablesModel> tablesModelArrayAdapter;
    private LinearLayout customerBookedInfo;

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
        customerBookedInfo = findViewById(R.id.customersBookedInfo);
        customerBookedName = findViewById(R.id.customersBookedName);
        customerBookedTableID = findViewById(R.id.customersBookedTableID);
        customerBookedDate = findViewById(R.id.customersBookedDate);
        customerBookedTime = findViewById(R.id.customersBookedTime);
        customerBookedPhone = findViewById(R.id.customersBookedPhone);

        initCurrentUser();
        initToolBar();
        initNavBar();

        // Initialize Tables Layout
        tablesModelArrayList = new ArrayList<>();
        tablesModelArrayAdapter = new TablesAdapter(this, tablesModelArrayList);
        binding.customersTableLayoutGridView.setAdapter(tablesModelArrayAdapter);
        // showTable Function with state

        // TODO: IF CURRENT USER HAS BOOKED A TABLE SHOW THE UI
        boolean flag = true;
        if (flag) {
            // if booked show ui
            customerBookedInfo.setVisibility(View.VISIBLE);
            // Get customer info - name and phone (need to get from the getUserFromFirestore())
            customerBookedDate.setText("01/02/2024");
            customerBookedTableID.setText("02");
            customerBookedTime.setText("19:30");
        }
        else {
            // else hide
            customerBookedInfo.setVisibility(View.INVISIBLE);
        }

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
        // Get currentUser
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        Uri avatarPhotoUrl = currentUser.getPhotoUrl();
        // Avatar Image
        Picasso.get().load(avatarPhotoUrl).placeholder(R.drawable.default_user).into(userAvatar);
        // Get user info from firestore
        getUserInfoFirestore(currentUser.getUid());
    }

    private void getUserInfoFirestore(String uid) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get user info
                    String name, phone;
                    name = document.getString("name");
                    phone = document.getString("phone");

                    // Set user info
                    userName.setText(name);
                    customerBookedName.setText(name);
                    customerBookedPhone.setText(phone);

                } else {
                    // User document not found
                    Log.d("Auth Firestore Database", "No such document");
                }
            } else {
                Log.d("Auth Firestore Database", "get failed with ", task.getException());
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