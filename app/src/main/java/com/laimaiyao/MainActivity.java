package com.laimaiyao;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.laimaiyao.fragment.CartFragment;
import com.laimaiyao.fragment.HomeFragment;
import com.laimaiyao.fragment.MineFragment;
import com.laimaiyao.fragment.SortFragment;

public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    private HomeFragment homeFragment;
    private SortFragment sortFragment;
    private CartFragment cartFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (homeFragment == null)
                        homeFragment = new HomeFragment();
                    transaction.replace(R.id.tb, homeFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_sort:
                    if (sortFragment == null)
                        sortFragment = new SortFragment();
                    transaction.replace(R.id.tb, sortFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_cart:
                    if (cartFragment == null)
                        cartFragment = new CartFragment();
                    transaction.replace(R.id.tb, cartFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_mine:
                    if (mineFragment == null)
                        mineFragment = new MineFragment();
                    transaction.replace(R.id.tb, mineFragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        transaction.replace(R.id.tb,homeFragment);
        transaction.commit();
    }

}
