package com.softwareengineering.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.softwareengineering.restaurant.databinding.ActivityAddFoodBinding;
import com.softwareengineering.restaurant.databinding.ActivityReportsDetailsBinding;

import java.util.ArrayList;

public class AddFoodActivity extends AppCompatActivity {

    private ActivityAddFoodBinding binding;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private ArrayList<String> foodType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        // Initialize toolbar
        toolbarInit();

        // Initialize types of food (Dish or Combo)
        foodType = new ArrayList<>();
        foodType.add("Dish");
        foodType.add("Combo");

        ArrayAdapter<String> foodTypesAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, foodType);
        foodTypesAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

        // binding foodTypeAdapter into spinner
        binding.adminAddFoodTypeSpinner.setAdapter(foodTypesAdapter);
        binding.adminAddFoodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Do something
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something
            }
        });

        // Set Done Button Click Listener
        binding.adminAddFoodDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void toolbarInit() {
        topMenuImg.setImageResource(R.drawable.back);
        topMenuImg.setColorFilter(ContextCompat.getColor(this, R.color.white));
        topMenuName.setText(R.string.add_new_dish);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}