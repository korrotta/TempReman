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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        ImageView starImage = convertView.findViewById(R.id.starImageView);

        userAvatar.setImageResource(review.getUserAvatar());
        cusName.setText(review.getCusName());
        Log.d("date", review.getDate().toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date.setText( dateFormat.format(review.getDate()));
        reviewText.setText(review.getReviewText());

        // Gọi phương thức updateStarImage để cập nhật hình ngôi sao
        updateStarImage(starImage, Integer.parseInt(review.getRate()));

        return convertView;
    }

    // Phương thức để cập nhật hình ngôi sao dựa trên giá trị rate
    private void updateStarImage(ImageView starImage, int rate) {
        switch (rate) {
            case 1:
                starImage.setImageResource(R.drawable.rating_one);
                break;
            case 2:
                starImage.setImageResource(R.drawable.rating_two);
                break;
            case 3:
                starImage.setImageResource(R.drawable.rating_three);
                break;
            case 4:
                starImage.setImageResource(R.drawable.rating_four);
                break;
            case 5:
                starImage.setImageResource(R.drawable.rating_five);
                break;
            default:
                starImage.setImageResource(R.drawable.rating);
                break;
        }
    }
}
