package com.imaginetventures.masterkey.activityClasses;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.MultiSelectionSpinner;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddingLeadActivity extends AppCompatActivity {
    private final String TAG = AddingLeadActivity.class.getName();
    private MultiSelectionSpinner spinner;
    private Calendar enqDate, followDate, currentTime;
    private EditText mobileNumberEditText, nameEditText, noteCallEditText;
    private AutoCompleteTextView areaAutoCompleteTextView;
    private Spinner sourceLeadSpinner;
    private TextView dateEnqTextView, followUpDateTextView;
    private List<String> spinnerListName = new ArrayList<String>(), spinnerListId = new ArrayList<String>();
    private DatePickerDialog enqDatePicker, followDatePicker;
    private Button submitButton;
    private List<String> areaList = new ArrayList<String>();
    private ArrayAdapter areaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_lead);

        spinner = (MultiSelectionSpinner) findViewById(R.id.addingLeadActivityNatureOfEnquirySpinner);
        mobileNumberEditText = (EditText) findViewById(R.id.addingLeadActivityCustomerMobileNumberEditText);
        nameEditText = (EditText) findViewById(R.id.addingLeadActivityCustomerNameEditText);
        areaAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.addingLeadActivityAreaEditText);
        noteCallEditText = (EditText) findViewById(R.id.addingLeadActivityNoteOnTheCallEditText);
        sourceLeadSpinner = (Spinner) findViewById(R.id.addingLeadActivitySourceOfLeadSpinner);
        dateEnqTextView = (TextView) findViewById(R.id.addingLeadActivityDateOfEnquiryTextView);
        followUpDateTextView = (TextView) findViewById(R.id.addingLeadActivityFollowUpDateTextView);
        submitButton = (Button) findViewById(R.id.addingLeadActivitySubmitButton);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mobileNumberEditText.setText(getIntent().getExtras().getString("data"));
        getData();
        enqDate = Calendar.getInstance();
        followDate = Calendar.getInstance();
        currentTime = Calendar.getInstance();
        dateEnqTextView.setText(ConstantValues.defaultAppDate.format(enqDate.getTime()));
        enqDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                enqDate.set(year, monthOfYear, dayOfMonth);
                dateEnqTextView.setText(ConstantValues.defaultAppDate.format(enqDate.getTime()));
            }
        }, enqDate.get(Calendar.YEAR), enqDate.get(Calendar.MONTH), enqDate.get(Calendar.DAY_OF_MONTH));

//        followUpDateTextView.setText(ConstantValues.defaultAppDate.format(followDate.getTime()));
        followDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                followDate.set(year, monthOfYear, dayOfMonth);
                followUpDateTextView.setText(ConstantValues.defaultAppDate.format(followDate.getTime()));
            }
        }, followDate.get(Calendar.YEAR), followDate.get(Calendar.MONTH), followDate.get(Calendar.DAY_OF_MONTH));

        dateEnqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enqDatePicker.getDatePicker().setMinDate(currentTime.getTimeInMillis() - 10000);
                enqDatePicker.show();
            }
        });

        followUpDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followDatePicker.getDatePicker().setMinDate(currentTime.getTimeInMillis() - 10000);
                followDatePicker.show();
            }
        });

        spinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {

            }

            @Override
            public void selectedStrings(List<String> strings) {

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEditText.getText().toString().trim().length() > 0) {
                    nameEditText.setError(null);
                    if (areaAutoCompleteTextView.getText().toString().trim().length() > 0) {
                        areaAutoCompleteTextView.setError(null);
                        if (spinner.getSelectedItemsAsString().trim().length() > 0) {
                            if (sourceLeadSpinner.getSelectedItemPosition() != 0) {
                                if (followUpDateTextView.getText().length() > 0) {
                                    followUpDateTextView.setError(null);
                                    updateData();
                                } else {
                                    followUpDateTextView.setError("Please Select Follow Up date");
                                }
                            } else {
                                Toast.makeText(AddingLeadActivity.this, "Please Select Source of Lead", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddingLeadActivity.this, "Please Select Nature of enquiry", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        areaAutoCompleteTextView.setError("Please Enter Area");
                        areaAutoCompleteTextView.requestFocus();
                    }
                } else {
                    nameEditText.setError("Please Enter Name");
                    nameEditText.requestFocus();
                }
            }
        });
        getAutoCompleteArea();
    }

    void updateData() {
        String url = ConstantValues.serverUrl + "update_lead_details?Mobile_number=" + mobileNumberEditText.getText().toString()
                + "&Name=" + ConstantValues.urlEncode(nameEditText.getText().toString())
                + "&Area=" + ConstantValues.urlEncode(areaAutoCompleteTextView.getText().toString().trim())
                + "&Date_of_enquiry=" + ConstantValues.defaultDate.format(enqDate.getTime())
                + "&Nature_of_enquiry=" + ConstantValues.urlEncode(spinner.getSelectedItemsAsString().trim())
                + "&Source_of_leads=" + ConstantValues.urlEncode(sourceLeadSpinner.getSelectedItem().toString().trim())
                + "&Notes=" + ConstantValues.urlEncode(noteCallEditText.getText().toString().trim())
                + "&Follow_up_date=" + ConstantValues.defaultDate.format(followDate.getTime());
        new WebService(AddingLeadActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    Toast.makeText(AddingLeadActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    new AlertDialogManager().showAlertDialog(AddingLeadActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(AddingLeadActivity.this, title, message, false);

            }
        });
    }

    void getData() {
        String url = ConstantValues.serverUrl + "get_service";
        new WebService(AddingLeadActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                spinnerListId.clear();
                spinnerListName.clear();
                JSONArray jsonArray = response.getJSONObject("service_details").getJSONArray("ServiceList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    spinnerListId.add(jsonArray.getJSONObject(i).getString("Service_id"));
                    spinnerListName.add(jsonArray.getJSONObject(i).getString("Service_name"));
                }
                spinner.setItems(spinnerListName);
            }

            @Override
            public void onError(String message, String title) {

            }
        });
    }

    void getAutoCompleteArea() {
        String url = ConstantValues.serverUrl + "get_area";
        new WebService(AddingLeadActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    areaList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        areaList.add(jsonArray.getString(i));
                    }
                    areaAdapter = new ArrayAdapter(AddingLeadActivity.this, android.R.layout.simple_list_item_1, areaList);
                    areaAutoCompleteTextView.setAdapter(areaAdapter);
                    areaAutoCompleteTextView.setThreshold(1);
                } else {

                }
            }

            @Override
            public void onError(String message, String title) {

            }
        });
    }


}
