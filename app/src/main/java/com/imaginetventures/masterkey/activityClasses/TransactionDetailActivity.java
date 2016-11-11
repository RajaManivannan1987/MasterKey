package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionDetailActivity extends AppCompatActivity {
    private TextView nameTextView, totalAmountTextView, dateTextView, timeTextView, statusTextView, addressTextView, serviceTextView, locationTextView, noteTextView, asssignEmployeeLabelTextView, orderIdTextView, resourceTextView;
    private String TAG = TransactionDetailActivity.class.getSimpleName();
    private LinearLayout assignedEmployeeLinearLayout, callLinearLayout, locationLinearLayout;
    private int transactionId = 0;
    private Button employeeButton, completedButton, paymentButton;
    private ImageView backImageView;
    private boolean isVisiblePayment = false;
    private boolean isVisibleCompleted = false;
    private boolean isVisibleEmployee = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        nameTextView = (TextView) findViewById(R.id.transactionDetailsActivityNameTextView);
        totalAmountTextView = (TextView) findViewById(R.id.transactionDetailsActivityTotalAmountTextView);
        dateTextView = (TextView) findViewById(R.id.transactionDetailsActivityDateTextView);
        timeTextView = (TextView) findViewById(R.id.transactionDetailsActivityTimeTextView);
        statusTextView = (TextView) findViewById(R.id.transactionDetailsActivityStatusTextView);
        addressTextView = (TextView) findViewById(R.id.transactionDetailsActivityAddressTextView);
        serviceTextView = (TextView) findViewById(R.id.transactionDetailsActivityServicesTextView);
        resourceTextView = (TextView) findViewById(R.id.transactionDetailsActivityResourceTextView);
        locationTextView = (TextView) findViewById(R.id.transactionDetailsActivityLocationTextView);
        callLinearLayout = (LinearLayout) findViewById(R.id.transactionDetailsActivityCallLinearLayout);
        locationLinearLayout = (LinearLayout) findViewById(R.id.transactionDetailsActivityLocationLinearLayout);
        noteTextView = (TextView) findViewById(R.id.transactionDetailsActivityNoteTextView);
        asssignEmployeeLabelTextView = (TextView) findViewById(R.id.transactionDetailsActivityAssignEmployeeLabelTextView);
        assignedEmployeeLinearLayout = (LinearLayout) findViewById(R.id.transactionDetailsActivityEmployeeLinearLayout);
        employeeButton = (Button) findViewById(R.id.transactionDetailsActivitySaveButton);
        completedButton = (Button) findViewById(R.id.transactionDetailsActivityCompletedButton);
        paymentButton = (Button) findViewById(R.id.transactionDetailsActivityPaymentButton);
        orderIdTextView = (TextView) findViewById(R.id.transactionDetailsActivityorderIdTextView);
        backImageView = (ImageView) findViewById(R.id.back);
        transactionId = getIntent().getExtras().getInt(ConstantValues.intentTransactionId);
//        refreshData();
        employeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionDetailActivity.this, EmployeeListActivity.class).putExtra(ConstantValues.intentTransactionId, transactionId));
            }
        });
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionDetailActivity.this, TransactionPaymentActivity.class).putExtra(ConstantValues.intentTransactionId, transactionId));
            }
        });
        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionDetailActivity.this, TransactionCompletedActivity.class).putExtra(ConstantValues.intentTransactionId, transactionId));
            }
        });
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void refreshData() {
        String url = ConstantValues.serverUrl + "transactiondetails?Transaction_id=" + transactionId;
        new WebService(TransactionDetailActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(final JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    final JSONObject jsonObject = response.getJSONObject("transactiondetails");
                    nameTextView.setText(jsonObject.getString("Customer_name"));
                    JSONArray serviceJsonArray = jsonObject.getJSONArray("Service");
                    String serviceName = "";
                    int serviceAmount = 0;
                    for (int i = 0; i < serviceJsonArray.length(); i++) {
                        if (!serviceName.equals(""))
                            serviceName += ", ";
                        serviceName += serviceJsonArray.getJSONObject(i).getString("Service_name");
//                        serviceAmount += serviceJsonArray.getJSONObject(i).getInt("Price");
                    }
                    totalAmountTextView.setText(jsonObject.getString("Amount"));
                    try {
                        dateTextView.setText(jsonObject.getString("Date"));
                        timeTextView.setText(jsonObject.getString("Time"));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    orderIdTextView.setText(jsonObject.getString("Job_id"));
                    statusTextView.setText(jsonObject.getString("Status"));

                    addressTextView.setText(jsonObject.getJSONObject("Address").getString("Door_no") + "," + jsonObject.getJSONObject("Address").getString("Location") + "," + jsonObject.getJSONObject("Address").getString("City") + "-" + jsonObject.getJSONObject("Address").getString("Pincode"));
                    serviceTextView.setText(serviceName);
                    locationTextView.setText(jsonObject.getJSONObject("Address").getString("Location"));
                    noteTextView.setText(jsonObject.getString("Notes"));
                    resourceTextView.setText(generateResourceString(jsonObject.getJSONArray("Resource")));
                    locationLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = null;
                            try {
                                gmmIntentUri = Uri.parse("geo:15.64,78.5?q=" + jsonObject.getJSONObject("Address").getString("Door_no") + "," + jsonObject.getJSONObject("Address").getString("Location") + "," + jsonObject.getJSONObject("Address").getString("City") + "-" + jsonObject.getJSONObject("Address").getString("Pincode"));
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });

                    callLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent in = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jsonObject.getString("Mobile_number")));
                                try {
                                    startActivity(in);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(getApplicationContext(), "No Calling feature", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    if (jsonObject.getString("Status").equalsIgnoreCase("Scheduled")) {
                        statusTextView.setBackgroundResource(R.drawable.status_background_scheduled);
                        totalAmountTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        isVisibleCompleted = true;
                        isVisiblePayment = false;
                        isVisibleEmployee = true;
                    } else if (jsonObject.getString("Status").equalsIgnoreCase("Completed")) {
                        statusTextView.setBackgroundResource(R.drawable.status_background_completed);
                        totalAmountTextView.setTextColor(getResources().getColor(R.color.completed));
                        totalAmountTextView.setText(jsonObject.getString("Amount"));
                        isVisibleCompleted = false;
                        isVisiblePayment = true;
                        isVisibleEmployee = false;
                    } else {
                        finish();
                    }
                    assignedEmployee(jsonObject.getJSONArray("Employee_List"));

                } else {
                    new AlertDialogManager().showAlertDialog(TransactionDetailActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(TransactionDetailActivity.this, title, message, false);
            }
        });
    }

    private String generateResourceString(JSONArray jsonArray) {
        String result = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!result.equalsIgnoreCase("")) {
                result += "\n";
            }
            try {
                result += jsonArray.getJSONObject(i).getString("Resource_category_name") + " - " + jsonArray.getJSONObject(i).getString("Quantity");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return result;
    }

    private void assignedEmployee(JSONArray jsonArray) throws JSONException {
        assignedEmployeeLinearLayout.removeAllViews();
        if (jsonArray.length() > 0) {
            asssignEmployeeLabelTextView.setVisibility(View.VISIBLE);
            if (isVisibleCompleted)
                completedButton.setVisibility(View.VISIBLE);
            else
                completedButton.setVisibility(View.GONE);
            if (isVisiblePayment)
                paymentButton.setVisibility(View.VISIBLE);
            else
                paymentButton.setVisibility(View.GONE);
            if (isVisibleEmployee)
                employeeButton.setVisibility(View.VISIBLE);
            else
                employeeButton.setVisibility(View.GONE);
            View view1 = new View(TransactionDetailActivity.this);
            view1.setPadding(5, 0, 5, 0);
            view1.setBackgroundResource(R.color.colorPrimaryDark);
            view1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
            assignedEmployeeLinearLayout.addView(view1);
            for (int i = 0; i < jsonArray.length(); i++) {
                View view = getLayoutInflater().inflate(R.layout.text_view_service, null);
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(jsonArray.getJSONObject(i).getString("Employee_name"));
                assignedEmployeeLinearLayout.addView(view);
                View view2 = new View(TransactionDetailActivity.this);
                view2.setPadding(5, 0, 5, 0);
                view2.setBackgroundResource(R.color.colorPrimaryDark);
                view2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                assignedEmployeeLinearLayout.addView(view2);
            }
            employeeButton.setText("Re-Assign Staff");
        } else {
            asssignEmployeeLabelTextView.setVisibility(View.GONE);
            employeeButton.setText("Assign Staff");
            completedButton.setVisibility(View.GONE);
            if (isVisibleEmployee)
                employeeButton.setVisibility(View.VISIBLE);
            else
                employeeButton.setVisibility(View.GONE);
            if (isVisiblePayment)
                paymentButton.setVisibility(View.VISIBLE);
            else
                paymentButton.setVisibility(View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
