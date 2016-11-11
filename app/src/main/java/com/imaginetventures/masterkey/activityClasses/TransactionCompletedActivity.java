package com.imaginetventures.masterkey.activityClasses;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.TransactionPaymentRecyclerViewAdapter;
import com.imaginetventures.masterkey.modelClasses.Services;
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


public class TransactionCompletedActivity extends AppCompatActivity {
    private String TAG = "TransactionCompletedActivity";
    private EditText jobAmountEditText, debitAccountEditText, creditAccountEditText;
    private ImageView backImageView;
    private Button submitButton;
    private RatingBar ratingBar;
    private int transactionId = 0;
    private JSONObject jsonObject;
    private List<String> employeeList = new ArrayList<String>(), employeeIdList = new ArrayList<String>();
    private JSONArray serviceJsonArray;

    private LinearLayout periodicityLinearLayout, ratingBarLinearLayout;

    private RecyclerView recyclerView;
    private List<Services> recyclerViewList = new ArrayList<Services>();
    private TransactionPaymentRecyclerViewAdapter recyclerViewAdapter;

    private boolean paymentStatus = false;
    private String statusString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_completed);

        jobAmountEditText = (EditText) findViewById(R.id.transactionCompletedJobAmountEditText);
        debitAccountEditText = (EditText) findViewById(R.id.transactionCompletedActivityDebitAccountEditEditText);
        creditAccountEditText = (EditText) findViewById(R.id.transactionCompletedActivityCreditAccountEditEditText);
        recyclerView = (RecyclerView) findViewById(R.id.transactionCompletedPeriodicityRecyclerView);
        periodicityLinearLayout = (LinearLayout) findViewById(R.id.transactionCompletedPeriodicityLinearLayout);
        ratingBar = (RatingBar) findViewById(R.id.transactionCompletedRatingBar);
        submitButton = (Button) findViewById(R.id.transactionCompletedSaveButton);
        transactionId = getIntent().getExtras().getInt(ConstantValues.intentTransactionId, 0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new TransactionPaymentRecyclerViewAdapter(TransactionCompletedActivity.this, recyclerViewList);
        recyclerView.setAdapter(recyclerViewAdapter);

        refreshData();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobAmountEditText.getText().length() > 0 && Integer.parseInt(jobAmountEditText.getText().toString()) > 0) {
                    jobAmountEditText.setError(null);
                    if (isValidatePeriodicity()) {
                        if (ratingBar.getRating() >= 1) {
                            try {
                                save();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        } else {
                            Toast.makeText(TransactionCompletedActivity.this, "Please Rate transaction", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TransactionCompletedActivity.this, "Enter periodicity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    jobAmountEditText.setError("Enter valid Amount");
                    jobAmountEditText.requestFocus();
                }
            }
        });


        backImageView = (ImageView) findViewById(R.id.back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void save() throws UnsupportedEncodingException, JSONException {
        String url = ConstantValues.serverUrl + "update_transaction_data?Transaction_id=" + ConstantValues.urlEncode(transactionId + "")
                + "&Amount_paid=" + ConstantValues.urlEncode(jobAmountEditText.getText().toString())
                + "&User_id=" + new Session(TransactionCompletedActivity.this).getUserId()
                + "&Rating=" + ratingBar.getRating()
                + "&Customer_id=" + jsonObject.getString("credit_id")
                + "&Branch_id=" + new Session(TransactionCompletedActivity.this).getBranchId()
                + "&Services=" + ConstantValues.urlEncode(getServiceData().toString());
        new WebService(TransactionCompletedActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    Toast.makeText(TransactionCompletedActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    new AlertDialogManager().showAlertDialog(TransactionCompletedActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(TransactionCompletedActivity.this, title, message, false);
            }
        });
    }

    private JSONArray getServiceData() {
        JSONArray result = new JSONArray();
        for (Services s : recyclerViewAdapter.getData()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Service_id", s.getId());
                jsonObject.put("Days", s.getDays());
                result.put(jsonObject);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return result;
    }

    private boolean isValidatePeriodicity() {
        for (Services s : recyclerViewAdapter.getData()) {
            if (s.getDays().equalsIgnoreCase("") || Integer.parseInt(s.getDays()) == 0) {
                return false;
            }
        }
        return true;
    }

    private void refreshData() {
        String url = ConstantValues.serverUrl + "get_transaction_data?Transaction_id=" + transactionId;
        new WebService(TransactionCompletedActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                jsonObject = response;
                serviceJsonArray = jsonObject.getJSONArray("services");
                recyclerViewList.clear();
                for (int i = 0; i < serviceJsonArray.length(); i++) {
                    Services services = new Services();
                    services.setId(serviceJsonArray.getJSONObject(i).getString("Category_id"));
                    services.setName(serviceJsonArray.getJSONObject(i).getString("Category_name"));
                    recyclerViewList.add(services);
                }
                jobAmountEditText.setText(jsonObject.getString("amount"));
                debitAccountEditText.setText(jsonObject.getString("debit_name"));
                debitAccountEditText.setEnabled(false);
                creditAccountEditText.setText("Revenue");
                creditAccountEditText.setEnabled(false);
                recyclerViewAdapter.notifyDataSetChanged();
                ViewGroup.LayoutParams layoutParams = periodicityLinearLayout.getLayoutParams();
                layoutParams.height = (int) getResources().getDimension(R.dimen.transaction_payment_item) * recyclerViewList.size();
                periodicityLinearLayout.setLayoutParams(layoutParams);
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(TransactionCompletedActivity.this, title, message, false);
            }
        });
    }
}
