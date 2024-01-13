package com.softwareengineering.restaurant.AdminPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.TablesAdapter;
import com.softwareengineering.restaurant.TablesModel;
import com.softwareengineering.restaurant.databinding.ActivityTablesBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class TablesActivity extends AppCompatActivity {

    final int MAX_TABLE = 18;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private RelativeLayout staffs, customers, menu, tables, reports, sales, logout;
    private ActivityTablesBinding binding;
    private ArrayList<TablesModel> tablesArrayList, deletedTablesArrayList;

    private String chosenID;

    private TablesAdapter tablesAdapter;
    private Dialog addTableDialog, removeTableDialog;
    private boolean selectedDelete = false;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DatabaseReference realtime = FirebaseDatabase.getInstance("https://restaurantmanagement-c201c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTablesBinding.inflate(getLayoutInflater());
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

        setItemBackgroundColors(tables);

        // Refresh Table Layout
        if (tablesArrayList != null) {
            tablesAdapter.notifyDataSetChanged();
        }

        // Initialize Tables list
        tablesArrayList = new ArrayList<>();

        // Initialize Deleted Tables in case of restore
        deletedTablesArrayList = new ArrayList<>();

        // Set data for Tables
        int tablesImg = R.drawable.table_top_view;
//        String[] tablesId = {
//                "1", "2", "3", "4", "5", "6",
//                "7", "8", "9", "10", "11", "12"
//        };
        tablesAdapter = new TablesAdapter(TablesActivity.this, tablesArrayList);

//        for (int i = 0; i < tablesId.length; i++) {
//            TablesModel tempTable = new TablesModel(tablesId[i], tablesImg);
//            tablesArrayList.add(tempTable);
//        }

        binding.adminTableLayoutGridView.setAdapter(tablesAdapter);
        binding.adminTableLayoutGridView.setClickable(true);
        //Show list of table with final adding button

        TablesModel emptyTable = new TablesModel(null, R.drawable.table_top_view_add);
        TablesModel removeTable = new TablesModel(null, R.drawable.table_top_view_delete);
        final TablesModel[] deletedTable = {new TablesModel(null, R.drawable.table_top_view)};

        tablesArrayList.clear();
        tablesAdapter.notifyDataSetChanged();
        showTableListWithLastEmpty(emptyTable, tablesImg);

        binding.adminTableLayoutGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove Table
                // Change item click behavior for adding/removing tables

                //Case : Add a table
                if (tablesAdapter.getItem(position).getImage() == emptyTable.getImage()) {
                    // Add Table
                    addTable(position);
                    addTableDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showTableListWithLastEmpty(emptyTable, tablesImg);
                        }
                    });
                }

                //Case : Chose to remove a table
                else if (tablesAdapter.getItem(position).getImage() == removeTable.getImage()) {
                    // Remove Table
                    removeTable(position);

                    removeTableDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            tablesAdapter.remove(tablesAdapter.getItem(position));
                            tablesAdapter.notifyDataSetChanged();
                            showTableListWithLastEmpty(emptyTable, tablesImg);
                        }
                    });
                }

                //Case: Chose a table to delete (single delete)
                else {
                    // TODO: REMEMBER TO CLEAR OR REMOVE DELETEDTABLEARRAY WHEN RESTORED DELETED TABLE
                    if (selectedDelete) {
                        // Change current table to delete and the previous selected back to normal
                        for (int i = 0; i < tablesArrayList.size(); i++) {
                            if (tablesAdapter.getItem(i).getImage() == R.drawable.table_top_view_delete) {
                                tablesAdapter.getItem(i).setImage(R.drawable.table_top_view);
                                tablesAdapter.getItem(i).setId(String.valueOf(i + 1));
                            }
                        }
                        //add id to global variable
                        chosenID = tablesAdapter.getItem(position).getId();

                        tablesAdapter.getItem(position).setImage(R.drawable.table_top_view_delete);
                        tablesAdapter.getItem(position).setId("");
                        tablesAdapter.notifyDataSetChanged();
                    } else {
                        // Change Table about to be deleted
                        selectedDelete = true;

                        chosenID = tablesAdapter.getItem(position).getId();
                        tablesAdapter.getItem(position).setImage(R.drawable.table_top_view_delete);
                        tablesAdapter.getItem(position).setId("");

                        // Store deletedTable
                        deletedTable[0] = tablesAdapter.getItem(position);
                        deletedTablesArrayList.add(deletedTable[0]);

                        tablesAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText("Tables");

        staffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(staffs);
                redirectActivity(TablesActivity.this, StaffsActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(TablesActivity.this, CustomersActivity.class);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(TablesActivity.this, MenuActivity.class);
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
                redirectActivity(TablesActivity.this, ReportsActivity.class);
            }
        });

        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(sales);
                redirectActivity(TablesActivity.this, SalesActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(TablesActivity.this, LoginActivity.class);
            }
        });

    }

    private void showTableListWithLastEmpty(TablesModel emptyTable, int tablesImg) {
        tablesArrayList.clear();
        firestore.collection("table").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        if (!doc.getString("state").equals("deleted")){

                            // show all table in firestore;
                            TablesModel tm = new TablesModel(doc.getLong("tableId").toString(), tablesImg);
                            tablesArrayList.add(tm);
                            // show emptyTable handle adding function:
                        }
                    }

                    if (tablesArrayList.size() == 0) return;
                    Collections.sort(tablesArrayList, Comparator.comparing((TablesModel::getId)));
                    tablesArrayList.add(emptyTable);
                    tablesAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setViewForTableList(int tablesImg) {
        firestore.collection("table").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        if (!doc.getString("state").equals("deleted")){
                           // showTable();
                            TablesModel tm = new TablesModel(doc.getLong("tableId").toString(), tablesImg);
                            tablesArrayList.add(tm);
                        }
                    }
                    Collections.sort(tablesArrayList, Comparator.comparing(TablesModel::getId));
                    tablesAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void removeTable(int selectedId) {
        removeTableDialog = new Dialog(this);
        removeTableDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        removeTableDialog.setContentView(R.layout.dialog_remove_table);


        Window window = removeTableDialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;

        window.setAttributes(windowAttributes);

        removeTableDialog.setCancelable(false);

        AppCompatButton yesBtn = removeTableDialog.findViewById(R.id.removeTableDialogYesBtn);
        AppCompatButton noBtn = removeTableDialog.findViewById(R.id.removeTableDialogNoBtn);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTableDialog.dismiss();
            }
        });

        String finalId = chosenID;
        Log.d("The chosen id: ", chosenID.toString());
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("table").document(finalId).update("state", "deleted");
                realtime.child("tableList").child(finalId).setValue("deleted");
                removeTableDialog.dismiss();
            }
        });

        removeTableDialog.show();
    }

    private void addTable(int selectedPosition) {
        addTableDialog = new Dialog(this);
        addTableDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addTableDialog.setContentView(R.layout.dialog_add_table);

        Window window = addTableDialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;

        window.setAttributes(windowAttributes);

        addTableDialog.setCancelable(false);

        EditText tableId = addTableDialog.findViewById(R.id.addTableIdEditText);
        Button addBtn = addTableDialog.findViewById(R.id.addTableDialogYesBtn);
        Button cancelBtn = addTableDialog.findViewById(R.id.addTableDialogNoBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTableDialog.dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = String.valueOf(tableId.getText());
                realtime.child("tableList").child("max").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Long max = 0L;
                        max = getMaxTableRecently(task, max);

                        //plus 1 as about to create new one
                        max++;

                        //if wanna reset old table that'd been deleted
                        if (Integer.parseInt(id) < max){
                            realtime.child("tableList").child(id).setValue("idle");
                            firestore.collection("table").document(id).update("state", "idle");
                        }
                        else {
                            addTableAtMaxPos(max);
                        }
                        //Now we have max number of table in the database recently
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failed" ,e.toString());
                    }
                });
                addTableDialog.dismiss();
            }
        });

        addTableDialog.show();

    }

    private void addTableAtMaxPos(Long max) {
        //adding new for real
        Long finalMax = max;
        firestore.collection("table").document(max.toString()).set(
                new HashMap<String, Object>() {{
                    put("bookedDate", null);
                    put("customerID", null);
                    put("foodList", null);
                    put("number", 0);
                    put("quantityList", null);
                    put("state", "idle");
                    put("tableId", finalMax);
                }}
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    realtime.child("tableList").child(finalMax.toString()).setValue("idle");
                    realtime.child("tableList").child("max").setValue(finalMax);
                } else {
                    Log.e("Exception", task.getException().toString());
                }
            }
        });
    }

    @Nullable
    private static Long getMaxTableRecently(@NonNull Task<DataSnapshot> task, Long max) {
        if (!task.isSuccessful()) {
            Log.e("taskFailed", task.getException().toString());
        }
        else {
            try {
                DataSnapshot maxRecent = task.getResult();
                max = (Long) maxRecent.getValue();
                Log.d("max", String.valueOf(max));
            } catch (Exception e) {
                Log.d("max", "onComplete: " + e.toString());
            }
        }
        return max;
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

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
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