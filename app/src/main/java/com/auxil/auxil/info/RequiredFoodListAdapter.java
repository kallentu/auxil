package com.auxil.auxil.info;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Array adapter for displaying the food items that a food bank needs the most.
 * Displayed in {@link FoodBankInfoFragment}.
 */
public class RequiredFoodListAdapter extends ArrayAdapter<String> {
    RequiredFoodListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
