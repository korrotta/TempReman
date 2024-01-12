package com.softwareengineering.restaurant.CustomerPackage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareengineering.restaurant.ItemClasses.Review;
import com.softwareengineering.restaurant.R;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, R.layout.customers_list_item_review, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customers_list_item_review, parent, false);
        }

        Review review = getItem(position);

        ImageView userAvatar = convertView.findViewById(R.id.userAvatar);
        TextView cusName = convertView.findViewById(R.id.cusName);
        TextView date = convertView.findViewById(R.id.date);
        TextView reviewText = convertView.findViewById(R.id.review);
        TextView rate = convertView.findViewById(R.id.rate);

        userAvatar.setImageResource(review.getUserAvatar());
        cusName.setText(review.getCusName());
        Log.d("date", review.getDate().toString());
        date.setText(review.getDate().toString());
        reviewText.setText(review.getReviewText());
        rate.setText(review.getRate());

        return convertView;
    }
}
