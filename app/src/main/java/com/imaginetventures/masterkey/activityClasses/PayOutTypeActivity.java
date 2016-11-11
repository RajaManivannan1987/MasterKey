package com.imaginetventures.masterkey.activityClasses;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.MenuListViewAdapter;
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
import java.util.Date;
import java.util.List;

public class PayOutTypeActivity extends AppCompatActivity {
    private String TAG = "PayOutTypeActivity";
    private TextView dateTextView;
    private RelativeLayout dateRelativeLayout;
    private EditText amountEditText;
    private Spinner payoutTypeSpinner, employeeNameSpinner;
    private List<String> payoutTypeData = new ArrayList<String>(), payoutTypeId = new ArrayList<String>(), employeeName = new ArrayList<String>(), employeeId = new ArrayList<String>();
    private DatePickerDialog datePickerDialog;
    private Button submitButton;
    private DrawerLayout drawerLayout;
    private ListView menuListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_out_type);

        dateTextView = (TextView) findViewById(R.id.payoutTypeActivityDateTextView);
        dateRelativeLayout = (RelativeLayout) findViewById(R.id.payoutTypeActivitySelectDateRelativeLayout);
        amountEditText = (EditText) findViewById(R.id.payoutTypeActivityAmountEditText);
        payoutTypeSpinner = (Spinner) findViewById(R.id.payoutTypeActivityPayoutTypeSpinner);
        employeeNameSpinner = (Spinner) findViewById(R.id.payoutTypeActivityEmployeeSpinner);
        submitButton = (Button) findViewById(R.id.payoutTypeActivitySubmitButton);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuListView = (ListView) findViewById(R.id.left_drawer);


        dateTextView.setText(ConstantValues.defaultAppDate1.format(new Date()));
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month=monthOfYear+1;
                dateTextView.setText(dayOfMonth + "-" + month + "-" + year);
            }
        }, Integer.parseInt(dateTextView.getText().toString().split("-")[2]), Integer.parseInt(dateTextView.getText().toString().split("-")[1])-1, Integer.parseInt(dateTextView.getText().toString().split("-")[0]));
        dateRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        getCashOutTypeData();
        getEmployeeData();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountEditText.getText().toString().trim();
                if (!amount.equalsIgnoreCase("") && Integer.parseInt(amount) > 0) {
                    try {
                        save();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    amountEditText.setError("Enter Amount");
                    amountEditText.requestFocus();
                }
            }
        });
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        menuListView.setAdapter(new MenuListViewAdapter(this, 1));
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(PayOutTypeActivity.this, TransactionListActivity.class));
                        closeDrawer();
                        break;
                    case 1:
                        startActivity(new Intent(PayOutTypeActivity.this, PayOutTypeActivity.class));
                        closeDrawer();
                        break;
                    case 2:
                        startActivity(new Intent(PayOutTypeActivity.this, PayRollActivity.class));
                        closeDrawer();
                        break;
                    case 3:
                        startActivity(new Intent(PayOutTypeActivity.this, AddLeadActivity.class));
                        closeDrawer();
                        break;
                    case 4:
                        new Session(PayOutTypeActivity.this).clearSession();
                        Intent intent = new Intent(PayOutTypeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        closeDrawer();
                        finish();
                        break;
                }
            }
        });
    }
    private void closeDrawer(){
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void save() throws UnsupportedEncodingException {
        String url = ConstantValues.serverUrl + "save_cash_out_transaction?" + "date=" + URLEncoder.encode(dateTextView.getText().toString(), "UTF-8") + "&" +
                "transaction_type_id=" + payoutTypeId.get(payoutTypeSpinner.getSelectedItemPosition()) + "&" +
                "branch_id=" + new Session(PayOutTypeActivity.this).getBranchId() + "&" +
                "employee_id=" + employeeId.get(employeeNameSpinner.getSelectedItemPosition()) + "&" +
                "amount=" + amountEditText.getText().toString();
        new WebService(PayOutTypeActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    new AlertDialogManager().showAlertDialog(PayOutTypeActivity.this, "Alert", response.getString("message"), true);
                    amountEditText.setText("");
                    employeeNameSpinner.setSelection(0);
                } else {
                    new AlertDialogManager().showAlertDialog(PayOutTypeActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(PayOutTypeActivity.this, title, message, false);
            }
        });
    }

    private void getCashOutTypeData() {
        String url = ConstantValues.serverUrl + "cash_out_type";
        new WebService(PayOutTypeActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    payoutTypeData.clear();
                    payoutTypeId.clear();
                    JSONArray jsonArray = response.getJSONArray("cash_out_type");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        payoutTypeData.add(jsonArray.getJSONObject(i).getString("transaction_type"));
                        payoutTypeId.add(jsonArray.getJSONObject(i).getString("transaction_type_id"));
                    }
                    payoutTypeSpinner.setAdapter(new ArrayAdapter<String>(PayOutTypeActivity.this, R.layout.payout_type_spinner_item, payoutTypeData));
                } else {
                    new AlertDialogManager().showAlertDialog(PayOutTypeActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(PayOutTypeActivity.this, title, message, false);
            }
        });
    }

    private void getEmployeeData() {
        String url = ConstantValues.serverUrl + "get_employee_list?branch_id=" + new Session(PayOutTypeActivity.this).getBranchId();
        new WebService(PayOutTypeActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    employeeName.clear();
                    employeeId.clear();

                    employeeName.add("None");
                    employeeId.add("0");
                    JSONArray jsonArray = response.getJSONArray("employees");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        employeeName.add(jsonArray.getJSONObject(i).getString("Employee_name"));
                        employeeId.add(jsonArray.getJSONObject(i).getString("Employee_id"));
                    }
                    employeeNameSpinner.setAdapter(new ArrayAdapter<String>(PayOutTypeActivity.this, R.layout.payout_type_spinner_item, employeeName));
                } else {
                    new AlertDialogManager().showAlertDialog(PayOutTypeActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(PayOutTypeActivity.this, title, message, false);
            }
        });
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
