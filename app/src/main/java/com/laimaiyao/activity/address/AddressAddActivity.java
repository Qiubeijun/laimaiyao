package com.laimaiyao.activity.address;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

@Route(path = ConfigConstants.ADDRESSADD)
public class AddressAddActivity extends BaseActivity  implements View.OnClickListener {
    private EditText et_name;
    private EditText et_phone;
    private EditText et_district;
    private EditText et_detail;
    private TextView tv_save;
    private Toolbar toolbar;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        InitView();
    }
    private void InitView(){
        toolbar = findViewById(R.id.toolbar_address_add);
        InitToolBar(toolbar);
        progressBar = findViewById(R.id.progressBar_Address_Add);
        et_name = findViewById(R.id.edit_address_name_add);
        et_phone = findViewById(R.id.edit_address_phone_add);
        et_district = findViewById(R.id.edit_address_district_add);
        et_detail = findViewById(R.id.edit_address_detail_add);
        tv_save = findViewById(R.id.tv_address_add_save);
        tv_save.setOnClickListener(this);
        et_name.setHint("姓名");
        et_phone.setHint("手机");
        et_district.setHint("地区");
        et_detail.setHint("详细地址");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_address_add_save:
                ToastUtils.showShort("点击保存");
                Save_Address();
                break;
        }
    }

    private void Save_Address(){
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(et_name.getText().toString())) {
            //mEmailView.setError(getString(R.string.error_field_required));
            ToastUtils.showShort("姓名不能为空");
            focusView = et_name;
            cancel = true;
        } else if (TextUtils.isEmpty(et_phone.getText().toString())) {
            //mEmailView.setError(getString(R.string.error_field_required));
            ToastUtils.showShort("手机不能为空");

            focusView = et_phone;
            cancel = true;
        }else if (TextUtils.isEmpty(et_district.getText().toString())) {
            //mEmailView.setError(getString(R.string.error_field_required));
            ToastUtils.showShort("地区不能为空");

            focusView = et_district;
            cancel = true;
        }else if (TextUtils.isEmpty(et_detail.getText().toString())) {
            //mEmailView.setError(getString(R.string.error_field_required));
            ToastUtils.showShort("详细地址不能为空");
            focusView = et_detail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if(HttpUtil.isNetworkAvailable(App.getContext())){
                progressBar.setVisibility(View.VISIBLE);
                Map<String, String> map=new HashMap<>();
                String UID = SPUtils.getInstance().getString("uid");
                String name = et_name.getText().toString();
                String phone = et_phone.getText().toString();
                String district = et_district.getText().toString();
                String detail = et_detail.getText().toString();

                String token = SPUtils.getInstance().getString("token");
                map.put("UID",UID);
                map.put("Name",name);
                map.put("Phone",phone);
                map.put("District",district);
                map.put("DetailedAddress",detail);
                HttpUtil.postMethodWithToken("/address/create", token, map, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.code()==200){
                            progressBar.setVisibility(View.GONE);
                            ToastUtils.showShort("添加完成");
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
