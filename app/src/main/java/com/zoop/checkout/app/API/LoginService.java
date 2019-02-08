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

public interface LoginService {
    @POST("v1/users/signin")
    Call<User> login(@Body LoginRequest loginRequest);

    @GET("v1/users/{user_id}/permissions")
    Call<UserPermissions> getUserPermissions(@Path("user_id") String userId, @Header("Authorization") String authorization);

    @GET("/v1/marketplaces/{marketplace_id}/api_keys")
    Call<APIKeys> getAPIKeys(@Path("marketplace_id") String marketplaceId, @Header("Authorization") String authorization);

    @GET("v1/marketplaces/{marketplace_id}")
    Call<Marketplace> getMarketplace(@Path("marketplace_id") String marketplaceId , @Header("Authorization") String authorization);

    @GET("v1/marketplaces/{marketplace_id}/sellers/{seller_id}")
    Call<Object> getSeller(@Path("marketplace_id") String marketplaceId, @Path("seller_id") String sellerId , @Header("Authorization") String authorization);

    @GET("v1/marketplaces/{marketplace_id}/plans")
    Call<Object> getPlans(@Path("marketplace_id") String marketplaceId, @Header("Authorization") String authorization);

    @GET("v1/marketplaces/{marketplace_id}/sellers/{seller_id}/subscriptions")
    Call<Object> getSubscriptions(@Path("marketplace_id") String marketplaceId, @Path("seller_id") String sellerId, @Header("Authorization") String authorization);
}
