package com.layoutxml.sabs.model;

import android.arch.lifecycle.MutableLiveData;

public class BillingModel {
    public MutableLiveData<Boolean> isSupportedLiveData;
    public MutableLiveData<Boolean> isPremiumLiveData;
    public MutableLiveData<String> priceLiveData;
    public MutableLiveData<String> threeMonthPriceLiveData;

    public BillingModel() {
        isSupportedLiveData = new MutableLiveData<>();
        isPremiumLiveData = new MutableLiveData<>();
        priceLiveData = new MutableLiveData<>();
        threeMonthPriceLiveData = new MutableLiveData<>();
        isSupportedLiveData.postValue(false);
        isPremiumLiveData.postValue(false);
        priceLiveData.postValue("");
    }
}
