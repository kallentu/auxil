package com.auxil.auxil.donate;

import android.util.Log;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserDataResponse;

import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class InAppPurchaseListener implements PurchasingListener {
//    private final InAppPurchaseManager purchaseManager;
    boolean reset =  false;

//    public InAppPurchaseListener(InAppPurchaseManager inAppPurchaseManager) {
//        purchaseManager = inAppPurchaseManager;
//    }

    @Override
    public void onUserDataResponse(UserDataResponse userDataResponse) {

    }

    public void onProductDataResponse( final ProductDataResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                for ( final String s : response.getUnavailableSkus()) {
                    Log.v(TAG,  "Unavailable SKU:" + s);
                }

                final Map<String, Product> products = response.getProductData();

                for ( final String key : products.keySet()) {
                    Product product = products.get(key);
                    Log.v(TAG, String.format( "Product: %s\n Type: %s\n SKU: %s\n Price: %s\n Description: %s\n" ,
                            product.getTitle(), product.getProductType(), product.getSku(), product.getPrice(), product.getDescription()));
                }
                break;
            case FAILED:
                Log.v(TAG,  "ProductDataRequestStatus: FAILED" );
                break ;
        }
    }

    @Override
    public void onPurchaseResponse(PurchaseResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                final Receipt receipt = response.getReceipt();
//                purchaseManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
//                Log.d(TAG, "onPurchaseResponse: receipt json:" + receipt.toJSON());
//                purchaseManager.handleReceipt(receipt, response.getUserData());
//                purchaseManager.refreshOranges();
                break;
        }
    }

    @Override
    public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                for ( final Receipt receipt : response.getReceipts()) {
                    // TODO: Process receipts
                }

                if (response.hasMore()) {
                    PurchasingService.getPurchaseUpdates(reset);
                }
                break;
            case FAILED:
                break;
        }
    }
}
