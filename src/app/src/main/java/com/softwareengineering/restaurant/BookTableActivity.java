package com.softwareengineering.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class BookTableActivity extends AppCompatActivity {

    private ImageView topMenuImg;
    private EditText nameET, phoneET;
    private Spinner daySpinner, monthSpinner, yearSpinner, timeSpinner;
    private TextView topMenuName, decreasePeopleTV, increasePeopleTV, numPeopleTV;

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

        topMenuName.setText("Book Table");
    }
}