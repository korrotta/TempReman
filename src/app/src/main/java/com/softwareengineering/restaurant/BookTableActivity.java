package com.softwareengineering.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class BookTableActivity extends AppCompatActivity {

    private ImageView topMenuImg;
    private EditText nameET, phoneET;
    private Spinner timeSpinner;
    private TextView topMenuName, decreasePeopleTV, increasePeopleTV, numPeopleTV;
    private AppCompatButton reserveButton;

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

        initToolBar();

        reserveButton.setOnClickListener(reserveTableEvent);

    }

    View.OnClickListener reserveTableEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleReserveTable();
        }
    };

    private void handleReserveTable() {
        // Get data from user input
        String name;
        String phone;
        final String[] time = new String[1];
        final int[] numPeople = new int[1];

        name = nameET.getText().toString();
        phone = phoneET.getText().toString();
        time[0] = "0:00";
        numPeople[0] = Integer.parseInt(numPeopleTV.getText().toString());
        // Handle data for spinner
        String[] timeString =  {
                "9:00", "10:00", "11:00", "12:00", "13:00", "14:00",
                "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
        };

        ArrayList<String> timeArraylist = new ArrayList<>(Arrays.asList(timeString));
        ArrayAdapter<String> timeArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeArraylist);
        timeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        timeSpinner.setAdapter(timeArrayAdapter);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time[0] = timeArrayAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        decreasePeopleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numPeople[0] > 1) {
                    numPeopleTV.setText(String.valueOf(--numPeople[0]));
                    numPeople[0] = Integer.parseInt(numPeopleTV.getText().toString());
                }
            }
        });
        increasePeopleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numPeople[0] < 8) {
                    numPeopleTV.setText(String.valueOf(++numPeople[0]));
                    numPeople[0] = Integer.parseInt(numPeopleTV.getText().toString());
                }
            }
        });

        // Reserve Table
        // TODO: RESERVE TABLE


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

}