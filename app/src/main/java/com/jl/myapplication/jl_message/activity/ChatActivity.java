package com.jl.myapplication.jl_message.activity;



import static com.jl.myapplication.App.TARGET_APP_KEY;
import static com.jl.myapplication.App.TARGET_ID;

import android.content.Intent;

import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityChatBinding;
import com.jl.myapplication.jl_message.adapter.ChatAdapter;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;

// 聊天页面
public class ChatActivity extends BaseActivity {
    private ActivityChatBinding mBinding;
    // 聊天内容
    private Conversation mConv;
    private ChatAdapter mAdapter;
    private String mTargetId;
    private String mTargetAppKey;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        setTitle("聊天界面");

        Intent intent = getIntent();
        mTargetId = intent.getStringExtra(TARGET_ID);
        mTargetAppKey = intent.getStringExtra(TARGET_APP_KEY);

        mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        if (mConv == null) {
            mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
        }
        mAdapter = new ChatAdapter(this,mConv);
        mBinding.listview.setAdapter(mAdapter);
    }
}
