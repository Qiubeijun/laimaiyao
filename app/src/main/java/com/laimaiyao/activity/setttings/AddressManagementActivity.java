package com.laimaiyao.activity.setttings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.laimaiyao.R;
import com.laimaiyao.utils.ConfigConstants;

@Route(path = ConfigConstants.ADDRESS)

public class AddressManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_management);

    }
}
