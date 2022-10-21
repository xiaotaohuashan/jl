package com.jl.myapplication.jl_home.fragment;

import android.os.AsyncTask;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentHomeBinding;
import com.jl.myapplication.jl_home.adapter.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private FragmentHomeBinding mBinding;
    private HomeAdapter mHomeAdapter;
    private int pageNum = 1;
    private int pageSize = 15;
    private List<String> mList;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        mBinding = getBindView();
        showMessage();
    }

    private void showMessage(){
        mBinding.mLoadLayout.setEmptyText("暂无查询结果");
//        mBinding.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                pageNum = 1;
//                setData();
//            }
//        });
        mHomeAdapter = new HomeAdapter(getActivity());
//        mBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mBinding.mRecyclerView.setLoadingListener(new XRecyclerViewTwo.LoadingListener() {
//            @Override
//            public void onRefresh() {
//
//            }
//
//            @Override
//            public void onLoadMore() {
//                LogUtils.i("55555");
//                pageNum ++;
//                for (int i = 0;i < 15 ;i ++){
//                    int mun = pageNum * pageSize - 15 + i;
//                    mList.add("第" + mun + "条数据");
//                }
//                mHomeAdapter.setList(mList);
//                mBinding.mRecyclerView.setNoMore(false);
//            }
//        });
//        mBinding.mRecyclerView.setAdapter(mHomeAdapter);
//        mBinding.mRecyclerView.setLoadingMoreEnabled(true);
        setData();
    }

    private void setData(){
        mList = new ArrayList<>();
        for (int i = 0;i < 15 ;i ++){
            int mun = pageNum * pageSize - 15 + i;
            mList.add("第" + mun + "条数据");
        }
        mHomeAdapter.setList(mList);
        LogUtils.i("11111");
        mBinding.mLoadLayout.showContent();

//        mBinding.mRecyclerView.setLoadError();
//         mBinding.mRecyclerView.setRefreshComplete();
//        mBinding.mRecyclerView.setNoMore(false);
//        if (mBinding.mSwipeRefreshLayout.isRefreshing()) {
//            mBinding.mSwipeRefreshLayout.setRefreshing(false);
//        }
    }


}
