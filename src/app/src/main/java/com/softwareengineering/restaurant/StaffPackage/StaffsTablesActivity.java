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
import android.widget.Spinner;
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
import com.softwareengineering.restaurant.CustomerPackage.DetailFoodActivity;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.TablesAdapter;
import com.softwareengineering.restaurant.TablesModel;
import com.softwareengineering.restaurant.databinding.ActivityStaffsTablesBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

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
    private Spinner timeFilter;

    private final String final_statePerTime[] = new String[1];
    private final int[] final_recentTimeRange = new int[1];

    private final ArrayList<String>[] bookedListInRange = new ArrayList[1];
    //Item images
    private final int idleTableImg = R.drawable.table_top_view;
    private final int inuseTableImg = R.drawable.table_top_view_inuse;
    private final int bookedTableImg = R.drawable.table_top_view_booked;
    private final int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private final String[] timeString = {
            "9:00 - 11:00", "11:00 - 13:00", "13:00 - 15:00",
            "15:00 - 17:00", "17:00 - 19:00", "19:00 - 21:00"
    };


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
        timeFilter = findViewById(R.id.filterTimeTablesStaffs);

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

        // Handle Time Filter Spinner
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeString);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        timeFilter.setAdapter(timeAdapter);


        final_recentTimeRange[0] = timeSelection(hourNow, true);

        //need to change this to base on time - DONE
        realtimeUpdateTableList();

        // Set Click Listener For Table Layout
        binding.staffsTableLayoutGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TablesModel t = (TablesModel) tablesModelArrayAdapter.getItem(position);
                Log.d("Position", "onItemClick: " + t.getId());

                //Checking if the table is idle in that range of time? Color check is way faster and more convenient -DONE
                if (t.getImage() == idleTableImg) {
                    Intent i = new Intent(StaffsTablesActivity.this, BookTableActivity.class);

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
                    startActivity(i);
                }
                else if(t.getImage() == bookedTableImg){
                    FirebaseFirestore.getInstance().collection("table").document(t.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                ArrayList<String> bookedDate = (ArrayList<String>) task.getResult().get("bookedDate");
                                ArrayList<String> bookedCustomer = (ArrayList<String>)task.getResult().get("customerID");

                                //This is customerid
                                String dataToTransfer = bookedCustomer.get(bookedDate.indexOf(getTimeFromRange(timeString[final_recentTimeRange[0]])));
                                Log.d("Test data", dataToTransfer);

                                //Also table id of course
                                String id = t.getId(); //table id
                                //Also time_range
                                String timeRange = timeString[final_recentTimeRange[0]]; //timerange: 9:00 - 11:00

                                String[] data = new String[3];
                                data[0] = dataToTransfer; //Phone number
                                data[1] = id; // Table Id
                                data[2] = timeRange; // Time range
                                //So all is done.

                                Intent i = new Intent(StaffsTablesActivity.this, TableDetailBooked.class);
                                i.putExtra("data", data);
                                //And finally UI switching to bookedTable.
                                startActivity(i);
                            }
                        }
                    });
                }
                else if(t.getImage() == inuseTableImg){
                    FirebaseFirestore.getInstance().collection("table").document(t.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(StaffsTablesActivity.this, TableDetailInuse.class);
                                i.putExtra("id", t.getId());
                                startActivity(i);
                            }
                        }
                    });
                }
            }
        });



        timeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle show table here -DONE
                final_recentTimeRange[0] = position;
                fetchTableList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private int timeSelection(int hourNow, boolean needChange){
        switch (hourNow){
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
                return 0;
            case 11:
            case 12:
                if (needChange)  timeFilter.setSelection(1);
                return 1;
            case 13:
            case 14:
                if (needChange)  timeFilter.setSelection(2);
                return 2;
            case 15:
            case 16:
                if (needChange)  timeFilter.setSelection(3);
                return 3;
            case 17:
            case 18:
                if (needChange)  timeFilter.setSelection(4);
                return 4;
            case 19:
            case 20:
                if (needChange)  timeFilter.setSelection(5);

                return 5;

        }
    }

    private String getTimeFromRange(String timeRange){
        switch (timeRange){
            case "9:00 - 11:00": return "9";
            case "11:00 - 13:00": return "11";
            case "13:00 - 15:00": return "13";
            case "15:00 - 17:00": return "15";
            case "17:00 - 19:00": return "17";
            case "19:00 - 21:00": return "19";
            default: return "0";
        }
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

                        //re-check state if it was booked for further base timeRange;
                        state = checkBookedInTimeRange(doc, state);
                        Log.d("State check", "onComplete: "+state);
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
        if (bookedDate != null){
            Log.d("checkig", "checkBookedInTimeRange: " + String.valueOf(getTimeFromRange(timeString[final_recentTimeRange[0]])));
            for (String hour: bookedDate) {
                if (hour.equals(String.valueOf(getTimeFromRange(timeString[final_recentTimeRange[0]])))){
                    state = "booked";
                }
            }
        }
        if (state.equals("inuse")){
            //If higher one zone
            if (final_recentTimeRange[0] > (timeSelection(hourNow,false)+1)) state = "idle"; // set idle if
        }
        return state;
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