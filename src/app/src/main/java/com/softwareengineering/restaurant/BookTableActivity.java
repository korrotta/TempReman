package com.softwareengineering.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class BookTableActivity extends AppCompatActivity {

    private ImageView topMenuImg;
    private EditText nameET, phoneET;
    private Spinner daySpinner, monthSpinner, yearSpinner, timeSpinner;
    private TextView topMenuName, decreasePeopleTV, increasePeopleTV, numPeopleTV;
    private AppCompatButton reserveButton;

    private Button reserveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        nameET = findViewById(R.id.nameBookTable);
        phoneET = findViewById(R.id.phoneBookTable);
        daySpinner = findViewById(R.id.daySpinnerBookTable);
        monthSpinner = findViewById(R.id.monthSpinnerBookTable);
        yearSpinner = findViewById(R.id.yearSpinnerBookTable);
        timeSpinner = findViewById(R.id.timeSpinnerBookTable);
        decreasePeopleTV = findViewById(R.id.decreasePeopleBookTable);
        increasePeopleTV = findViewById(R.id.increasePeopleBookTable);
        numPeopleTV = findViewById(R.id.numPeopleBookTable);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        initToolBar();

    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.topmenu);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //FIXME: WHERE'S THE SPINNER? RESERVE BUTTON?
        //on reserve click
        reserveButton = findViewById(R.id.reserveButton);

        reserveButton.setOnClickListener(reserveButtonClickEvent);
        //


        topMenuName.setText("Book Table");
    }
    View.OnClickListener reserveButtonClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: As staff you can create an anonymous user, only check if the user's chosen date and time range to book is idle
        }
    };
}