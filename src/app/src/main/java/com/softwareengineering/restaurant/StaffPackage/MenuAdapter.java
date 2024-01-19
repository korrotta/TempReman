package com.softwareengineering.restaurant.StaffPackage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.softwareengineering.restaurant.ItemClasses.MenuItem;
import com.softwareengineering.restaurant.ItemClasses.OrderItem;
import com.softwareengineering.restaurant.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<MenuItem> menuItems;
    private List<OrderItem> selectedItems;

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
        this.selectedItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.staff_items_menu, parent, false);
            holder = new ViewHolder();
            holder.imageFood = convertView.findViewById(R.id.image_food);
            holder.nameFood = convertView.findViewById(R.id.name_food);
            holder.price = convertView.findViewById(R.id.price);
            holder.btnPlus = convertView.findViewById(R.id.btn_plus);
            holder.btnMinus = convertView.findViewById(R.id.btn_minus);
            holder.quantity = convertView.findViewById(R.id.quantity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MenuItem menuItem = menuItems.get(position);

        setImageFromReference(menuItem.getImageReference(), menuItem.getName(), holder.imageFood);

        holder.nameFood.setText(menuItem.getName());
        holder.price.setText(menuItem.getFormattedPrice());
        holder.quantity.setText(String.valueOf(menuItem.getQuantity()));
        showNewLayout(holder, menuItem);

        return convertView;
    }

    public void updateData(List<MenuItem> newList) {
        this.menuItems.clear();
        this.menuItems.addAll(newList);
    }

    private static class ViewHolder {
        ImageView imageFood;
        TextView nameFood;
        TextView price;
        ImageView btnPlus;
        ImageView btnMinus;
        TextView quantity;
    }

    private void showNewLayout(ViewHolder holder, MenuItem menuItem) {
        // Xử lý sự kiện cho các nút trong layout mới
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tăng giá trị quantity
                Long newQuantity = Long.parseLong(holder.quantity.getText().toString()) + 1;
                holder.quantity.setText(String.valueOf(newQuantity));
                menuItem.setQuantity(newQuantity);
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Giảm giá trị quanity, đảm bảo giá trị không âm
                Long newQuantity = Math.max(Long.parseLong(holder.quantity.getText().toString()) - 1, 0);

                holder.quantity.setText(String.valueOf(newQuantity));
                menuItem.setQuantity(newQuantity);
            }
        });

        // Lưu thông tin món ăn đã chọn vào danh sách
        saveSelectedItem(menuItem, Integer.parseInt(holder.quantity.getText().toString()));
    }

    private void saveSelectedItem(MenuItem menuItem, Integer quantity) {
        OrderItem orderItem = new OrderItem(menuItem.getName(), menuItem.getPrice(), quantity);
        selectedItems.add(orderItem);
    }

    public List<OrderItem> getSelectedItems() {
        return selectedItems;
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


