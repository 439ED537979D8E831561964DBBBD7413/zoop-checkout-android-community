package com.zoop.checkout.app;

import android.app.Activity;

import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.sessions.Retrofit;

import java.util.concurrent.CountDownLatch;

/**
 * Created by mainente on 04/08/17.
 */

public class CallUpdateApiParameters {

    private APIParametersThread apiParametersThread = null;

    private CountDownLatch countDownLatchAPIInitialization = null;

    private Activity activity;


    public void initializeApiParameters(Activity activity) {
        this.activity = activity;
        countDownLatchAPIInitialization = new CountDownLatch(1);
        apiParametersThread = new APIParametersThread();
        apiParametersThread.start();

        //TransactionQueueReversal.getInstance();
    }

    private static CallUpdateApiParameters instance = null;


    public static CallUpdateApiParameters getInstance() {
        if (null == instance) {
            instance = new CallUpdateApiParameters();

        }
        return instance;
    }

    public void waitInitialization() {
        if (null != countDownLatchAPIInitialization) {
            try {
                countDownLatchAPIInitialization.await();
                ZLog.t(677122);
            } catch (Exception e) {
                ZLog.exception(677044, e);
            }
        }
    }

    private class APIParametersThread extends Thread {
        public APIParametersThread() {
            super("Zoop APT");
        }

        public void run() {
            try {
                //APIParameters.setParameterSetId();
                APIParameters ap = APIParameters.getInstance();
                String sellerId = APIParameters.getInstance().getStringParameter("sellerId");
                ap.processAPIParametersInitialization(sellerId);

                ap.updateAPIParameters(activity);

                countDownLatchAPIInitialization.countDown();
                countDownLatchAPIInitialization = null;
            } catch (Exception e) {
                ZLog.exception(677043, e);
            }
        }

        ;
    }

}
