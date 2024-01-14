package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.softwareengineering.restaurant.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TableDetailBooked extends AppCompatActivity {

    private TextView idOnImage, uname, phone, idText, bookedDate, bookedTime;
    private Button toInUse, toIdle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_detail_booked);

        idOnImage = (TextView) findViewById(R.id.table_booked_id);
        idText = (TextView) findViewById(R.id.table_booked_id2);
        uname = (TextView) findViewById(R.id.table_booked_username);
        phone = (TextView) findViewById(R.id.table_booked_phoneNumber);
        bookedDate = (TextView) findViewById(R.id.table_booked_date);
        bookedTime = (TextView) findViewById(R.id.table_booked_timerange);

        toInUse = (Button) findViewById(R.id.table_booked_toInUseButton);
        toIdle = (Button) findViewById(R.id.table_booked_cancelButton); //toIdle, cancel book, not quit

        setDateToCurrentTime();
        getDataFromPreviousIntent();
    }

    private void setDateToCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date timeNow = Calendar.getInstance().getTime();
        bookedDate.setText(dateFormat.format(timeNow));
    }

    private void getDataFromPreviousIntent(){
        String[] datas = getIntent().getStringArrayExtra("data");
        if (datas!= null) {
            Log.d("", "getDataFromPreviousIntent: " + datas[0] + " " + datas[1] + " " + datas[2]);
        }
    }
}