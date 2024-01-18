package com.softwareengineering.restaurant.AdminPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.softwareengineering.restaurant.SalesAdapter;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SalesActivity extends AppCompatActivity {

    private enum FilterType {
        THIS_WEEK,
        THIS_MONTH,
        THIS_YEAR
    }

    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private RelativeLayout staffs, customers, menu, tables, reports, sales, logout;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private LineChart saleLineChart;
    private ListView salesListView;
    private ArrayList<String> dateArrayList;
    private ArrayList<Long> saleArrayList;
    private SalesAdapter salesAdapter;
    private Spinner salesFilterSpinner;

    // Referenced Week of Month
    private final int referencedWeek = Calendar.WEEK_OF_MONTH;

    // Referenced Month (December)
    private final int referencedMonth = Calendar.DECEMBER;

    // Referenced Year (2023)
    private final int referencedYear = 2023;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        drawerLayout = findViewById(R.id.adminDrawerLayout);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        staffs = findViewById(R.id.staffsDrawer);
        customers = findViewById(R.id.customersDrawer);
        menu = findViewById(R.id.menuDrawer);
        tables = findViewById(R.id.tablesDrawer);
        reports = findViewById(R.id.reportsDrawer);
        sales = findViewById(R.id.salesDrawer);
        logout = findViewById(R.id.adminLogoutDrawer);
        saleLineChart = findViewById(R.id.lineChartSale);
        salesListView = findViewById(R.id.salesListView);
        salesFilterSpinner = findViewById(R.id.saleFilterSpinner);

        initToolBar();
        initNavBar();
        initSpinnerFilter();
    }

    private void initSpinnerFilter() {
        ArrayList<String> spinnerFilterArray = new ArrayList<>();
        spinnerFilterArray.add("This Week");
        spinnerFilterArray.add("This Month");
        spinnerFilterArray.add("This Year");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(SalesActivity.this, android.R.layout.simple_spinner_item, spinnerFilterArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesFilterSpinner.setAdapter(spinnerAdapter);


        salesFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FilterType selectedFilter = getFilterType(position);
                fetchBill(selectedFilter);
                handleSaleChart(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private FilterType getFilterType(int position) {
        switch (position) {
            case 0:
                return FilterType.THIS_WEEK;
            case 1:
                return FilterType.THIS_MONTH;
            case 2:
                return FilterType.THIS_YEAR;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    private void fetchBill(FilterType filterType) {
        dateArrayList = new ArrayList<>();
        saleArrayList = new ArrayList<>();
        firestore.collection("bill").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot document : task.getResult()) {
                        Long value = document.getLong("value");
                        Date date = document.getTimestamp("date").toDate();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formatDate = simpleDateFormat.format(date);

                        // Add a filter for This Week, This Month, This Year
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        switch (filterType) {
                            case THIS_WEEK: {
                                if (weekOfMonth == referencedWeek) {
                                    dateArrayList.add(formatDate);
                                    saleArrayList.add(value);
                                }
                                break;
                            }
                            case THIS_MONTH: {
                                if (month == referencedMonth) {
                                    dateArrayList.add(formatDate);
                                    saleArrayList.add(value);
                                }
                                break;
                            }
                            case THIS_YEAR: {
                                if (year == referencedYear) {
                                    dateArrayList.add(formatDate);
                                    saleArrayList.add(value);
                                }
                                break;
                            }
                        }

                    }

                    // Sort the data by date
                    sortDataByDate();

                    updateSalesListView();

                } else {
                    Log.d("FIRESTORE BILL", "Cannot get Bill collections document");
                }
            }
        });
    }

    private void updateSalesListView() {
        salesAdapter = new SalesAdapter(this, dateArrayList, saleArrayList);
        salesAdapter.notifyDataSetChanged();
        salesListView.setAdapter(salesAdapter);
    }

    private void sortDataByDate() {
        // Create a list of indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < dateArrayList.size(); i++) {
            indices.add(i);
        }

        // Sort the indices based on date
        Collections.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer index1, Integer index2) {
                try {
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateArrayList.get(index1));
                    Date date2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateArrayList.get(index2));
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        // Rearrange the original lists based on sorted indices
        List<String> sortedDates = new ArrayList<>();
        List<Long> sortedSales = new ArrayList<>();

        for (int index : indices) {
            sortedDates.add(dateArrayList.get(index));
            sortedSales.add(saleArrayList.get(index));
        }

        // Update the original lists with sorted data
        dateArrayList.clear();
        dateArrayList.addAll(sortedDates);

        saleArrayList.clear();
        saleArrayList.addAll(sortedSales);
    }

    private void handleSaleChart(FilterType filterType) {
        firestore.collection("bill")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Entry> entries = new ArrayList<>();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

                            for (DocumentSnapshot document : task.getResult()) {
                                Long value = document.getLong("value");
                                Date date = document.getTimestamp("date").toDate();

                                // Use calendar to check week
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);

                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                                int month = calendar.get(Calendar.MONTH);
                                int year = calendar.get(Calendar.YEAR);

                                // Set intervalStart based on the selected filter type
                                switch (filterType) {
                                    case THIS_WEEK: {
                                        if (weekOfMonth == referencedWeek) {
                                            entries.add(new Entry(dayOfWeek, value.floatValue()));
                                        }
                                        break;
                                    }
                                    case THIS_MONTH: {
                                        if (month == referencedMonth) {
                                            entries.add(new Entry(dayOfMonth, value.floatValue()));
                                        }
                                        break;
                                    }
                                    case THIS_YEAR: {
                                        if (year == referencedYear) {
                                            entries.add(new Entry(month, value.floatValue()));
                                        }
                                        break;
                                    }
                                }
                            }

                            // Sort entries
                            entries.sort(new Comparator<Entry>() {
                                @Override
                                public int compare(Entry entry1, Entry entry2) {
                                    return Float.compare(entry1.getX(), entry2.getX());
                                }
                            });

                            // Create a dataset from the entries
                            LineDataSet dataSet = new LineDataSet(entries, "Sales Profit");
                            dataSet.setCircleColor(ContextCompat.getColor(SalesActivity.this, R.color.dark_green));
                            dataSet.setColor(ContextCompat.getColor(SalesActivity.this, R.color.green));
                            dataSet.setCircleRadius(6f);
                            dataSet.setLineWidth(6f);

                            LineData lineData = new LineData(dataSet);

                            // Set the data for the chart
                            saleLineChart.setData(lineData);

                            // Customize chart appearance and behavior as needed
                            Description description = new Description();
                            description.setText("");
                            saleLineChart.setDescription(description);
                            saleLineChart.getAxisRight().setEnabled(false);
                            saleLineChart.getXAxis().setTextSize(11f);
                            saleLineChart.getXAxis().setDrawAxisLine(false);
                            saleLineChart.getAxisLeft().setTextSize(12f);
                            saleLineChart.getLineData().setValueTextSize(12f);
                            saleLineChart.setBorderColor(ContextCompat.getColor(SalesActivity.this, R.color.black));
                            saleLineChart.setBorderWidth(5f);

                            // Refresh the chart to display the data
                            saleLineChart.invalidate();
                        } else {
                            // Handle errors
                            Log.d("FIRESTORE", "Error getting firestore collection");
                        }
                    }
                });
    }

    private void initToolBar() {
        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText(R.string.sales);
    }

    private void initNavBar() {
        setItemBackgroundColors(sales);

        staffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(staffs);
                redirectActivity(SalesActivity.this, StaffsActivity.class);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(SalesActivity.this, CustomersActivity.class);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                redirectActivity(SalesActivity.this, MenuActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(SalesActivity.this, TablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(SalesActivity.this, ReportsActivity.class);
            }
        });

        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(sales);
                closeDrawer(drawerLayout);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(SalesActivity.this, LoginActivity.class);
            }
        });

    }

    private void setItemBackgroundColors(RelativeLayout selectedItem) {
        staffs.setBackgroundColor(selectedItem == staffs ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        customers.setBackgroundColor(selectedItem == customers ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        menu.setBackgroundColor(selectedItem == menu ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        tables.setBackgroundColor(selectedItem == tables ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        reports.setBackgroundColor(selectedItem == reports ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        sales.setBackgroundColor(selectedItem == sales ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
        logout.setBackgroundColor(selectedItem == logout ? ContextCompat.getColor(this, R.color.light_orange) : ContextCompat.getColor(this, R.color.white));
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

}