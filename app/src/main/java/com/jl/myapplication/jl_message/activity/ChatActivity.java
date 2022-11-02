package com.jl.myapplication.jl_message.activity;



import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jl.myapplication.App.TARGET_APP_KEY;
import static com.jl.myapplication.App.TARGET_ID;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.App;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityChatBinding;
import com.jl.myapplication.jl_me.activity.AboutUseActivity;
import com.jl.myapplication.jl_message.TipItem;
import com.jl.myapplication.jl_message.TipView;
import com.jl.myapplication.jl_message.adapter.ChatAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

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
        mAdapter = new ChatAdapter(this,mConv,longClickListener);
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

    public void onEvent(CommandNotificationEvent event) {
        if (event.getType().equals(CommandNotificationEvent.Type.single)) {
            String msg = event.getMsg();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(msg);
                        JSONObject jsonContent = object.getJSONObject("content");
                        String messageString = jsonContent.getString("message");
                        if (TextUtils.isEmpty(messageString)) {
                            setTitle(mConv.getTitle());
                        } else {
                            setTitle(messageString);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private ChatAdapter.ContentLongClickListener longClickListener = new ChatAdapter.ContentLongClickListener() {

        @Override
        public void onContentLongClick(final int position, View view) {

            final Message msg = mAdapter.getMessage(position);

            if (msg == null) {
                return;
            }
            //如果是文本消息
            if ((msg.getContentType() == ContentType.text) && ((TextContent) msg.getContent()).getStringExtra("businessCard") == null) {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("复制"))
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 0) {
                                        if (msg.getContentType() == ContentType.text) {
                                            final String content = ((TextContent) msg.getContent()).getText();
                                            if (Build.VERSION.SDK_INT > 11) {
                                                ClipboardManager clipboard = (ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("Simple text", content);
                                                clipboard.setPrimaryClip(clip);
                                            } else {
                                                android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                if (clip.hasText()) {
                                                    clip.getText();
                                                }
                                            }
                                            Toast.makeText(ChatActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                        Intent intent = new Intent(ChatActivity.this, AboutUseActivity.class);
                                        App.forwardMsg.clear();
                                        App.forwardMsg.add(msg);
                                        startActivity(intent);
                                    } else {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                    //发送方
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("复制"))
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("撤回"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 0) {
                                        if (msg.getContentType() == ContentType.text) {
                                            final String content = ((TextContent) msg.getContent()).getText();
                                            if (Build.VERSION.SDK_INT > 11) {
                                                ClipboardManager clipboard = (ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("Simple text", content);
                                                clipboard.setPrimaryClip(clip);
                                            } else {
                                                android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                if (clip.hasText()) {
                                                    clip.getText();
                                                }
                                            }
                                            Toast.makeText(ChatActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                        //转发
                                        if (msg.getContentType() == ContentType.text || msg.getContentType() == ContentType.image ||
                                                (msg.getContentType() == ContentType.file && (msg.getContent()).getStringExtra("video") != null)) {
                                            Intent intent = new Intent(ChatActivity.this, AboutUseActivity.class);
                                            App.forwardMsg.clear();
                                            App.forwardMsg.add(msg);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(ChatActivity.this, "只支持转发文本,图片,小视频", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 2) {
                                        //撤回
                                        mConv.retractMessage(msg, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 855001) {
                                                    Toast.makeText(ChatActivity.this, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                } else if (i == 0) {
                                                    mAdapter.delMsgRetract(msg);
                                                }
                                            }
                                        });
                                    } else {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                }
                //除了文本消息类型之外的消息类型
            } else {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 1) {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mAdapter.removeMessage(msg);
                                    } else {
                                        Intent intent = new Intent(ChatActivity.this, AboutUseActivity.class);
                                        App.forwardMsg.clear();
                                        App.forwardMsg.add(msg);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                    //发送方
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("撤回"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 1) {
                                        //撤回
                                        mConv.retractMessage(msg, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 855001) {
                                                    Toast.makeText(ChatActivity.this, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                } else if (i == 0) {
                                                    mAdapter.delMsgRetract(msg);
                                                }
                                            }
                                        });
                                    } else if (position == 0) {
                                        Intent intent = new Intent(ChatActivity.this, AboutUseActivity.class);
                                        App.forwardMsg.clear();
                                        App.forwardMsg.add(msg);
                                        startActivity(intent);
                                    } else {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                }
            }
        }
    };
}
