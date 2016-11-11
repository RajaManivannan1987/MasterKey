package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.MenuListViewAdapter;
import com.imaginetventures.masterkey.adapterClasses.PayrollRecyclerViewAdapter;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;

public class PayRollActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PayrollRecyclerViewAdapter adapter;
    private DrawerLayout drawerLayout;
    private ListView menuListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_roll);

        recyclerView = (RecyclerView) findViewById(R.id.payrollActivityRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PayrollRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuListView = (ListView) findViewById(R.id.left_drawer);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        menuListView.setAdapter(new MenuListViewAdapter(this, 2));
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(PayRollActivity.this, TransactionListActivity.class));
                        closeDrawer();
                        break;
                    case 1:
                        startActivity(new Intent(PayRollActivity.this, PayOutTypeActivity.class));
                        closeDrawer();
                        break;
                    case 2:
                        startActivity(new Intent(PayRollActivity.this, PayRollActivity.class));
                        closeDrawer();
                        break;
                    case 3:
                        startActivity(new Intent(PayRollActivity.this, AddLeadActivity.class));
                        closeDrawer();
                        break;
                    case 4:
                        new Session(PayRollActivity.this).clearSession();
                        Intent intent = new Intent(PayRollActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        closeDrawer();
                        finish();
                        break;
                }
            }
        });
    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.getEmployeeData();
    }
    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }
}
