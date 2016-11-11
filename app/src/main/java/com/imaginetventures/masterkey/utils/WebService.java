package com.imaginetventures.masterkey.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sanjay on 3/24/16.
 */
public class WebService {
    private Activity act;
    private String TAG="";
    String networkErrorMessage="Network error – please try again.";
    String poorNetwork="Your data connection is too slow – please try again when you have a better network connection";
    String timeout="Response timed out – please try again.";
    String authorizationFailed="Authorization failed – please try again.";
    String serverNotResponding="Server not responding – please try again.";
    String parseError="Data not received from server – please try again.";

    String networkErrorTitle="Network error";
    String poorNetworkTitle="Poor Network Connection";
    String timeoutTitle="Network Error";
    String authorizationFailedTitle="Network Error";
    String serverNotRespondingTitle="Server Error";
    String parseErrorTitle="Network Error";
    public WebService(Activity context,String TAG){
        this.act=context;
        this.TAG=TAG+" WebService";
    }
    public void volleyGetData(final String url, final VolleyResponseListerner listener) {


        final ProgressDialog pDialog = new ProgressDialog(act);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        RequestQueue queue = Volley.newRequestQueue(act);
        Log.d(TAG,"volleyGetData request url - "+url);
        if (isOnline()) {
            pDialog.show();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, (String) null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG,"volleyGetData response - "+ response.toString());
                            // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                            try {
                                listener.onResponse(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //result = response;
                            pDialog.dismiss();
                            // Log.d("result", result.toString());

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // VolleyLog.d("VolleyError", "Error: " + error.getMessage());
                    pDialog.dismiss();
                    if (error instanceof TimeoutError){
                        //listener.onError(error.toString());

                        listener.onError(timeout,timeoutTitle);
                   /* Toast.makeText(act,
                            "Bad Network, Try again",

                            Toast.LENGTH_LONG).show();*/
                    }else if( error instanceof NoConnectionError) {
                        //listener.onError(error.toString());
                        listener.onError(poorNetwork,poorNetworkTitle);
                   /* Toast.makeText(act,
                            "Bad Network, Try again",

                            Toast.LENGTH_LONG).show();*/
                    } else if (error instanceof AuthFailureError) {
                        //listener.onError(error.toString());
                        //act.startActivity(new Intent(act, CrashScreenActivity.class));
                        listener.onError(authorizationFailed,authorizationFailedTitle);

                    /*Toast.makeText(act,
                            "Auth failed",

                            Toast.LENGTH_LONG).show();*/
                    } else if (error instanceof ServerError) {
                        //listener.onError(error.toString());
//                        act.startActivity(new Intent(act, CrashScreenActivity.class));
                        listener.onError(serverNotResponding,serverNotRespondingTitle);
                   /* Toast.makeText(act,
                            "Server Not Responding",

                            Toast.LENGTH_LONG).show();*/
                    } else if (error instanceof NetworkError) {
                        //listener.onError(error.toString());
//                        act.startActivity(new Intent(act, CrashScreenActivity.class));
                        listener.onError(networkErrorMessage,networkErrorTitle);
                    /*Toast.makeText(act,
                            "Network Error",

                            Toast.LENGTH_LONG).show();*/
                    } else if (error instanceof ParseError) {
                        // listener.onError(error.toString());
//                        act.startActivity(new Intent(act, CrashScreenActivity.class));
                        listener.onError(parseError,parseErrorTitle);
                   /* Toast.makeText(act,
                            "try again"+error.getMessage(),

                            Toast.LENGTH_LONG).show();*/
                    }


                }

            });
            int MY_SOCKET_TIMEOUT_MS = 30000;
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjReq);

        } else {
            new AlertDialogManager().alertBox(act, poorNetworkTitle, poorNetwork);
        }
        //return result;
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
