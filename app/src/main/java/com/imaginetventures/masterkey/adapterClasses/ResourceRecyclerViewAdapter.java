package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.activityClasses.ResourceActivity;
import com.imaginetventures.masterkey.modelClasses.Resource;
import com.imaginetventures.masterkey.utils.ConstantValues;
import com.imaginetventures.masterkey.utils.ResourceDialog;

import java.util.Calendar;
import java.util.List;

/**
 * Created by IM028 on 6/8/16.
 */
public class ResourceRecyclerViewAdapter extends RecyclerView.Adapter<ResourceRecyclerViewAdapter.ResourceViewHolder> {
    private ResourceActivity activity;
    private List<Resource> resourceList;
    private LayoutInflater inflater;


    public ResourceRecyclerViewAdapter(ResourceActivity activity, List<Resource> resourceList) {
        this.activity = activity;
        this.resourceList = resourceList;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResourceViewHolder(inflater.inflate(R.layout.item_list_resource, parent, false));
    }

    @Override
    public void onBindViewHolder(ResourceViewHolder holder, final int position) {
        holder.nameTextView.setText(resourceList.get(position).getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(resourceList.get(position).getLastService().getTime());
        calendar.add(Calendar.DAY_OF_MONTH, resourceList.get(position).getPeriodicity());
        holder.nextServiceTextView.setText(ConstantValues.defaultAppDate.format(calendar.getTime()));
        holder.serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResourceDialog dialog = new ResourceDialog(activity, resourceList.get(position).getName(), resourceList.get(position).getPeriodicity() + "", resourceList.get(position).getId());
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return resourceList.size();
    }


    class ResourceViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, nextServiceTextView;
        public Button serviceButton;

        public ResourceViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.itemListResourceNameTextView);
            nextServiceTextView = (TextView) itemView.findViewById(R.id.itemListResourceNextServiceDateTextView);
            serviceButton = (Button) itemView.findViewById(R.id.itemListResourceServiceDateButton);
        }
    }

}
