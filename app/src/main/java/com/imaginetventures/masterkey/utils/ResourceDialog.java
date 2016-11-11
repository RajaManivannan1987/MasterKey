package com.imaginetventures.masterkey.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.activityClasses.ResourceActivity;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


/**
 * Created by IM028 on 6/9/16.
 */
public class ResourceDialog extends Dialog {
    private String TAG = "ResourceDialog";
    private ResourceActivity context;
    private String name, periodicity, id;

    public ResourceDialog(ResourceActivity context, String name, String periodicity, String id) {
        super(context);
        this.context = context;
        this.name = name;
        this.periodicity = periodicity;
        this.id = id;
    }

    private TextView titleTextView, dateTextView;
    private EditText periodicityEditText;
    private Button saveButton;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_resource);

        titleTextView = (TextView) findViewById(R.id.resourceDialogHeadingTextView);
        dateTextView = (TextView) findViewById(R.id.resourceDialogDateTextView);
        periodicityEditText = (EditText) findViewById(R.id.resourceDialogPeriodicityEditText);
        saveButton = (Button) findViewById(R.id.resourceDialogSubmitButton);

        titleTextView.setText(name);
        periodicityEditText.setText(periodicity);
        dateTextView.setText(ConstantValues.defaultAppDate.format(calendar.getTime()));
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                dateTextView.setText(ConstantValues.defaultAppDate.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        String url = ConstantValues.serverUrl + "update_resource_service_date?Resource_id=" + id
                + "&Date=" + ConstantValues.defaultDate.format(calendar.getTime())
                + "&Periodicity=" + periodicityEditText.getText().toString();
        new WebService(context, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    new AlertDialogManager().showAlertDialog(context, "Alert", response.getString("message"), true);
                    dismiss();
                    context.getData();
                } else {
                    new AlertDialogManager().showAlertDialog(context, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(context, title, message, false);
            }
        });
    }
}
