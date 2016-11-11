package com.imaginetventures.masterkey.activityClasses;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.modelClasses.EmployeePayRollData;
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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class PayRollDetailActivity extends AppCompatActivity {
    private String TAG = "PayRollDetailActivity";
    private TextView nameTextView, monthYearTextView, basicSalaryTextView,
            travelTextView, refreshmentTextView, personalTextView, othersTextView,
            advancePayTextView, totalTextView, fromDateTextView, toDateTextView, loanAmountTextView, salaryPayableTextView;
    private EditText daysWorkedEditText, bonusEditText, loanEditText, totalWorkingDaysTextView, loanPaymentEditText;
    private Date date;
    private String employeeId = "";
    private Calendar calendar = Calendar.getInstance(), fromDate = Calendar.getInstance(), toDate = Calendar.getInstance();
    private Button submitButton;
    private int basicSalary = 0, travel = 0, refreshment = 0, personal = 0, others = 0;
    private double basicSal = 0, travel1Loc = 0, refreshmentLoc = 0, personalLoc = 0, otherLoc = 0;
    private double percentage = 0, taxDetect = 0, grossSalary = 0, grossSalary1 = 0, loanAmountPayment = 0;
    private DatePickerDialog toDatePicker;
    private JSONObject jsonObject = new JSONObject();
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                refreshTotal();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_roll_detail);

        nameTextView = (TextView) findViewById(R.id.payRollDetailActivityEmployeeNameTextView);
        monthYearTextView = (TextView) findViewById(R.id.payRollDetailActivityMonthYearTextView);
        fromDateTextView = (TextView) findViewById(R.id.payRollDetailActivityFromDateTextView);
        toDateTextView = (TextView) findViewById(R.id.payRollDetailActivityToDateTextView);
        totalWorkingDaysTextView = (EditText) findViewById(R.id.payRollDetailActivityTotalWorkingDaysTextView);
        daysWorkedEditText = (EditText) findViewById(R.id.payRollDetailActivityDaysWorkedEditText);
        basicSalaryTextView = (TextView) findViewById(R.id.payRollDetailActivityBasicSalaryTextView);
        travelTextView = (TextView) findViewById(R.id.payRollDetailActivityTravelAllowanceTextView);
        refreshmentTextView = (TextView) findViewById(R.id.payRollDetailActivityRefreshmentAllowanceTextView);
        personalTextView = (TextView) findViewById(R.id.payRollDetailActivityPersonalAllowanceTextView);
        othersTextView = (TextView) findViewById(R.id.payRollDetailActivityOtherAllowanceTextView);
        bonusEditText = (EditText) findViewById(R.id.payRollDetailActivityBonusEditText);
        loanEditText = (EditText) findViewById(R.id.payRollDetailActivityLoanPaymentEditText);
        loanAmountTextView = (TextView) findViewById(R.id.payRollDetailActivityLoanAmountTextView);
        salaryPayableTextView = (TextView) findViewById(R.id.payRollDetailActivitySalaryPayableTextView);
        loanPaymentEditText = (EditText) findViewById(R.id.payRollDetailActivityLoanPaymentEditText);
        advancePayTextView = (TextView) findViewById(R.id.payRollDetailActivityAdvancePaymentTextView);
        totalTextView = (TextView) findViewById(R.id.payRollDetailActivityTotalTextView);
        submitButton = (Button) findViewById(R.id.payRollDetailActivitySubmitButton);
        toDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                toDate.set(year, monthOfYear, dayOfMonth);
                toDateTextView.setText(ConstantValues.defaultAppDate.format(toDate.getTime()));
//                totalWorkingDaysTextView.setText(getDifferenceDay(toDate, fromDate) + "");
                try {
                    refreshTotal();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }, toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH));
        try {
            employeeId = getIntent().getStringExtra(ConstantValues.intentEmployeeId);
            date = ConstantValues.defaultAppDateMonthYear.parse(getIntent().getStringExtra(ConstantValues.intentMonthYear));
            calendar.setTime(date);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        refreshData();

        daysWorkedEditText.addTextChangedListener(textWatcher);
        bonusEditText.addTextChangedListener(textWatcher);
        loanEditText.addTextChangedListener(textWatcher);
        totalWorkingDaysTextView.addTextChangedListener(textWatcher);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDifferenceDay(toDate, fromDate) > 0) {
                    toDatePicker.getDatePicker().setMinDate(fromDate.getTime().getTime());
                    toDatePicker.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());
                    toDatePicker.show();
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int noWorkingDay = 0;
                if (!totalWorkingDaysTextView.getText().toString().equalsIgnoreCase("")) {
                    noWorkingDay = Integer.parseInt(totalWorkingDaysTextView.getText().toString());
                }
                if (getDifferenceDay(toDate, fromDate) >= noWorkingDay) {
                    int noWorkedDay1 = 0;
                    if (!daysWorkedEditText.getText().toString().equalsIgnoreCase("")) {
                        noWorkedDay1 = Integer.parseInt(daysWorkedEditText.getText().toString());
                    }
                    if (noWorkingDay >= noWorkedDay1) {
                        if (noWorkedDay1 > 0) {
                            try {
                                save();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        } else {
                            new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, "Alert", "Worked days must be greater than zero", false);
                        }
                    } else {
                        new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, "Alert", "Worked days must be less than or equal to worked days", false);
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, "Alert", ConstantValues.defaultAppDateMonthYear.format(date) + " total days is " + calendar.getActualMaximum(Calendar.DAY_OF_MONTH), false);
                }
            }
        });
    }

    private void refreshData() {
        monthYearTextView.setText(ConstantValues.defaultAppDateMonthYear.format(date));
//        totalWorkingDaysTextView.setText(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "");
        String url = ConstantValues.serverUrl + "payroll_details?Employee_id=" + employeeId + "&month=" + ConstantValues.defaultAppDate2.format(date);
        new WebService(PayRollDetailActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    jsonObject = response.getJSONObject("employees");
                    nameTextView.setText(jsonObject.getString("Employee_name"));

                    basicSalaryTextView.setText(jsonObject.getString("Basic_salary_per_month"));
                    basicSalary = Integer.parseInt(jsonObject.getString("Basic_salary_per_month"));

                    travelTextView.setText(jsonObject.getString("Travel_allowance_per_month"));
                    travel = Integer.parseInt(jsonObject.getString("Travel_allowance_per_month"));

                    refreshmentTextView.setText(jsonObject.getString("Refreshment_allowance_per_month"));
                    refreshment = Integer.parseInt(jsonObject.getString("Refreshment_allowance_per_month"));

                    personalTextView.setText(jsonObject.getString("Personal_allowance_per_month"));
                    personal = Integer.parseInt(jsonObject.getString("Personal_allowance_per_month"));

                    othersTextView.setText(jsonObject.getString("Other_allowance_per_month"));
                    others = Integer.parseInt(jsonObject.getString("Other_allowance_per_month"));
                    if (jsonObject.getString("Balance_amount") != null && !jsonObject.getString("Balance_amount").equalsIgnoreCase("null")) {
                        advancePayTextView.setText(0 + "");
                    } else {
                        advancePayTextView.setText("0");
                    }
                    loanAmountTextView.setText(jsonObject.getString("Balance_amount"));
                    try {
                        fromDate.setTime(ConstantValues.defaultDate.parse(jsonObject.getString("from_date")));
                        calendar = fromDate;
                        fromDateTextView.setText(ConstantValues.defaultAppDate.format(fromDate.getTime()));
                    } catch (ParseException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    toDateTextView.setText(ConstantValues.defaultAppDate.format(toDate.getTime()));
                    totalWorkingDaysTextView.setText(getDifferenceDay(toDate, fromDate) + "");


//                    percentage = Float.parseFloat(jsonObject.getString("tax_percentage"));
                    try {
                        refreshTotal();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, title, message, false);
            }
        });
    }

    private void refreshTotal() throws Exception {
        int noWorkingDay = 0;
        if (!totalWorkingDaysTextView.getText().toString().equalsIgnoreCase("")) {
            noWorkingDay = Integer.parseInt(totalWorkingDaysTextView.getText().toString());
        }
        if (true || calendar.getActualMaximum(Calendar.DAY_OF_MONTH) >= noWorkingDay) {
            int noWorkedDay1 = 0;
            if (!daysWorkedEditText.getText().toString().equalsIgnoreCase("")) {
                noWorkedDay1 = Integer.parseInt(daysWorkedEditText.getText().toString());
            }
            if (noWorkingDay >= noWorkedDay1) {
                int noWorkedDay = 0;
                if (!daysWorkedEditText.getText().toString().equalsIgnoreCase("")) {
                    noWorkedDay = Integer.parseInt(daysWorkedEditText.getText().toString());
                }
                int bonus = 0;
                if (!bonusEditText.getText().toString().equalsIgnoreCase("")) {
                    bonus = Integer.parseInt(bonusEditText.getText().toString());
                }
                int loan = 0;
                if (!loanEditText.getText().toString().equalsIgnoreCase("")) {
                    loan = Integer.parseInt(loanEditText.getText().toString());
                }
                double subTotal = 0;
                subTotal = ((double) noWorkedDay / (double) fromDate.getActualMaximum(Calendar.DAY_OF_MONTH));

                float subTotal1 = 0;
                subTotal1 = (basicSalary + travel + refreshment + personal + others);

                double total = (subTotal * subTotal1) + bonus;

                basicSal = subTotal * basicSalary;
                if (!Double.isNaN(basicSal)) {
                    basicSalaryTextView.setText(String.format("%.2f", basicSal));
                } else {
                    basicSalaryTextView.setText(String.format("%.2f", 0));
                }

                travel1Loc = subTotal * travel;
                if (!Double.isNaN(travel1Loc)) {
                    travelTextView.setText(String.format("%.2f", travel1Loc));
                } else {
                    travelTextView.setText(String.format("%.2f", 0));
                }

                refreshmentLoc = subTotal * refreshment;
                if (!Double.isNaN(refreshmentLoc)) {
                    refreshmentTextView.setText(String.format("%.2f", refreshmentLoc));
                } else {
                    refreshmentTextView.setText(String.format("%.2f", 0));
                }

                personalLoc = subTotal * personal;
                if (!Double.isNaN(personalLoc)) {
                    personalTextView.setText(String.format("%.2f", personalLoc));
                } else {
                    personalTextView.setText(String.format("%.2f", 0));
                }

                otherLoc = subTotal * others;
                if (!Double.isNaN(otherLoc)) {
                    othersTextView.setText(String.format("%.2f", otherLoc));
                } else {
                    othersTextView.setText(String.format("%.2f", 0));
                }

                double grossPercentage = 100 - percentage;
                grossSalary1 = ((grossPercentage / 100) * total);
                if (loanPaymentEditText.getText().toString().length() > 0) {
                    loanAmountPayment = Double.parseDouble(loanPaymentEditText.getText().toString());
                } else {
                    loanAmountPayment = 0;
                }
                grossSalary = ((grossPercentage / 100) * total) - loanAmountPayment;
                salaryPayableTextView.setText(String.format("%.2f", grossSalary1));
                taxDetect = (percentage / 100) * total;

                Log.d(TAG, "Total:" + String.format("%.2f", total) + " GrossSalary:" + String.format("%.2f", grossSalary) + " TaxDetected:" + String.format("%.2f", taxDetect));
                if (!Double.isNaN(grossSalary)) {
                    totalTextView.setText(String.format("%.2f", grossSalary));
                } else {
                    totalTextView.setText(String.format("%.2f", 0));
                    grossSalary = 0;
                }

            } else {
                Toast.makeText(PayRollDetailActivity.this, "Worked days must be less than or equal to worked days", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PayRollDetailActivity.this, ConstantValues.defaultAppDateMonthYear.format(date) + " total days is " + calendar.getActualMaximum(Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
        }
    }

    private void save() throws Exception {
        String url = ConstantValues.serverUrl + "save_payroll?To_date=" + ConstantValues.defaultDate.format(toDate.getTime())
                + "&Employee_id=" + employeeId
                + "&Total_working=" + totalWorkingDaysTextView.getText().toString()
                + "&Days_worked=" + daysWorkedEditText.getText().toString()
                + "&Basic_salary=" + jsonObject.getString("Basic_salary_per_month")
                + "&Travel_allowance=" + jsonObject.getString("Travel_allowance_per_month")
                + "&Refreshment_allowance=" + jsonObject.getString("Refreshment_allowance_per_month")
                + "&Personal_allowance=" + jsonObject.getString("Personal_allowance_per_month")
                + "&Other_allowance=" + jsonObject.getString("Other_allowance_per_month")
                + "&Bonus=" + bonusEditText.getText().toString()
                + "&Actual_salary=" + salaryPayableTextView.getText()
                + "&Loan_deduction=" + loanPaymentEditText.getText()
                + "&After_deduction_salary=" + totalTextView.getText()
                + "&User_id=" + new Session(PayRollDetailActivity.this).getUserId()
                + "&Branch_id=" + new Session(PayRollDetailActivity.this).getBranchId();
//        String url = ConstantValues.serverUrl + "save_payroll?employee_id=" + employeeId + "&month=" + URLEncoder.encode(ConstantValues.defaultAppDate2.format(date), "UTF-8") +
//                "&total_days=" + totalWorkingDaysTextView.getText().toString() + "&working_days=" + daysWorkedEditText.getText().toString() +
//                "&basic_salary=" + URLEncoder.encode(String.format("%.2f", basicSal), "UTF-8") +
//                "&traval_allowance=" + URLEncoder.encode(String.format("%.2f", travel1Loc), "UTF-8") +
//                "&personal_allowance=" + URLEncoder.encode(String.format("%.2f", personalLoc), "UTF-8") +
//                "&refreshment_allowance=" + URLEncoder.encode(String.format("%.2f", refreshmentLoc), "UTF-8") +
//                "&other_allowance=" + URLEncoder.encode(String.format("%.2f", otherLoc), "UTF-8") +
//                "&bonus=" + bonusEditText.getText().toString() + "&loan_recovery=" + loanEditText.getText().toString() +
//                "&tax_deduction=" + URLEncoder.encode(String.format("%.2f", taxDetect), "UTF-8") +
//                "&gross_salary=" + URLEncoder.encode(String.format("%.2f", grossSalary), "UTF-8");
        new WebService(PayRollDetailActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    Toast.makeText(PayRollDetailActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    finish();
//                    new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, "Alert", response.getString("message"), true);
                } else {
                    new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(PayRollDetailActivity.this, title, message, false);
            }
        });
    }

    private long getDifferenceDay(Calendar toDate, Calendar fromDate) {
        return (toDate.getTime().getTime() - fromDate.getTime().getTime()) / (1000 * 60 * 60 * 24);
    }
}
