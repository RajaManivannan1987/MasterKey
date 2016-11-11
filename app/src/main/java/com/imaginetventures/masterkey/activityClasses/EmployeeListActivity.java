package com.imaginetventures.masterkey.activityClasses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.EmployeeListRecyclerViewAdapter;
import com.imaginetventures.masterkey.modelClasses.EmployeeData;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EmployeeListRecyclerViewAdapter recyclerViewAdapter;
    private List<EmployeeData> data = new ArrayList<EmployeeData>();
    private String TAG = "EmployeeListActivity";
    private Button saveButton;
    private int transactionId = 0;
    private SearchView searchView;
    private ImageView backImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        recyclerView = (RecyclerView) findViewById(R.id.employeeListActivityRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new EmployeeListRecyclerViewAdapter(this, data);
        recyclerView.setAdapter(recyclerViewAdapter);
        searchView = (SearchView) findViewById(R.id.employeeListActivitySearchView);
        saveButton = (Button) findViewById(R.id.employeeListActivitySaveButton);
        transactionId = getIntent().getExtras().getInt(ConstantValues.intentTransactionId);
        refreshData();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.searchText(newText);
                return true;
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeText = "";
                for (String s : recyclerViewAdapter.getSelectedEmployee()) {
                    if (employeeText != "")
                        employeeText += ",";
                    employeeText += s;
                }
                try {
                    saveEmployee(employeeText);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        backImageView=(ImageView)findViewById(R.id.back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void saveEmployee(String employeeId) throws UnsupportedEncodingException {
        String url = ConstantValues.serverUrl + "saveemployee?Branch_id=" + new Session(EmployeeListActivity.this).getBranchId() + "&Transaction_id=" + transactionId + "&" +
                "Employee_id=" + URLEncoder.encode(employeeId,"UTF-8");
        new WebService(EmployeeListActivity.this,TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    Toast.makeText(EmployeeListActivity.this,response.getString("message"),Toast.LENGTH_SHORT).show();
                    finish();
//                    new AlertDialogManager().showAlertDialog(EmployeeListActivity.this, "Alert", response.getString("message"), true);
                } else {
                    new AlertDialogManager().showAlertDialog(EmployeeListActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(EmployeeListActivity.this, "Alert", message, false);
            }
        });
    }

    private void refreshData() {
        String url = ConstantValues.serverUrl + "employee?Branch_id=" + new Session(EmployeeListActivity.this).getBranchId() + "&Transaction_id=" + transactionId;
        new WebService(EmployeeListActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    data.clear();
                    JSONArray employee = response.getJSONArray("employee");
                    for (int i = 0; i < employee.length(); i++) {
                        EmployeeData employeeData = new EmployeeData();
                        employeeData.setId(employee.getJSONObject(i).getString("Employee_id"));
                        employeeData.setName(employee.getJSONObject(i).getString("Employee_name"));
                        employeeData.setFlag(employee.getJSONObject(i).getString("Status"));
                        data.add(employeeData);
                    }
                    recyclerViewAdapter.setSelectedEmployee();
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    new AlertDialogManager().showAlertDialog(EmployeeListActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(EmployeeListActivity.this, title, message, false);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();

    }
}
