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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.BookTableActivity;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.TablesAdapter;
import com.softwareengineering.restaurant.TablesModel;
import com.softwareengineering.restaurant.databinding.ActivityStaffsTablesBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffsTablesActivity extends AppCompatActivity {

    private ActivityStaffsTablesBinding binding;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private CircleImageView userAvatar;
    private TextView topMenuName, userName;
    private RelativeLayout customers, menu, tables, reports, payment, account, logout;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ArrayList<TablesModel> tablesModelArrayList;
    private ArrayAdapter<TablesModel> tablesModelArrayAdapter;
    private ArrayList<String> tablesState;

    //Item images
    private final int idleTableImg = R.drawable.table_top_view;
    private final int inuseTableImg = R.drawable.table_top_view_inuse;
    private final int bookedTableImg = R.drawable.table_top_view_booked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffsTablesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        initCurrentUser();
        initToolBar();
        initNavBar();

        // Initialize Tables Layout
        TablesModel tables = new TablesModel("1", R.drawable.table_top_view);
        tablesModelArrayList = new ArrayList<>();
        tablesModelArrayList.add(tables);
        tablesModelArrayAdapter = new TablesAdapter(this, tablesModelArrayList);
        tablesModelArrayAdapter.notifyDataSetChanged();
        binding.staffsTableLayoutGridView.setAdapter(tablesModelArrayAdapter);
        // showTable Function with state

        realtimeUpdateTableList();
        // Set Click Listener For Table Layout
        binding.staffsTableLayoutGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TablesModel t = (TablesModel) tablesModelArrayAdapter.getItem(position);

                //Checking if the table is idle in that range of time?
                firestore.collection("table").document(t.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.getString("state").equals("idle")) {
                                Intent i = new Intent(StaffsTablesActivity.this, BookTableActivity.class);
                                i.putExtra("id", t.getId());
                                startActivity(i);
                            }
                        }
                        else Log.e("GG", "onComplete: " + task.getException().toString());;
                    }
                });

                //Intent intent = new Intent(StaffsTablesActivity.this, TablesDetails.class);
            }
        });

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
        setItemBackgroundColors(tables);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(StaffsTablesActivity.this, StaffsMenuActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(StaffsTablesActivity.this, StaffsCustomersActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                recreate();
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(StaffsTablesActivity.this, StaffsReportsActivity.class);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(payment);
                redirectActivity(StaffsTablesActivity.this, StaffsPaymentActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(StaffsTablesActivity.this, StaffsAccountActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(StaffsTablesActivity.this, LoginActivity.class);
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
}