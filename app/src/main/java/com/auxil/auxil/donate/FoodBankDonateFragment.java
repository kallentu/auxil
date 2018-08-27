package com.auxil.auxil.donate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.UserDataResponse;
import com.auxil.auxil.R;

import java.util.HashSet;
import java.util.Set;

/** Fragment for donating food and money to a selected food bank. */
public class FoodBankDonateFragment extends Fragment {
    /** Store data related to purchase receipts. */
//    private InAppPurchaseManager inAppPurchaseManager;

    /** Data pertaining to who the current user is. */
    private String currentUserId =  null;
    private String currentMarketplace =  null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setUpIAP();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_bank_donate, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Set<String> productSkus =  new HashSet<>();
        productSkus.add( "com.amazon.example.iap.consumable" );
        productSkus.add( "com.amazon.example.iap.entitlement" );
        productSkus.add( "com.amazon.example.iap.subscription" );

        PurchasingService.getUserData();
        PurchasingService.getPurchaseUpdates(false);
        PurchasingService.getProductData(productSkus);
    }

    public void onUserDataResponse(final UserDataResponse response) {
        final UserDataResponse.RequestStatus status = response.getRequestStatus();

        switch (status) {
            case SUCCESSFUL:
                currentUserId = response.getUserData().getUserId();
                currentMarketplace = response.getUserData().getMarketplace();
                break;
            case FAILED:
            case NOT_SUPPORTED:
                break;
        }
    }

//    private void setUpIAP() {
//        inAppPurchaseManager = new InAppPurchaseManager(this);
//        final InAppPurchaseListener purchaseListener =
//                new InAppPurchaseListener(inAppPurchaseManager);
//
//        PurchasingService.registerListener(this.getContext(), purchaseListener);
//    }

}
