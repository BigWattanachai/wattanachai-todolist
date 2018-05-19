package com.bot.wattanachaitodolist.infra.line.api.v2;


import com.bot.wattanachaitodolist.infra.line.api.v2.response.AccessToken;
import com.bot.wattanachaitodolist.infra.line.api.v2.response.Verify;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * <p>LINE v2 API interface</p>
 */
public interface LineAPI {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/v2.1/token")
    Call<AccessToken> accessToken(
            @Field("grant_type") String grant_type,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("redirect_uri") String callback_url,
            @Field("code") String code);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/v2.1/token")
    Call<AccessToken> refreshToken(
            @Field("grant_type") String grant_type,
            @Field("refresh_token") String refresh_token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("oauth2/v2.1/verify")
    Call<Verify> verify(
            @Query("access_token") String access_token);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/v2.1/revoke")
    Call<Void> revoke(
            @Field("access_token") String access_token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret);

}

