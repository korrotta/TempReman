package com.softwareengineering.restaurant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    ImageView avatar;
    TextView textViewUsername, textViewEmail;
    FirebaseAuth auth;
    Button buttonLogout;
    FirebaseUser fbUser;
    FirebaseFirestore fsStorage;
    StorageReference storageReference;
    String userEmail, userName, userPhotoUrl;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        avatar = view.findViewById(R.id.avatar);
        textViewUsername = view.findViewById(R.id.displayUsername);
        textViewEmail = view.findViewById(R.id.displayEmail);
        auth = FirebaseAuth.getInstance();
        buttonLogout = view.findViewById(R.id.logout);
        fbUser = auth.getCurrentUser();
        fsStorage = FirebaseFirestore.getInstance();
        userEmail = fbUser.getEmail();
        userName = fbUser.getDisplayName();
        userPhotoUrl = String.valueOf(fbUser.getPhotoUrl());

        if (fbUser == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        if (TextUtils.isEmpty(userName)) {
            if (TextUtils.isEmpty(userPhotoUrl)) {
                storageReference = FirebaseStorage.getInstance().getReference("userImg/default/defaultUser.png");
                try {
                    File defaultAvatar = File.createTempFile("defaultAvatar", ".png");
                    storageReference.getFile(defaultAvatar)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(defaultAvatar.getAbsolutePath());
                                    avatar.setImageBitmap(bitmap);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to load Avatar Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Picasso.get().load(String.valueOf(fbUser.getPhotoUrl())).into(avatar);
            }
            textViewUsername.setText("Username");
            textViewEmail.setText(String.valueOf(fbUser.getEmail()));
        }
        else {
            Picasso.get().load(String.valueOf(fbUser.getPhotoUrl())).into(avatar);
            textViewUsername.setText(String.valueOf(fbUser.getDisplayName()));
            textViewEmail.setText(String.valueOf(fbUser.getEmail()));
        }

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}