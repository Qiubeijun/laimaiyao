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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.WISHLIST)
public class WishListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private View card_item_cart;
    private List<Product> datas =new ArrayList<Product>();
    private int current_page=1;
    private int MaxPage=0;
    private boolean noMoreData;
    private OneAdapter oneAdapter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        initview();
        //InitData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ToastUtils.showShort("再次刷新");
        current_page=1;
        progressBar.setVisibility(View.VISIBLE);
        InitData();
    }

    public void initview(){
        progressBar = findViewById(R.id.progressBar_wishList);
        toolbar = findViewById(R.id.wish_list_toolbar);
        InitToolBar(toolbar,R.string.title_wish_list);
        refreshLayout = findViewById(R.id.rl_wish_list);
        recyclerView = findViewById(R.id.rv_wish_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        oneAdapter = new OneAdapter(new OneListener() {
            @Override
            public boolean isMyItemViewType(int position, Object o) {
                return true;
            }

            @Override
            public OneViewHolder getMyViewHolder(ViewGroup parent) {
                return new OneViewHolder<Product>(parent, R.layout.item_wish){

                    @Override
                    protected void bindViewCasted(int position, final Product product) {
                        TextView product_name = itemView.findViewById(R.id.tv_produce_name);
                        product_name.setText(product.getPName());
                        TextView product_price = itemView.findViewById(R.id.tv_product_price);
                        product_price.setText(product.getPrice());
                        CardView cardView = itemView.findViewById(R.id.CardView_wish);
                        cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance().build(ConfigConstants.PRODUCT).withString("PID",product.getPID()).navigation(App.getContext(),new LoginNavigationCallbackImpl());

                            }
                        });
                    }
                };
            }
        });
        recyclerView.setAdapter(oneAdapter);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(current_page<=MaxPage) {
                    requestData(current_page);
                }
            }
        });
    }

    private void InitData(){
        progressBar.setVisibility(View.VISIBLE);
        getMaxPage();
    }
    private void InitToolBar(Toolbar toolbar,int title){
        toolbar.setTitle(title);
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
    }

    private void requestData(final int page){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("page",String.valueOf(page));
            HttpUtil.postMethodWithToken("/wishlist/get", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        if(page>=MaxPage){
                            noMoreData=true;
                        }
                        else {
                            current_page+=1;
                        }
                        List<Product> products =new Gson().fromJson(response.body(),new TypeToken<List<Product>>(){}.getType());
                        datas.addAll(products);
                        oneAdapter.setData(datas);
                        oneAdapter.notifyDataSetChanged();
                        refreshLayout.finishLoadMore(0, true, noMoreData);
                    }
                    else if (response.code()==300){
                        Toast.makeText(WishListActivity.this, "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "--"+t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void getMaxPage(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            HttpUtil.postMethodWithToken("/wishlist/count", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            MaxPage = jsonObject.getInt("maxpage");
                            if (current_page<=MaxPage){
                                datas.clear();
                                requestData(current_page);
                            }
                            else {
                                //如果初始为0
                            }
                        }catch (Exception e){ }
                    }
                    else if (response.code()==300){
                        Toast.makeText(WishListActivity.this, "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
