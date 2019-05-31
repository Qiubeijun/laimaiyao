package com.laimaiyao.activity.order;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.utils.ConfigConstants;

@Route(path = ConfigConstants.ORDERDETAIL)
public class OrderDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
    }
}
