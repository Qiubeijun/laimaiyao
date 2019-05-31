package com.laimaiyao.activity.order;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.utils.ConfigConstants;

@Route(path = ConfigConstants.ORDERLIST)
public class OrderListActivity extends BaseActivity {

    private Toolbar toolbar;

    @Autowired
    public String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        //设置toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_order_list);
        initToolBar(toolbar);


    }
    private void initToolBar(Toolbar toolbar){
        toolbar.setTitle("");
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
}
