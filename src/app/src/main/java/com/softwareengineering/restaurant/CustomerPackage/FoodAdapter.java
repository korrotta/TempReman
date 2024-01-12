package com.softwareengineering.restaurant.CustomerPackage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends BaseAdapter {
    private Context context;
    private List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.customers_items_menu, parent, false);
        }

        Food food = (Food) getItem(position);
        populateItemView(itemView, food);

        return itemView;
    }

    public void updateData(List<Food> newList) {
        this.foodList.clear();
        this.foodList.addAll(newList);
    }

    private void populateItemView(View itemView, Food food) {
        // Thiết lập dữ liệu cho mỗi ItemView
        ImageView imageView = itemView.findViewById(R.id.image_food);
        TextView nameTextView = itemView.findViewById(R.id.name_food);
        TextView statusTextView = itemView.findViewById(R.id.status);
        TextView priceTextView = itemView.findViewById(R.id.price);
        // Format giá tiền với dấu chấm sau mỗi 3 chữ số
        String formattedPrice = formatPrice(food.getPrice());
        priceTextView.setText(formattedPrice);

        setImageFromReference(food.getImageReference(), food.getName(), imageView);
//
//        // Kích thước cố định (ví dụ: 200x200 pixels)
//        int targetWidth = 200;
//        int targetHeight = 200;
//
//        int radius = 10;
//
//        RequestOptions requestOptions = new RequestOptions()
//                .transforms(new CenterCrop(), new RoundedCorners(radius))
//                .override(targetWidth, targetHeight);
//
//        Glide.with(context)
//                .load(food.getImageUrl())
//                .apply(requestOptions)
//                .into(imageView);
//
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Giả sử isStatusTrue là biến boolean kiểm tra điều kiện
        boolean isStatusTrue = food.getStatus();
        statusTextView.setText(isStatusTrue ? "Sale" : "Stop sale");

        TextView statusButton = itemView.findViewById(R.id.status);

        if (isStatusTrue) {
            // Nếu là true, đặt màu nền xanh lá cây
            statusButton.setBackgroundResource(R.drawable.custom_button_status_green);
        } else {
            // Nếu là false, đặt màu nền đỏ
            statusButton.setBackgroundResource(R.drawable.custom_button_status_red);
        }

        nameTextView.setText(food.getName());
        statusButton.setText(isStatusTrue ? "Sale" : "Stop sale");

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailFoodActivity.class);
                intent.putExtra("foodName", food.getName());
                intent.putExtra("foodImageUrl", food.getImageUrl());
                intent.putExtra("foodStatus", food.getStatus());
                intent.putExtra("foodPrice", String.valueOf(food.getPrice()));
                intent.putExtra("foodType", String.valueOf(food.getType()));
                context.startActivity(intent);
            }
        });
    }

    private String formatPrice(double price) {
        // Sử dụng NumberFormat để định dạng số theo định dạng tiền tệ
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price);
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
        int targetWidth = 200;
        int targetHeight = 200;

        int radius = 10;

        RequestOptions requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(radius))
                .override(targetWidth, targetHeight);

        Glide.with(context)
                .load(img)
                .apply(requestOptions)
                .into(foodImgView);

        foodImgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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



