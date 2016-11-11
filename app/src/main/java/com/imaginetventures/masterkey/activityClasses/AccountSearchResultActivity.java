package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.AccountSearchAdapter;
import com.imaginetventures.masterkey.modelClasses.Account;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AccountSearchResultActivity extends AppCompatActivity {
    private String TAG = AccountSearchResultActivity.class.getName();
    private List<Account> accountList = new ArrayList<>();
    private ListView listView;
    private SearchView searchView;
    private AccountSearchAdapter adapter;
    private TextView textView;
    private String id;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_search_result);


        listView = (ListView) findViewById(R.id.accountSearchListView);
        searchView = (SearchView) findViewById(R.id.accountSearchSearchView);
        textView = (TextView) findViewById(R.id.accountSearchTitleTextView);

        //    command by raja. //  this is karthick old code

        /*textView.setText(getIntent().getExtras().getString(ConstantValues.intentAccountSearchTitle, "Account"));
        accountList = (List<Account>) getIntent().getSerializableExtra(ConstantValues.intentAccountSearchList);*/

        //    developed by raja. //  this is raja new code

        Bundle bundle = this.getIntent().getExtras();
        int id = bundle.getInt("transactionId");
        textView.setText(bundle.getString(ConstantValues.intentAccountSearchTitle, "Account"));
        getData(id);
        //   finish -----

        adapter = new AccountSearchAdapter(this, accountList);
        listView.setAdapter(adapter);
        searchView.requestFocus();


        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.accountSearchIdTextView);
                setResult(RESULT_OK, new Intent().putExtra(ConstantValues.intentAccountSearchResult, textView.getText().toString()));
                finish();
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getData(int id) {
        String url = ConstantValues.serverUrl + "get_record_payment?Transaction_id=" + id;
        new WebService(AccountSearchResultActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                Log.d(TAG, response.toString());
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    jsonObject = response.getJSONObject("payment_data");
                    accountList.clear();
                    JSONArray debitJsonArray = jsonObject.getJSONArray("debit_account");
                    for (int i = 0; i < debitJsonArray.length(); i++) {
                        Account account = new Account();
                        account.setId(debitJsonArray.getJSONObject(i).getString("account_id"));
                        account.setName(debitJsonArray.getJSONObject(i).getString("account_name").replace("-", " "));
                        accountList.add(account);
                    }
                    adapter.notifyDataSetChanged();
                  /*  creditEditText.setText(jsonObject.getString("account_name"));
                    creditEditText.setEnabled(false);
                    amountJobEditText.setText(jsonObject.getString("Total_amount"));*/
                } else {
                    new AlertDialogManager().showAlertDialog(AccountSearchResultActivity.this, "Alert", response.getString("message"), false);
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(AccountSearchResultActivity.this, title, message, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}
