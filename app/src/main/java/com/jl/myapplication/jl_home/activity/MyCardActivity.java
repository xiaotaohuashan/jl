package com.jl.myapplication.jl_home.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.jl.core.base.activity.BaseActivity;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityMyCardBinding;
import com.jl.myapplication.jl_home.adapter.HomeAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lenovo on 2018/1/17.
 */
//我的卡卷（用户红包列表）
public class MyCardActivity extends BaseActivity {
    private ActivityMyCardBinding mBinding;
    private HomeAdapter mHomeAdapter;
    private int pageNum = 1;
    private int pageSize = 15;
    private List<String> mList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_card;
    }

    @Override
    protected void initData() {
        mBinding = getBindView();
        setTitle("我的卡卷");
//        mBinding.mLoadLayout.setEmptyIcon(R.mipmap.b);
        mBinding.mLoadLayout.setEmptyText("暂无查询结果");
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                    mHomeAdapter.setList(mList);
                    refreshLayout.finishLoadMore();
                }else {
                    refreshLayout.finishLoadMore();
                    refreshLayout.setNoMoreData(true);
                }
            }
        });
        mHomeAdapter = new HomeAdapter(this);
        mBinding.recyclerView.setAdapter(mHomeAdapter);
        setData();
//        mBinding.mLoadLayout.showContent();
//        mBinding.mLoadLayout.showEmpty();
        mBinding.mLoadLayout.showFailed();
    }

    private void setData(){
        mList = new ArrayList<>();
        for (int i = 0;i < 15 ;i ++){
            int mun = pageNum * pageSize - 15 + i;
            mList.add("第" + mun + "条数据");
        }

        mHomeAdapter.setList(mList);
        LogUtils.i("22222");
//        mBinding.mLoadLayout.showContent();
//
////        mBinding.mRecyclerView.setLoadError();
//        mBinding.mRecyclerView.setRefreshComplete();
//        mBinding.mRecyclerView.setNoMore(false);
//        if (mBinding.mSwipeRefreshLayout.isRefreshing()) {
//            mBinding.mSwipeRefreshLayout.setRefreshing(false);
//        }
    }
}
