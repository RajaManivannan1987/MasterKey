package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.activityClasses.TransactionDetailActivity;
import com.imaginetventures.masterkey.modelClasses.TransactionListData;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sanjay on 3/25/16.
 */
public class TransactionListRecyclerViewAdapter extends RecyclerView.Adapter<TransactionListRecyclerViewAdapter.CustomViewHolder> implements
        StickyRecyclerHeadersAdapter {
    private Context context;
    private List<TransactionListData> list;
    private LayoutInflater layoutInflater;
    private String TAG = TransactionListRecyclerViewAdapter.class.getSimpleName();
    private List<String> dateListData = new ArrayList<String>();

    public TransactionListRecyclerViewAdapter(Activity context1, List<TransactionListData> list, List<String> dateListData) {
        context = context1;
        this.list = list;
        this.dateListData = dateListData;
        layoutInflater = context1.getLayoutInflater();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.transaction_list_recyclerview_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {

        holder.nameTextView.setText(list.get(position).getCustomerName());
        holder.statusTextView.setText(list.get(position).getStatus());
        if (list.get(position).getStatus().equalsIgnoreCase("Payment pending")) {
            holder.statusTextView.setBackgroundResource(R.drawable.status_background_pending);
        } else if (list.get(position).getStatus().equalsIgnoreCase("Scheduled")) {
            holder.statusTextView.setBackgroundResource(R.drawable.status_background_scheduled);
        } else if (list.get(position).getStatus().equalsIgnoreCase("Completed")) {
            holder.statusTextView.setBackgroundResource(R.drawable.status_background_completed);
        }
        holder.locationTextView.setText(list.get(position).getCustomerAddress());
        holder.timeTextView.setText(list.get(position).getTime());
        List<String> services = list.get(position).getServiceName();
        String serviceText = "";
        for (String s : services) {
            if (serviceText != "")
                serviceText += ",";
            serviceText += s;
        }
        holder.servicesTextView.setText(serviceText);
        holder.orderIdTextView.setText(list.get(position).getJobId());
        holder.mobileTextView.setText(list.get(position).getMobileNo());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, TransactionDetailActivity.class).putExtra(ConstantValues.intentTransactionId, list.get(position).getTransactionId()));
            }
        });

    }

    @Override
    public long getHeaderId(int position) {
        return positionDate(list.get(position).getDate());
    }

    private int positionDate(String date) {
        for (int i = 0; i < dateListData.size(); i++) {
            if (date.equalsIgnoreCase(dateListData.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.transaction_list_item_header, parent, false);
        CustomViewHolderHeader holder = new CustomViewHolderHeader(view);
        return holder;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.dateTetxView);
        textView.setText(list.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, statusTextView, locationTextView, timeTextView, servicesTextView, mobileTextView, orderIdTextView;
        public LinearLayout linearLayout;

        public CustomViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            statusTextView = (TextView) itemView.findViewById(R.id.statusTextView);
            locationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            servicesTextView = (TextView) itemView.findViewById(R.id.servicesTextView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            mobileTextView = (TextView) itemView.findViewById(R.id.mobileNumberTextView);
            orderIdTextView = (TextView) itemView.findViewById(R.id.orderIdTextView);
        }
    }

    protected class CustomViewHolderHeader extends RecyclerView.ViewHolder {
        public CustomViewHolderHeader(View itemView) {
            super(itemView);
        }
    }
}
