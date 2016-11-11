package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.modelClasses.Services;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IM028 on 4/1/16.
 */
public class TransactionPaymentRecyclerViewAdapter extends RecyclerView.Adapter<TransactionPaymentRecyclerViewAdapter.CustomViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Services> data = new ArrayList<Services>();

    public TransactionPaymentRecyclerViewAdapter(Activity context, List<Services> data) {
        this.context = context;
        inflater = context.getLayoutInflater();
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.transaction_payment_recyclerview_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        holder.textView.setText("Periodicity for\n" + data.get(position).getName());
        holder.editText.setHint("Enter " + data.get(position).getName() + " Periodicity");
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                data.get(position).setDays(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<Services> getData() {
        return data;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        private EditText editText;

        public CustomViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.serviceNameTextView);
            editText = (EditText) itemView.findViewById(R.id.serviceDaysEditText);
        }
    }
}
