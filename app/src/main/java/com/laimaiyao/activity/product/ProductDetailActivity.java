package com.laimaiyao.activity.product;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.PRODUCT)
public class ProductDetailActivity extends BaseActivity {

    @Autowired
    String PID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ToastUtils.showShort(PID);
    }
    private void InitView(){

    }
    private boolean GetDetail(){
        if (HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map = new HashMap<>();
            map.put("PID",PID);
            HttpUtil.getMethod("/product/detail", map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    int code = response.code();
                    if(code==200){
                        try {
                            JSONArray jsonArray = new JSONArray(response.body());
                            JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length()-1);
                            ToastUtils.showShort(jsonObject.getInt("total"));
                        }catch (Exception e){

                        }
                    }else if (code==300){

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
            return true;
        }else
            return false;
    }
}
