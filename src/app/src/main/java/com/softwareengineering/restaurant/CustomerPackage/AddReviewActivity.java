package com.softwareengineering.restaurant.CustomerPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwareengineering.restaurant.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AddReviewActivity extends AppCompatActivity {
    ImageView btn_back;
    private final String TAG = "AddReviewActivity_userCheck";
    Button submit;
    EditText reviewContent;

    ArrayList<ImageView> starRating;

    //Inside listener variable zone:
    int g_rating = -1;
    String g_reviewerName = "";
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);


        starRating = new ArrayList<ImageView>(5);
        starRating.add((ImageView)findViewById(R.id.rate1));
        starRating.add((ImageView)findViewById(R.id.rate2));
        starRating.add((ImageView)findViewById(R.id.rate3));
        starRating.add((ImageView)findViewById(R.id.rate4));
        starRating.add((ImageView)findViewById(R.id.rate5));

        submit = (Button) findViewById(R.id.btn_submit);
        reviewContent = (EditText) findViewById(R.id.reviewContent);

        submit.setOnClickListener(submitButtonClickEvent);

        firestore.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) g_reviewerName = task.getResult().getString("name");
                else Log.e(TAG, "onComplete: Error: ", task.getException());
            }
        });

        handleStarOnClick();
        Log.d(TAG, "onClick: submitButton g_rating value: " + g_rating);
        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(view -> onBackPressed());
    }

    View.OnClickListener submitButtonClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = String.valueOf(reviewContent.getText());

            if (content == null || content.equals("")) {
                //TODO: UI HANDLE EMPTY REVIEW EXCEPTION
                Log.d(TAG, "onClick: submitButton" + "data is null");
                return;
            }
            if (g_rating == -1) {
                //TODO: UI HANDLE ZERO RATING EXCEPTION
                return;
            }
            firestore.collection("reviews").add(new HashMap<String, Object>(){{
                put("content", content);
                put("rate", g_rating+1);
                put("userid", currentUser.getUid());
                put("datetime", Calendar.getInstance().getTime());
                put("name", g_reviewerName);
            }});

            Log.d(TAG, "onClick: "+ content);
            finish();
        }
    };

    private void handleStarOnClick(){
        for (int i=0; i<starRating.size(); i++){
            final int selectedIndex = i;
            starRating.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    g_rating = selectedIndex;
                    for (int j=0; j<=selectedIndex; j++){
                        starRating.get(j).setImageResource(R.drawable.filled_start);
                    }
                    for (int j=selectedIndex+1; j < starRating.size(); j++){
                        starRating.get(j).setImageResource(R.drawable.star);
                    }
                }
            });
        }
    }
}