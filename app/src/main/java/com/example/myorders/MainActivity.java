package com.example.myorders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myorders.adapters.OrderAdapter;
import com.example.myorders.models.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Bottom navigation views
    private BottomNavigationView bottomNav;
    private View layoutOrdersPage, layoutHomePage, layoutPaymentsPage, layoutAccountPage;

    // Orders page views
    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> allOrdersList;
    private List<Order> filteredList;

    private TextView tabAll, tabCompleted, tabCancelled, tabBookedAgain;
    private View bannerInfo, btnCloseBanner;
    private EditText etSearch;
    private View btnFilter, btnSort, btnHelp;

    private String currentSelectedTab = "all"; // all, completed, cancelled, booked_again
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind main structure views
        bottomNav = findViewById(R.id.bottom_navigation);
        layoutOrdersPage = findViewById(R.id.layout_orders_page);
        layoutHomePage = findViewById(R.id.layout_home_page);
        layoutPaymentsPage = findViewById(R.id.layout_payments_page);
        layoutAccountPage = findViewById(R.id.layout_account_page);

        // Bind orders page specific views
        rvOrders = findViewById(R.id.rv_orders);
        tabAll = findViewById(R.id.tab_all);
        tabCompleted = findViewById(R.id.tab_completed);
        tabCancelled = findViewById(R.id.tab_cancelled);
        tabBookedAgain = findViewById(R.id.tab_booked_again);
        bannerInfo = findViewById(R.id.banner_info);
        btnCloseBanner = findViewById(R.id.btn_close_banner);
        etSearch = findViewById(R.id.et_search);
        btnFilter = findViewById(R.id.btn_filter);
        btnSort = findViewById(R.id.btn_sort);
        btnHelp = findViewById(R.id.btn_help);

        // Setup bottom navigation listener
        setupBottomNavigation();

        // Initialize orders page data and elements
        allOrdersList = new ArrayList<>();
        filteredList = new ArrayList<>();
        generateMockData();

        // Setup RecyclerView
        adapter = new OrderAdapter(this, filteredList);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        // Initial filter load
        applyFilter();

        // Setup orders page listeners
        setupTabListeners();
        setupBannerListener();
        setupSearchListener();
        setupHelpButton();
        setupFilterSortButtons();
    }

    private void setupBottomNavigation() {
        // Set Orders tab selected by default on startup
        bottomNav.setSelectedItemId(R.id.navigation_orders);
        showPage("orders");

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                showPage("home");
                return true;
            } else if (itemId == R.id.navigation_orders) {
                showPage("orders");
                return true;
            } else if (itemId == R.id.navigation_payments) {
                showPage("payments");
                return true;
            } else if (itemId == R.id.navigation_account) {
                showPage("account");
                return true;
            }
            return false;
        });
    }

    private void showPage(String pageName) {
        // Reset all to GONE
        layoutOrdersPage.setVisibility(View.GONE);
        layoutHomePage.setVisibility(View.GONE);
        layoutPaymentsPage.setVisibility(View.GONE);
        layoutAccountPage.setVisibility(View.GONE);
        btnHelp.setVisibility(View.GONE);

        // Show selected page
        switch (pageName) {
            case "home":
                layoutHomePage.setVisibility(View.VISIBLE);
                break;
            case "orders":
                layoutOrdersPage.setVisibility(View.VISIBLE);
                btnHelp.setVisibility(View.VISIBLE);
                break;
            case "payments":
                layoutPaymentsPage.setVisibility(View.VISIBLE);
                break;
            case "account":
                layoutAccountPage.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void generateMockData() {
        allOrdersList.add(new Order(
                "#ORD12345",
                "Four Wheeler",
                "05 Feb, 4:46 PM",
                "741, Gumanwara",
                "00, Main Rd, Shivaji Nagar, Jhansi, Uttar Pradesh 284001, India",
                229.0,
                "cancelled"
        ));

        allOrdersList.add(new Order(
                "#ORD12346",
                "Four Wheeler",
                "05 Feb, 4:46 PM",
                "741, Gumanwara",
                "00, Main Rd, Shivaji Nagar, Jhansi, Uttar Pradesh 284001, India",
                229.0,
                "cancelled"
        ));

        allOrdersList.add(new Order(
                "#ORD12347",
                "Four Wheeler",
                "05 Feb, 4:46 PM",
                "332, Gumanwara",
                "GC72+GGV, Kamrari, Madhya Pradesh 475661, India",
                1515.0,
                "cancelled"
        ));

        allOrdersList.add(new Order(
                "#ORD12348",
                "Four Wheeler",
                "05 Feb, 4:46 PM",
                "332, Gumanwara",
                "GC72+GGV, Kamrari, Madhya Pradesh 475661, India",
                1634.0,
                "completed"
        ));

        allOrdersList.add(new Order(
                "#ORD12349",
                "Three Wheeler",
                "10 Feb, 1:15 PM",
                "Railway Station, Jhansi",
                "Elite Crossing, Jhansi, Uttar Pradesh 284001, India",
                120.0,
                "completed"
        ));

        allOrdersList.add(new Order(
                "#ORD12350",
                "Four Wheeler",
                "12 Feb, 9:30 AM",
                "Civil Lines, Jhansi",
                "Sadar Bazar, Jhansi, Uttar Pradesh 284001, India",
                350.0,
                "booked_again"
        ));
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab("all"));
        tabCompleted.setOnClickListener(v -> selectTab("completed"));
        tabCancelled.setOnClickListener(v -> selectTab("cancelled"));
        tabBookedAgain.setOnClickListener(v -> selectTab("booked_again"));
    }

    private void selectTab(String tab) {
        currentSelectedTab = tab;

        // Reset backgrounds & colors
        resetTabStyles();

        // Apply new style to selected
        switch (tab) {
            case "all":
                tabAll.setBackgroundResource(R.drawable.bg_tab_selected);
                tabAll.setTextColor(getResources().getColor(R.color.black));
                tabAll.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case "completed":
                tabCompleted.setBackgroundResource(R.drawable.bg_tab_selected);
                tabCompleted.setTextColor(getResources().getColor(R.color.black));
                tabCompleted.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case "cancelled":
                tabCancelled.setBackgroundResource(R.drawable.bg_tab_selected);
                tabCancelled.setTextColor(getResources().getColor(R.color.black));
                tabCancelled.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case "booked_again":
                tabBookedAgain.setBackgroundResource(R.drawable.bg_tab_selected);
                tabBookedAgain.setTextColor(getResources().getColor(R.color.black));
                tabBookedAgain.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
        }

        // Apply filtering
        applyFilter();
    }

    private void resetTabStyles() {
        TextView[] tabs = {tabAll, tabCompleted, tabCancelled, tabBookedAgain};
        for (TextView tab : tabs) {
            tab.setBackgroundResource(R.drawable.bg_tab_unselected);
            tab.setTextColor(getResources().getColor(R.color.grey_text));
            tab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    private void setupBannerListener() {
        btnCloseBanner.setOnClickListener(v -> {
            bannerInfo.setVisibility(View.GONE);
            Toast.makeText(this, "Banner closed", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupHelpButton() {
        btnHelp.setOnClickListener(v -> 
            Toast.makeText(this, "Opening support chat and helpline...", Toast.LENGTH_LONG).show()
        );
    }

    private void setupFilterSortButtons() {
        btnFilter.setOnClickListener(v -> 
            Toast.makeText(this, "Filter Options Clicked", Toast.LENGTH_SHORT).show()
        );

        btnSort.setOnClickListener(this::showSortPopupMenu);
    }

    private void showSortPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().clear();
        popup.getMenu().add(0, 1, 0, "Price: Low to High");
        popup.getMenu().add(0, 2, 1, "Price: High to Low");
        popup.getMenu().add(0, 3, 2, "Order ID: Newest First");

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        Collections.sort(filteredList, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Sorted by Price: Low to High", Toast.LENGTH_SHORT).show();
                        return true;
                    case 2:
                        Collections.sort(filteredList, (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Sorted by Price: High to Low", Toast.LENGTH_SHORT).show();
                        return true;
                    case 3:
                        Collections.sort(filteredList, (o1, o2) -> o2.getOrderId().compareTo(o1.getOrderId()));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Sorted by Order ID", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private void applyFilter() {
        filteredList.clear();

        for (Order order : allOrdersList) {
            // Check status tab matching
            boolean matchesTab = false;
            if (currentSelectedTab.equals("all")) {
                matchesTab = true;
            } else if (currentSelectedTab.equals("completed") && order.getStatus().equalsIgnoreCase("completed")) {
                matchesTab = true;
            } else if (currentSelectedTab.equals("cancelled") && order.getStatus().equalsIgnoreCase("cancelled")) {
                matchesTab = true;
            } else if (currentSelectedTab.equals("booked_again") && order.getStatus().equalsIgnoreCase("booked_again")) {
                matchesTab = true;
            }

            // Check search text matching
            boolean matchesSearch = false;
            if (currentSearchQuery.isEmpty()) {
                matchesSearch = true;
            } else {
                if (order.getOrderId().toLowerCase().contains(currentSearchQuery) ||
                        order.getPickupAddress().toLowerCase().contains(currentSearchQuery) ||
                        order.getDropoffAddress().toLowerCase().contains(currentSearchQuery) ||
                        order.getVehicleType().toLowerCase().contains(currentSearchQuery)) {
                    matchesSearch = true;
                }
            }

            if (matchesTab && matchesSearch) {
                filteredList.add(order);
            }
        }

        if (adapter != null) {
            adapter.updateData(filteredList);
        }
    }
}
