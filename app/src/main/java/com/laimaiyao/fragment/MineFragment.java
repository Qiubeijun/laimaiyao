package com.laimaiyao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.activity.login.LoginActivity;
import com.laimaiyao.activity.setttings.SettingsActivity;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout item_my_attention;
    private LinearLayout item_browsing_history;
    private LinearLayout item_my_healthy_info;
    private LinearLayout item_call_server;
    private LinearLayout item_order_pay;
    private LinearLayout item_order_prepare;
    private LinearLayout item_order_receive;
    private LinearLayout item_order_review;
    private LinearLayout item_order_refund;
    private RelativeLayout order_all;
    private View view;
    private View orderTab;
    private View serviceTab;
    private Toolbar toolbar;
    private CardView userinfo;
    private CircleImageView user_avatar;
    private TextView user_nickname;
    private Button bt_settings;

    //private OnFragmentInteractionListener mListener;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_mine, container, false);
         initview();
        return view;
    }

    private void initview(){
        userinfo = view.findViewById(R.id.card_user_info);
        user_nickname = view.findViewById(R.id.tv_nickname);
        toolbar = view.findViewById(R.id.toolbar_mine);
        initToolbar(toolbar,"",false);
        orderTab = view.findViewById(R.id.order_tab);
        serviceTab = view.findViewById(R.id.service_tab);
        order_all = orderTab.findViewById(R.id.order_all);
        item_order_pay = orderTab.findViewById(R.id.order_pay);
        item_order_prepare = orderTab.findViewById(R.id.order_prepare);
        item_order_receive = orderTab.findViewById(R.id.order_receive);
        item_order_review = orderTab.findViewById(R.id.order_review);
        item_order_refund = orderTab.findViewById(R.id.order_refund);
        item_my_attention = serviceTab.findViewById(R.id.my_attention);
        item_browsing_history = serviceTab.findViewById(R.id.browsing_history);
        item_my_healthy_info = serviceTab.findViewById(R.id.my_healthy_information);
        item_call_server = serviceTab.findViewById(R.id.call_server);
        if(HttpUtil.Islogined()){
            user_nickname.setText(SPUtils.getInstance().getString("nickname"));
        }
        userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HttpUtil.Islogined()){

                } else {
                    Intent intent = new Intent();
                    intent.setClass(App.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        order_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ConfigConstants.ORDERLIST)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        item_order_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ConfigConstants.ORDERLIST)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        item_order_prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ConfigConstants.ORDERLIST)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        item_order_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ConfigConstants.ORDERLIST)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        item_order_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ConfigConstants.ORDERLIST)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        item_order_refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { }
        });
        item_my_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ConfigConstants.WISHLIST)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        item_my_healthy_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("敬请期待");
            }
        });
        item_browsing_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ARouter.getInstance().build(ConfigConstants.HISTORY)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());*/
                ToastUtils.showShort("敬请期待");

            }
        });
        item_call_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ConfigConstants.ADDRESS)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mine,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.settings) {
            Intent intent = new Intent(App.getContext(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment中初始化Toolbar
     * @param toolbar
     * @param title 标题
     * @param isDisplayHomeAsUp 是否显示返回箭头
     */
    public void initToolbar(Toolbar toolbar, String title, boolean isDisplayHomeAsUp) {
        AppCompatActivity appCompatActivity= (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUp);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (HttpUtil.Islogined()){
            user_nickname.setText(SPUtils.getInstance().getString("nickname"));
        }
        else {
            user_nickname.setText("登录/注册");
        }
    }
}
