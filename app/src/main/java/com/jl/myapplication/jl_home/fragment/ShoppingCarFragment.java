package com.jl.myapplication.jl_home.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentOrganizationBinding;
import com.jl.myapplication.databinding.FragmentShoppingCarBinding;
import com.jl.myapplication.jl_home.adapter.HomeAdapter;
import com.jl.myapplication.jl_home.adapter.ShoppingCarAdapter;
import com.jl.myapplication.model.DataBean;
import com.jl.myapplication.model.ShoppingCarBean;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// 购物车
public class ShoppingCarFragment extends BaseFragment {
    private FragmentShoppingCarBinding mBinding;
    private ShoppingCarAdapter mAdapter;
    private int pageNum = 1;
    private int pageSize = 15;
    private List<ShoppingCarBean> mList;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_shopping_car;
    }

    @Override
    public void setListener() {
        mBinding.ivAllSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllSelect = true;
                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    if (!mAdapter.getList().get(i).isSelect) {
                        isAllSelect = false;
                    }
                }
                if (isAllSelect) {
                    mBinding.ivAllSelect.setBackgroundResource(R.mipmap.confirm_empty);
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        mAdapter.getList().get(i).isSelect = false;
                    }
                } else {
                    mBinding.ivAllSelect.setBackgroundResource(R.mipmap.confirm);
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        mAdapter.getList().get(i).isSelect = true;
                    }
                }
                mAdapter.notifyDataSetChanged();
                mBinding.tvMoney.setText("总额：￥" + getAllMoney());
            }
        });
        mAdapter.setListener(new ShoppingCarAdapter.onItemClickListener() {
            @Override
            public void onItemClickListener(int position, List<ShoppingCarBean> mList) {

            }

            @Override
            public void onSelectClickListener(int position, boolean b) {
                boolean isAllSelect = true;
                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    if (!mAdapter.getList().get(i).isSelect) {
                        isAllSelect = false;
                    }
                }
                if (isAllSelect) {
                    mBinding.ivAllSelect.setBackgroundResource(R.mipmap.confirm);
                } else {
                    mBinding.ivAllSelect.setBackgroundResource(R.mipmap.confirm_empty);
                }
                mBinding.tvMoney.setText("总额：￥" + getAllMoney());
            }

            @Override
            public void onNumberChangeClickListener(int position, int number) {
                mBinding.tvMoney.setText("总额：￥" + getAllMoney());
            }
        });
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

            }
        });
        mAdapter = new ShoppingCarAdapter(getActivity());
        mBinding.recyclerView.setAdapter(mAdapter);
        setData();
    }

    private void setData(){
        mList = ShoppingCarBean.getTestData3();
        mAdapter.setList(mList);
        mBinding.tvAllNumber.setText("共" + mList.size() + "套商品");
    }

    //计算商品所需总金额
    private BigDecimal getAllMoney() {
        BigDecimal money = new BigDecimal("0");
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (mAdapter.getList().get(i).isSelect){
                BigDecimal num = new BigDecimal("" + mAdapter.getList().get(i).number);
                BigDecimal num1 =mAdapter.getList().get(i).money.multiply(num);
                money =  money.add(num1);
            }
        }
        return money;
    }
}
