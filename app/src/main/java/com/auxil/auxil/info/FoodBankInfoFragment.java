package com.auxil.auxil.info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.auxil.auxil.R;

public class FoodBankInfoFragment extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Removes the top nav bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_food_bank_info);
    }
}
