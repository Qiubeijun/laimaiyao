package com.laimaiyao;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laimaiyao.fragment.CartFragment;
import com.laimaiyao.fragment.HomeFragment;
import com.laimaiyao.fragment.MineFragment;
import com.laimaiyao.fragment.SortFragment;
import com.laimaiyao.product.ProductListActivity;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    private HomeFragment homeFragment;
    private SortFragment sortFragment;
    private CartFragment cartFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private LinearLayout include;
    private Toolbar toolbar;
    private SearchFragment searchFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    include.setVisibility(View.VISIBLE);
                    if (homeFragment == null)
                        homeFragment = new HomeFragment();
                    transaction.replace(R.id.tb, homeFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_sort:
                    include.setVisibility(View.VISIBLE);
                    if (sortFragment == null)
                        sortFragment = new SortFragment();
                    transaction.replace(R.id.tb, sortFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_cart:
                    include.setVisibility(View.GONE);
                    if (cartFragment == null)
                        cartFragment = new CartFragment();
                    transaction.replace(R.id.tb, cartFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_mine:
                    include.setVisibility(View.GONE);

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
        include = findViewById(R.id.search_layout);
        TextView textView = findViewById(R.id.edt_search);
        searchFragment = SearchFragment.newInstance();
        searchFragment.setOnSearchClickListener(new IOnSearchClickListener() {
            @Override
            public void OnSearchClick(String keyword) {
                Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
                startActivity(intent);
                //这里处理逻辑
                //Toast.makeText(ToolBarActivity.this, keyword, Toast.LENGTH_SHORT).show();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFragment.showFragment(getSupportFragmentManager(),SearchFragment.TAG);
            }
        });
        //toolbar=include.findViewById(R.id.search_head);



    }

}
