package com.zoop.checkout.app;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created by sander on 08/03/2018.
 */

public class CheckoutApplication extends Application {

    public static FirebaseAnalytics mFirebaseAnalytics;
    private static CheckoutApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    public static CheckoutApplication getInstance() {
        if (instance == null)
            instance = new CheckoutApplication();
        return instance;
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
}
