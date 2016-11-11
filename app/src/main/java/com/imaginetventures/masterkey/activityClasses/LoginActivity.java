package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText userNameEditText, passwordEditText;
    private Button loginSubmitButton;
    private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameEditText = (EditText) findViewById(R.id.loginActivityUsernameEditText);
        passwordEditText = (EditText) findViewById(R.id.loginActivityPasswordEditText);
        loginSubmitButton = (Button) findViewById(R.id.loginActivityLoginButton);
        forgotPasswordTextView = (TextView) findViewById(R.id.loginActivityForgotPasswordTextView);

        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameEditText.getText().toString().trim().length() >= 6) {
                    if (passwordEditText.getText().toString().length() >= 6) {
                        try {
                            String url = ConstantValues.serverUrl + "logincheck?" +
                                    "email=" + URLEncoder.encode(userNameEditText.getText().toString().trim(), "UTF-8") + "&" +
                                    "password=" + URLEncoder.encode(passwordEditText.getText().toString(), "UTF-8");
                            new WebService(LoginActivity.this, TAG).volleyGetData(url, new VolleyResponseListerner() {
                                @Override
                                public void onResponse(JSONObject response) throws JSONException {
                                    if (response.getString("status").equalsIgnoreCase("Success")) {
                                        new Session(LoginActivity.this).createSession(response.getJSONObject("logincheck"));
                                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                        finish();
                                    } else {
                                        new AlertDialogManager().showAlertDialog(LoginActivity.this, "Alert", response.getString("message"), false);
                                    }
                                }

                                @Override
                                public void onError(String message, String title) {
                                    new AlertDialogManager().showAlertDialog(LoginActivity.this, title, message, false);
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        passwordEditText.setError("Password must be greater than or equal to 6");
                        passwordEditText.requestFocus();
                    }
                } else {
                    userNameEditText.setError("Check Username");
                    userNameEditText.requestFocus();
                }
            }
        });
    }
}
