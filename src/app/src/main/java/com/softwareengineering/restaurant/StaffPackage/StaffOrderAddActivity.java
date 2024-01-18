package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.softwareengineering.restaurant.ItemClasses.MenuItem;
import com.softwareengineering.restaurant.ItemClasses.OrderItem;
import com.softwareengineering.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class StaffOrderAddActivity extends AppCompatActivity {
    private GridView list_menu;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_order_add);

        btn_add = findViewById(R.id.btn_add);
        list_menu = findViewById(R.id.list_menu);

        // Tạo danh sách MenuItem (giả sử bạn đã có dữ liệu)
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.drawable.pasta, "Bucatini All'Amatriciana", 15000));
        menuItems.add(new MenuItem(R.drawable.pasta, "Food 2", 20000));
        // Thêm các MenuItem khác

        // Tạo Adapter và thiết lập cho GridView
        MenuAdapter menuAdapter = new MenuAdapter(this, menuItems);
        list_menu.setAdapter(menuAdapter);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffOrderAddActivity.this, StaffOrderActivity.class);
                // Lấy danh sách món ăn đã chọn từ Adapter
                List<OrderItem> listFoodSelected = menuAdapter.getSelectedItems();

                intent.putParcelableArrayListExtra("listFoodSelected", new ArrayList<>(listFoodSelected));
                startActivity(intent);

                finish();
            }
        });
    }
}