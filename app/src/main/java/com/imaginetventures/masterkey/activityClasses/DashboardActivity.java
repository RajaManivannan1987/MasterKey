package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.DashboardInterBranchDuesAdapter;
import com.imaginetventures.masterkey.adapterClasses.MenuListViewAdapter;
import com.imaginetventures.masterkey.modelClasses.InterBranchDues;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.Menu;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.MenuListener;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IM028 on 6/3/16.
 */
public class DashboardActivity extends AppCompatActivity {
    private final String TAG = DashboardActivity.class.getSimpleName();
    private TextView mtdRevenueTextView, mtdExpenseTextView,
            hoDuesTextView, transAssignedTextView,
            transCompletedTextView, transCompletedRevenueTextView,
            avgRatCompletedTransTextView, noOfRattingBelow4TextView,
            avgRatingTextView, mtdAvgRatingTextView;
    private DrawerLayout drawerLayout;
    private ListView menuListView;
    private List<InterBranchDues> interBranchDuesList = new ArrayList<InterBranchDues>();
    private RecyclerView recyclerView;
    private DashboardInterBranchDuesAdapter adapter;
    private LinearLayout recyclerViewLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mtdRevenueTextView = (TextView) findViewById(R.id.dashboardActivityRevenueTextView);
        mtdExpenseTextView = (TextView) findViewById(R.id.dashboardActivityExpenseTextView);
        hoDuesTextView = (TextView) findViewById(R.id.dashboardActivityDuesTextView);

        transAssignedTextView = (TextView) findViewById(R.id.dashboardActivityNoOfTransactionAssignedTextView);
        transCompletedTextView = (TextView) findViewById(R.id.dashboardActivityNoOfTransactionCompletedTextView);
        transCompletedRevenueTextView = (TextView) findViewById(R.id.dashboardActivityRevenueCompletedTransactionTextView);
        avgRatCompletedTransTextView = (TextView) findViewById(R.id.dashboardActivityAvgRatingCompletedTransactionTextView);
        noOfRattingBelow4TextView = (TextView) findViewById(R.id.dashboardActivityCompletedTransactionRating4TextView);

        avgRatingTextView = (TextView) findViewById(R.id.dashboardActivityEmployeeRatingTotalTextView);
        mtdAvgRatingTextView = (TextView) findViewById(R.id.dashboardActivityEmployeeRatingTotalMTDTextView);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.dashboardActivityInterBranchDuesRecyclerView);
        recyclerViewLinearLayout = (LinearLayout) findViewById(R.id.dashboardActivityInterBranchDuesRecyclerViewLinearLayout);

        LinearLayoutManager layoutCompat = new LinearLayoutManager(this);
        layoutCompat.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutCompat);

        adapter = new DashboardInterBranchDuesAdapter(this, interBranchDuesList);
        recyclerView.setAdapter(adapter);

        menuListView = (ListView) findViewById(R.id.left_drawer);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        menuListView.setAdapter(new MenuListViewAdapter(this, 0));
        menuListView.setOnItemClickListener(Menu.getMenuItemCheckListener(this, new MenuListener() {
            @Override
            public void onItemChecked() {
                closeDrawer();
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        getEODReport();
        getEmployeeRating();
        getInterBranchReport();
    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    private void getData() {
        String url = ConstantValues.serverUrl + "get_dashboard_report?Branch_id=" + new Session(DashboardActivity.this).getBranchId();
        new WebService(DashboardActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    mtdRevenueTextView.setText(response.getJSONObject("data").getString("Revenue"));
                    mtdExpenseTextView.setText(response.getJSONObject("data").getString("Expense"));
                    hoDuesTextView.setText(response.getJSONObject("data").getString("Dues"));

                } else {
                    new AlertDialogManager().showAlertDialog(DashboardActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(DashboardActivity.this, title, message, false);
            }
        });
    }

    private void getEmployeeRating() {
        String url = ConstantValues.serverUrl + "get_employee_rating?Branch_id=" + new Session(DashboardActivity.this).getBranchId();
        new WebService(DashboardActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    avgRatingTextView.setText(response.getJSONObject("data").getString("total_average"));
                } else {
                    new AlertDialogManager().showAlertDialog(DashboardActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(DashboardActivity.this, title, message, false);
            }
        });
    }

    private void getEODReport() {
        Calendar c = Calendar.getInstance();
        String url = ConstantValues.serverUrl + "get_eod_report?Date=" + ConstantValues.defaultDate.format(c.getTime()) + "&Branch_id=" + new Session(DashboardActivity.this).getBranchId();
        new WebService(DashboardActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    transAssignedTextView.setText(response.getJSONObject("eod_details").getString("total_txn"));
                    transCompletedTextView.setText(response.getJSONObject("eod_details").getString("total_complete_txn"));
                    transCompletedRevenueTextView.setText(response.getJSONObject("eod_details").getString("total_complete_revenue"));
                    avgRatCompletedTransTextView.setText(response.getJSONObject("eod_details").getString("Avg_rating"));
                    noOfRattingBelow4TextView.setText(response.getJSONObject("eod_details").getString("Less_4"));
                } else {
                    new AlertDialogManager().showAlertDialog(DashboardActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(DashboardActivity.this, title, message, false);
            }
        });
    }

    private void getInterBranchReport() {
        String url = ConstantValues.serverUrl + "get_interbranch_report?Branch_id=" + new Session(DashboardActivity.this).getBranchId();
        new WebService(DashboardActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                interBranchDuesList.clear();
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        InterBranchDues interBranchDues = new InterBranchDues();
                        try {
                            interBranchDues.setAccount_name(jsonArray.getJSONObject(i).getString("account_name").replace("-", " "));
                            try {
                                interBranchDues.setDebit(jsonArray.getJSONObject(i).getString("debit"));
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            try {
                                interBranchDues.setCredit(jsonArray.getJSONObject(i).getString("credit"));
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                        interBranchDuesList.add(interBranchDues);
                    }
                    adapter.notifyDataSetChanged();
                    ViewGroup.LayoutParams layoutParams = recyclerViewLinearLayout.getLayoutParams();
                    layoutParams.height = (int) getResources().getDimension(R.dimen.dashboard_inter_branch_dues_item) * adapter.getItemCount();
                    recyclerViewLinearLayout.setLayoutParams(layoutParams);

                } else {
                    new AlertDialogManager().showAlertDialog(DashboardActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(DashboardActivity.this, title, message, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }
}
