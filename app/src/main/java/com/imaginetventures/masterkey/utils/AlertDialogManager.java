/*
 * Copyright (C) 2015 Sanvis Health, LLC
 *
 * All rights reserved.
 *
 * This document contains proprietary information.
 * It is the exclusive confidential property of Sanvis Health, LLC.
 *
 * Copying, disclosure to others, or other use is prohibited
 * without the express, written authorization of Sanvis Health, LLC
 *
 */


package com.imaginetventures.masterkey.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.imaginetventures.masterkey.R;


/**
 * class for alert box
 */

public class AlertDialogManager {



    /**
     * @param context context of activity
     * @param title   alert box title
     * @param message alert box message
     * @param status  alert box icon boolean
     */
    public void showAlertDialog(final Context context, String title, String message,
                                Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if (status != null)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.success : R.drawable.wrong);

            // Setting OK Button

            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();

                }
            });
        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * @param activity context activity
     * @param title    alert box title
     * @param message  alert box message
     * @return
     */
    public void alertBox(final Context activity, String title, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Toast.makeText(activity, "You clicked on YES", Toast.LENGTH_SHORT).show();

                dialog.cancel();
            }
        });


        // Showing Alert Message
        alertDialog.create().show();


    }

}


