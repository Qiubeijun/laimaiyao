package com.laimaiyao.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.model.CartItem;
import com.laimaiyao.utils.ConfigConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MI on 2019/5/30
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>  {
    private List<CartItem> mCartItemList = new ArrayList<CartItem>();
    private Context mContext;
    private CheckInterface checkInterface;
    private ModifyCountInterface modifyCountInterface;
    private boolean isShow = true;//是否显示编辑/完成


    //构造方法
    public CartAdapter(Context context) {
        mContext = context;
    }
    //数字回调

    public void setData(List<CartItem> shoppingCartBeanList) {
        this.mCartItemList = shoppingCartBeanList;
        notifyDataSetChanged();
    }

    /**
     * 单选接口
     *
     * @param checkInterface
     */
    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    /**
     * 改变商品数量接口
     *
     * @param modifyCountInterface
     */
    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
        this.modifyCountInterface = modifyCountInterface;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        TextView price;
        TextView amount;
        Button sub;
        Button add;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_cart);
            name =  view.findViewById(R.id.tv_commodity_name);
            price = view.findViewById(R.id.tv_cart_price);
            sub =  view.findViewById(R.id.button_sub);
            add =  view.findViewById(R.id.button_add);
            amount =  view.findViewById(R.id.amount_cart_count);
            checkBox = view.findViewById(R.id.ck_chose);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_cart, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //final int position = holder.getAdapterPosition();

        //单选框按钮
        holder.checkBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        CartItem mCartItem = mCartItemList.get(position);
                        mCartItem.setChecked(mCartItem.isChecked());
                        checkInterface.checkGroup(position, ((CheckBox) v).isChecked());//向外暴露接口
                    }
                }
        );
        //add图标单击事件
        holder.add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CartItem mCartItem = mCartItemList.get(position);
                ToastUtils.showShort("add");
                //暴露增加接口
                modifyCountInterface.doIncrease(position, holder.amount, holder.checkBox.isChecked());

            }
        });
        //sub图标单机事件

        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CartItem mCartItem = mCartItemList.get(position);
                ToastUtils.showShort("sub");
                //暴露增加接口
                modifyCountInterface.doDecrease(position, holder.amount, holder.checkBox.isChecked());

            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                CartItem mCartItem = mCartItemList.get(position);
                //暴露删减接口
                modifyCountInterface.childDelete(position);
                return true;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                CartItem mCartItem = mCartItemList.get(position);
                ARouter.getInstance().build(ConfigConstants.PRODUCT).withString("PID",mCartItem.getPID()).navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartItem mCartItem = mCartItemList.get(position);
        holder.name.setText(mCartItem.getPName());
        holder.price.setText(""+mCartItem.getPrice());
        holder.amount.setText(""+mCartItem.getAmount());
        holder.checkBox.setChecked(mCartItem.isChecked());
    }

    @Override
    public int getItemCount() {
        return mCartItemList.size();
    }

    public Object getItem(int position) {
        return mCartItemList.get(position);
    }

    /**
     * 是否显示可编辑
     *
     * @param flag
     */
    public void isShow(boolean flag) {
        isShow = flag;
        notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 复选框接口
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param position  元素位置
         * @param isChecked 元素选中与否
         */
        void checkGroup(int position, boolean isChecked);
    }

    public interface ModifyCountInterface {
        /**
         * 增加操作
         *
         * @param childPosition 子元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        public void doIncrease(int childPosition, View showCountView, boolean isChecked);

        /**
         * 删减操作
         *
         * @param childPosition 子元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        public void doDecrease(int childPosition, View showCountView, boolean isChecked);
        /**
         * 删除子item
         *
         * @param position
         */
        void childDelete(int position);
    }

}
