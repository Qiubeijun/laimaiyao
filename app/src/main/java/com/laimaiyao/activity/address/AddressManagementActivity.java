package com.laimaiyao.activity.address;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.laimaiyao.adapter.OneAdapter;
import com.laimaiyao.adapter.OneListener;
import com.laimaiyao.adapter.OneViewHolder;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.model.Address;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.ADDRESS)

public class AddressManagementActivity extends AppCompatActivity {

    private Toolbar toolbar_address;
    private RecyclerView recyclerView;
    private TextView tv_edit;//编辑按钮
    private OneAdapter AddressAdapter;
    private List<Address> addressList = new ArrayList<>();
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_management);
        InitView();
        InitData();
    }
    void InitView(){
        toolbar_address = findViewById(R.id.toolbar_address);
        initToolBar(toolbar_address);
        recyclerView = findViewById(R.id.rv_address_list);
        tv_edit = findViewById(R.id.tv_address_edit);
        progressBar = findViewById(R.id.progressBar_Address);
        AddressAdapter = new OneAdapter(new OneListener() {
            @Override
            public boolean isMyItemViewType(int position, Object o) {
                return true;
            }

            @Override
            public OneViewHolder getMyViewHolder(ViewGroup parent) {
                return new OneViewHolder<Address>(parent, R.layout.item_address){

                    @Override
                    protected void bindViewCasted(int position, final Address address) {
                        TextView name = itemView.findViewById(R.id.address_item_name);
                        name.setText(address.getName());
                        TextView phone = itemView.findViewById(R.id.address_item_phone);
                        phone.setText(address.getPhone());
                        TextView district = itemView.findViewById(R.id.address_item_address);
                        district.setText(address.getDistrict()+address.getDetailedAddress());
                        ImageView edit = itemView.findViewById(R.id.address_item_edit);
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance()
                                        .build(ConfigConstants.ADDRESSEDIT)
                                        .withString("AID",address.getAID()+"")
                                        .withString("name",address.getName())
                                        .withString("phone",address.getPhone())
                                        .withString("district",address.getDistrict())
                                        .withString("detail",address.getDetailedAddress())
                                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
                            }
                        });
                    }
                };
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(AddressAdapter);
    }
    void InitData(){
        progressBar.setVisibility(View.VISIBLE);
        getCart();
    }
    public void initToolBar(Toolbar toolbar){
        //toolbar.setTitle(title);
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

    private void getCart(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            HttpUtil.postMethodWithToken("/address/get", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        List<Address> mAddressList =new Gson().fromJson(response.body(),new TypeToken<List<Address>>(){}.getType());
                        if(mAddressList.size()!=0){
                            addressList.clear();
                            addressList.addAll(mAddressList);
                            AddressAdapter.setData(addressList);
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                    else if (response.code()==300){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==404){
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
