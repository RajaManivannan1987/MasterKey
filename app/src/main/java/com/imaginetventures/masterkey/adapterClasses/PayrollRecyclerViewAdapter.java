package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.activityClasses.PayRollDetailActivity;
import com.imaginetventures.masterkey.modelClasses.EmployeeData;
import com.imaginetventures.masterkey.modelClasses.EmployeePayRollData;
import com.imaginetventures.masterkey.utils.AlertDialogManager;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;
import com.imaginetventures.masterkey.utils.WebService;
import com.imaginetventures.masterkey.utils.interfaceClass.VolleyResponseListerner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IM028 on 4/4/16.
 */
public class PayrollRecyclerViewAdapter extends RecyclerView.Adapter<PayrollRecyclerViewAdapter.CustomViewHolder> {
    private String TAG = "PayrollRecyclerViewAdapter";
    private final int HEADERTYPE = 1;
    private final int CONTENTTYPE = 2;
    private LayoutInflater inflater;
    private Activity context;
    private List<EmployeePayRollData> listData = new ArrayList<EmployeePayRollData>();
    private Calendar calendar = Calendar.getInstance();

    public PayrollRecyclerViewAdapter(Activity context) {
        inflater = context.getLayoutInflater();
        this.context = context;
//        getEmployeeData();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADERTYPE)
            view = inflater.inflate(R.layout.payroll_recycler_view_first_item, parent, false);
        else
            view = inflater.inflate(R.layout.payroll_recycler_view_content_item, parent, false);
        return new CustomViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case HEADERTYPE:
                holder.headerContentDayYear.setText(ConstantValues.defaultAppDateMonthYear.format(calendar.getTime()));
                holder.headerPreviousImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isValidMonthPrev()) {
                            calendar.add(Calendar.MONTH, -1);
                            getEmployeeData();
                        } else {
                            new AlertDialogManager().showAlertDialog(context, "Alert", "Data Restricted", false);
                        }
                    }
                });
                holder.headerNextImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isValidMonthNext()) {
                            calendar.add(Calendar.MONTH, 1);
                            getEmployeeData();
                        } else {
                            new AlertDialogManager().showAlertDialog(context, "Alert", "Data Restricted", false);
                        }
                    }
                });
                break;
            case CONTENTTYPE:
                holder.contentTextView.setText(listData.get(position).getEmployeeName());
                holder.contentLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, PayRollDetailActivity.class).putExtra(ConstantValues.intentEmployeeId, listData.get(position).getEmployeeId()).putExtra(ConstantValues.intentMonthYear, ConstantValues.defaultAppDateMonthYear.format(calendar.getTime())));
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return CONTENTTYPE;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView headerPreviousImageView, headerNextImageView;
        public TextView headerContentDayYear;

        public TextView contentTextView;
        public LinearLayout contentLinearLayout;

        public CustomViewHolder(View itemView, int type) {
            super(itemView);
            switch (type) {
                case 1:
                    headerPreviousImageView = (ImageView) itemView.findViewById(R.id.payrollActivityPrevMonthImageView);
                    headerNextImageView = (ImageView) itemView.findViewById(R.id.payrollActivityNextMonthImageView);
                    headerContentDayYear = (TextView) itemView.findViewById(R.id.payrollActivityMonthYearTextView);
                    break;
                case 2:
                    contentLinearLayout = (LinearLayout) itemView.findViewById(R.id.payrollRecyclerViewContentLinearLayout);
                    contentTextView = (TextView) itemView.findViewById(R.id.payrollRecyclerViewContentTextView);
                    break;
            }

        }
    }

    public void getEmployeeData() {
        String url = ConstantValues.serverUrl + "payroll?Branch_id=" + new Session(context).getBranchId() + "&month=" + ConstantValues.defaultAppDate2.format(calendar.getTime());
        new WebService(context, TAG).volleyGetData(url, new VolleyResponseListerner() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                listData.clear();
                if (response.getString("status").equalsIgnoreCase("Success")) {
                    JSONArray jsonArray = response.getJSONArray("employees");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        EmployeePayRollData employeePayRollData = new EmployeePayRollData();
                        employeePayRollData.setEmployeeName(jsonArray.getJSONObject(i).getString("Employee_name"));
                        employeePayRollData.setEmployeeId(jsonArray.getJSONObject(i).getString("Employee_id"));
                        listData.add(employeePayRollData);
                    }
                } else {
                    new AlertDialogManager().showAlertDialog(context, "Alert", response.getString("message"), false);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(String message, String title) {
                listData.clear();
                new AlertDialogManager().showAlertDialog(context, title, message, false);
                notifyDataSetChanged();
            }
        });
    }

    private boolean isValidMonthNext() {
        Calendar calendar1 = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)) {
            return false;
        }
        return true;
    }

    private boolean isValidMonthPrev() {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, -2);
        if (calendar.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
            return false;
        }
        return true;
    }
}
