package com.example.wendy.myapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wendy.myapp.module.BaseDropdownEntity;
import com.example.wendy.myapp.module.Category;

import java.util.ArrayList;

public class mSpinnerAdapter extends ArrayAdapter<BaseDropdownEntity> {
    private Context context;
    private ArrayList<BaseDropdownEntity> data;

    public mSpinnerAdapter(Context _context,
                           int textViewResourceId,
                           ArrayList<BaseDropdownEntity> _data) {
        super(_context, textViewResourceId, _data);
        this.context = _context;
        this.data = _data;
    }

    @Override
    public  int getCount() {
        return data.size();
    }

    @Override
    public BaseDropdownEntity getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array and the current position
        // You can NOW reference each method you has created in your bean object
        label.setText(data.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(data.get(position).getName());

        return label;
    }

}
