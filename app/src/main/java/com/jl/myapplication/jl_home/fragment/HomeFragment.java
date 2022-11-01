package com.jl.myapplication.jl_home.fragment;

import android.content.Intent;
import android.view.View;

import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentHomeBinding;
import com.jl.myapplication.jl_home.activity.GoodsActivity;
import com.jl.myapplication.jl_home.adapter.HomeAdapter;
import com.jl.myapplication.jl_home.adapter.ImageAdapter;
import com.jl.myapplication.model.DataBean;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private FragmentHomeBinding mBinding;
    private HomeAdapter mAdapter;
    private int pageNum = 1;
    private int pageSize = 15;
    private List<String> mList;
    private List<String> mNewImages = new ArrayList<>();
    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void setListener() {
        mBinding.tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GoodsActivity.class));
            }
        });
    }

    @Override
    public void initView() {
        mBinding = getBindView();
        mNewImages.add("https://tc.16pic.com/00/93/62/16pic_9362798_b.png");
        mNewImages.add("https://img-qn.51miz.com/preview/element/00/01/22/13/E-1221354-3186DE29.jpg");
        mNewImages.add("https://pics4.baidu.com/feed/060828381f30e92421eb77eee4b292001f95f7e0.jpeg?token=0e013d11c770267d49400993b919c110");
        showMessage();
    }

    private void showMessage(){
        mAdapter = new HomeAdapter(getActivity());
        //自定义的图片适配器，也可以使用默认的BannerImageAdapter
        ImageAdapter adapter = new ImageAdapter(DataBean.getTestData3(),getContext());
        mBinding.viewBanner.setAdapter(adapter)
                .addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(getContext()))//设置指示器
                .setOnBannerListener((data, position) -> {
                    //点击事件
                    LogUtils.i("66666" + position);
                });

        setData();
    }

    private void setData(){
        mList = new ArrayList<>();
        for (int i = 0;i < 15 ;i ++){
            int mun = pageNum * pageSize - 15 + i;
            mList.add("第" + mun + "条数据");
        }
        mAdapter.setList(mList);
    }
}
