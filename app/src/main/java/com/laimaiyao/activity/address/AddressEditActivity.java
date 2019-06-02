package com.laimaiyao.activity.address;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.ADDRESSEDIT)
public class AddressEditActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_phone;
    private EditText et_district;
    private EditText et_detail;
    private TextView tv_save;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    @Autowired
    String AID;
    @Autowired
    String name;
    @Autowired
    String phone;
    @Autowired
    String district;
    @Autowired
    String detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        InitView();
    }
    private void InitView(){
        toolbar = findViewById(R.id.toolbar_address_edit);
        InitToolBar(toolbar);
        progressBar = findViewById(R.id.progressBar_Address_Edit);
        et_name = findViewById(R.id.edit_address_name);
        et_phone = findViewById(R.id.edit_address_phone);
        et_district = findViewById(R.id.edit_address_district);
        et_detail = findViewById(R.id.edit_address_detail);
        tv_save = findViewById(R.id.tv_address_edit_save);
        tv_save.setOnClickListener(this);
        et_name.setText(name);
        et_phone.setText(phone);
        et_district.setText(district);
        et_detail.setText(detail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_address_edit_save:
                progressBar.setVisibility(View.VISIBLE);
                Save_Address();
                break;

        }
    }
    private void Save_Address(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String name = et_name.getText().toString();
            String phone = et_phone.getText().toString();
            String district = et_district.getText().toString();
            String detail = et_detail.getText().toString();

            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("AID",AID);
            map.put("Name",name);
            map.put("Phone",phone);
            map.put("District",district);
            map.put("DetailedAddress",detail);
            HttpUtil.postMethodWithToken("/address/update", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        ToastUtils.showShort("修改完成");
                        finish();
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
    private void InitToolBar(Toolbar toolbar){
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
}
