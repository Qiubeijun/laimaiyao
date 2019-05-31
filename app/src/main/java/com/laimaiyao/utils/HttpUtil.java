package com.laimaiyao.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.laimaiyao.App;
import com.laimaiyao.api.BaseApi;
import com.laimaiyao.api.LoginApi;
import com.laimaiyao.model.User;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HttpUtil {
    public static final String BASE_URL = "http://59.110.157.244/";
    private static boolean islogined;

    public static boolean isNetworkAvailable(Context context) {
        boolean isAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static void postMethod(String url,Map<String, String> map, final Callback<String> callback) {

        //指定客户端
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);

        Call<String> call = loginApi.login(url,map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call,t);
            }
        });
    }

    public static void postMethodWithToken (String url,String token,Map<String, String> map, final Callback<String> callback) {
        //指定客户端
        /*OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String token=SPUtils.getInstance().getString("token",null);
                Request request = original.newBuilder()
                        .header("token",token)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();*/

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        BaseApi baseApi = retrofit.create(BaseApi.class);

        Call<String> call = baseApi.login(url,token,map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) { callback.onResponse(call, response); }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call,t);
            }
        });
    }

    public static void getMethodWithToken(String url,String token,Map<String,String> map,final Callback<String> callback){
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        BaseApi baseApi = retrofit.create(BaseApi.class);

        Call<String> call = baseApi.get(url,token,map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) { callback.onResponse(call, response); }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call,t);
            }
        });
    }
    public static void getMethod(String url,Map<String,String> map,final Callback<String> callback){
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);

        Call<String> call = loginApi.get(url,map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) { callback.onResponse(call, response); }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call,t);
            }
        });
    }

    public static boolean refreshloginstatus(){
        islogined = false;
        String token = SPUtils.getInstance().getString("token",null);
        User user = LitePal.findFirst(User.class);
        if(!SPUtils.getInstance().getBoolean(ConfigConstants.SP_IS_LOGIN,false)){
            ToastUtils.showShort("用户未登录");
        }else {
            /*if (user == null) {
                Toast.makeText(App.getContext(), "User不存在", Toast.LENGTH_SHORT).show();
            } else */
            {
                String UID = user.getUID();
                if (!token.equals(null)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", token);
                    HttpUtil.postMethod("/checkuser", map, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                int status = jsonObject.getInt("status");
                                if (status == 403) {
                                    Toast.makeText(App.getContext(), "Token过期，请重新登录", Toast.LENGTH_SHORT).show();
                                    SPUtils.getInstance().put(ConfigConstants.SP_IS_LOGIN, false);
                                    islogined = false;
                                } else if (status == 200) {
                                    islogined = true;
                                    SPUtils.getInstance().put(ConfigConstants.SP_IS_LOGIN, true);
                                    ContentValues values = new ContentValues();
                                    values.put("Token", jsonObject.getString("Token"));
                                    LitePal.update(User.class, values, 1);
                                    Toast.makeText(App.getContext(), "Token有效，刷新Token", Toast.LENGTH_SHORT).show();
                                }else if (status==500)
                                {
                                    ToastUtils.showShort("内部出错");
                                }
                            } catch (Exception e) { }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            }
        }

        return islogined;
    }

    public static boolean Islogined(){
        return SPUtils.getInstance().getBoolean(ConfigConstants.SP_IS_LOGIN,false);
    }
}