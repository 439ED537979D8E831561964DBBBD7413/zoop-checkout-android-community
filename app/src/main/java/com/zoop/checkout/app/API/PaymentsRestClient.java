package com.zoop.checkout.app.API;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import cz.msebera.android.httpclient.entity.StringEntity;

public class PaymentsRestClient {
    private static final String BASE_URL = "https://api.zoop.ws/v1";

    private AsyncHttpClient client;

    public PaymentsRestClient() {
        this.client = new AsyncHttpClient();
    }

    public PaymentsRestClient(String auth) {
        this.client = new AsyncHttpClient();
        this.client.setBasicAuth(auth, "");
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(Context context, String url, StringEntity entity, ResponseHandlerInterface responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
