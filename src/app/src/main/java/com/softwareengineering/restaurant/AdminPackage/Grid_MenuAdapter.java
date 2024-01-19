package com.softwareengineering.restaurant.AdminPackage;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.R;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public class Grid_MenuAdapter extends BaseAdapter {
    private Context context;
    private Food food;
    private ArrayList<String> foodName;
    private ArrayList<String> foodImgRef;

    public Grid_MenuAdapter(Context context, ArrayList<String> data, ArrayList<String> images) {
        this.context = context;
        this.foodName = data;
        this.foodImgRef = images;
    }

    public void updateData(ArrayList<String> newNameList, ArrayList<String> newImgRef) {
        this.foodName = newNameList;
        this.foodImgRef = newImgRef;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return foodName.size();
    }

    @Override
    public Object getItem(int position) {
        return new Pair<String,String>(foodName.get(position), foodImgRef.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_grid_menu_item, parent, false);
            viewHolder = new MyViewHolder(convertView);
            viewHolder.imageView = convertView.findViewById(R.id.foodThumbnails);
            viewHolder.textView = convertView.findViewById(R.id.foodName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }

        if (foodImgRef.get(position) == null) Log.e("Null pos", String.valueOf(position));
        else {
            StorageReference imageReference = FirebaseStorage.getInstance().getReferenceFromUrl(foodImgRef.get(position));
            setImageFromReference(imageReference, foodName.get(position), viewHolder.imageView);
            viewHolder.textView.setText(foodName.get(position));
        }
        return convertView;
    }

    //To set the image from imageReference in firestore
    public void setImageFromReference(StorageReference imgRef, String imageName, ImageView foodImgView){
        File dataFolder = context.getDataDir();
        File imgFile = new File(dataFolder, imageName+".jpg");

        if (!imgFile.exists()) {
            // Load image from database
            imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // Load with Glide and customize image size and rounded corners
                        RequestOptions requestOptions = new RequestOptions()
                                .centerCrop() // Sử dụng centerCrop để đảm bảo kích thước ảnh được cố định và đầy đủ cả chiều ngang và chiều cao
                                .transforms(new RoundedCorners(20)) // Bo góc 20px
                                .override(300, 300) // Kích thước ảnh
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE);

                        Glide.with(context)
                                .load(task.getResult())
                                .apply(requestOptions)
                                .into(foodImgView);

                        // Download to cache:
                        createCacheFile(imgFile, imgRef);
                    } else {
                        Log.e("Load image Task", "Failed");
                    }
                }
            });
        } else {
            Log.d("Error", imgFile.getAbsolutePath());
            Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            foodImgView.setImageBitmap(img);
        }
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

class MyViewHolder{
    public ImageView imageView;
    public TextView textView;

    public MyViewHolder(View itemView) {
        //super(itemView);
        if (itemView == null) {
            imageView = itemView.findViewById(R.id.foodThumbnails);
            textView = itemView.findViewById(R.id.foodName);
        }
    }
}

