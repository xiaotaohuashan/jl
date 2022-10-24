package com.jl.myapplication.jl_home.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentOrganizationBinding;
import com.jl.myapplication.databinding.FragmentShoppingCarBinding;
import com.jl.myapplication.jl_home.adapter.HomeAdapter;
import com.jl.myapplication.jl_home.adapter.ShoppingCarAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

// 购物车
public class ShoppingCarFragment extends BaseFragment {
    private FragmentShoppingCarBinding mBinding;
    private ShoppingCarAdapter mAdapter;
    private int pageNum = 1;
    private int pageSize = 15;
    private List<String> mList;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_shopping_car;
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        mBinding = getBindView();
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNum = 1;
                setData();
                refreshLayout.finishRefresh();
                // 还有更多数据
                refreshLayout.setNoMoreData(false);
            }
        });

        mBinding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNum != 4){
                    pageNum ++;
                    for (int i = 0;i < 15 ;i ++){
                        int mun = pageNum * pageSize - 15 + i;
                        mList.add("第" + mun + "条数据");
                    }
                    mAdapter.setList(mList);
                    refreshLayout.finishLoadMore();
                }else {
                    refreshLayout.finishLoadMore();
                    refreshLayout.setNoMoreData(true);
                }
            }
        });
        mAdapter = new ShoppingCarAdapter(getActivity());
        mBinding.recyclerView.setAdapter(mAdapter);
        setData();
    }

    private void setData(){
        mList = new ArrayList<>();
        for (int i = 0;i < 8 ;i ++){
            int mun = pageNum * pageSize - 8 + i;
            mList.add("第" + mun + "条数据");
        }

        mAdapter.setList(mList);
    }
}
