package com.softwareengineering.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class BookTableActivity extends AppCompatActivity {

    private ImageView topMenuImg;
    private EditText nameET, phoneET;
    private TextView timeTextView;
    private TextView topMenuName;
    private AppCompatButton reserveButton;
    private TextView tableId;
    private Handler handler;

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
        timeTextView = findViewById(R.id.timeTVBookTable);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        reserveButton = findViewById(R.id.reserveButton);

        tableId = findViewById(R.id.no_tableAvailable);

        reserveButton = findViewById(R.id.reserveButton);

        final_tableID[0] = getIntent().getStringExtra("id");
        if (final_tableID[0] == "" || final_tableID[0] == null){
            return;
        }

        Log.d(TAG, "onCreate: " + final_tableID[0]);

        initToolBar();
        UISetup();
        fetchUserRole();

        reserveButton.setOnClickListener(reserveTableEvent);
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

            // Check phone number field
            if (phoneET.getText().toString().isEmpty()) {
                Toast.makeText(BookTableActivity.this, "Phone number must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            //Booking time handle
            // Fake time
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Calendar.MONTH,Calendar.DATE, 9,30, 0);
            int hour = calendar.get(Calendar.HOUR);

            int[] hourParsed;
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


                                }
                            }
                        });

                showSuccessDialog(BookTableActivity.this::finish);
            }
        }
    };

    private void showSuccessDialog(Runnable onDismissAction) {
        Dialog successDialog = new Dialog(this);
        successDialog.setContentView(R.layout.reserve_success_dialog);
        successDialog.setCancelable(true);

        // Handle close button click
        ImageButton closeButton = successDialog.findViewById(R.id.imageButtonClose);
        closeButton.setOnClickListener(v -> {
            if (successDialog.isShowing()) {
                successDialog.dismiss();
            }
        });

        successDialog.setOnDismissListener(dialog -> onDismissAction.run());
        successDialog.show();
    }


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
        //tableID:
        tableId.setText(final_tableID[0]);

        String time_range = getIntent().getStringExtra("time_range");
        final_time[0] = time_range;
        timeTextView.setText(final_time[0]);
    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.back);

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