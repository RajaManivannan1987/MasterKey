package com.imaginetventures.masterkey.activityClasses;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.MenuListViewAdapter;
import com.imaginetventures.masterkey.modelClasses.Account;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordExpenseActivity extends AppCompatActivity {
    private final String TAG = RecordExpenseActivity.class.getName();
    private final int DEBITACCOUNT = 0, CONTRAACCOUNT = 1, CREDITACCOUNT = 2;
    private List<Account> contraList = new ArrayList<Account>(), debitList = new ArrayList<Account>(), creditList = new ArrayList<Account>();
    private TextView whoseBookTextView, dateTextView, debitAccountTextView, creditAccountTextView, contraAccountTextView;
    private EditText amountEditText, noteEditText;
    private Calendar c = Calendar.getInstance();
    private int transactionId = 0;
    private DatePickerDialog datePickerDialog;
    private boolean isContraDebit = false, isContraCredit = false;
    private TableRow contraAccountTableRow;
    private String selectedDebitId = "0", selectedContraId = "0", selectedCreditId = "0";
    private Button saveButton;
    private DrawerLayout drawerLayout;
    private ListView menuListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_expense);

        whoseBookTextView = (TextView) findViewById(R.id.recordExpenseActivityWhoseBookTextView);
        dateTextView = (TextView) findViewById(R.id.recordExpenseActivityDateTextView);
        amountEditText = (EditText) findViewById(R.id.recordExpenseActivityAmountEditText);
        debitAccountTextView = (TextView) findViewById(R.id.recordExpenseActivityDebitAccountTextView);
        creditAccountTextView = (TextView) findViewById(R.id.recordExpenseActivityCreditAccountTextView);
        contraAccountTableRow = (TableRow) findViewById(R.id.recordExpenseActivityContraAccountTableRow);
        contraAccountTextView = (TextView) findViewById(R.id.recordExpenseActivityContraAccountTextView);
        noteEditText = (EditText) findViewById(R.id.recordExpenseActivityNotesEditText);
        saveButton = (Button) findViewById(R.id.recordExpenseActivitySaveButton);

        whoseBookTextView.setText(new Session(this).getName());

        dateTextView.setText(ConstantValues.defaultAppDate.format(c.getTime()));
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                dateTextView.setText(ConstantValues.defaultAppDate.format(c.getTime()));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        debitAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("transactionId", transactionId);
                bundle.putString(ConstantValues.intentAccountSearchTitle, "Debit Account");
                Intent intent = new Intent(RecordExpenseActivity.this, AccountSearchResultActivity.class);
//                intent.putExtra(ConstantValues.intentAccountSearchList, (Serializable) creditList);
//                intent.putExtra(ConstantValues.intentAccountSearchTitle, "Debit Account");
                intent.putExtras(bundle);
                startActivityForResult(intent, DEBITACCOUNT);
            }
        });
        creditAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("transactionId", transactionId);
                bundle.putString(ConstantValues.intentAccountSearchTitle, "Credit Account");

                Intent intent = new Intent(RecordExpenseActivity.this, AccountSearchResultActivity.class);
//                intent.putExtra(ConstantValues.intentAccountSearchList, (Serializable) creditList);
//                intent.putExtra(ConstantValues.intentAccountSearchTitle, "Credit Account");
                intent.putExtras(bundle);
                startActivityForResult(intent, CREDITACCOUNT);
            }
        });
        contraAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("transactionId", transactionId);
                bundle.putString(ConstantValues.intentAccountSearchTitle, "Contra Account");
                Intent intent = new Intent(RecordExpenseActivity.this, AccountSearchResultActivity.class);
//                intent.putExtra(ConstantValues.intentAccountSearchList, (Serializable) creditList);
//                intent.putExtra(ConstantValues.intentAccountSearchTitle, "Contra Account");
                intent.putExtras(bundle);
                startActivityForResult(intent, CONTRAACCOUNT);
            }
        });

        getDebitCreditAccount();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountEditText.getText().toString().length() > 0 && Integer.parseInt(amountEditText.getText().toString()) > 0) {
                    amountEditText.setError(null);
                    if (!selectedDebitId.equalsIgnoreCase("0")) {
                        if (!selectedCreditId.equalsIgnoreCase("0")) {
                            if (isContraCredit || isContraDebit) {
                                if (!selectedContraId.equalsIgnoreCase("0")) {
                                    save();
                                } else {
                                    Toast.makeText(RecordExpenseActivity.this, "Please select contra account", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                save();
                            }
                        } else {
                            Toast.makeText(RecordExpenseActivity.this, "Please select credit account", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RecordExpenseActivity.this, "Please select debit account", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    amountEditText.setError("Please enter amount");
                }
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuListView = (ListView) findViewById(R.id.left_drawer);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        menuListView.setAdapter(new MenuListViewAdapter(this, 4));
        menuListView.setOnItemClickListener(Menu.getMenuItemCheckListener(this, new MenuListener() {
            @Override
            public void onItemChecked() {
                closeDrawer();
            }
        }));

    }

    private void save() {
        String selectedAccount;
        if (isContraDebit || isContraCredit)
            selectedAccount = "1";
        else
            selectedAccount = "0";
        String url = ConstantValues.serverUrl + "update_expense_details?Book_id=" + new Session(RecordExpenseActivity.this).getUserId()
                + "&Branch_id=" + new Session(RecordExpenseActivity.this).getBranchId()
                + "&User_id=" + new Session(RecordExpenseActivity.this).getUserId()
                + "&Date=" + ConstantValues.defaultDate.format(c.getTime())
                + "&Amount=" + amountEditText.getText().toString()
                + "&Debit_id=" + selectedDebitId
                + "&Credit_id=" + selectedCreditId
                + "&Contra_id=" + selectedContraId
                + "&Select_account=" + selectedAccount
                + "&Notes=" + noteEditText.getText().toString();
        new WebService(RecordExpenseActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    new AlertDialogManager().showAlertDialog(RecordExpenseActivity.this, "Alert", response.getString("message"), true);
                    refresh();
                } else {
                    new AlertDialogManager().showAlertDialog(RecordExpenseActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(RecordExpenseActivity.this, title, message, false);
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

    private void getDebitCreditAccount() {
        transactionId = Integer.parseInt(new Session(RecordExpenseActivity.this).getBranchId());
        String url = ConstantValues.serverUrl + "get_debit_account_details?Branch_id=" + new Session(RecordExpenseActivity.this).getBranchId();
        new WebService(RecordExpenseActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    JSONArray jsonArray = response.getJSONArray("debit_account");
                    creditList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Account account = new Account();
                        account.setName(jsonArray.getJSONObject(i).getString("account_name").replace("-", " "));
                        account.setId(jsonArray.getJSONObject(i).getString("account_id"));
                        creditList.add(account);

                    }
                } else {
                    new AlertDialogManager().showAlertDialog(RecordExpenseActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(RecordExpenseActivity.this, title, message, false);
            }
        });

    }

    //flag 1 - Debit Account, 2 - Credit Account
    private void getContraData(String debitIt, final int flag) {
        String url = ConstantValues.serverUrl + "get_contra_account?Debit_id=" + debitIt;
        new WebService(RecordExpenseActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {

                if (response.getString("status").equalsIgnoreCase("Success")) {
                    if (flag == 1) {
                        isContraDebit = true;
                    } else if (flag == 2) {
                        isContraCredit = true;
                    }
                    contraList.clear();
                    JSONArray contraJsonArray = response.getJSONArray("contra_data");
                    for (int i = 0; i < contraJsonArray.length(); i++) {
                        Account account = new Account();
                        account.setId(contraJsonArray.getJSONObject(i).getString("account_id"));
                        account.setName(contraJsonArray.getJSONObject(i).getString("account_name").replace("-", " "));
                        contraList.add(account);
                    }
                    if ((isContraCredit || isContraDebit))
                        contraAccountTableRow.setVisibility(View.VISIBLE);
                } else {
                    if (flag == 1) {
                        isContraDebit = false;
                    } else if (flag == 2) {
                        isContraCredit = false;
                    }
                    if (!(isContraCredit || isContraDebit)) {
                        contraAccountTableRow.setVisibility(View.GONE);
                        contraList.clear();
                        selectedContraId = "0";
                    }
                }

            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(RecordExpenseActivity.this, title, message, false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DEBITACCOUNT:
                if (resultCode == RESULT_OK) {
                    selectedDebitId = data.getExtras().getString(ConstantValues.intentAccountSearchResult);
                    setDebitAccount();
                } else {

                }
                break;
            case CREDITACCOUNT:
                if (resultCode == RESULT_OK) {
                    selectedCreditId = data.getExtras().getString(ConstantValues.intentAccountSearchResult);
                    setCreditAccount();
                } else {

                }
                break;
            case CONTRAACCOUNT:
                if (resultCode == RESULT_OK) {
                    selectedContraId = data.getExtras().getString(ConstantValues.intentAccountSearchResult);
                    setContraAccount();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDebitAccount() {
        debitAccountTextView.setText(getDebitAccountName());
        getContraData(selectedDebitId, 1);
    }

    private String getDebitAccountName() {
        String result = "";
        for (Account account : creditList) {
            if (account.getId().equalsIgnoreCase(selectedDebitId)) {
                return account.getName();
            }
        }
        return result;
    }

    private void setCreditAccount() {
        creditAccountTextView.setText(getCreditAccountName());
        getContraData(selectedCreditId, 2);
    }

    private String getCreditAccountName() {
        String result = "";
        for (Account account : creditList) {
            if (account.getId().equalsIgnoreCase(selectedCreditId)) {
                return account.getName();
            }
        }
        return result;
    }

    private void setContraAccount() {
        contraAccountTextView.setText(getContraAccountName());
    }

    private String getContraAccountName() {
        String result = "";
        for (Account account : contraList) {
            if (account.getId().equalsIgnoreCase(selectedContraId)) {
                return account.getName();
            }
        }
        return result;
    }

    private void refresh() {
        c = Calendar.getInstance();
        dateTextView.setText(ConstantValues.defaultAppDate.format(c.getTime()));
        amountEditText.setText("");
        selectedDebitId = "0";
        selectedContraId = "0";
        selectedCreditId = "0";
        debitAccountTextView.setText("");
        creditAccountTextView.setText("");
        contraAccountTextView.setText("");
        contraAccountTableRow.setVisibility(View.GONE);
        isContraDebit = false;
        isContraCredit = false;
        noteEditText.setText("");

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
