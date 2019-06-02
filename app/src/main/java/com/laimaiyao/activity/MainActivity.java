package com.laimaiyao.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.fragment.CartFragment;
import com.laimaiyao.fragment.HomeFragment;
import com.laimaiyao.fragment.MineFragment;
import com.laimaiyao.fragment.SortFragment;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;
import com.laimaiyao.utils.PermissionUtil;


//import com.laimaiyao.activity.product.ProductListActivity;
@Route(path = ConfigConstants.MAIN)
public class MainActivity extends BaseActivity {

    //private TextView mTextMessage;
    private HomeFragment homeFragment;
    private SortFragment sortFragment;
    private CartFragment cartFragment ;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private BottomNavigationView bottomNavigationView;
    private int LastFragmentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionUtil.getInstance().chekPermissions(this,permissions,new PermissionUtil.IPermissionsResult() {
            @Override
            public void passPermissons() {
                //Toast.makeText(App.getContext(), "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();
                HttpUtil.refreshloginstatus();

            }

            @Override
            public void forbitPermissons() {
                finish();
                Toast.makeText(App.getContext(), "权限不通过!", Toast.LENGTH_SHORT).show();
            }
        });
        initview();
    }
    private void initview(){
        //Drawable app_icon=(Drawable)findViewById(R.mipmap.ic_launcher);
         /*//开屏动画
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new LineDrawStrategy())
                .setAppStatement("安心买，放心药！")
                .setColorOfAppStatement(R.color.color_379A9D)
                .create();
        openingStartAnimation.show(this);*/
        //mTextMessage = (TextView) findViewById(R.id.message);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                fragmentManager = getSupportFragmentManager();
                transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        if (homeFragment == null)
                            homeFragment = new HomeFragment();
                        else if (LastFragmentID == R.id.navigation_cart)
                            homeFragment = new HomeFragment();
                        transaction.replace(R.id.tb, homeFragment);
                        transaction.commit();
                        LastFragmentID = R.id.navigation_home;
                        return true;
                    case R.id.navigation_sort:
                        if (sortFragment == null)
                            sortFragment = new SortFragment();
                        transaction.replace(R.id.tb, sortFragment);
                        transaction.commit();
                        LastFragmentID = R.id.navigation_sort;
                        return true;
                    case R.id.navigation_cart:
                        /*if (cartFragment == null) {
                            ToastUtils.showShort("准备拦截");
                            cartFragment = (CartFragment) ARouter.getInstance().build(ConfigConstants.CART).navigation(App.getContext(),new LoginNavigationCallbackImpl());
                        }else if(LastFragmentID == R.id.navigation_cart){
                            cartFragment = (CartFragment) ARouter.getInstance().build(ConfigConstants.CART).navigation(App.getContext(),new LoginNavigationCallbackImpl());
                        }
                        transaction.replace(R.id.tb, cartFragment);
                        transaction.commit();
                        LastFragmentID = R.id.navigation_cart;*/
                        ARouter.getInstance().build(ConfigConstants.CART).navigation(App.getContext(),new LoginNavigationCallbackImpl());
                        return true;
                    case R.id.navigation_mine:
                        if (mineFragment == null)
                            mineFragment = new MineFragment();
                        transaction.replace(R.id.tb, mineFragment);
                        transaction.commit();
                        LastFragmentID = R.id.navigation_mine;
                        return true;
                }
                return false;
            }
        });
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        transaction.replace(R.id.tb,homeFragment);
        transaction.commit();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
