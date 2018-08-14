package com.auxil.auxil.info;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.auxil.auxil.R;

public class FoodBankInfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_food_bank_info, container);
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
