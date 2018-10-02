package com.auxil.auxil.donate;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.auxil.auxil.R;
import com.auxil.auxil.info.FoodBankInfoFragment;

import java.util.ArrayList;

/**
 * Array adapter for displaying the food items that a food bank needs the most.
 * Displayed in {@link FoodBankInfoFragment}.
 */
public class RequiredFoodListAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> requiredFoods;

    public RequiredFoodListAdapter(@NonNull Context context, int resource, ArrayList<String> requiredFoods) {
        super(context, resource);
        this.requiredFoods = requiredFoods;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String foodItem = getItem(position);

        // Checks for existing view already used
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_info, parent, false);
        }

        TextView foodView = (TextView) convertView.findViewById(R.id.food_item);
        foodView.setText(foodItem);

        return convertView;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return requiredFoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
