package com.imaginetventures.masterkey.adapterClasses;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.modelClasses.EmployeeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sanjay on 3/29/16.
 */
public class EmployeeListRecyclerViewAdapter extends RecyclerView.Adapter<EmployeeListRecyclerViewAdapter.CustomViewHolder> {
    private String TAG = "EmployeeListRecyclerViewAdapter";
    private List<EmployeeData> data = new ArrayList<EmployeeData>(), dataBackup = new ArrayList<EmployeeData>();
    private Context context;
    private List<String> selectedEmployee = new ArrayList<String>();
    private LayoutInflater layoutInflater;

    public EmployeeListRecyclerViewAdapter(Activity context, List<EmployeeData> data) {
        this.context = context;
        this.data = data;
        this.dataBackup = data;
        layoutInflater = context.getLayoutInflater();
        setSelectedEmployee();
    }

    public void setSelectedEmployee() {
        selectedEmployee.clear();
        for (EmployeeData emp : data) {
            if (emp.getFlag().equalsIgnoreCase("1")) {
                selectedEmployee.add(emp.getId());
            }
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.employee_list_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        holder.checkBox.setText(data.get(position).getName());
        holder.checkBox.setChecked(selectedEmployee.contains(data.get(position).getId()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectedEmployee.contains(data.get(position).getId()))
                        selectedEmployee.add(data.get(position).getId());
                } else if (selectedEmployee.contains(data.get(position).getId())) {
                    selectedEmployee.remove(data.get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void searchText(String text) {
        List<EmployeeData> result = new ArrayList<EmployeeData>();
        for (EmployeeData em : dataBackup) {
            if (selectedEmployee.contains(em.getId()) || em.getName().contains(text)) {
                result.add(em);
            }
        }
        data = result;
        notifyDataSetChanged();
    }

    public List<String> getSelectedEmployee() {
        return selectedEmployee;
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;

        public CustomViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.employeeListItemCheckBox);
        }
    }
}
