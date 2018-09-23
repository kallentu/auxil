package com.auxil.auxil.info;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auxil.auxil.FoodBank;
import com.auxil.auxil.R;

/** Fragment for displaying information from a selected food bank. */
public class FoodBankInfoFragment extends Fragment {
    private FoodBank foodBank;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_bank_info, container, false);

        // Updates the views with correct information
        if (getArguments() != null) {
            foodBank = (FoodBank) getArguments().getSerializable("foodbank");
            TextView name = view.findViewById(R.id.food_bank_name);
            TextView address = view.findViewById(R.id.food_bank_address);
            TextView number = view.findViewById(R.id.food_bank_number);
            TextView website = view.findViewById(R.id.food_bank_website);

            if (!foodBank.name().isEmpty()) name.setText(foodBank.name());
            if (!foodBank.address().isEmpty()) address.setText(foodBank.address());
            if (!foodBank.number().isEmpty()) number.setText(foodBank.number());
            if (!foodBank.website().isEmpty()) website.setText(foodBank.website());
        }
        return view;
    }
}
