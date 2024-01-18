package com.softwareengineering.restaurant.StaffPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;

import com.softwareengineering.restaurant.ItemClasses.MenuItem;
import com.softwareengineering.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class StaffOrderAddActivity extends AppCompatActivity {
    private GridView list_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_order_add);

        list_menu = findViewById(R.id.list_menu);

        // Tạo danh sách MenuItem (giả sử bạn đã có dữ liệu)
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.drawable.pasta, "Bucatini All'Amatriciana", 15000));
        menuItems.add(new MenuItem(R.drawable.pasta, "Food 2", 20000));
        // Thêm các MenuItem khác

        // Tạo Adapter và thiết lập cho GridView
        MenuAdapter menuAdapter = new MenuAdapter(this, menuItems);
        list_menu.setAdapter(menuAdapter);
    }
}