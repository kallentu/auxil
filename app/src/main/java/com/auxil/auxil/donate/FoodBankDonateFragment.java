package com.auxil.auxil.donate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.auxil.auxil.R;

/** Fragment for donating food and money to a selected food bank. */
public class FoodBankDonateFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_food_bank_donate, container, false);
        setUpRequiredFoodListAdapter(view);
        return view;
    }

    /**
     * Sets up {@link RequiredFoodListAdapter}.
     * Used to set up the food information that food banks want the most.
     */
    private void setUpRequiredFoodListAdapter(View view) {
        ListView infoList = view.findViewById(R.id.food_bank_required_foods_list);
        RequiredFoodListAdapter listAdapter =
                new RequiredFoodListAdapter(view.getContext(), R.layout.adapter_info);
        infoList.setAdapter(listAdapter);
    }
}
