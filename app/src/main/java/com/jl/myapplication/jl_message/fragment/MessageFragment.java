package com.jl.myapplication.jl_message.fragment;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.utils.ToastUtils;
import com.jl.core.view.XRecyclerViewTwo;
import com.jl.myapplication.App;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentMessageBinding;
import com.jl.myapplication.jl_message.HandleResponseCode;
import com.jl.myapplication.jl_message.activity.ChatActivity;
import com.jl.myapplication.jl_message.activity.SearchForAddFriendActivity;
import com.jl.myapplication.jl_message.adapter.MessageAdapter;
import com.jl.myapplication.jl_message.utils.SharePreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class MessageFragment extends BaseFragment {
    private FragmentMessageBinding mBinding;
    private MessageAdapter mHomeAdapter;
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
        // 得到聊天列表
        mList = JMessageClient.getConversationList();
        showMessage();
        setData();
    }

    private void showMessage(){
        mBinding.mLoadLayout.setEmptyText("暂无查询结果");
        mBinding.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                setData();
                mBinding.mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mHomeAdapter = new MessageAdapter(getActivity());
        mBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.mRecyclerView.setLoadingMoreEnabled(true);
        mBinding.mRecyclerView.setLoadingListener(new XRecyclerViewTwo.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {

                mBinding.mRecyclerView.setNoMore(false);
            }
        });
        mBinding.mRecyclerView.setAdapter(mHomeAdapter);

        mBinding.mRecyclerView.setOnItemClickListener(new XRecyclerViewTwo.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(App.TARGET_ID, mList.get(position).getTargetId());
                intent.putExtra(App.TARGET_APP_KEY, mList.get(position).getTargetAppKey());
                intent.setClass(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setData(){
        mHomeAdapter.setList(mList);
        mBinding.mRecyclerView.setLoadError();
        mBinding.mLoadLayout.showContent();
    }

    // 注册
    private void register(String userId,String password){
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

    // 登陆
    private void login(String userId,String password){
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
}

