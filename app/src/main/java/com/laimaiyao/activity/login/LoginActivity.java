package com.laimaiyao.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.laimaiyao.R;
import com.laimaiyao.activity.BaseActivity;
import com.laimaiyao.model.User;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
@Route(path = ConfigConstants.LOGIN)
public class LoginActivity extends BaseActivity {

    private static final String[] DUMMY_CREDENTIALS = new String[]{"foo@example.com:hello", "bar@example.com:world"};
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Tencent mTencent;
    private IUiListener loginListener;
    private IUiListener userInfoListener;
    private UserInfo userInfo;
    private Button mEmailSignInButton;
    private Button RegisterButton;
    private ImageView button_login_qq;
    private ImageView button_login_wechat;
    private Toolbar toolbar;
    private PromptDialog promptDialog;


    @Autowired
    public String Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //设置toolBar
        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        initToolBar(toolbar,R.string.title_login);
        //初始化界面
        InitViews();
    }

    public void initToolBar(Toolbar toolbar,int title){
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

    public void InitViews(){

        // Set up the login form.

        /*mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    AttemptLogin(1);
                    return true;
                }
                return false;
            }
        });*/

        // LoginButton listening
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        RegisterButton = (Button) findViewById(R.id.register_button);
        button_login_qq=findViewById(R.id.img_qq);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AttemptLogin(1);
            }
        });
        //RegisterButton listening
        RegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        //qq_loginButton listening
        button_login_qq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AttemptLogin(1);
            }
        });
    }

    private void AttemptLogin(int IdentityType) {
        if(IdentityType==1){
            LoginByPassword();
        } else if(IdentityType==2){
            LoginByQQ();
        }
    }

    private void LoginByPassword(){
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        //!TextUtils.isEmpty(password) && !isPasswordValid(password)
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/
        if (cancel) {
            focusView.requestFocus();
        }
        else {
            promptDialog = new PromptDialog(this);
            promptDialog.showLoading("正在登录");
            Map<String, String> map=new HashMap<>();
            map.put("login_type","1");
            map.put("Identifier",email);
            map.put("Credential",password);
            if(HttpUtil.isNetworkAvailable(LoginActivity.this)){
                HttpUtil.postMethod("/login",map,new Callback<String>(){
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //response.body().
                        try {
                            JSONObject jsonObject= new JSONObject(response.body());
                            int code=jsonObject.getInt("Code");
                            if(code==200){
                                //promptDialog.showSuccess("登陆成功");
                                Gson gson = new Gson();
                                //LitePal.deleteAll(User.class);
                                User user = gson.fromJson(response.body(),User.class);
                                user.save();
                                SPUtils.getInstance().put("token",user.getToken());
                                SPUtils.getInstance().put("uid",user.getUID());
                                SPUtils.getInstance().put("nickname",user.getNickName());
                                SPUtils.getInstance().put(ConfigConstants.SP_IS_LOGIN, true);
                                Toast.makeText(LoginActivity.this, "--"+response.body(), Toast.LENGTH_SHORT).show();
                                if (!StringUtils.isEmpty(Path)) {
                                    ARouter.getInstance().build(Path)
                                            .with(getIntent().getExtras())
                                            .navigation();
                                }
                                finish();
                            }
                            else if (code==300){
                                promptDialog.dismiss();
                                SPUtils.getInstance().put(ConfigConstants.SP_IS_LOGIN,false);
                                Toast.makeText(LoginActivity.this, "--"+response.body(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e) {}
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
                Toast.makeText(LoginActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    public void LoginByQQ(){
        //登录的初始化
        qq_login_init();
        mTencent.login(LoginActivity.this,"all",loginListener);
        Toast.makeText(LoginActivity.this, "准备登录",
                Toast.LENGTH_SHORT).show();
         /*
         向服务端发送请求，如果服务端反馈登陆成功信息，同时服务端更新token
         如果服务端反馈没有账户信息，服务端将创建一个新用户
         */
    }

    public void qq_login_init(){
        mTencent= Tencent.createInstance("101549286",LoginActivity.this.getApplicationContext());
        loginListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub
            }
            /**
             * 返回json数据例子
             *
             * {"ret":0,"pay_token":"D3D678728DC580FBCDE15722B72E7365",
             * "pf":"desktop_m_qq-10000144-android-2002-",
             * "query_authority_cost":448,
             * "authority_cost":-136792089,
             * "openid":"015A22DED93BD15E0E6B0DDB3E59DE2D",
             * "expires_in":7776000,
             * "pfkey":"6068ea1c4a716d4141bca0ddb3df1bb9",
             * "msg":"",
             * "access_token":"A2455F491478233529D0106D2CE6EB45",
             * "login_cost":499}
             */
            @Override
            public void onComplete(Object value) {
                // TODO Auto-generated method stub
                if (value == null) {
                    System.out.println("没有数据返回..");
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) value;
                    int ret = jo.getInt("ret");
                    System.out.println("json=" + String.valueOf(jo));
                    if (ret == 0) {
                        Toast.makeText(LoginActivity.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        SPUtils.getInstance().put(ConfigConstants.SP_IS_LOGIN, true);
                        String openID = jo.getString("openid");
                        String accessToken = jo.getString("access_token");
                        String expires = jo.getString("expires_in");
                        mTencent.setOpenId(openID);
                        mTencent.setAccessToken(accessToken, expires);
                        //获取用户详细信息
                        userInfo = new UserInfo(LoginActivity.this,mTencent.getQQToken());
                        userInfo.getUserInfo(userInfoListener);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
            }
        };

        userInfoListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub

            }

            /**
             * 返回用户信息例子
             *
             * {"is_yellow_year_vip":"0","ret":0,
             * "figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/40",
             * "figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "nickname":"攀爬←蜗牛","yellow_vip_level":"0","is_lost":0,"msg":"",
             * "city":"黄冈","
             * figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/50",
             * "vip":"0","level":"0",
             * "figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "province":"湖北",
             * "is_yellow_vip":"0","gender":"男",
             * "figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/30"}
             */
            @Override
            public void onComplete(Object arg0) {
                Log.d("tag","收集用户信息");
                // TODO Auto-generated method stub
                if(arg0 == null){
                    Log.d("tag","没有获取到数据");
                    System.out.println("没有获取到数据");
                    return;
                }
                try {
                    Log.d("tag","收集用户信息2");
                    JSONObject jo = (JSONObject) arg0;
                    int ret = jo.getInt("ret");
                    Log.d("tag","json=" + String.valueOf(jo));
                    System.out.println("json=" + String.valueOf(jo));
                    String nickName = jo.getString("nickname");
                    String gender = jo.getString("gender");

                    Toast.makeText(LoginActivity.this, "你好。"+nickName ,
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.forget_password:
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

