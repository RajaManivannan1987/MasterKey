package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.modelClasses.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IM028 on 6/2/16.
 */
public class AccountSearchAdapter extends BaseAdapter implements Filterable {
    private List<Account> accountListWithOutFilter = new ArrayList<Account>();
    private List<Account> accountList = new ArrayList<Account>();
    private Activity activity;
    private LayoutInflater layoutInflater;

    public AccountSearchAdapter(Activity activity, List<Account> accountList) {
        Log.d("TransactionPayment", accountList.toString());
        accountListWithOutFilter = accountList;
        this.accountList = accountList;
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_account_search, parent, false);
        }
        TextView nameTextView = (TextView) convertView.findViewById(R.id.accountSearchNameTextView);
        TextView idTextView = (TextView) convertView.findViewById(R.id.accountSearchIdTextView);
        nameTextView.setText(accountList.get(position).getName());
        idTextView.setText(accountList.get(position).getId());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    //no constraint given, just return all the data. (no search)
                    results.count = accountListWithOutFilter.size();
                    results.values = accountListWithOutFilter;
                } else {//do the search
                    List<Account> resultsData = new ArrayList<Account>();
                    String searchStr = constraint.toString().toUpperCase();
                    for (Account o : accountListWithOutFilter)
                        if (o.getName().toUpperCase().contains(searchStr)) resultsData.add(o);
                    results.count = resultsData.size();
                    results.values = resultsData;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                accountList = (ArrayList<Account>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
