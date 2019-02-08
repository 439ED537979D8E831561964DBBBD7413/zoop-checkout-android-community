package com.zoop.checkout.app.API;

import com.zoop.checkout.app.Model.APIKeys;
import com.zoop.checkout.app.Model.LoginRequest;
import com.zoop.checkout.app.Model.Marketplace;
import com.zoop.checkout.app.Model.User;
import com.zoop.checkout.app.Model.UserPermissions;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ReceiptService {
    @GET
    Call<Object> getReceipt(@Header("Authorization") String authorization, @Url String url);
}
