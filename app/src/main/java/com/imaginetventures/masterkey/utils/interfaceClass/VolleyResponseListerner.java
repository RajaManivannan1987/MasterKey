package com.imaginetventures.masterkey.utils.interfaceClass;


import org.json.JSONException;
import org.json.JSONObject;

public interface VolleyResponseListerner {

      void onResponse(JSONObject response) throws JSONException;

    void onError(String message, String title);
}
