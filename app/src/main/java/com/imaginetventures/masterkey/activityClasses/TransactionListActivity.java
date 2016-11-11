package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.MenuListViewAdapter;
import com.imaginetventures.masterkey.adapterClasses.TransactionListRecyclerViewAdapter;
import com.imaginetventures.masterkey.modelClasses.TransactionListData;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.Menu;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.MenuListener;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity {
    private String TAG = TransactionListActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private TransactionListRecyclerViewAdapter transactionListRecyclerViewAdapter;
    private List<TransactionListData> listData = new ArrayList<TransactionListData>();
    private List<String> dateListData = new ArrayList<String>();
    private DrawerLayout drawerLayout;
    private ListView menuListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        recyclerView = (RecyclerView) findViewById(R.id.transactionListActivityRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        transactionListRecyclerViewAdapter = new TransactionListRecyclerViewAdapter(this, listData, dateListData);
        recyclerView.setAdapter(transactionListRecyclerViewAdapter);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(transactionListRecyclerViewAdapter));
        transactionListRecyclerViewAdapter.notifyDataSetChanged();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        menuListView = (ListView) findViewById(R.id.left_drawer);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        menuListView.setAdapter(new MenuListViewAdapter(this, 1));
        menuListView.setOnItemClickListener(Menu.getMenuItemCheckListener(this, new MenuListener() {
            @Override
            public void onItemChecked() {
                closeDrawer();
            }
        }));
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();

    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    private void getData() {
        String url = ConstantValues.serverUrl + "transaction?Branch_id=" + new Session(TransactionListActivity.this).getBranchId();
        new WebService(TransactionListActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    listData.clear();
                    dateListData.clear();
                    try {
                        JSONArray dateJsonArray = response.getJSONObject("transaction").getJSONArray("date");
                        for (int i = 0; i < dateJsonArray.length(); i++) {
                            try {
                                dateListData.add(dateJsonArray.getString(i));
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    try {
                        JSONArray jsonArray = response.getJSONObject("transaction").getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TransactionListData transactionListData = new TransactionListData();
                                transactionListData.setTransactionId(Integer.parseInt(jsonObject.getString("Transaction_id")));
                                transactionListData.setCustomerAddress(jsonObject.getString("Customer_address"));
                                transactionListData.setCustomerName(jsonObject.getString("Customer_name"));
                                transactionListData.setStatus(jsonObject.getString("Status"));
                                transactionListData.setTime(jsonObject.getString("Time"));
                                transactionListData.setJobId(jsonObject.getString("Job_id"));
                                transactionListData.setDate(jsonObject.getString("Date"));
                                transactionListData.setMobileNo(jsonObject.getString("Customer_mobile_no"));
                                JSONArray service = jsonObject.getJSONArray("Service_name");
                                List<String> serviceList = new ArrayList<String>();
                                for (int j = 0; j < service.length(); j++) {
                                    serviceList.add(service.getString(j));
                                }
                                transactionListData.setServiceName(serviceList);
                                listData.add(transactionListData);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    transactionListRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    new AlertDialogManager().showAlertDialog(TransactionListActivity.this, "Alert", response.getString("message"), false);
                    transactionListRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(TransactionListActivity.this, title, message, false);
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
