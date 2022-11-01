package com.jl.myapplication.jl_message.activity;



import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jl.myapplication.App.TARGET_APP_KEY;
import static com.jl.myapplication.App.TARGET_ID;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityChatBinding;
import com.jl.myapplication.jl_message.adapter.ChatAdapter;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

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
        // 创建单聊会话
        mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        if (mConv == null) {
            mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
        }
        mAdapter = new ChatAdapter(this,mConv);
        mBinding.listview.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        String targetId = getIntent().getStringExtra(TARGET_ID);
        if (null != targetId) {
            String appKey = getIntent().getStringExtra(TARGET_APP_KEY);
            // 进入单聊会话
            JMessageClient.enterSingleConversation(targetId, appKey);
        }
        super.onResume();
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.btnSend.setVisibility(VISIBLE);
                    mBinding.btnMultimedia.setVisibility(GONE);
                    mBinding.btnSend.setBackgroundResource(R.drawable.btn_send_bg);
                } else {
                    mBinding.btnMultimedia.setVisibility(VISIBLE);
                    mBinding.btnSend.setVisibility(GONE);
                }
            }
        });

        mBinding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mcgContent = mBinding.etChat.getText().toString();
                scrollToBottom();
                if (mcgContent.equals("")) {
                    return;
                }
                Message msg;
                TextContent content = new TextContent(mcgContent);
                // 创建一条消息
                msg = mConv.createSendMessage(content);
                // 极光IM发送消息
                JMessageClient.sendMessage(msg);
                mAdapter.addMsgToList(msg);
                mBinding.etChat.setText("");
            }
        });
    }

    private void scrollToBottom() {
//        mBinding.listview.requestLayout();
//        mBinding.listview.post(new Runnable() {
//            @Override
//            public void run() {
//                mBinding.listview.setSelection(mBinding.listview.getBottom());
//            }
//        });
    }
}
