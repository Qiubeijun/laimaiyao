package com.laimaiyao.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by MI on 2019/3/31
 */
public interface LoginApi {
    @FormUrlEncoded
    @POST
    Call<String> login(@Url String url,@FieldMap Map<String,String> map);
    @GET
    Call<String> get(@Url String url,  @QueryMap Map<String,String> map);
}
