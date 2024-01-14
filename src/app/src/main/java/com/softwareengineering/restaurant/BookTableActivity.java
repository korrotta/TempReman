package com.softwareengineering.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BookTableActivity extends AppCompatActivity {

    private ImageView topMenuImg;
    private EditText nameET, phoneET;
    private Spinner timeSpinner;
    private TextView topMenuName, decreasePeopleTV, increasePeopleTV, numPeopleTV;
    private AppCompatButton reserveButton;
    private TextView tableId;

    private final String[] final_tableID = new String[1];
    private final String[] final_time = new String[1];
    private final String[] final_userRole = new String[1];

    //final userData
    private final int[] final_numPeople = new int[1];
    private final String TAG = "BookTable_Check";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        nameET = findViewById(R.id.nameBookTable);
        phoneET = findViewById(R.id.phoneBookTable);
        timeSpinner = findViewById(R.id.timeSpinnerBookTable);
        decreasePeopleTV = findViewById(R.id.decreasePeopleBookTable);
        increasePeopleTV = findViewById(R.id.increasePeopleBookTable);
        numPeopleTV = findViewById(R.id.numPeopleBookTable);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        tableId = findViewById(R.id.no_tableAvailable);

        reserveButton = findViewById(R.id.reserveButton);

        final_tableID[0] = getIntent().getStringExtra("id");
        if (final_tableID[0] == "" || final_tableID[0] == null){
            return; //>?????
        }

        Log.d(TAG, "onCreate: " + final_tableID[0]);

        initToolBar();
        UISetup();
        fetchUserRole();

        reserveButton.setOnClickListener(reserveTableEvent);
        //TODO: Handle data synchronizing
    }

    private void fetchUserRole(){

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            final_userRole[0] = task.getResult().getString("role");
                        }
                    }
                });
    }
    View.OnClickListener reserveTableEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Booking time handle
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int min = Calendar.getInstance().get(Calendar.MINUTE);

            int hourParsed[] = new int[2];
            hourParsed = timeParser(final_time[0]);

            if (hour >= hourParsed[1])
                Toast.makeText(BookTableActivity.this, "Time has passed, book failed", Toast.LENGTH_SHORT).show();
            else {
                Log.d("Success", final_tableID[0]);
                //Able to book - changing firestore data:
                FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    ArrayList<String> bookedDate = (ArrayList<String>) task.getResult().get("bookedDate");
                                    ArrayList<String> bookedCustomer = (ArrayList<String>)task.getResult().get("customerID");

                                    if (bookedCustomer == null) {
                                        //null -> create new one
                                        bookedCustomer = new ArrayList<String>();

                                    }
                                    if (bookedDate == null){
                                        bookedDate = new ArrayList<String>();
                                    }

                                    //Customer handling
                                    if (final_userRole[0].equals("customer")){
                                        bookedCustomer.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        bookedDate.add(String.valueOf(timeParser(final_time[0])[0]));

                                        DocumentReference doc = FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]);
                                        //update
                                        doc.update("bookedDate", bookedDate);
                                        doc.update("customerID", bookedCustomer);

                                        //Write to firestore booking
                                        bookingDocumentWrite();
                                    }

                                    //Staff booked for outsider
                                    else {
                                        bookedCustomer.add(phoneET.getText().toString());
                                        bookedDate.add(String.valueOf(timeParser(final_time[0])[0]));

                                        DocumentReference doc = FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]);
                                        //update
                                        doc.update("bookedDate", bookedDate);
                                        doc.update("customerID", bookedCustomer);
                                    }
                                    finish();
                                }
                            }
                        });
            }
        }
    };

    private void bookingDocumentWrite(){
        FirebaseFirestore.getInstance().collection("booking").add(
                new HashMap<String, Object>(){{
                    put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    put("name", nameET.getText().toString());
                    put("phonenumber", phoneET.getText().toString());
                    put("number", String.valueOf(final_numPeople[0]));
                    put("timerange", final_time[0]);
                    put("tableid", final_tableID[0]);
                }}
        ).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) Log.d(TAG, "onComplete: " + "DONE");
            }
        });
    }


    private void UISetup() {
        // Get data from user input
        String name;
        String phone;

        //tableID:
        tableId.setText(final_tableID[0]);

        name = nameET.getText().toString();
        phone = phoneET.getText().toString();
        final_time[0] = "0:00";
        final_numPeople[0] = Integer.parseInt(numPeopleTV.getText().toString());
        // Handle data for spinner
        String[] timeString =  {
                "9:00 - 11:00", "11:00 - 13:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00", "19:00 - 21:00"
        };

        ArrayList<String> timeArraylist = new ArrayList<>(Arrays.asList(timeString));
        ArrayAdapter<String> timeArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeArraylist);
        timeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        timeSpinner.setAdapter(timeArrayAdapter);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final_time[0] = timeArrayAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        decreasePeopleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (final_numPeople[0] > 1) {
                    numPeopleTV.setText(String.valueOf(--final_numPeople[0]));
                    final_numPeople[0] = Integer.parseInt(numPeopleTV.getText().toString());
                }
            }
        });
        increasePeopleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (final_numPeople[0] < 8) {
                    numPeopleTV.setText(String.valueOf(++final_numPeople[0]));
                    final_numPeople[0] = Integer.parseInt(numPeopleTV.getText().toString());
                }
            }
        });

    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.topmenu);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topMenuName.setText("Book Table");
    }

    private int[] timeParser(String timeRange){
        String[] carriage = timeRange.split(" ");
        String[] holder=  new String[2];
        holder[0] = carriage[0];
        holder[1] = carriage[2];
        int[] finalInt = new int[2];
        finalInt[0] = Integer.parseInt(holder[0].split(":")[0]);
        finalInt[1] = Integer.parseInt(holder[1].split(":")[0]);
        return finalInt;
    }
}