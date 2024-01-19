package com.softwareengineering.restaurant.CustomerPackage;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
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
import com.softwareengineering.restaurant.StaffPackage.TableDetailBooked;
import com.softwareengineering.restaurant.StaffPackage.TableDetailInuse;
import com.softwareengineering.restaurant.TablesAdapter;
import com.softwareengineering.restaurant.TablesModel;
import com.softwareengineering.restaurant.databinding.ActivityCustomersTablesBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


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
    private Spinner timeFilter;

    private final boolean[] final_isBooked = new boolean[1];
    private final String[] final_selectedTime = new String[1];
    private final String[] timeString = {
            "9:00 - 11:00", "11:00 - 13:00", "13:00 - 15:00",
            "15:00 - 17:00", "17:00 - 19:00", "19:00 - 21:00"
    };
    private final int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

    private final String[] final_bookedId = new String[1];
    private final String[] final_bookedTimeRange = new String[1];

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
        timeFilter = findViewById(R.id.filterTimeTables);

        initCurrentUser();
        initToolBar();
        initNavBar();

        // Initialize Tables Layout
        tablesModelArrayList = new ArrayList<>();
        tablesModelArrayAdapter = new TablesAdapter(this, tablesModelArrayList);
        binding.customersTableLayoutGridView.setAdapter(tablesModelArrayAdapter);
        // showTable Function with state

        final_isBooked[0] = false;
        // TODO: IF CURRENT USER HAS BOOKED A TABLE SHOW THE UI

        listenToDataChange();

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeString);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        timeFilter.setAdapter(timeAdapter);

        int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final_selectedTime[0] = timeSelection(hourNow, true);

        timeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Time range here:
                final_selectedTime[0] = getTimeFromRange(timeString[position]);
                // Handle show table
                fetchTableList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set Click Listener For Table Layout
        binding.customersTableLayoutGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TablesModel t = (TablesModel) tablesModelArrayAdapter.getItem(position);

                //Checking if the table is idle in that range of time? Color check is way faster and more convenient -DONE
                if (t.getImage() == idleTableImg) {
                    if (final_isBooked[0]) {
                        Toast.makeText(CustomersTablesActivity.this, "Can not book 2 tables for a customer!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent i = new Intent(CustomersTablesActivity.this, BookTableActivity.class);

                    //FIXME: What to put here?
                    //Booked detail need info of the one who ordered. So fetch firestore data from here, take customer id
                    //What if customer id is anonymous, because customer booked the table for him?
                    //Set all data to default data? Where's the phone number? Then what if that one calling again to cancel it?
                    //How to find out? No name search, no phone number search.
                    //Or, we can set the id by the phone number he entered. Then show it like a phone number, default name
                    //Okay done
                    //
                    //Then what to put here? Nothing, this is in idle tho. But what to put in bookedActivity? This one.
                    //Further develop, but absolutely must be this one

                    i.putExtra("id", t.getId());
                    i.putExtra("time_range", final_selectedTime[0]);
                    startActivity(i);
                } else if (t.getImage() == bookedTableImg) {
                    if (final_isBooked[0] && (!final_bookedId[0].equals(t.getId()) || !getTimeFromRange(final_bookedTimeRange[0]).equals(final_selectedTime[0]))) {
                        Toast.makeText(CustomersTablesActivity.this, "Not your table to view", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!final_isBooked[0]) {
                        Toast.makeText(CustomersTablesActivity.this, "Not your table to view", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseFirestore.getInstance().collection("table").document(t.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<String> bookedDate = (ArrayList<String>) task.getResult().get("bookedDate");
                                ArrayList<String> bookedCustomer = (ArrayList<String>) task.getResult().get("customerID");

                                String dataToTransfer = bookedCustomer.get(bookedDate.indexOf(final_selectedTime[0]));

                                Log.d("Test data", dataToTransfer);
                                String id = t.getId();
                                String timeRange = timeString[Integer.parseInt(final_selectedTime[0]) / 2 - 4];

                                String[] data = new String[3];
                                data[0] = dataToTransfer;
                                data[1] = id;
                                data[2] = timeRange;

                                Intent i = new Intent(CustomersTablesActivity.this, TableDetailBooked.class);
                                i.putExtra("data", data);
                                startActivity(i);
                            }
                        }
                    });

                }

                //Handle table inuse
                else if (t.getImage() == inuseTableImg) {
                    //Cannot view someone's table
                    FirebaseFirestore.getInstance().collection("table").document(t.getId()).get().addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().getString("userinuse").equals(mAuth.getCurrentUser().getUid())) {
                                            Toast.makeText(CustomersTablesActivity.this, "Not your table to view", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        //Data sending through intent
                                        Intent i = new Intent(CustomersTablesActivity.this, TableDetailInuse.class);
                                        String[] data = new String[2];
                                        data[0] = mAuth.getCurrentUser().getUid();
                                        data[1] = t.getId();

                                        i.putExtra("data", data);
                                        startActivity(i);
                                    }
                                }
                            });

                }
                // If table is available go to Book Table Activity

                // Else show dialog for booked and in use table
            }
        });
        realtimeUpdateTableList();
    }

    private void listenToDataChange() {
        firestore.collection("booking").whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        //Search have data about table
                        if (!value.isEmpty() && value != null) {
                            for (DocumentChange doc : value.getDocumentChanges()) {
                                Log.d("exists or not", "onComplete: " + "exist");
                                final_isBooked[0] = true;
                                changeBookedView(doc.getDocument());

                                //Booked something here. So fetch the booked table.
                                final_bookedId[0] = doc.getDocument().getString("tableid");
                                final_bookedTimeRange[0] = doc.getDocument().getString("timerange");
                            }
                        }

                        //Search no data
                        else {
                            Log.d("Reach", "Reached remove for sure wtf");
                            final_isBooked[0] = false;
                            changeBookedView(null);
                            Log.d("exists or not", "not exists");
                        }
                    }
                });
    }

    private void changeBookedView(DocumentSnapshot doc) {
        if (final_isBooked[0]) {
            customerBookedInfo.setVisibility(View.VISIBLE);
            // Get customer info - name and phone (need to get from the getUserFromFirestore())
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            customerBookedDate.setText(dateFormat.format(date));
            customerBookedTableID.setText(doc.getString("tableid"));
            customerBookedTime.setText(doc.getString("timerange"));
        } else {
            customerBookedInfo.setVisibility(View.GONE);
            Log.d("exists or not", "destroyedView");
        }

    }

    private String getTimeFromRange(String timeRange) {
        switch (timeRange) {
            case "9:00 - 11:00":
                return "9";
            case "11:00 - 13:00":
                return "11";
            case "13:00 - 15:00":
                return "13";
            case "15:00 - 17:00":
                return "15";
            case "17:00 - 19:00":
                return "17";
            case "19:00 - 21:00":
                return "19";
            default:
                return "0";
        }
    }

    private void realtimeUpdateTableList() {

        firestore.collection("table").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Staff table event", "onEvent: " + error.toString());
                    return;
                }
                if (value != null && !value.isEmpty()) {
                    fetchTableList();
                }
            }
        });
    }


    private void fetchTableList() {
        firestore.collection("table").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    tablesModelArrayList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String state = doc.getString("state");

                        //re-check state if it was booked for further base timeRange;
                        state = checkBookedInTimeRange(doc, state);
                        Log.d("State check", "onComplete: " + state);
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

    private String checkBookedInTimeRange(QueryDocumentSnapshot doc, String state) {
        ArrayList<String> bookedDate = (ArrayList<String>) doc.get("bookedDate");
        if (bookedDate != null) {
            for (String hour : bookedDate) {
                if (hour.equals(final_selectedTime[0])) {
                    state = "booked";
                }
            }
        }
        if (state.equals("inuse")) {
            Log.d("Parse", String.valueOf(Integer.parseInt(final_selectedTime[0]) / 2 - 4));
            Log.d("Parse", String.valueOf(Integer.parseInt(timeSelection(hourNow, false)) / 2 - 3));
            if (Integer.parseInt(final_selectedTime[0]) / 2 - 4 > Integer.parseInt(timeSelection(hourNow, false)) / 2 - 3)
                state = "idle";
        }
        return state;
    }

    private int declareTableImage(String state) {
        switch (state) {
            case "idle":
                return idleTableImg;
            case "booked":
                return bookedTableImg;
            case "inuse":
                return inuseTableImg;
            case "deleted":
                return -1;
            default:
                return -1; //as deleted
        }
    }

    private void initNavBar() {
        setItemBackgroundColors(tables);

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

    private String timeSelection(int hourNow, boolean needChange) {
        switch (hourNow) {
            case 21:
            case 22:
            case 23:
            case 24:
            case 0:
            default:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                if (needChange) timeFilter.setSelection(0);

                return "9";
            case 11:
            case 12:
                if (needChange) timeFilter.setSelection(1);
                return "11";
            case 13:
            case 14:
                if (needChange) timeFilter.setSelection(2);
                return "13";
            case 15:
            case 16:
                if (needChange) timeFilter.setSelection(3);
                return "15";
            case 17:
            case 18:
                if (needChange) timeFilter.setSelection(4);
                return "17";
            case 19:
            case 20:
                if (needChange) timeFilter.setSelection(5);
                return "19";

        }
    }
}