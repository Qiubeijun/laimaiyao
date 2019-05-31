package com.laimaiyao.activity.product;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.utils.ConfigConstants;

@Route(path = ConfigConstants.HISTORY)
public class BrowsingHistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_history);
    }
}
