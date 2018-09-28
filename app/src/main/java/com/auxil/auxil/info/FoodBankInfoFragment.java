package com.auxil.auxil.info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
            assert foodBank != null;

            if (!foodBank.name().isEmpty()) {
                TextView name = view.findViewById(R.id.food_bank_name);
                name.setText(foodBank.name());
            }
            if (!foodBank.address().isEmpty()) {
                TextView address = view.findViewById(R.id.food_bank_address);
                address.setText(foodBank.address());
            }
            if (!foodBank.number().isEmpty()) {
                Button number = view.findViewById(R.id.food_bank_number);
                number.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                        phoneIntent.setData(Uri.parse("tel:" + foodBank.number()));
                        if (getContext() != null) getContext().startActivity(phoneIntent);
                    }
                });
            }
            // Sets the website link on button
            if (!foodBank.website().isEmpty()) {
                Button website = view.findViewById(R.id.food_bank_website);
                website.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent =
                                new Intent(Intent.ACTION_VIEW, Uri.parse(foodBank.website()));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (getContext() != null) getContext().startActivity(browserIntent);
                    }
                });
            }
        }
        return view;
    }
}
