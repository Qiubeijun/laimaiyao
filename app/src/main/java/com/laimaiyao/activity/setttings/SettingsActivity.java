package com.laimaiyao.activity.setttings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.laimaiyao.R;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

public class SettingsActivity extends AppCompatActivity {

    private Button bt_log_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bt_log_out = findViewById(R.id.bt_log_out);
        bt_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtils.getInstance().put(ConfigConstants.SP_IS_LOGIN,false);
                ToastUtils.showShort("退出登录成功");
                finish();
            }
        });
        if (HttpUtil.Islogined())
            bt_log_out.setVisibility(View.VISIBLE);
        else
            bt_log_out.setVisibility(View.INVISIBLE);
    }
}
