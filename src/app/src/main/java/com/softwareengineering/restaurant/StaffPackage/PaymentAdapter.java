package com.softwareengineering.restaurant.StaffPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.softwareengineering.restaurant.ItemClasses.PaymentFood;
import com.softwareengineering.restaurant.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends ArrayAdapter<PaymentFood> {
    private Context context;
    private List<PaymentFood> data;

    public PaymentAdapter(Context context, List<PaymentFood> data) {
        super(context, R.layout.staff_list_item_payment, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.staff_list_item_payment, parent, false);
        }

        PaymentFood currentItem = data.get(position);

        TextView foodName = listItem.findViewById(R.id.foodName);
        TextView quantity = listItem.findViewById(R.id.quantity);
        TextView price = listItem.findViewById(R.id.price);

        foodName.setText(currentItem.getFoodName());
        quantity.setText(String.valueOf(currentItem.getQuantity()));
//        price.setText(String.valueOf(currentItem.getPrice()));

        // Format giá tiền với dấu chấm sau mỗi 3 chữ số
        String formattedPrice = formatPrice(currentItem.getPrice());
        price.setText(formattedPrice);

        return listItem;
    }

    private String formatPrice(double price) {
        // Sử dụng NumberFormat để định dạng số theo định dạng tiền tệ
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price);
    }
}
