package com.laimaiyao.activity.product;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.adapter.OneAdapter;
import com.laimaiyao.adapter.OneListener;
import com.laimaiyao.adapter.OneViewHolder;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.model.Product;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.RESULT)
public class ProductListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;
    private OneAdapter mOneAdapter;
    private View EmptyView;
    private List<Product> products = new ArrayList<>();
    private int current_page=1;
    private int MaxPage=0;
    private int total;
    private boolean noMoreData;
    @Autowired
    String Search_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        InitView();
        InitData();

    }
    private void InitView(){
        toolbar = findViewById(R.id.toolbar_product);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRefreshLayout = findViewById(R.id.rl_product_list);
        mRecyclerView = findViewById(R.id.rv_product_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOneAdapter = new OneAdapter(new OneListener() {
            @Override
            public boolean isMyItemViewType(int position, Object o) {
                return true;
            }

            @Override
            public OneViewHolder getMyViewHolder(ViewGroup parent) {

                return new OneViewHolder<Product>(parent,R.layout.item_product) {

                    @Override
                    protected void bindViewCasted(int position, final Product product) {
                        TextView product_title = itemView.findViewById(R.id.product_name);
                        ImageView product_img = itemView.findViewById(R.id.product_pic);
                        TextView product_price = itemView.findViewById(R.id.product_price);
                        TextView product_comment_amount = itemView.findViewById(R.id.product_comment_amount);
                        TextView product_rate_goodcomment = itemView.findViewById(R.id.product_good_comment_rate);
                        CardView card_product = itemView.findViewById(R.id.card_product);
                        product_title.setText(product.getPName());
                        //product_img
                        product_price.setText("￥"+product.getPrice());
                        product_comment_amount.setText(product.getComment()+"条评论");
                        //float rate_good_count = Integer.parseInt(product.getBestCount())/Integer.parseInt(product.getComment());
                        //product_rate_goodcomment.setText(String.valueOf(rate_good_count));
                         card_product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance().build(ConfigConstants.PRODUCT)
                                        .withString("PID",product.getPID())
                                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
                            }
                        });
                    }
                };
            }
        });
        mRecyclerView.setAdapter(mOneAdapter);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableAutoLoadMore(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
              //RequestData(current_page);
            }
        });


    }

    private void InitData(){
        RequestData(current_page);
    }
    private boolean RequestData( int page){
        if (HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map = new HashMap<>();
            String key = Search_Key;
            String token = SPUtils.getInstance().getString("token");
            map.put("key",Search_Key);
            map.put("token",token);
            HttpUtil.getMethodWithToken("/product/search", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    int code = response.code();
                    if(code==200){
                        try {
                            JSONArray jsonArray = new JSONArray(response.body());
                            JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length()-1);
                            ToastUtils.showShort(jsonObject.getInt("total"));
                        }catch (Exception e){
                            System.out.println(e);
                        }
                        List<Product> product = new Gson().fromJson(response.body(),new TypeToken<List<Product>>(){}.getType());
                        product.remove(product.size()-1);

                        products.addAll(product);
                        mOneAdapter.setData(products);
                        mOneAdapter.notifyDataSetChanged();

                    }else if (code==300){
                        ToastUtils.showShort("请求错误");
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

    private void GetMaxPage(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map = new HashMap<>();
            String key = Search_Key;
            String token = SPUtils.getInstance().getString("token");
            map.put("key",Search_Key);
            map.put("token",token);
            HttpUtil.postMethodWithToken("/product/search", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            MaxPage = jsonObject.getInt("maxpage");
                            if (current_page<=MaxPage){
                                RequestData(current_page);
                            }
                            else {
                                //如果初始为0
                            }
                        }catch (Exception e){ }
                    }
                    else if (response.code()==300){
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
