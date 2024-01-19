package com.softwareengineering.restaurant.StaffPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TableDetailBooked extends AppCompatActivity {

    private TextView idOnImage, uname, phone, idText, bookedDate, bookedTime, topMenuName;
    private Button toInUse, toIdle;
    private ImageView topMenuImg;
    private final String datas[] = new String[3];
    private final boolean[] final_isCustomer = new boolean[1];
    private final String[] final_name = new String[1];
    private final String[] final_phone = new String[1];
    private final String[] final_role = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_detail_booked);

        final_isCustomer[0] = false;
        setUiItemsView();
        initToolBar();
        fetchUserRole();

        setDateToCurrentTime(); //Set time
        getDataFromPreviousIntent(); //datas[] now have all data from the previous intent


        fetchCustomerIdentity(); //get identity and all data. set item data


        toInUse.setOnClickListener(ToInUseClickEvent);
        toIdle.setOnClickListener(RemoveBookedClickEvent);
    }


    //Method n listener

    private void fetchUserRole() {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final_role[0] = task.getResult().getString("role");
                        }
                    }
                });
    }

    View.OnClickListener ToInUseClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("final_role", final_role[0]);
            if (final_role[0].equals("customer")){
                Toast.makeText(TableDetailBooked.this, "Unauthorized!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (final_isCustomer[0]) {
                removeDataInArrayList(FirebaseFirestore.getInstance().collection("table"));
                removeBookingDocument();

                FirebaseFirestore.getInstance().collection("table").document(datas[1]).update("state", "inuse");
            }
            else {
                //Change state to InUse.
                removeDataInArrayList(FirebaseFirestore.getInstance().collection("table"));
                FirebaseFirestore.getInstance().collection("table").document(datas[1]).update("state", "inuse");
            }


            FirebaseFirestore.getInstance().collection("table").document(datas[1]).update("userinuse", datas[0]);
            finish();
        }
    };

    View.OnClickListener RemoveBookedClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            CollectionReference tableReference = FirebaseFirestore.getInstance().collection("table");

            if (final_isCustomer[0]){

                removeDataInArrayList(tableReference);
                removeBookingDocument();
            }
            //handle as staff booked - anonymous
            else {
                removeDataInArrayList(tableReference);
            }

            finish();
        }
    };


    private String rangeToKeyConverter(String timeRange){
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
    private void removeDataInArrayList(CollectionReference tableReference) {
        tableReference.document(datas[1]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<String> bookedDate = (ArrayList<String>) task.getResult().get("bookedDate");
                    ArrayList<String> bookedCustomer = (ArrayList<String>)task.getResult().get("customerID");

                    //in case double handler
                    if (bookedDate.isEmpty() || bookedCustomer.isEmpty() || bookedDate == null || bookedCustomer == null) {
                        Toast.makeText(TableDetailBooked.this, "Someone cancel this, remove failed.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    String key = rangeToKeyConverter(datas[2]);//key from datas
                    //single handle
                    int i = bookedDate.indexOf(key);
                    bookedCustomer.remove(i);
                    bookedDate.remove(i);

                    //update
                    tableReference.document(datas[1]).update("bookedDate", bookedDate);
                    tableReference.document(datas[1]).update("customerID", bookedCustomer);
                }
            }
        });
    }

    private void removeBookingDocument() {
        //update in booking collection also
        FirebaseFirestore.getInstance().collection("booking").whereEqualTo("userid",
                FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        FirebaseFirestore.getInstance().collection("booking").document(doc.getId()).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Log.d("Reach delete", "done");
                                            finish();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void setUiItemsView() {
        idOnImage = (TextView) findViewById(R.id.table_booked_id);
        idText = (TextView) findViewById(R.id.table_booked_id2);
        uname = (TextView) findViewById(R.id.table_booked_username);
        phone = (TextView) findViewById(R.id.table_booked_phoneNumber);
        bookedDate = (TextView) findViewById(R.id.table_booked_date);
        bookedTime = (TextView) findViewById(R.id.table_booked_timerange);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        toInUse = (Button) findViewById(R.id.table_booked_toInUseButton);
        toIdle = (Button) findViewById(R.id.table_booked_cancelButton); //toIdle, cancel book, not quit
    }

    private void initToolBar() {
        topMenuName.setText(R.string.table_detail);
        topMenuImg.setOnClickListener(v -> finish());
        topMenuImg.setImageResource(R.drawable.back);
    }

    private void setDateToCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date timeNow = Calendar.getInstance().getTime();
        bookedDate.setText(dateFormat.format(timeNow));
    }

    private void getDataFromPreviousIntent(){
        String dataHolder[] = getIntent().getStringArrayExtra("data");
        if (dataHolder!= null) {
            datas[0] = dataHolder[0]; // CustomerId / AnonymousPhoneNumber
            datas[1] = dataHolder[1]; // TableId
            datas[2] = dataHolder[2]; // TimeRange
        }
    }

    private void fetchCustomerIdentity() {
        FirebaseFirestore.getInstance().collection("users").document(datas[0]).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //User exists
                            if (task.getResult().exists()){
                                Log.d("Reach", datas[0]);
                                final_isCustomer[0] = true;
                                final_name[0] = task.getResult().getString("name");
                                final_phone[0] = task.getResult().getString("phone");
                                setTrueContentForItems();
                            }
                            else {
                                Log.d("Reach", "Reach");
                                final_isCustomer[0] = false; //user didn't exists
                                final_name[0] = "Anonymous"; //name
                                final_phone[0] = datas[0]; //let phone
                                setTrueContentForItems();
                            }
                        }
                    }
                });
    }

    private void setTrueContentForItems(){
        idOnImage.setText(datas[1]);
        idText.setText(datas[1]);
        uname.setText(final_name[0]);

        phone.setText(final_phone[0]);
        bookedTime.setText(datas[2]);//time range

    }
}