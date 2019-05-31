package com.laimaiyao.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laimaiyao.App;
import com.laimaiyao.R;
import com.laimaiyao.interceptor.LoginNavigationCallbackImpl;
import com.laimaiyao.model.ProductCategory;
import com.laimaiyao.utils.ConfigConstants;
import com.laimaiyao.utils.HttpUtil;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import kale.adapter.CommonAdapter;
import kale.adapter.item.AdapterItem;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.ITabView;
import q.rorbin.verticaltablayout.widget.TabView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.laimaiyao.utils.ConfigConstants.SORT_COLUMN;

public class SortFragment extends Fragment {

    private VerticalTabLayout mVerticalTabLayout;
    private GridView mGridView;
    private View view;
    private LinearLayout include;
    private SearchFragment searchFragment;
    private TextView textView;
    private List<String> data_sort_column = new ArrayList<String>();
    private List<ProductCategory> data_sub_sort = new ArrayList<ProductCategory>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sort, container, false);
        data_sort_column.clear();
        data_sort_column.addAll(Arrays.asList(SORT_COLUMN.split(",")));
        InitView();
        return view;
    }

    private void InitView() {
        include = view.findViewById(R.id.search_layout_sort);
        textView = include.findViewById(R.id.edt_search);
        searchFragment = SearchFragment.newInstance();
        searchFragment.setOnSearchClickListener(new IOnSearchClickListener() {
            @Override
            public void OnSearchClick(String keyword) {
                ARouter.getInstance()
                        .build(ConfigConstants.RESULT)
                        .withString("Search_Key",keyword)
                        .navigation(App.getContext(),new LoginNavigationCallbackImpl());
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFragment.showFragment(getChildFragmentManager(),SearchFragment.TAG);
            }
        });
        mVerticalTabLayout = view.findViewById(R.id.VerticalTabLayout);
        mVerticalTabLayout.setTabAdapter(new TabAdapter() {
            @Override
            public int getCount() {
                return data_sort_column.size();
            }

            @Override
            public ITabView.TabBadge getBadge(int position) {
                return null;
            }

            @Override
            public ITabView.TabIcon getIcon(int position) {
                return null;
            }

            @Override
            public ITabView.TabTitle getTitle(int position) {
                return new ITabView.TabTitle.Builder().setContent(data_sort_column.get(position)).build();
            }

            @Override
            public int getBackground(int position) {
                return 0;
            }
        });
        mGridView = view.findViewById(R.id.gridview);
        requestsort(1,String.valueOf(0));
        mVerticalTabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                if(position==0){
                    requestsort(1,String.valueOf(position));
                }else {
                    requestsort(1,String.valueOf(position));
                }
            }

            @Override
            public void onTabReselected(TabView tab, int position) {}
        });
    }

    private boolean requestsort(int ishot, String ParentID) {

        if (HttpUtil.isNetworkAvailable(getContext())) {
            Map<String, String> map = new HashMap<>();
            map.put("ishot", String.valueOf(ishot));
            map.put("parentID", ParentID);
            String token = SPUtils.getInstance().getString("token",null);
            HttpUtil.getMethodWithToken("/sort",token,map,new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    int code = response.code();
                    if (code == 200){
                        ToastUtils.showShort(response.body());
                        List<ProductCategory> productCategories = new Gson().fromJson(response.body(),new TypeToken<List<ProductCategory>>(){}.getType());
                        data_sub_sort.clear();
                        data_sub_sort.addAll(productCategories);
                        mGridView.setAdapter(new CommonAdapter<ProductCategory>(data_sub_sort,1) {
                            public AdapterItem<ProductCategory> createItem(Object type) {
                                return new TextItem();
                            }
                        });
                    }
                    else if (code == 300){ }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(App.getContext(), "--" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        else
            return false;
    }
    public class TextItem implements AdapterItem<ProductCategory> {

        @Override
        public int getLayoutResId() {
            return R.layout.item_sort;
        }

        TextView textView;
        CircleImageView mCircleImageView;
        LinearLayout mLinearLayout;

        @Override
        public void bindViews(View root) {
            textView = root.findViewById(R.id.tv_sort_item);
            mCircleImageView = root.findViewById(R.id.img_sort_item);
            mLinearLayout = root.findViewById(R.id.linear_item_sort);
        }

        @Override
        public void handleData(final ProductCategory model, int position) {

            textView.setText(model.getCategoryName());
            mCircleImageView.setImageResource(R.drawable.ic_pic);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance()
                            .build(ConfigConstants.RESULT)
                            .withString("Search_Key",model.getCategoryName())
                            .navigation(App.getContext(),new LoginNavigationCallbackImpl());
                }
            });
        }
        @Override
        public void setViews() {

        }
    }
}