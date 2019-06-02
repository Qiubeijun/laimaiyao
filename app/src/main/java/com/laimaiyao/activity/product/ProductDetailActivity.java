package com.laimaiyao.activity.product;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.model.Product;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.PRODUCT)
public class ProductDetailActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar_detail;
    private LinearLayout bt_collet;
    private LinearLayout bt_cart;
    private TextView tv_add_to_cart;
    private TextView product_name;
    private TextView product_price;
    private ImageView icon_collect;
    private boolean isCollected = false;
    private ProgressBar progressBar;
    private Product product;

    @Autowired
    String PID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ToastUtils.showShort(PID);
        InitView();
        InitData();
    }
    private void InitView(){
        toolbar_detail = findViewById(R.id.toolbar_detail);
        InitToolBar(toolbar_detail);
        product_name = findViewById(R.id.tv_detail_name);
        product_price = findViewById(R.id.tv_detail_price);
        bt_collet = findViewById(R.id.bt_collect);
        bt_cart = findViewById(R.id.bt_cart);
        bt_collet.setOnClickListener(this);
        bt_cart.setOnClickListener(this);
        tv_add_to_cart = findViewById(R.id.tv_add_to_cart);
        tv_add_to_cart.setOnClickListener(this);
        icon_collect = findViewById(R.id.img_icon_collect);
        progressBar = findViewById(R.id.progressBar_Detail);
    }
    private void InitData(){
        progressBar.setVisibility(View.VISIBLE);
        GetDetail();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_collect://点击收藏按钮
                if(HttpUtil.Islogined()) {
                    progressBar.setVisibility(View.VISIBLE);
                    if(isCollected) {
                        del_from_wishlist();
                    }else
                        add_to_wishlist();
                }else {
                    ARouter.getInstance()
                            .build(ConfigConstants.LOGIN)
                            .navigation(App.getContext(), new LoginNavigationCallbackImpl());
                }

                break;
            case R.id.bt_cart:
                if(HttpUtil.Islogined()){
                    ARouter.getInstance()
                            .build(ConfigConstants.CART)
                            .navigation(App.getContext(), new LoginNavigationCallbackImpl());
                }else
                ARouter.getInstance()
                        .build(ConfigConstants.LOGIN)
                        .navigation(App.getContext(), new LoginNavigationCallbackImpl());
                break;
            case R.id.tv_add_to_cart:
                if(HttpUtil.Islogined()){
                    progressBar.setVisibility(View.VISIBLE);
                    get_product_from_cart();
                }
                break;
        }
    }

    private void InitToolBar(android.support.v7.widget.Toolbar toolbar){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean GetDetail(){
        if (HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map = new HashMap<>();
            map.put("PID",PID);
            //未登录
            if(!HttpUtil.Islogined()) {
                HttpUtil.getMethod("/product/detail", map, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        int code = response.code();
                        if (code == 200) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                product = gson.fromJson(response.body(),Product.class);
                                ToastUtils.showShort(product.getPName());
                                product_name.setText(product.getPName());
                                product_price.setText("￥"+product.getPrice());

                            } catch (Exception e) {

                            }
                        } else if (code == 300) {

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            else{//登录了
                String UID = SPUtils.getInstance().getString("uid");
                map.put("UID",UID);
                String token = SPUtils.getInstance().getString("token");
                HttpUtil.getMethodWithToken("/product/detailWithUID",token, map, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        int code = response.code();
                        if (code == 200) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                product = gson.fromJson(response.body(),Product.class);
                                JSONObject jsonObject = new JSONObject(response.body());
                                isCollected = jsonObject.getBoolean("isCollected");
                                if(isCollected){
                                    icon_collect.setImageResource(R.drawable.icon_like_fill);
                                }
                                product_name.setText(product.getPName());
                                product_price.setText(product.getPrice());
                            } catch (Exception e) {

                            }
                        } else if (code == 300) {

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }
            return true;
        }else
            return false;
    }

    private void get_product_from_cart(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/cart/getone", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        ToastUtils.showShort("购物车已存在此商品");
                    }
                    else if (response.code()==300){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==400){
                        Add_to_Cart();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void Add_to_Cart(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            map.put("Amount","1");
            map.put("PName",product.getPName());
            map.put("Price",product.getPrice());
            map.put("isChecked","true");
            HttpUtil.postMethodWithToken("/cart/add", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        ToastUtils.showShort("加入购物车成功");
                    }
                    else if (response.code()==300){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void add_to_wishlist(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/wishlist/add", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        ToastUtils.showShort("收藏成功");
                        icon_collect.setImageResource(R.drawable.icon_like_fill);
                        isCollected = !isCollected;
                        progressBar.setVisibility(View.GONE);
                    }
                    else if (response.code()==300){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void del_from_wishlist(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/wishlist/delete", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        ToastUtils.showShort("取消收藏成功");
                        icon_collect.setImageResource(R.drawable.icon_like);
                        isCollected = !isCollected;
                        progressBar.setVisibility(View.GONE);
                    }
                    else if (response.code()==300){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
