package com.softwareengineering.restaurant.StaffPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Cache;

public class StaffOrderActivity extends AppCompatActivity {
    private TextView topMenuName;
    private ListView listView;
    private StaffOrderAdapter adapter;
    private List<StaffOrderItem> orderItems;
    private ImageView topMenuImg;
    private TextView name, tableId, date, total;
    private Button addOrder, cancelOrder, paymentOrder;
    private final String final_tableID[] = new String[1];
    private final String final_customerId[] = new String[1];

    private static String customerName = "1!1";

    private static Long priceInBill = 0L;

    private static String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_order);

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        addOrder = findViewById(R.id.staff_order_addButton);
        cancelOrder = findViewById(R.id.staffsOrderCancelBtn);
        paymentOrder = findViewById(R.id.staffsOrderPaymentBtn);
        listView = findViewById(R.id.listView);

        name = (TextView) findViewById(R.id.staff_order_name);
        date = (TextView) findViewById(R.id.staff_order_date);
        tableId = (TextView) findViewById(R.id.staff_order_numberId);
        total = (TextView) findViewById(R.id.staff_order_total);

        topMenuName.setText(R.string.order);
        topMenuImg.setImageResource(R.drawable.back);
        topMenuImg.setOnClickListener(v -> finish());

        getDataFromPreviousIntent();
        realtimeUpdateOrderedList();
        fetchUserRole();
        setDataForTextView();

        // Khởi tạo danh sách mẫu (bạn có thể thay thế bằng dữ liệu thực tế)
        orderItems = new ArrayList<>();
        // Khởi tạo Adapter
        adapter = new StaffOrderAdapter(this, orderItems);
        // Kết nối ListView với Adapter
        listView.setAdapter(adapter);

        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Customer can not add food
                if (userRole.equals("customer")) {
                    Toast.makeText(StaffOrderActivity.this, "Do not have permission for this.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(StaffOrderActivity.this, StaffOrderAddActivity.class);
                intent.putExtra("tableId", final_tableID[0]);
                startActivity(intent);

            }
        });

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cancel Order
                finish();
            }
        });

        paymentOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Payment here

                //get the final timestamp

                //customer can not finish eating themselves
                if (userRole.equals("customer")) {
                    Toast.makeText(StaffOrderActivity.this, "Do not have permission for this.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //put data into firestore bill collection
                FirebaseFirestore.getInstance().collection("bill").add(new HashMap<String, Object>(){{
                    put("customerName", customerName);
                    put("date", Calendar.getInstance().getTime());
                    put("value", priceInBill);
                }}).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d("Completed", task.getResult().getId());

                        FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).update("foodName", new ArrayList<String>());
                        FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).update("foodPrice", new ArrayList<String>());
                        FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).update("quantityList", new ArrayList<String>());
                        FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).update("state", "idle");
                        FirebaseFirestore.getInstance().collection("table").document(final_tableID[0]).update("userinuse", "");
                        finish();
                    }
                });
            }
        });
    }


    private void fetchUserRole(){
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            userRole = task.getResult().getString("role");
                        }
                        else {
                            Log.d("Error", task.getException().toString());
                        }
                    }
                });
    }
    private void setDataForTextView(){

        tableId.setText(final_tableID[0]);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date.setText(dateFormat.format(Calendar.getInstance().getTime()));
    }

    private void getDataFromPreviousIntent(){
        String data[] = getIntent().getStringArrayExtra("data");
        if (data!= null){
            final_customerId[0] = data[0];
            final_tableID[0] = data[1];
            customerName = data[2];
            Log.d("name", customerName);
            name.setText(customerName);
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
                    priceInBill = totalPrice;
                }

                adapter.notifyDataSetChanged();
                total.setText(totalPrice.toString());
            }
        });

    }
}