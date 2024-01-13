package com.softwareengineering.restaurant.CustomerPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.softwareengineering.restaurant.R;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class DetailFoodActivity extends AppCompatActivity {
    ImageView btn_back, image;
    TextView name, status, price, type, des, ingredients;

    String g1_role = "";
    String foodImageRef = "";
    private Context context = DetailFoodActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);

        name = findViewById(R.id.name_food);
        image = findViewById(R.id.image_food);
        type = findViewById(R.id.type_food);
        price = findViewById(R.id.price);

        des = findViewById(R.id.des);
        ingredients = findViewById(R.id.ingredients);

        btn_back = findViewById(R.id.btn_back);
        status = findViewById(R.id.s_statusBar);

        btn_back.setOnClickListener(view -> onBackPressed());
        status.setOnClickListener(changeStatusClickEvent);

        Intent intent = getIntent();

        fetchUserRole();

        if (intent != null) {
            String foodName = intent.getStringExtra("foodName");
            foodImageRef = intent.getStringExtra("foodImageRef");
            boolean foodStatus = intent.getBooleanExtra("foodStatus", false);  // default value is false
            String foodPriceString = intent.getStringExtra("foodPrice");
            double foodPrice = Double.parseDouble(foodPriceString);
            String foodType = intent.getStringExtra("foodType");

            if (foodStatus) {
                // Nếu là true, đặt màu nền xanh lá cây
                status.setBackgroundResource(R.drawable.custom_button_status_green);
            } else {
                // Nếu là false, đặt màu nền đỏ
                status.setBackgroundResource(R.drawable.custom_button_status_red);
            }
            name.setText(foodName);
            status.setText(foodStatus ? "Sale" : "Stop sale");
            price.setText(formatPrice(foodPrice));
            type.setText(foodType);

            getDescAndIngredient(foodImageRef);
            setImageFromReference(FirebaseStorage.getInstance().getReferenceFromUrl(foodImageRef), foodName, image);

        }
    }

    View.OnClickListener changeStatusClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Objects.equals(g1_role, "")) return;
            if (Objects.equals(g1_role, "customer")) return;
            if (Objects.equals(g1_role, "admin")) return;

            //update UI for status changed, only when user role is staff
            status.setBackgroundResource(status.getText().equals("Sale")? R.drawable.custom_button_status_red:  R.drawable.custom_button_status_green);
            status.setText(status.getText().equals("Sale")? "Stop sale":"Sale");

            //firestore update
            FirebaseFirestore.getInstance().collection("food").whereEqualTo("imageRef", foodImageRef).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc: task.getResult().getDocuments()){
                                FirebaseFirestore.getInstance().collection("food").document(doc.getId()).update("state", (status.getText().equals("Sale")? Boolean.TRUE: Boolean.FALSE));
                            }
                        }
                    });

        }
    };

    private void fetchUserRole(){
        FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            g1_role = task.getResult().getString("role");
                        }
                    }
                });
    }
    private String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price) + " VND";
    }

    public void setImageFromReference(StorageReference imgRef, String imageName, ImageView foodImgView){
        File dataFolder = context.getDataDir();
        File imgFile = new File(dataFolder, imageName+".jpg");
        if (imgRef == null) {
            Log.d("imgNameNull", imageName);
            return;
        }
        if (!imgFile.exists()) {
            //Load image from database
            imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        //Load with Glide
//                        Glide.with(context)
//                                .load(task.getResult())
//                                .diskCacheStrategy(DiskCacheStrategy.ALL) // Sử dụng DiskCacheStrategy.ALL để cache hình ảnh ở cả ổ đĩa và bộ nhớ
//                                .into(foodImgView);
                        //Download to cache:
                        createCacheFile(imgFile, imgRef);
                    }
                    else {
                        Log.e("Load image Task", "Failed");
                    }
                }
            });
        }

        //Load image after done
        Log.d("Error", imgFile.getAbsolutePath());
        Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        foodImgView.setImageBitmap(img);
        // Kích thước cố định (ví dụ: 200x200 pixels)
        int targetWidth = 380;
        int targetHeight = 380;
        int radius = 10;

        RequestOptions requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(radius))
                .override(targetWidth, targetHeight);

        Glide.with(this)
                .load(img)
                .apply(requestOptions)
                .into(image);
    }

    //To create a cache file that will make the UI load faster than before
    private void createCacheFile(File file, StorageReference imgRef){
        File dataDir = context.getDataDir();
        try {
            // Ensure the directory exists or create it if it doesn't
            if (!dataDir.exists()) {
                dataDir.mkdirs(); // Create directory and its parent directories if not existing
            }
            Log.d("Error", file.getAbsolutePath());
            file.createNewFile(); // Create

        }catch (IOException | SecurityException e){
            Log.e("Error", e.toString());
        }
        imgRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Image link", taskSnapshot.getTask().getResult().toString());
            }
        });
    }

    private void getDescAndIngredient(String foodImgRef){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("food").whereEqualTo("imageRef", foodImgRef).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        des.setText(doc.getString("description"));
                        ingredients.setText(doc.getString("ingredients"));
                    }
                }
            }
        });
    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}