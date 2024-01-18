package com.softwareengineering.restaurant.StaffPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.softwareengineering.restaurant.ItemClasses.OrderItem;
import com.softwareengineering.restaurant.ItemClasses.StaffOrderItem;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Cache;

public class StaffOrderActivity extends AppCompatActivity {
    private TextView topMenuName;
    private ListView listView;
    private StaffOrderAdapter adapter;
    private List<StaffOrderItem> orderItems;

    private TextView name, tableId, date, total;
    private Button addOrder;

    private final String final_tableID[] = new String[1];
    private final String final_customerId[] = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_order);

        topMenuName = findViewById(R.id.topMenuName);
        addOrder = findViewById(R.id.staff_order_addButton);
        listView = findViewById(R.id.listView);

        name = (TextView) findViewById(R.id.staff_order_name);
        date = (TextView) findViewById(R.id.staff_order_date);
        tableId = (TextView) findViewById(R.id.staff_order_numberId);
        total = (TextView) findViewById(R.id.staff_order_total);

        topMenuName.setText("Order");


        getDataFromPreviousIntent();
        realtimeUpdateOrderedList();
        setDataForTextView();

        // Khởi tạo danh sách mẫu (bạn có thể thay thế bằng dữ liệu thực tế)
        orderItems = new ArrayList<>();
        // Khởi tạo Adapter
        adapter = new StaffOrderAdapter(this, orderItems);
        // Kết nối ListView với Adapter
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("listFoodSelected")) {
            List<OrderItem> listFoodSelected = intent.getParcelableArrayListExtra("listFoodSelected");

            // Thêm các món đã chọn vào danh sách orderItems
            for (OrderItem selected : listFoodSelected) {
                StaffOrderItem staffOrderItem = new StaffOrderItem(
                        R.drawable.circle_background,
                        selected.getNameFood(),
                        selected.getPrice(),
                        selected.getQuantity());

                Toast.makeText(StaffOrderActivity.this, selected.getNameFood(), Toast.LENGTH_SHORT).show();
                orderItems.add(staffOrderItem);
            }

            // Cập nhật giao diện
            adapter.notifyDataSetChanged();
        }

        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffOrderActivity.this, StaffOrderAddActivity.class);
                intent.putExtra("tableId", final_tableID[0]);
                startActivity(intent);

            }
        });
    }

    private void setDataForTextView(){
        tableId.setText(final_tableID[0]);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date.setText(dateFormat.format(Calendar.getInstance().getTime()));

        FirebaseFirestore.getInstance().collection("users").document(final_customerId[0]).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            name.setText(task.getResult().getString("name"));
                        }
                    }
                });
    }
    private void getDataFromPreviousIntent(){
        String data[] = getIntent().getStringArrayExtra("data");
        if (data!= null){
            final_customerId[0] = data[0];
            final_tableID[0] = data[1];
        }
    }
    private void realtimeUpdateOrderedList(){
        FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()){
                    fetchOrderedList();
                }
            }
        });
    }

    private void fetchOrderedList(){
        FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                orderItems.clear();
                ArrayList<String> foodNameList = (ArrayList<String>) task.getResult().get("foodName");
                ArrayList<Long> quantityList = (ArrayList<Long>) task.getResult().get("quantityList");
                ArrayList<Long> foodPriceList = (ArrayList<Long>) task.getResult().get("foodPrice");
                //Empty for empty
                if (foodNameList == null || quantityList == null || foodPriceList == null) {
                    total.setText("0");
                    return;
                }
                if (foodNameList.isEmpty() || quantityList.isEmpty() || foodPriceList.isEmpty()) {
                    total.setText("0");
                    return;
                }

                Long totalPrice = 0L;
                for (int i = 0; i< foodPriceList.size(); i ++){
                    StaffOrderItem soi = new StaffOrderItem(
                            R.drawable.circle_background,
                            foodNameList.get(i),
                            foodPriceList.get(i),
                            quantityList.get(i));
                    orderItems.add(soi);
                    totalPrice+= foodPriceList.get(i)*quantityList.get(i);
                }

                adapter.notifyDataSetChanged();
                total.setText(totalPrice.toString());
            }
        });

    }
}