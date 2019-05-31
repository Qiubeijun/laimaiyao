package com.laimaiyao.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.adapter.CartAdapter;
import com.laimaiyao.adapter.OneAdapter;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.model.CartItem;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = ConfigConstants.CART)
public class CartFragment extends Fragment implements CartAdapter.CheckInterface,CartAdapter.ModifyCountInterface,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    private View view;
    private Toolbar toolbar;
    private TextView tv_edit;
    private TextView tv_settlement;//结算
    private RecyclerView recyclerView;
    private TextView tv_show_price;//合计价格

    private OneAdapter oneAdapter;
    private CartAdapter cartAdapter;
    private ProgressBar progressBar;
    private double totalPrice = 0.00;// 购买的商品总价
    private int totalCount = 0;// 购买的商品总数量
    private CheckBox checkBox;
    private boolean flag = false;
    private SparseArray<Boolean> mSelectState = new SparseArray<Boolean>();
    private List<CartItem> datas =new ArrayList<CartItem>();


    /**
     * 批量模式下，用来记录当前选中状态
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        ARouter.getInstance().inject(this);
        InitView(view);
        InitData();
        return view;
    }
    private void InitData(){
        getCart();
    }
    private void InitView( View  view1){
        progressBar = view1.findViewById(R.id.progressBar);
        checkBox = view1.findViewById(R.id.ck_all);
        toolbar = view1.findViewById(R.id.toolbar_cart);
        tv_settlement = view1.findViewById(R.id.tv_settlement);
        tv_settlement.setOnClickListener(this);
        tv_edit = view1.findViewById(R.id.tv_header_right);
        tv_show_price = view1.findViewById(R.id.tv_show_price);
        recyclerView = view1.findViewById(R.id.rv_cart_list);


        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        cartAdapter = new CartAdapter(App.getContext());
        cartAdapter.setCheckInterface(this);
        cartAdapter.setModifyCountInterface(this);
        recyclerView.setAdapter(cartAdapter);
        checkBox.setOnClickListener(this);
    }

    /**
     * 单选
     *
     * @param position  组元素位置
     * @param isChecked 组元素选中与否
     */
    @Override
    public void checkGroup(int position, boolean isChecked) {

        datas.get(position).setChecked(isChecked);
        if (isAllCheck())
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
        cartAdapter.notifyDataSetChanged();
        statistics();
    }
    /**
     * 遍历list集合
     *
     * @return
     */
    private boolean isAllCheck() {

        for (CartItem group : datas) {
            if (!group.isChecked()==false)
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
        CartItem CartItem = datas.get(position);
        int currentCount = CartItem.getAmount();
        currentCount++;
        CartItem.setAmount(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        cartAdapter.notifyDataSetChanged();
        statistics();
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
        CartItem CartItem = datas.get(position);
        int currentCount = CartItem.getAmount();
        if (currentCount == 1) {
            return;
        }
        currentCount--;
        CartItem.setAmount(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        cartAdapter.notifyDataSetChanged();
        statistics();

    }

    /**
     * 删除 
     *
     * @param position
     */
    @Override
    public void childDelete(int position) {
        datas.remove(position);
        cartAdapter.notifyDataSetChanged();
        statistics();
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
            //右上角按钮
            case R.id.tv_header_right:
                flag = !flag;
                if (flag) {
                    tv_edit.setText("完成");
                    cartAdapter.isShow(false);
                } else {
                    tv_edit.setText("编辑");
                    cartAdapter.isShow(true);
                }
                break;
            //结算按钮
            case R.id.tv_settlement:
                ARouter.getInstance()
                        .build(ConfigConstants.ORDERCONFIRMATION)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
                break;
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
        tv_show_price.setText("合计:" + totalPrice);
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
                            datas.addAll(mCartList);
                            cartAdapter.setData(mCartList);
                        }
                    }
                    else if (response.code()==300){
                        Toast.makeText(App.getContext(), "--"+response.body(), Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==404){
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

    private void ItemAdd(String PID){
        if(HttpUtil.isNetworkAvailable(App.getContext())){
            Map<String, String> map=new HashMap<>();
            String UID = SPUtils.getInstance().getString("uid");
            String token = SPUtils.getInstance().getString("token");
            map.put("UID",UID);
            map.put("PID",PID);
            HttpUtil.postMethodWithToken("/cart/add", token, map, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code()==200){

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
