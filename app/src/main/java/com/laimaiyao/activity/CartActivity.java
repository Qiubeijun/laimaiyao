package com.laimaiyao.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.adapter.CartAdapter;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.model.CartItem;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.CART)
public class CartActivity extends AppCompatActivity implements View.OnClickListener,CartAdapter.CheckInterface, CartAdapter.ModifyCountInterface {

    private Toolbar toolbar;
    private TextView tv_edit;//编辑按钮
    private TextView tv_settlement;//结算按钮
    private TextView tv_cart_delete;//删除按钮
    private RecyclerView recyclerView;
    private TextView tv_show_price;//合计价格

    private CartAdapter cartAdapter;
    private ProgressBar progressBar;
    private double totalPrice = 0.00;// 购买的商品总价
    private int totalCount = 0;// 购买的商品总数量
    private CheckBox checkBox;
    private boolean flag = false;
    private int CurrentCount;
    private SparseArray<Boolean> mSelectState = new SparseArray<Boolean>();
    private List<CartItem> datas =new ArrayList<CartItem>();
    private CartItem currentCart;
    private View getView;
    private int touch_position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);
        InitView();
        InitData();
    }
    private void InitData(){
        progressBar.setVisibility(View.VISIBLE);
        getCart();
    }
    private void InitView(){
        toolbar = findViewById(R.id.toolbar_cart);
        InitToolBar(toolbar);
        progressBar = findViewById(R.id.progressBar);
        checkBox = findViewById(R.id.ck_all);
        toolbar = findViewById(R.id.toolbar_cart);
        tv_settlement = findViewById(R.id.tv_settlement);
        tv_cart_delete = findViewById(R.id.tv_cart_delete);
        tv_settlement.setOnClickListener(this);
        tv_cart_delete.setOnClickListener(this);
        tv_edit = toolbar.findViewById(R.id.tv_header_right);
        tv_edit.setOnClickListener(this);
        tv_show_price = findViewById(R.id.tv_show_price);
        recyclerView =findViewById(R.id.rv_cart_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(CartActivity.this);
        cartAdapter.setCheckInterface(this);
        cartAdapter.setModifyCountInterface(this);
        recyclerView.setAdapter(cartAdapter);
        checkBox.setOnClickListener(this);
    }
    private void InitToolBar(android.support.v7.widget.Toolbar toolbar){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 单选
     *
     * @param position  组元素位置
     * @param isChecked 组元素选中与否
     */
    @Override
    public void checkGroup(int position, boolean isChecked) {

        //将当前项的设定为需要设定的状态
        datas.get(position).setChecked(isChecked);
        //这个设定完之后是不是所有的项都被全选了
        if (isAllCheck())//是，将结算栏的checkbox设定为选中状态
            checkBox.setChecked(true);
        else//不是
            checkBox.setChecked(false);
        cartAdapter.notifyDataSetChanged();
        statistics();
    }
    /**
     * 遍历list集合查看是不是所有的项都被选中
     *
     * @return
     */
    private boolean isAllCheck() {

        for (CartItem group : datas) {
            if (!group.isChecked())
                return false;
        }
        return true;
    }

    /**
     * 增加
     *
     * @param position      组元素位置
     * @param showCountView 用于展示变化后数量的View
     * @param isChecked     子元素选中与否
     */
    @Override
    public void doIncrease(int position, View showCountView, boolean isChecked) {
        currentCart = datas.get(position);
        getView = showCountView;
        CurrentCount = currentCart.getAmount();
        if (CurrentCount == 99) {
            ToastUtils.showShort("不能再加了");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        ItemAdd(currentCart.getPID());

    }

    /**
     * 删减
     *
     * @param position      组元素位置
     * @param showCountView 用于展示变化后数量的View
     * @param isChecked     子元素选中与否
     */
    @Override
    public void doDecrease(int position, View showCountView, boolean isChecked) {
        currentCart = datas.get(position);
        getView = showCountView;
        CurrentCount = currentCart.getAmount();
        if (CurrentCount == 1) {
            ToastUtils.showShort("不能再减了");
            //((TextView) showCountView).setEnabled(false);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        ItemSub(currentCart.getPID());


    }

    /**
     * 删除
     *
     * @param position
     */
    @Override
    public void childDelete(int position) {
        progressBar.setVisibility(View.VISIBLE);
        touch_position = position;
        ItemDel(datas.get(position).getPID());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //全选按钮
            case R.id.ck_all:
                if (datas.size() != 0) {
                    if (checkBox.isChecked()) {
                        for (int i = 0; i < datas.size(); i++) {
                            datas.get(i).setChecked(true);
                        }
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < datas.size(); i++) {
                            datas.get(i).setChecked(false);
                        }
                        cartAdapter.notifyDataSetChanged();
                    }
                }
                statistics();
                break;
            //右上角的编辑按钮
            case R.id.tv_header_right:
                ToastUtils.showShort("111");
                flag = !flag;
                if (flag) {
                    tv_edit.setText("完成");
                    tv_settlement.setVisibility(View.GONE);
                    tv_cart_delete.setVisibility(View.VISIBLE);
                    cartAdapter.isShow(false);
                } else {
                    tv_edit.setText("编辑");
                    tv_settlement.setVisibility(View.VISIBLE);
                    tv_cart_delete.setVisibility(View.GONE);
                    cartAdapter.isShow(true);
                }
                break;
            //结算按钮
            case R.id.tv_settlement:
                //进入订单页面
                if(totalCount==0){
                    ToastUtils.showShort("请选中商品");
                }else {
                    ARouter.getInstance()
                            .build(ConfigConstants.ORDERCONFIRMATION)
                            .navigation(App.getContext(), new LoginNavigationCallbackImpl());
                }
                break;
            //删除按钮
            case R.id.tv_cart_delete:
                if(totalCount==0){
                    ToastUtils.showShort("请选中需要删除的商品");
                }
                else {
                    AlertDialog alert = new AlertDialog.Builder(this).create();
                    alert.setTitle("操作提示");
                    alert.setMessage("您确定要将这些商品从购物车中移除吗？");
                    alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    DelAll();
                                    //modifyCountInterface.childDelete(holder.getAdapterPosition());//删除 目前只是从item中移除
                                }
                            });
                    alert.show();
                }

        }

    }

    /**
     * 统计操作
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作
     * 3.给底部的textView进行数据填充
     */
    public void statistics() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < datas.size(); i++) {
            CartItem mCartItem = datas.get(i);
            if (mCartItem.isChecked()) {
                totalCount++;
                totalPrice += mCartItem.getPrice() * mCartItem.getAmount();
            }
        }
        DecimalFormat df = new DecimalFormat("#.00");
        if(totalCount==0) {
            tv_show_price.setText("合计:￥0.00");
        }
        else
            tv_show_price.setText("合计:￥" + df.format(totalPrice));
        tv_settlement.setText("结算(" + totalCount + ")");
    }

    private void getCart(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            HttpUtil.postMethodWithToken("/cart/get", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        List<CartItem> mCartList =new Gson().fromJson(response.body(),new TypeToken<List<CartItem>>(){}.getType());
                        if(mCartList.size()!=0){
                            datas.clear();
                            datas.addAll(mCartList);
                            cartAdapter.setData(mCartList);
                            progressBar.setVisibility(View.GONE);
                            statistics();
                            if(isAllCheck()) {//购物车中所有的商品都被选中
                                checkBox.setChecked(true);
                            }
                        }
                    }
                    else if (response.code()==300){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==404){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();

                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void ItemAdd(String PID){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/cart/increase", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        CurrentCount++;
                        currentCart.setAmount(CurrentCount);
                        //((TextView) getView).setText(CurrentCount + "");
                        cartAdapter.notifyDataSetChanged();
                        statistics();

                    }
                    else if (response.code()==300){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void ItemSub(String PID){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/cart/sub", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        CurrentCount--;
                        currentCart.setAmount(CurrentCount);
                        //((TextView) getView).setText(CurrentCount + "");
                        cartAdapter.notifyDataSetChanged();
                        statistics();
                    }
                    else if (response.code()==300){
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void DelAll(){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            //map.put("PID",PID);
            HttpUtil.postMethodWithToken("/cart/delall", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                    }
                    else if (response.code()==300){
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }

    private void ItemDel(String PID){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/cart/del", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);
                        datas.remove(touch_position);
                        cartAdapter.setData(datas);
                        cartAdapter.notifyDataSetChanged();
                        ToastUtils.showShort("已删除");
                        statistics();

                    }
                    else if (response.code()==300){
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }

    private void ItemUpdate(String PID){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/cart/update", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){
                        progressBar.setVisibility(View.GONE);

                    }
                    else if (response.code()==300){
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(App.getContext(), "--"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
