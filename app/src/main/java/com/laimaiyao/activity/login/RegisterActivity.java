package com.laimaiyao.activity.login;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.laimaiyao.R;
import com.laimaiyao.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
	private Button bt_reg;
	private AutoCompleteTextView tv_username;
	private EditText tv_password;
	private EditText tv_confirm_password;
	private PromptDialog promptDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		InitView();
		bt_reg =findViewById(R.id.bt_reg);
		bt_reg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Register();
			}
		});

	}
	private void InitView(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_register);
		toolbar.setTitle(R.string.title_register);
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
		tv_username = findViewById(R.id.tv_username);
		tv_password = findViewById(R.id.tv_password);
		tv_confirm_password = findViewById(R.id.tv_confirm_password);
	}
	private void Register(){
		tv_username.setError(null);
		tv_password.setError(null);
		tv_confirm_password.setError(null);
		boolean cancel = false;
		View focusView = null;

		String username = tv_username.getText().toString();
		String password = tv_password.getText().toString();
		String confirm_password = tv_confirm_password.getText().toString();

		if (TextUtils.isEmpty(username)) {
			tv_username.setError(getString(R.string.error_invalid_email));
			focusView = tv_username;
			cancel = true;
		}
		if (TextUtils.isEmpty(password)) {
			tv_password.setError(getString(R.string.error_invalid_password));
			focusView = tv_password;
			cancel = true;
		}
		if (TextUtils.isEmpty(confirm_password)) {
			tv_confirm_password.setError(getString(R.string.error_invalid_password));
			focusView = tv_confirm_password;
			cancel = true;
		}
		if(!password.equals(confirm_password)) {
			tv_confirm_password.setError("两次输入密码不一致");
			focusView = tv_confirm_password;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		}else {
			Map<String, String> map=new HashMap<>();
			map.put("type","1");
			map.put("username",tv_username.getText().toString());
			map.put("password",tv_password.getText().toString());
			promptDialog = new PromptDialog(this);
			promptDialog.showLoading("正在注册");
			if(HttpUtil.isNetworkAvailable(RegisterActivity.this)){
				HttpUtil.postMethod("/register",map,new Callback<String>(){
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						//response.body().
						try {
							JSONObject jsonObject= new JSONObject(response.body());
							int code=jsonObject.getInt("Code");
							if(code==200){
								promptDialog.dismiss();
								Toast.makeText(RegisterActivity.this, "--"+response.body(), Toast.LENGTH_SHORT).show();
								finish();
							}
							else if (code==300){
								promptDialog.dismiss();
								Toast.makeText(RegisterActivity.this, "--"+response.body(), Toast.LENGTH_SHORT).show();
							}
						}
						catch (Exception e) {}
					}

					@Override
					public void onFailure(Call<String> call, Throwable t) {
						promptDialog.dismiss();
						Toast.makeText(RegisterActivity.this, "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}else
				Toast.makeText(RegisterActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
		}

	}
}
