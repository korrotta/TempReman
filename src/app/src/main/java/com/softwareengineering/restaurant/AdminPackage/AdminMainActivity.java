package com.softwareengineering.restaurant.AdminPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int[] itemImage = {
                R.drawable.staffs, R.drawable.customers, R.drawable.menu, R.drawable.tables,
                R.drawable.reports, R.drawable.sales, R.drawable.account
        };

        String[] itemName = {
                "Staffs", "Customers", "Menu", "Tables",
                "Reports", "Sales", "Account"
        };


        AdminGridAdapter adminGridAdapter = new AdminGridAdapter(AdminMainActivity.this, itemImage, itemName);
        binding.adminMainActGridView.setAdapter(adminGridAdapter);

        binding.adminMainActGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startItemActivity(position);
            }
        });

    }

    private void startItemActivity(int position) {
        Class toStartActivity = null;

        switch (position) {
            case 0: {
                toStartActivity = StaffsActivity.class;
                break;
            }
            case 1: {
                toStartActivity = CustomersActivity.class;
                break;
            }
            case 2: {
                toStartActivity = MenuActivity.class;
                break;
            }
            case 3: {
                toStartActivity = TablesActivity.class;
                break;
            }
            case 4: {
                toStartActivity = ReportsActivity.class;
                break;
            }
            case 5: {
                toStartActivity = SalesActivity.class;
                break;
            }
            case 6: {
                toStartActivity = AccountActivity.class;
                break;
            }
        }

        Intent intent = new Intent(AdminMainActivity.this, toStartActivity);
        startActivity(intent);
    }
}