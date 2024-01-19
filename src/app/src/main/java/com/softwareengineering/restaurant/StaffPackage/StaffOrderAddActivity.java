package com.softwareengineering.restaurant.StaffPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.CustomerPackage.FoodAdapter;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.ItemClasses.MenuItem;
import com.softwareengineering.restaurant.ItemClasses.OrderItem;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffOrderAddActivity extends AppCompatActivity {
    private GridView staffsOrderAddGV;
    private Button staffsOrderAddConfirmBtn;
    private ImageView topMenuImg;
    private TextView topMenuName;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private List<MenuItem> menuItems = new ArrayList<>();
    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItemsHolder = new ArrayList<>();
    private EditText searchMenu;
    private LinearLayout saladButton, pizzaButton, drinkButton, dessertButton, pastaButton, burgerButton, otherButton;

    private StaffsMenuActivity.FILTER_TYPE g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;

    private static final ArrayList<HashMap<String,Object>> menuItemHashMap[] = new ArrayList[1];


    //adding data handler
    private static ArrayList<String> nameList = new ArrayList<>();
    private static ArrayList<Long> quantityList = new ArrayList<>();
    private static ArrayList<Long> priceList = new ArrayList<>();

    private String tableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_order_add);

        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        staffsOrderAddConfirmBtn = findViewById(R.id.staffsOrderAddConfirmButton);
        staffsOrderAddGV = findViewById(R.id.staffsOrderAddGridView);
        searchMenu = findViewById(R.id.search_menu);

        // Filter button setup here:
        saladButton = findViewById(R.id.saladFilter);
        drinkButton = findViewById(R.id.drinkFilter);
        dessertButton = findViewById(R.id.dessertFilter);
        pizzaButton = findViewById(R.id.pizzaFilter);
        pastaButton = findViewById(R.id.pastaFilter);
        burgerButton = findViewById(R.id.burgerFilter);
        otherButton = findViewById(R.id.otherFilter);

        //getTableId
        tableId = getIntent().getStringExtra("tableId");


        // Tạo Adapter và thiết lập cho GridView
        menuAdapter = new MenuAdapter(this, menuItems);
        staffsOrderAddGV.setAdapter(menuAdapter);

        menuItemHashMap[0] = new ArrayList<>();
        //Synchronize by event listener
        //first fetch and only fetch for this
        fetchFoodList();

        //ItemHashMaps now have all data


        // Initialize Toolbar
        initToolBar();

        // Handle search menu
        handleSearchMenu();

        //filter click
        saladButton.setOnClickListener(saladClickEvent);
        drinkButton.setOnClickListener(drinkClickEvent);
        dessertButton.setOnClickListener(dessertClickEvent);
        burgerButton.setOnClickListener(burgerClickEvent);
        pizzaButton.setOnClickListener(pizzaClickEvent);
        pastaButton.setOnClickListener(pastaClickEvent);
        otherButton.setOnClickListener(otherClickEvent);

        staffsOrderAddConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffOrderAddActivity.this, StaffOrderActivity.class);
                //fetch data from firestore
                firestore.collection("table").document(tableId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            nameList = (ArrayList<String>) task.getResult().get("foodName");
                            priceList = (ArrayList<Long>) task.getResult().get("foodPrice");
                            quantityList = (ArrayList<Long>) task.getResult().get("quantityList");

                            if (nameList == null || nameList.isEmpty()){
                                nameList = new ArrayList<String>();
                                priceList = new ArrayList<Long>();
                                quantityList = new ArrayList<Long>();
                            }

                            //add data
                            for (HashMap m : menuItemHashMap[0]){
                                MenuItem item = ((MenuItem)m.get("menuitem"));
                                //add anyway don't need q>0, we need the food existed or not

                                int pos = nameList.indexOf(item.getName());
                                //not exists
                                if (pos == -1) {
                                    if (item.getQuantity()>0) {
                                        Log.d("name", item.getName());
                                        nameList.add(item.getName());
                                        priceList.add(item.getPrice());
                                        quantityList.add(item.getQuantity());
                                    }
                                }
                                //exists
                                else {
                                    quantityList.set(pos, quantityList.get(pos)+ item.getQuantity());
                                }

                            }
                            firestore.collection("table").document(tableId).update("foodName", nameList);
                            firestore.collection("table").document(tableId).update("foodPrice", priceList);
                            firestore.collection("table").document(tableId).update("quantityList", quantityList).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                            }
                                        }
                                    });
                        }
                    }
                });
                finish();
            }
        });
    }

    private void handleSearchMenu() {
        searchMenu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchSearchedFoodList(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void fetchFoodList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference foodCollection = db.collection("food");
        menuItems.clear();

        foodCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (Boolean.TRUE.equals(doc.getBoolean("state"))) {
                            Log.d("null_Long", doc.getId().toString());
                            MenuItem menuItem = new MenuItem(
                                    doc.getString("imageURL"),
                                    doc.getString("name"),
                                    doc.getLong("price"),
                                    doc.getString("type"),
                                    0L);
                            menuItems.add(menuItem);
                            menuItemHashMap[0].add(new HashMap<String, Object>() {{
                                put("menuitem", menuItem);
                                put("quantity", menuItem.getQuantity());
                            }});
                        }
                    }
                    menuItemsHolder.clear();
                    //changed UI
                    menuAdapter.notifyDataSetChanged();
                    menuItemsHolder.addAll(menuItems);
                } else {
                    Log.e("CustomersMenuActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void fetchSearchedFoodList(String searchedName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference foodCollection = db.collection("food");
        menuItems.clear();

        foodCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (doc.getBoolean("state") == null)
                            Log.d("null_Long", doc.getId().toString());
                        MenuItem menuItem = new MenuItem(
                                doc.getString("imageURL"),
                                doc.getString("name"),
                                doc.getLong("price"),
                                doc.getString("type"),
                                0L
                        );

                        if (menuItem.getName().toLowerCase().contains(searchedName)
                                || menuItem.getName().toLowerCase().equals(searchedName)) {
                            menuItems.add(menuItem);
                        }
                    }
                    menuItemsHolder.clear();
                    //changed UI
                    menuAdapter.notifyDataSetChanged();
                    menuItemsHolder.addAll(menuItems);
                } else {
                    Log.e("CustomersMenuActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.back);

        topMenuImg.setOnClickListener(v -> finish());

        topMenuName.setText(R.string.menu);
    }

    View.OnClickListener saladClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == StaffsMenuActivity.FILTER_TYPE.SALAD) {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                deselectFilter(saladButton);

            }
            else {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.SALAD;
                filterClickedShowing("Salad");
                changeToggleColor(saladButton);
            }
        }
    };

    View.OnClickListener drinkClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == StaffsMenuActivity.FILTER_TYPE.DRINK) {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                deselectFilter(drinkButton);
            }
            else {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.DRINK;
                filterClickedShowing("Drink");
                changeToggleColor(drinkButton);
            }
        }
    };

    View.OnClickListener dessertClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == StaffsMenuActivity.FILTER_TYPE.DESSERT) {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                deselectFilter(dessertButton);
                return;
            }
            g1_filterType = StaffsMenuActivity.FILTER_TYPE.DESSERT;
            filterClickedShowing("Dessert");
            changeToggleColor(dessertButton);
        }
    };

    View.OnClickListener pastaClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == StaffsMenuActivity.FILTER_TYPE.PASTA) {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                deselectFilter(pastaButton);
                return;
            }
            g1_filterType = StaffsMenuActivity.FILTER_TYPE.PASTA;
            filterClickedShowing("Pasta");
            changeToggleColor(pastaButton);
        }
    };

    View.OnClickListener burgerClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == StaffsMenuActivity.FILTER_TYPE.BURGER) {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                deselectFilter(burgerButton);
                return;
            }
            g1_filterType = StaffsMenuActivity.FILTER_TYPE.BURGER;
            filterClickedShowing("Burger");
            changeToggleColor(burgerButton);
        }
    };

    View.OnClickListener pizzaClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == StaffsMenuActivity.FILTER_TYPE.PIZZA) {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                deselectFilter(pizzaButton);
                return;
            }
            g1_filterType = StaffsMenuActivity.FILTER_TYPE.PIZZA;
            filterClickedShowing("Pizza");
            changeToggleColor(pizzaButton);
        }
    };

    View.OnClickListener otherClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == StaffsMenuActivity.FILTER_TYPE.OTHERS) {
                g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                deselectFilter(otherButton);
                return;
            }
            g1_filterType = StaffsMenuActivity.FILTER_TYPE.OTHERS;
            filterClickedShowing("Other");
            changeToggleColor(otherButton);
        }
    };

    //Filter handler:
    private void filterClickedShowing(String filterValue) {
        if (filterValue != "Other") {
            //Known that foodList is fetched successfully
            ArrayList<HashMap<String, Object>> hashMapItemFilter = menuItemHashMap[0].stream()
                    .filter(x -> ((MenuItem)x.get("menuitem")).getType().equals(filterValue))
                    .collect(Collectors.toCollection(ArrayList::new));

            Log.d("Quantity", "reach");

            ArrayList<MenuItem> menuItemFilter = new ArrayList<MenuItem>();
            for (HashMap m: hashMapItemFilter) {
                menuItemFilter.add((MenuItem) m.get("menuitem"));
            }
            menuAdapter.updateData(menuItemFilter);
            menuAdapter.notifyDataSetChanged();
        } else {
            String[] basicType = {"Salad", "Pasta", "Drink", "Dessert", "Pizza", "Burger"};

            ArrayList<MenuItem> menuItemFilter = menuItemsHolder.stream()
                    .filter(x -> !Arrays.asList(basicType).contains(x.getType()))
                    .collect(Collectors.toCollection(ArrayList::new));

            menuAdapter.updateData(menuItemFilter);
            menuAdapter.notifyDataSetChanged();
        }
    }

    private void realtimeUpdateMenu() {
        firestore.collection("food").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    menuItemsHolder.clear();
                    fetchFoodList(); //FIXME: i can't find out where's foodListHolder value after calling the second level callback
                    Log.d("Drink_reach", String.valueOf(menuItems.size()));

                    // Handle filters
                    switch (g1_filterType) {
                        case DRINK: {
                            g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                            deselectFilter(drinkButton);
                            break;
                        }
                        case PASTA: {
                            g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                            deselectFilter(pastaButton);
                            break;
                        }
                        case SALAD: {
                            g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                            deselectFilter(saladButton);
                            break;
                        }
                        case PIZZA: {
                            g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                            deselectFilter(pizzaButton);
                            break;
                        }
                        case DESSERT: {
                            g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                            deselectFilter(dessertButton);
                            break;
                        }
                        case BURGER: {
                            g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                            deselectFilter(burgerButton);
                            break;
                        }
                        case OTHERS: {
                            g1_filterType = StaffsMenuActivity.FILTER_TYPE.FULL;
                            deselectFilter(otherButton);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        });
    }

    private void changeToggleColor(LinearLayout selectedFilter) {
        int selectedColor = getResources().getColor(R.color.orange);
        int deselectedColor = getResources().getColor(R.color.white);

        saladButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == saladButton ? selectedColor : deselectedColor));
        drinkButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == drinkButton ? selectedColor : deselectedColor));
        pizzaButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == pizzaButton ? selectedColor : deselectedColor));
        dessertButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == dessertButton ? selectedColor : deselectedColor));
        pastaButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == pastaButton ? selectedColor : deselectedColor));
        burgerButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == burgerButton ? selectedColor : deselectedColor));
        otherButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == otherButton ? selectedColor : deselectedColor));
    }

    private void deselectFilter(LinearLayout selectedFilter) {
        int deselectedColor = getResources().getColor(R.color.white);
        selectedFilter.setBackgroundTintList(ColorStateList.valueOf(deselectedColor));

        menuItems.clear();
        menuItems.addAll(menuItemsHolder);
        menuAdapter.notifyDataSetChanged();
    }

}