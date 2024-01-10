package com.softwareengineering.restaurant.AdminPackage;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
            setImageFromReference(imageReference, viewHolder.imageView);
            viewHolder.textView.setText(foodName.get(position));
        }


        return convertView;
    }

    public void setImageFromReference(StorageReference imgRef, ImageView foodImgView){
        final long ONE_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                foodImgView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                Log.e("Error Imaging", exception.toString());
            }
        });
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

