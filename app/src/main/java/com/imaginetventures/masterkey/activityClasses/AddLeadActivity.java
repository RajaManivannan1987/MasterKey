package com.imaginetventures.masterkey.activityClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.adapterClasses.MenuListViewAdapter;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.Menu;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.MenuListener;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONException;
import org.json.JSONObject;

public class AddLeadActivity extends AppCompatActivity {
    private String TAG = "AddLeadActivity";
    private DrawerLayout drawerLayout;
    private ListView menuListView;
    private EditText mobileNumberEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lead);

        mobileNumberEditText = (EditText) findViewById(R.id.addLeadActivityMobileNumberEditText);
        submitButton = (Button) findViewById(R.id.addLeadActivitySubmitButton);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuListView = (ListView) findViewById(R.id.left_drawer);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        menuListView.setAdapter(new MenuListViewAdapter(this, 2));
        menuListView.setOnItemClickListener(Menu.getMenuItemCheckListener(this, new MenuListener() {
            @Override
            public void onItemChecked() {
                closeDrawer();
            }
        }));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNumberEditText.getText().toString().trim().length() == 10)
                    save();
                else
                    new AlertDialogManager().showAlertDialog(AddLeadActivity.this, "Alert", "Please Enter valid mobile number", false);
            }
        });
    }

    private void closeDrawer() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    private void save() {
        String url = ConstantValues.serverUrl + "check_phone_number?Phone_number=" + mobileNumberEditText.getText().toString().trim();
        new WebService(AddLeadActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    mobileNumberEditText.setText("");
                    new AlertDialogManager().alertBox(AddLeadActivity.this, "Alert", response.getString("message"));
                } else {
//                    new AlertDialogManager().showAlertDialog(AddLeadActivity.this, "Alert", response.getString("message"), false);
                    startActivity(new Intent(AddLeadActivity.this, AddingLeadActivity.class).putExtra("data", mobileNumberEditText.getText().toString()));
                    finish();
                }
            }

            @Override
            public void onError(String message, String title) {
                new AlertDialogManager().showAlertDialog(AddLeadActivity.this, title, message, false);
            }
        });
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
