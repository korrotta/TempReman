package com.softwareengineering.restaurant.AdminPackage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.databinding.ActivityAddFoodBinding;
import com.softwareengineering.restaurant.databinding.ActivityReportsDetailsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddFoodActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private ActivityAddFoodBinding binding;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private ArrayList<String> foodType;

    private EditText name;
    private EditText cost;
    private EditText ingredient;
    private EditText description;
    private ImageView addImageButton;
    private Button doneBtn;

    //Level 1 callback variable
    private Uri g1_imageUri;
    private String g1_typeChosen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);

        name = findViewById(R.id.adminAddFoodNameET);
        cost = findViewById(R.id.adminAddFoodCostET);
        ingredient = findViewById(R.id.adminAddFoodIngredientsET);
        description = findViewById(R.id.adminAddFoodDescET);

        addImageButton = findViewById(R.id.adminAddFoodImage);

        doneBtn = findViewById(R.id.adminAddFoodDoneButton);

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
                g1_typeChosen = parent.getSelectedItem().toString();
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

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                someActivityResultLauncher.launch(i);
            }
        });

        doneBtn.setOnClickListener(doneClickEvent);
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

    View.OnClickListener doneClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //none empty of all edittext
            //TODO: UI HANDLE ALL THESE EXCEPTION
            if (name.getText().equals("") || name.getText() == null){
                return;
            }

            if (cost.getText().equals("") || cost.getText() == null){
                return;
            }

            if (ingredient.getText().equals("") || ingredient.getText() == null){
                return;
            }

            if (description.getText().equals("") || description.getText() == null){
                return;
            }
            if (g1_imageUri == null) {
                return;
            }

            setupDataToFirestore();

            Log.d("Lv1 callback uri", g1_imageUri.toString());
        }
    };

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData()!= null) {

                    Uri imgUri = result.getData().getData();
                    addImageButton.setImageURI(imgUri);
                    g1_imageUri = imgUri;

                } else if (result.getResultCode() == RESULT_CANCELED) {
                    //Do nothing
                }
            });


    private void setupDataToFirestore(){

        uploadImageToStorage();
    }
    private void uploadImageToStorage(){

        FirebaseStorage.getInstance().getReference().child("foodImg/"+name.getText().toString() +".jpg").putFile(g1_imageUri).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirebaseFirestore.getInstance().collection("food").add(
                                new HashMap<String, Object>() {{
                                    put("name", name.getText().toString());
                                    put("imageRef", taskSnapshot.getStorage().toString());
                                    put("imageURL", taskSnapshot.getStorage().getDownloadUrl().toString());
                                    put("ingredients", ingredient.getText().toString());
                                    put("price", Long.valueOf(cost.getText().toString()));
                                    put("state", Boolean.TRUE);
                                    put("type", g1_typeChosen);
                                    put("description", description.getText().toString());
                                }});
                        finish();
                    }
                });
    }
}