package com.jl.myapplication.jl_message.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentMessageBinding;
import com.jl.myapplication.jl_message.HandleResponseCode;
import com.jl.myapplication.jl_message.activity.ChatTwoActivity;
import com.jl.myapplication.jl_message.activity.SearchForAddFriendActivity;
import com.jl.myapplication.jl_message.adapter.MessageAdapter;
import com.jl.myapplication.jl_message.utils.SharePreferenceManager;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class MessageFragment extends BaseFragment {
    private FragmentMessageBinding mBinding;
    private MessageAdapter mAdapter;
    private int pageNum = 1;
    private int pageSize = 15;
    private List<Conversation> mList = new ArrayList<Conversation>();
    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void setListener() {
        mBinding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register("123456","000000");
            }
        });

        mBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login("123456","000000");
            }
        });

        mBinding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchForAddFriendActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initView() {
        mBinding = getBindView();
        // 获取会话列表
        mList = JMessageClient.getConversationList();
        mAdapter = new MessageAdapter(getActivity());
        mAdapter.setList(mList);
        showMessage();
        setData();
    }

    private void showMessage(){
        mBinding.mLoadLayout.setEmptyText("暂无查询结果");

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new MessageAdapter.onItemClickListener() {
            @Override
            public void onItemClickListener(int position, List<Conversation> mList) {
                Intent intent = new Intent(getContext(), ChatTwoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(App.TARGET_ID, mList.get(position).getTargetId());
                intent.putExtra(App.TARGET_APP_KEY, mList.get(position).getTargetAppKey());
                intent.setClass(getContext(), ChatTwoActivity.class);
                startActivity(intent);
            }
        });
//        mBinding.recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//            }
//        });

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
                    refreshLayout.finishLoadMore();
                }else {
                    refreshLayout.finishLoadMore();
                    refreshLayout.setNoMoreData(true);
                }
            }
        });
    }

    private void setData(){

        mBinding.mLoadLayout.showContent();
    }

    // 极光IM注册
    private void register(String userId,String password){
        // 极光IM注册
        JMessageClient.register(userId, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    SharePreferenceManager.setRegisterName(userId);
                    SharePreferenceManager.setRegistePass(password);
                    ToastUtils.show(getContext(), "注册成功");
                } else {
                    HandleResponseCode.onHandle(getContext(), i, false);
                }
            }
        });
    }

    // 极光IM登陆
    private void login(String userId,String password){
        // 极光IM登录
        JMessageClient.login(userId, password, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                if (responseCode == 0) {
                    SharePreferenceManager.setCachedPsw(password);
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    File avatarFile = myInfo.getAvatarFile();
                    //登陆成功,如果用户有头像就把头像存起来,没有就设置null
                    if (avatarFile != null) {
                        SharePreferenceManager.setCachedAvatarPath(avatarFile.getAbsolutePath());
                    } else {
                        SharePreferenceManager.setCachedAvatarPath(null);
                    }
                    ToastUtils.show(getContext(), "登陆成功");

                } else {
                    ToastUtils.show(getContext(), "登陆失败" + responseMessage);
                }
            }
        });
    }

    public void sortConvList() {
        if (mAdapter != null) {
            mAdapter.sortConvList();
        }
    }
}

