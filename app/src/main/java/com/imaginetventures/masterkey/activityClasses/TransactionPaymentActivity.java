package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.modelClasses.Account;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionPaymentActivity extends AppCompatActivity {
    private final String TAG = TransactionPaymentActivity.class.getName();
    private final int DEBITACCOUNT = 0, CONTRAACCOUNT = 1;
    private EditText amountJobEditText, creditEditText;
    private TextView debitEditText, contraEditText;
    private Button submitButton;
    private int transactionId = 0;
    private List<Account> contraList = new ArrayList<Account>();
    private List<Account> debitList = new ArrayList<Account>();
    private int isContra = 0;
    private String selectedContraId = "0", selectedDebitId = "0";
    private JSONObject jsonObject;
    private TableRow contraAccountTableRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_payment);

        amountJobEditText = (EditText) findViewById(R.id.transactionPaymentActivityJobAmountEditText);
        creditEditText = (EditText) findViewById(R.id.transactionPaymentActivityCreditAccountEditText);
        debitEditText = (TextView) findViewById(R.id.transactionPaymentActivityDebitAccountEditText);
        contraAccountTableRow = (TableRow) findViewById(R.id.transactionPaymentActivityContraAccountTableRow);
        contraEditText = (TextView) findViewById(R.id.transactionPaymentActivityContraAccountEditText);
        submitButton = (Button) findViewById(R.id.transactionPaymentActivitySaveButton);
        transactionId = getIntent().getExtras().getInt(ConstantValues.intentTransactionId, 0);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountJobEditText.getText().toString().length() > 0 && Integer.parseInt(amountJobEditText.getText().toString()) > 0) {
                    amountJobEditText.setError(null);
                    if (!selectedDebitId.equalsIgnoreCase("0")) {
                        debitEditText.setError(null);
                        if (isContra == 0 || !selectedContraId.equalsIgnoreCase("0")) {
                            contraEditText.setError(null);
                            try {
                                save();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        } else {
                            contraEditText.setError("Select Contra Account");
                            contraEditText.requestFocus();
                        }
                    } else {
                        debitEditText.setError("Select Debit Account");
                        debitEditText.requestFocus();
                    }
                } else {
                    amountJobEditText.setError("Enter valid amount");
                    amountJobEditText.requestFocus();
                }
            }
        });
        debitEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("transactionId", transactionId);
                bundle.putString(ConstantValues.intentAccountSearchTitle, "Debit Account");
                Intent intent = new Intent(TransactionPaymentActivity.this, AccountSearchResultActivity.class);

                //    command by raja. //  this is karthick old code

//                intent.putExtra(ConstantValues.intentAccountSearchList, (Serializable) debitList);
//                intent.putExtra("transactionId", transactionId);
//                intent.putExtra(ConstantValues.intentAccountSearchTitle, "Debit Account");
                intent.putExtras(bundle);
                startActivityForResult(intent, DEBITACCOUNT);
            }
        });
        contraEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionPaymentActivity.this, AccountSearchResultActivity.class);
                intent.putExtra(ConstantValues.intentAccountSearchList, (Serializable) contraList);
                intent.putExtra(ConstantValues.intentAccountSearchTitle, "Credit Account");
                intent.putExtra("id", transactionId);
                startActivityForResult(intent, CONTRAACCOUNT);
            }
        });

        getData();
    }

    private void save() throws JSONException {
        String url = ConstantValues.serverUrl + "update_payment_details?Amount=" + amountJobEditText.getText().toString()
                + "&Debit_id=" + selectedDebitId
                + "&Credit_id=" + jsonObject.getString("account_id")
                + "&Transaction_id=" + jsonObject.getString("Transaction_id")
                + "&User_id=" + new Session(TransactionPaymentActivity.this).getUserId()
                + "&Payment_contra_account=" + isContra
                + "&Payment_contra_id=" + selectedContraId;
        new WebService(TransactionPaymentActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    Toast.makeText(TransactionPaymentActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    new AlertDialogManager().showAlertDialog(TransactionPaymentActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(TransactionPaymentActivity.this, title, message, false);
            }
        });
    }

    private void getData() {
        String url = ConstantValues.serverUrl + "get_record_payment?Transaction_id=" + transactionId;
        new WebService(TransactionPaymentActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                Log.d(TAG, response.toString());
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    jsonObject = response.getJSONObject("payment_data");
                    debitList.clear();
                    JSONArray debitJsonArray = jsonObject.getJSONArray("debit_account");
                    for (int i = 0; i < debitJsonArray.length(); i++) {
                        Account account = new Account();
                        account.setId(debitJsonArray.getJSONObject(i).getString("account_id"));
                        account.setName(debitJsonArray.getJSONObject(i).getString("account_name").replace("-", " "));
                        debitList.add(account);
                    }
                    creditEditText.setText(jsonObject.getString("account_name"));
                    creditEditText.setEnabled(false);
                    amountJobEditText.setText(jsonObject.getString("Total_amount"));
                } else {
                    new AlertDialogManager().showAlertDialog(TransactionPaymentActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(TransactionPaymentActivity.this, title, message, false);
            }
        });
    }

    private void getContraData(final String debitIt) {
        selectedContraId = "0";
        String url = ConstantValues.serverUrl + "get_contra_account?Debit_id=" + debitIt;
        new WebService(TransactionPaymentActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                contraList.clear();
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    isContra = 1;
                    selectedContraId = "0";
                    JSONArray contraJsonArray = response.getJSONArray("contra_data");
                    for (int i = 0; i < contraJsonArray.length(); i++) {
                        Account account = new Account();
                        account.setId(contraJsonArray.getJSONObject(i).getString("account_id"));
                        account.setName(contraJsonArray.getJSONObject(i).getString("account_name").replace("-", " "));
                        contraList.add(account);
                    }
                    contraAccountTableRow.setVisibility(View.VISIBLE);
                } else {
                    contraAccountTableRow.setVisibility(View.GONE);
                    isContra = 0;
                }

            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(TransactionPaymentActivity.this, title, message, false);
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
        debitEditText.setText(getDebitAccountName());
        getContraData(selectedDebitId);
    }

    private String getDebitAccountName() {
        String result = "";
        for (Account account : debitList) {
            if (account.getId().equalsIgnoreCase(selectedDebitId)) {
                return account.getName();
            }
        }
        return result;
    }

    private void setContraAccount() {
        contraEditText.setText(getContraAccountName());
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

}
