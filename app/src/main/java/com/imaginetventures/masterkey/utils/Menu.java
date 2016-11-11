package com.imaginetventures.masterkey.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.imaginetventures.masterkey.activityClasses.AddLeadActivity;
import com.imaginetventures.masterkey.activityClasses.DashboardActivity;
import com.imaginetventures.masterkey.activityClasses.LoginActivity;
import com.imaginetventures.masterkey.activityClasses.RecordExpenseActivity;
import com.imaginetventures.masterkey.activityClasses.ResourceActivity;
import com.imaginetventures.masterkey.activityClasses.TransactionListActivity;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.interfaceClass.MenuListener;

/**
 * Created by IM028 on 6/8/16.
 */
public class Menu {
    public static AdapterView.OnItemClickListener getMenuItemCheckListener(final Activity activity, final MenuListener listener) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        activity.startActivity(new Intent(activity, DashboardActivity.class));
                        break;
                    case 1:
                        activity.startActivity(new Intent(activity, TransactionListActivity.class));
                        break;
                    case 2:
                        activity.startActivity(new Intent(activity, AddLeadActivity.class));
                        break;
                    case 3:
                        activity.startActivity(new Intent(activity, ResourceActivity.class));
                        break;
                    case 4:
                        activity.startActivity(new Intent(activity, RecordExpenseActivity.class));

                        break;
                    case 5:
                        new Session(activity).clearSession();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                        break;

                }
                listener.onItemChecked();
            }
        };
    }
}
