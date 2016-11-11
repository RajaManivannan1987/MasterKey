package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.MenuListViewAdapter;
import com.imaginetventures.masterkey.adapterClasses.ResourceRecyclerViewAdapter;
import com.imaginetventures.masterkey.modelClasses.Resource;
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

public class ResourceActivity extends AppCompatActivity {
    private String TAG = "ResourceActivity";
    private DrawerLayout drawerLayout;
    private ListView menuListView;
    private RecyclerView recyclerView;
    private ResourceRecyclerViewAdapter adapter;
    private List<Resource> resourceList = new ArrayList<Resource>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        recyclerView = (RecyclerView) findViewById(R.id.resourceActivityRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ResourceRecyclerViewAdapter(this, resourceList);
        recyclerView.setAdapter(adapter);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuListView = (ListView) findViewById(R.id.left_drawer);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        menuListView.setAdapter(new MenuListViewAdapter(this, 3));
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

    public void getData() {
        String url = ConstantValues.serverUrl + "get_resource_service_list?Branch_id=" + new Session(ResourceActivity.this).getBranchId();
        new WebService(ResourceActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    resourceList.clear();
                    JSONArray jsonArray = response.getJSONArray("Resource_details");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            Resource resource = new Resource();
                            resource.setId(jsonArray.getJSONObject(i).getString("Resource_id"));
                            resource.setName(jsonArray.getJSONObject(i).getString("Resource_name"));
                            resource.setPeriodicity(jsonArray.getJSONObject(i).getInt("Periodicity"));
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(ConstantValues.defaultDate.parse(jsonArray.getJSONObject(i).getString("Last_service_date")));
                            resource.setLastService(calendar);
                            resourceList.add(resource);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    new AlertDialogManager().showAlertDialog(ResourceActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {

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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

}
