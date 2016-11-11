package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.modelClasses.InterBranchDues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IM028 on 6/7/16.
 */
public class DashboardInterBranchDuesAdapter extends RecyclerView.Adapter<DashboardInterBranchDuesAdapter.CustomView> {
    private List<InterBranchDues> listData = new ArrayList<InterBranchDues>();
    private LayoutInflater inflater;

    public DashboardInterBranchDuesAdapter(Activity activity, List<InterBranchDues> listData) {
        this.listData = listData;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public CustomView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomView(inflater.inflate(R.layout.list_item_dashboard_interbranch, parent, false));
    }

    @Override
    public void onBindViewHolder(CustomView holder, int position) {
        holder.nameTextView.setText(listData.get(position).getAccount_name());
        int amount = 0;
        try {
            amount = Integer.parseInt(listData.get(position).getDebit()) - Integer.parseInt(listData.get(position).getCredit());
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.amountTextView.setText(amount + "");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class CustomView extends RecyclerView.ViewHolder {
        public TextView nameTextView, amountTextView;

        public CustomView(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.itemListDashboardInterBranchNameTextView);
            amountTextView = (TextView) itemView.findViewById(R.id.itemListDashboardInterBranchDuesTextView);
        }
    }
}
