package com.imaginetventures.masterkey.utils;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;

/**
 * Created by Sanjay on 3/24/16.
 */
public class ConstantValues {
    //    public static String serverUrl="http://imaginetventures.me/sample/masterkey/webservice/";
    public static final String serverUrl = "http://imaginetventures.me/sample/masterkey/demo/webservice/";

    public static final String intentTransactionId = "intentTransactionId";
    public static final String intentEmployeeId = "intentEmployeeId";
    public static final String intentMonthYear = "intentMonthYear";
    public static final String intentAccountSearchList = "intentAccountSearchList";//
    public static final String intentAccountSearchTitle = "intentAccountSearchTitle";
    public static final String intentAccountSearchResult = "intentAccountSearchResult";

    public static final SimpleDateFormat defaultDate = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat defaultAppDate = new SimpleDateFormat("dd MMM yyyy");
    public static final SimpleDateFormat defaultTime = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat defaultAppTime = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat defaultAppDate1 = new SimpleDateFormat("dd-MM-yyyy");

    public static final SimpleDateFormat defaultAppDate2 = new SimpleDateFormat("MM-yyyy");
    public static final SimpleDateFormat defaultAppDateMonthYear = new SimpleDateFormat("MMMM yyyy");

    public static String urlEncode(String text) {
        String string = "";
        try {
            string = URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {

        }
        return string;
    }

}
