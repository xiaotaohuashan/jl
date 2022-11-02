package com.jl.myapplication.jl_message.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.timepicker.TimeFormat;
import com.jl.core.log.LogUtils;
import com.jl.core.utils.DateUtils;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.R;
import com.jl.myapplication.jl_me.activity.AboutUseActivity;
import com.jl.myapplication.jl_me.activity.SettingActivity;
import com.jl.myapplication.jl_message.DialogCreator;
import com.jl.myapplication.jl_message.HandleResponseCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;


public class ChatAdapter extends BaseAdapter {
    public static final int PAGE_MESSAGE_COUNT = 18;

    //文本
    private final int TYPE_SEND_TXT = 0;
    private final int TYPE_RECEIVE_TXT = 1;

    // 图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;

    //文件
    private final int TYPE_SEND_FILE = 4;
    private final int TYPE_RECEIVE_FILE = 5;
    // 语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;
    // 位置
    private final int TYPE_SEND_LOCATION = 8;
    private final int TYPE_RECEIVER_LOCATION = 9;
    //群成员变动
    private final int TYPE_GROUP_CHANGE = 10;
    //视频
    private final int TYPE_SEND_VIDEO = 11;
    private final int TYPE_RECEIVE_VIDEO = 12;
    //自定义消息
    private final int TYPE_CUSTOM_TXT = 13;

    private Activity mActivity;
    private LayoutInflater mInflater;
    private Context mContext;
    private int mWidth;
    private Conversation mConv;
    private List<Message> mMsgList = new ArrayList<Message>();//所有消息列表
    private int mOffset = PAGE_MESSAGE_COUNT;
    //发送图片消息的队列
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private Dialog mDialog;
    private boolean mHasLastPage = false;
    private Map<Integer, UserInfo> mUserInfoMap = new HashMap<>();
    private UserInfo mUserInfo;
    public Animation mSendingAnim;
    private ContentLongClickListener mLongClickListener;
    private int mSendMsgId;
    public ChatAdapter(Activity context, Conversation conv, ContentLongClickListener longClickListener) {
        mContext = context;
        mActivity = context;
        mConv = conv;
        mInflater = LayoutInflater.from(mContext);
        // 获得消息列表
        this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
        // 颠倒消息列表
        reverse(mMsgList);
        mLongClickListener = longClickListener;
        // 用户信息
        if (mConv.getType() == ConversationType.single) {
            mUserInfo = (UserInfo) mConv.getTargetInfo();
        }
        // 发送消息时的动画
        mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.jmui_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        mSendingAnim.setInterpolator(lin);
    }

    @Override
    public int getCount() {
        LogUtils.i("聊天" + mMsgList.size());
        return mMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Message getMessage(int position) {
        return mMsgList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message msg = mMsgList.get(position);
        //消息接收方发送已读回执
        if (msg.getDirect() == MessageDirect.receive && !msg.haveRead()) {
            msg.setHaveRead(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                }
            });
        }
        final UserInfo userInfo = msg.getFromUser();
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByType(msg, position);
            holder.msgTime = (TextView) convertView.findViewById(R.id.jmui_send_time_txt);
            holder.headIcon = (ImageView) convertView.findViewById(R.id.jmui_avatar_iv);
            holder.displayName = (TextView) convertView.findViewById(R.id.jmui_display_name_tv);
            holder.txtContent = (TextView) convertView.findViewById(R.id.jmui_msg_content);
            holder.sendingIv = (ImageView) convertView.findViewById(R.id.jmui_sending_iv);
            holder.resend = (ImageButton) convertView.findViewById(R.id.jmui_fail_resend_ib);
            holder.ivDocument = (ImageView) convertView.findViewById(R.id.iv_document);
            holder.text_receipt = (TextView) convertView.findViewById(R.id.text_receipt);
            switch (msg.getContentType()) {
                case text:
                    holder.ll_businessCard = (LinearLayout) convertView.findViewById(R.id.ll_businessCard);
                    holder.business_head = (ImageView) convertView.findViewById(R.id.business_head);
                    holder.tv_nickUser = (TextView) convertView.findViewById(R.id.tv_nickUser);
                    holder.tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);
                    break;
                case image:
                    holder.picture = (ImageView) convertView.findViewById(R.id.jmui_picture_iv);
                    holder.progressTv = (TextView) convertView.findViewById(R.id.jmui_progress_tv);
                    break;
                case file:
                    String extra = msg.getContent().getStringExtra("video");
                    if (!TextUtils.isEmpty(extra)) {
                        holder.picture = (ImageView) convertView.findViewById(R.id.jmui_picture_iv);
                        holder.progressTv = (TextView) convertView.findViewById(R.id.jmui_progress_tv);
                        holder.videoPlay = (LinearLayout) convertView.findViewById(R.id.message_item_video_play);
                    } else {
                        holder.progressTv = (TextView) convertView.findViewById(R.id.jmui_progress_tv);
                        holder.contentLl = (LinearLayout) convertView.findViewById(R.id.jmui_send_file_ll);
                        holder.sizeTv = (TextView) convertView.findViewById(R.id.jmui_send_file_size);
                        holder.alreadySend = (TextView) convertView.findViewById(R.id.file_already_send);
                    }
                    if (msg.getDirect().equals(MessageDirect.receive)) {
                        holder.fileLoad = (TextView) convertView.findViewById(R.id.jmui_send_file_load);
                    }
                    break;
                case voice:
                    holder.voice = (ImageView) convertView.findViewById(R.id.jmui_voice_iv);
                    holder.voiceLength = (TextView) convertView.findViewById(R.id.jmui_voice_length_tv);
                    holder.readStatus = (ImageView) convertView.findViewById(R.id.jmui_read_status_iv);
                    break;
                case location:
                    holder.location = (TextView) convertView.findViewById(R.id.jmui_loc_desc);
                    holder.picture = (ImageView) convertView.findViewById(R.id.jmui_picture_iv);
                    holder.locationView = convertView.findViewById(R.id.location_view);
                    break;
                default:
                    holder.groupChange = (TextView) convertView.findViewById(R.id.jmui_group_content);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 显示时间
        long nowDate = msg.getCreateTime();
        if (position == 0 || position % 18 == 0) {
            DateUtils timeFormat = new DateUtils(mContext, nowDate);
            holder.msgTime.setText(timeFormat.getDetailTime());
            holder.msgTime.setVisibility(View.VISIBLE);
        } else {
            long lastDate = mMsgList.get(position - 1).getCreateTime();
            // 如果两条消息之间的间隔超过五分钟则显示时间
            if (nowDate - lastDate > 300000) {
                DateUtils timeFormat = new DateUtils(mContext, nowDate);
                holder.msgTime.setText(timeFormat.getDetailTime());
                holder.msgTime.setVisibility(View.VISIBLE);
            } else {
                holder.msgTime.setVisibility(View.GONE);
            }
        }

        //显示头像
        if (holder.headIcon != null) {
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            holder.headIcon.setImageBitmap(bitmap);
                        } else {
                            holder.headIcon.setImageResource(R.drawable.jmui_head_icon);
                        }
                    }
                });
            } else {
                holder.headIcon.setImageResource(R.drawable.jmui_head_icon);
            }

            // 点击头像跳转到个人信息界面
            holder.headIcon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                }
            });

            holder.headIcon.setTag(position);
        }

        // 显示聊天内容
        switch (msg.getContentType()) {
            case text:
                TextContent textContent = (TextContent) msg.getContent();
                String extraBusiness = textContent.getStringExtra("businessCard");
                if (extraBusiness != null) {
                    holder.txtContent.setVisibility(View.GONE);
                    holder.ll_businessCard.setVisibility(View.VISIBLE);
                    // 名片
                    handleBusinessCard(msg, holder, position);
                } else {
                    holder.ll_businessCard.setVisibility(View.GONE);
                    holder.txtContent.setVisibility(View.VISIBLE);
                    final String content = ((TextContent) msg.getContent()).getText();
                    holder.txtContent.setText(content);
                    handleTextMsg(msg, holder, position);
                }
                break;
            case image:
                    handleImgMsg(msg, holder, position);
                break;
            case file:
                FileContent fileContent = (FileContent) msg.getContent();
                String extra = fileContent.getStringExtra("video");
//                if (!TextUtils.isEmpty(extra)) {
//                    mController.handleVideo(msg, holder, position);
//                } else {
//                    mController.handleFileMsg(msg, holder, position);
//                }
                break;
            case voice:
//                mController.handleVoiceMsg(msg, holder, position);
                break;
            case location:
//                mController.handleLocationMsg(msg, holder, position);
                break;
            case eventNotification:
//                mController.handleGroupChangeMsg(msg, holder);
                break;
            case prompt:
//                mController.handlePromptMsg(msg, holder);
                break;
            case custom:
//                mController.handleCustomMsg(msg, holder);
                break;
            default:
//                mController.handleUnSupportMsg(msg, holder);
                break;
        }

        //已读未读
        if (msg.getDirect() == MessageDirect.send && !msg.getContentType().equals(ContentType.prompt) && msg.getContentType() != ContentType.custom   && msg.getContentType() != ContentType.video) {
            // 获取当前未发送已读回执的人数
            if (msg.getUnreceiptCnt() == 0) {
                if (!((UserInfo) msg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                    holder.text_receipt.setText("已读");
                }
                holder.text_receipt.setTextColor(Color.parseColor("#999999"));
            } else {
                holder.text_receipt.setTextColor(Color.parseColor("#2DD0CF"));
                if (!((UserInfo) msg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                    holder.text_receipt.setText("未读");
                }
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = mMsgList.get(position);
        //是文字类型或者自定义类型（用来显示群成员变化消息）
        switch (msg.getContentType()) {
            case text:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_TXT
                        : TYPE_RECEIVE_TXT;
            case image:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_IMAGE
                        : TYPE_RECEIVER_IMAGE;
            case file:
                String extra = msg.getContent().getStringExtra("video");
                if (!TextUtils.isEmpty(extra)) {
                    return msg.getDirect() == MessageDirect.send ? TYPE_SEND_VIDEO
                            : TYPE_RECEIVE_VIDEO;
                } else {
                    return msg.getDirect() == MessageDirect.send ? TYPE_SEND_FILE
                            : TYPE_RECEIVE_FILE;
                }
            case voice:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_VOICE
                        : TYPE_RECEIVER_VOICE;
            case location:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_LOCATION
                        : TYPE_RECEIVER_LOCATION;
            case eventNotification:
            case prompt:
                return TYPE_GROUP_CHANGE;
            default:
                return TYPE_CUSTOM_TXT;
        }
    }

    private View createViewByType(Message msg, int position) {
        // 会话类型
        switch (msg.getContentType()) {
            case text:
                return getItemViewType(position) == TYPE_SEND_TXT ?
                        mInflater.inflate(R.layout.jmui_chat_item_send_text, null) :
                        mInflater.inflate(R.layout.jmui_chat_item_receive_text, null);
            case image:
                return getItemViewType(position) == TYPE_SEND_IMAGE ?
                        mInflater.inflate(R.layout.jmui_chat_item_send_image, null) :
                        mInflater.inflate(R.layout.jmui_chat_item_receive_image, null);
            case file:
                String extra = msg.getContent().getStringExtra("video");
                if (!TextUtils.isEmpty(extra)) {
                    return getItemViewType(position) == TYPE_SEND_VIDEO ?
                            mInflater.inflate(R.layout.jmui_chat_item_send_video, null) :
                            mInflater.inflate(R.layout.jmui_chat_item_receive_video, null);
                } else {
                    return getItemViewType(position) == TYPE_SEND_FILE ?
                            mInflater.inflate(R.layout.jmui_chat_item_send_file, null) :
                            mInflater.inflate(R.layout.jmui_chat_item_receive_file, null);
                }
            case voice:
                return getItemViewType(position) == TYPE_SEND_VOICE ?
                        mInflater.inflate(R.layout.jmui_chat_item_send_voice, null) :
                        mInflater.inflate(R.layout.jmui_chat_item_receive_voice, null);
            case location:
                return getItemViewType(position) == TYPE_SEND_LOCATION ?
                        mInflater.inflate(R.layout.jmui_chat_item_send_location, null) :
                        mInflater.inflate(R.layout.jmui_chat_item_receive_location, null);
            case eventNotification:
            case prompt:
                if (getItemViewType(position) == TYPE_GROUP_CHANGE) {
                    return mInflater.inflate(R.layout.jmui_chat_item_group_change, null);
                }
            default:
                return mInflater.inflate(R.layout.jmui_chat_item_group_change, null);
        }
    }


    public static class ViewHolder {
        public TextView msgTime;
        public ImageView headIcon;
        public ImageView ivDocument;
        public TextView displayName;
        public TextView txtContent;
        public ImageView picture;
        public TextView progressTv;
        public ImageButton resend;
        public TextView voiceLength;
        public ImageView voice;
        public ImageView readStatus;
        public TextView location;
        public TextView groupChange;
        public ImageView sendingIv;
        public LinearLayout contentLl;
        public TextView sizeTv;
        public LinearLayout videoPlay;
        public TextView alreadySend;
        public View locationView;
        public LinearLayout ll_businessCard;
        public ImageView business_head;
        public TextView tv_nickUser;
        public TextView tv_userName;
        public TextView text_receipt;
        public TextView fileLoad;
    }

    public void addMsgToList(Message msg) {
        mMsgList.add(msg);
        notifyDataSetChanged();
    }

    // 颠倒消息列表
    private void reverse(List<Message> list) {
        if (list.size() > 0) {
            Collections.reverse(list);
        }
    }

    public void handleBusinessCard(final Message msg, final ViewHolder holder, int position) {
        final TextContent[] textContent = {(TextContent) msg.getContent()};
        final String[] mUserName = {textContent[0].getStringExtra("userName")};
        final String mAppKey = textContent[0].getStringExtra("appKey");
        holder.ll_businessCard.setTag(position);
        int key = (mUserName[0] + mAppKey).hashCode();
        UserInfo userInfo = mUserInfoMap.get(key);
        if (userInfo != null) {
            String name = userInfo.getNickname();
            //如果没有昵称,名片上面的位置显示用户名
            //如果有昵称,上面显示昵称,下面显示用户名
            if (TextUtils.isEmpty(name)) {
                holder.tv_userName.setText("");
                holder.tv_nickUser.setText(mUserName[0]);
            } else {
                holder.tv_nickUser.setText(name);
                holder.tv_userName.setText("用户名: " + mUserName[0]);
            }
            if (userInfo.getAvatarFile() != null) {
                holder.business_head.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatarFile().getAbsolutePath()));
            } else {
                holder.business_head.setImageResource(R.drawable.jmui_head_icon);
            }
        } else {
            // 获取用户信息
            JMessageClient.getUserInfo(mUserName[0], mAppKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (i == 0) {
                        mUserInfoMap.put((mUserName[0] + mAppKey).hashCode(), userInfo);
                        String name = userInfo.getNickname();
                        //如果没有昵称,名片上面的位置显示用户名
                        //如果有昵称,上面显示昵称,下面显示用户名
                        if (TextUtils.isEmpty(name)) {
                            holder.tv_userName.setText("");
                            holder.tv_nickUser.setText(mUserName[0]);
                        } else {
                            holder.tv_nickUser.setText(name);
                            holder.tv_userName.setText("用户名: " + mUserName[0]);
                        }
                        if (userInfo.getAvatarFile() != null) {
                            holder.business_head.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatarFile().getAbsolutePath()));
                        } else {
                            holder.business_head.setImageResource(R.drawable.jmui_head_icon);
                        }
                    } else {
                        HandleResponseCode.onHandle(mContext, i, false);
                    }
                }
            });
        }

        holder.ll_businessCard.setOnLongClickListener(mLongClickListener);
        holder.ll_businessCard.setOnClickListener(new BusinessCard(mUserName[0], mAppKey, holder));
        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    if (null != mUserInfo) {
                        holder.sendingIv.setVisibility(View.GONE);
                        holder.resend.setVisibility(View.VISIBLE);
                        holder.text_receipt.setVisibility(View.GONE);
                    }
                    break;
                case send_success:
                    holder.text_receipt.setVisibility(View.VISIBLE);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.text_receipt.setVisibility(View.GONE);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    sendingTextOrVoice(holder, msg);
                    break;
            }
        }
        if (holder.resend != null) {
            holder.resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showResendDialog(holder, msg);
                }
            });
        }
    }

    //正在发送文字或语音
    private void sendingTextOrVoice(final ViewHolder holder, final Message msg) {
        holder.text_receipt.setVisibility(View.GONE);
        holder.resend.setVisibility(View.GONE);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);
        //消息正在发送，重新注册一个监听消息发送完成的Callback
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.sendingIv.clearAnimation();
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        addMsgToList(customMsg);
                    } else if (status == 803005) {
                        holder.resend.setVisibility(View.VISIBLE);
                        ToastUtils.show(mContext, "发送失败, 你不在该群组中");
                    } else if (status != 0) {
                        holder.resend.setVisibility(View.VISIBLE);
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }
    }

    //重发对话框
    public void showResendDialog(final ViewHolder holder, final Message msg) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.jmui_cancel_btn) {
                    mDialog.dismiss();
                } else {
                    mDialog.dismiss();
                    switch (msg.getContentType()) {
                        case text:
                        case voice:
                            resendTextOrVoice(holder, msg);
                            break;
                        case image:
                            resendImage(holder, msg);
                            break;
                        case file:
                            resendFile(holder, msg);
                            break;
                    }
                }
            }
        };
        mDialog = DialogCreator.createResendDialog(mContext, listener);
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }

    // 重新发送文字或语音
    private void resendTextOrVoice(final ViewHolder holder, Message msg) {
        holder.resend.setVisibility(View.GONE);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);

        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                        holder.resend.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(msg, options);
    }

    // 重新发送图像
    private void resendImage(final ViewHolder holder, Message msg) {
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);
        holder.picture.setAlpha(0.75f);
        holder.resend.setVisibility(View.GONE);
        holder.progressTv.setVisibility(View.VISIBLE);
        try {
            // 显示上传进度
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String progressStr = (int) (progress * 100) + "%";
                            holder.progressTv.setText(progressStr);
                        }
                    });
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        holder.sendingIv.clearAnimation();
                        holder.sendingIv.setVisibility(View.GONE);
                        holder.progressTv.setVisibility(View.GONE);
                        holder.picture.setAlpha(1.0f);
                        if (status != 0) {
                            HandleResponseCode.onHandle(mContext, status, false);
                            holder.resend.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            JMessageClient.sendMessage(msg, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 重新发送文件
    private void resendFile(final ViewHolder holder, final Message msg) {
        if (holder.contentLl != null)
            holder.contentLl.setBackgroundColor(Color.parseColor("#86222222"));
        holder.resend.setVisibility(View.GONE);
        holder.progressTv.setVisibility(View.VISIBLE);
        try {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String progressStr = (int) (progress * 100) + "%";
                            holder.progressTv.setText(progressStr);
                        }
                    });
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        holder.progressTv.setVisibility(View.GONE);
                        //此方法是api21才添加的如果低版本会报错找不到此方法.升级api或者使用ContextCompat.getDrawable
                        holder.contentLl.setBackground(mContext.getDrawable(R.drawable.jmui_msg_send_bg));
                        if (status != 0) {
                            HandleResponseCode.onHandle(mContext, status, false);
                            holder.resend.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            JMessageClient.sendMessage(msg, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static abstract class ContentLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            onContentLongClick((Integer) v.getTag(), v);
            return true;
        }

        public abstract void onContentLongClick(int position, View view);
    }

    private class BusinessCard implements View.OnClickListener {
        private String userName;
        private String appKey;
        private ViewHolder mHolder;

        public BusinessCard(String name, String appKey, ViewHolder holder) {
            this.userName = name;
            this.appKey = appKey;
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            if (mHolder.ll_businessCard != null && v.getId() == mHolder.ll_businessCard.getId()) {
                JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        Intent intent = new Intent();
                        if (i == 0) {
                            if (userInfo.isFriend()) {
                                intent.setClass(mContext, AboutUseActivity.class);
                            } else {
                                intent.setClass(mContext, SettingActivity.class);
                            }
                            intent.putExtra(App.TARGET_APP_KEY, appKey);
                            intent.putExtra(App.TARGET_ID, userName);
                            intent.putExtra("fromSearch", true);
                            mContext.startActivity(intent);
                        }else {
                            ToastUtils.show(mContext, "获取信息失败,稍后重试");
                        }
                    }
                });
            }

        }
    }

    //找到撤回的那一条消息,并且用撤回后event下发的去替换掉这条消息在集合中的原位置
    List<Message> forDel;
    int i;

    public void delMsgRetract(Message msg) {
        forDel = new ArrayList<>();
        i = 0;
        for (Message message : mMsgList) {
            if (msg.getServerMessageId().equals(message.getServerMessageId())) {
                i = mMsgList.indexOf(message);
                forDel.add(message);
            }
        }
        mMsgList.removeAll(forDel);
        mMsgList.add(i, msg);
        notifyDataSetChanged();
    }

    List<Message> del = new ArrayList<>();

    public void removeMessage(Message message) {
        for (Message msg : mMsgList) {
            if (msg.getServerMessageId().equals(message.getServerMessageId())) {
                del.add(msg);
            }
        }
        mMsgList.removeAll(del);
        notifyDataSetChanged();
    }

    public void handleTextMsg(final Message msg, final ViewHolder holder, int position) {
        final String content = ((TextContent) msg.getContent()).getText();
//        SimpleCommonUtils.spannableEmoticonFilter(holder.txtContent, content);
        holder.txtContent.setText(content);
        holder.txtContent.setTag(position);
        holder.txtContent.setOnLongClickListener(mLongClickListener);
        // 检查发送状态，发送方有重发机制
        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    if (null != mUserInfo) {
                        holder.sendingIv.setVisibility(View.GONE);
                        holder.resend.setVisibility(View.VISIBLE);
                        holder.text_receipt.setVisibility(View.GONE);
                    }
                    break;
                case send_success:
                    holder.text_receipt.setVisibility(View.VISIBLE);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.text_receipt.setVisibility(View.GONE);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    sendingTextOrVoice(holder, msg);
                    break;
                default:
            }

        } else {
            if (mConv.getType() == ConversationType.group) {
                if (msg.isAtMe()) {
                    mConv.updateMessageExtra(msg, "isRead", true);
                }
                if (msg.isAtAll()) {
                    mConv.updateMessageExtra(msg, "isReadAtAll", true);
                }
                holder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }
            }
        }
        if (holder.resend != null) {
            holder.resend.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showResendDialog(holder, msg);
                }
            });
        }
    }

    // 处理图片
    public void handleImgMsg(final Message msg, final ViewHolder holder, final int position) {
        final ImageContent imgContent = (ImageContent) msg.getContent();
        final String jiguang = imgContent.getStringExtra("jiguang");
        // 先拿本地缩略图
        final String path = imgContent.getLocalThumbnailPath();
        if (path == null) {
            //从服务器上拿缩略图
            imgContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        ImageView imageView = setPictureScale(jiguang, msg, file.getPath(), holder.picture);
                        Glide.with(mContext).load(file).into(imageView);
                    }
                }
            });
        } else {
            ImageView imageView = setPictureScale(jiguang, msg, path, holder.picture);
            Glide.with(mContext).load(new File(path)).into(imageView);
        }

        // 接收图片
        if (msg.getDirect() == MessageDirect.receive) {
            //群聊中显示昵称
            if (mConv.getType() == ConversationType.group) {
                holder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    holder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    holder.displayName.setText(msg.getFromUser().getNickname());
                }
            }

            switch (msg.getStatus()) {
                case receive_fail:
                    holder.picture.setImageResource(R.drawable.jmui_fetch_failed);
                    holder.resend.setVisibility(View.VISIBLE);
                    holder.resend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imgContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                                @Override
                                public void onComplete(int i, String s, File file) {
                                    if (i == 0) {
                                        ToastUtils.show(mContext, "下载成功");
                                        holder.sendingIv.setVisibility(View.GONE);
                                        notifyDataSetChanged();
                                    } else {
                                        ToastUtils.show(mContext, "下载失败" + s);
                                    }
                                }
                            });
                        }
                    });
                    break;
                default:
            }
            // 发送图片方，直接加载缩略图
        } else {
            //检查状态
            switch (msg.getStatus()) {
                case created:
                    holder.picture.setEnabled(false);
                    holder.resend.setEnabled(false);
                    holder.text_receipt.setVisibility(View.GONE);
                    holder.sendingIv.setVisibility(View.VISIBLE);
                    holder.resend.setVisibility(View.GONE);
                    holder.progressTv.setText("0%");
                    break;
                case send_success:
                    holder.picture.setEnabled(true);
                    holder.sendingIv.clearAnimation();
                    holder.text_receipt.setVisibility(View.VISIBLE);
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.picture.setAlpha(1.0f);
                    holder.progressTv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.resend.setEnabled(true);
                    holder.picture.setEnabled(true);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.text_receipt.setVisibility(View.GONE);
                    holder.picture.setAlpha(1.0f);
                    holder.progressTv.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    holder.picture.setEnabled(false);
                    holder.resend.setEnabled(false);
                    holder.text_receipt.setVisibility(View.GONE);
                    holder.resend.setVisibility(View.GONE);
                    sendingImage(msg, holder);
                    break;
                default:
                    holder.picture.setAlpha(0.75f);
                    holder.sendingIv.setVisibility(View.VISIBLE);
                    holder.sendingIv.startAnimation(mSendingAnim);
                    holder.progressTv.setVisibility(View.VISIBLE);
                    holder.progressTv.setText("0%");
                    holder.resend.setVisibility(View.GONE);
                    //从别的界面返回聊天界面，继续发送
                    if (!mMsgQueue.isEmpty()) {
                        Message message = mMsgQueue.element();
                        if (message.getId() == msg.getId()) {
                            MessageSendingOptions options = new MessageSendingOptions();
                            options.setNeedReadReceipt(true);
                            JMessageClient.sendMessage(message, options);
                            mSendMsgId = message.getId();
                            sendingImage(message, holder);
                        }
                    }
            }
        }
        if (holder.picture != null) {
            // 点击预览图片
//            holder.picture.setOnClickListener(new BtnOrTxtListener(position, holder));
            holder.picture.setTag(position);
            holder.picture.setOnLongClickListener(mLongClickListener);

        }
        if (msg.getDirect().equals(MessageDirect.send) && holder.resend != null) {
            holder.resend.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showResendDialog(holder, msg);
                }
            });
        }
    }

    /**
     * 设置图片最小宽高
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    private ImageView setPictureScale(String extra, Message message, String path, final ImageView imageView) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);


        //计算图片缩放比例
        double imageWidth = opts.outWidth;
        double imageHeight = opts.outHeight;
        return setDensity(extra, message, imageWidth, imageHeight, imageView);
    }

    private ImageView setDensity(String extra, Message message, double imageWidth, double imageHeight, ImageView imageView) {
        if (extra != null) {
            imageWidth = 200;
            imageHeight = 200;
        } else {
            if (imageWidth > 350) {
                imageWidth = 550;
                imageHeight = 250;
            } else if (imageHeight > 450) {
                imageWidth = 300;
                imageHeight = 450;
            } else if ((imageWidth < 50 && imageWidth > 20) || (imageHeight < 50 && imageHeight > 20)) {
                imageWidth = 200;
                imageHeight = 300;
            } else if (imageWidth < 20 || imageHeight < 20) {
                imageWidth = 100;
                imageHeight = 150;
            } else {
                imageWidth = 300;
                imageHeight = 450;
            }
        }

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) imageWidth;
        params.height = (int) imageHeight;
        imageView.setLayoutParams(params);

        return imageView;
    }

    private void sendingImage(final Message msg, final ViewHolder holder) {
        holder.picture.setAlpha(0.75f);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);
        holder.progressTv.setVisibility(View.VISIBLE);
        holder.progressTv.setText("0%");
        holder.resend.setVisibility(View.GONE);
        //如果图片正在发送，重新注册上传进度Callback
        if (!msg.isContentUploadProgressCallbackExists()) {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double v) {
                    String progressStr = (int) (v * 100) + "%";
                    holder.progressTv.setText(progressStr);
                }
            });
        }
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    if (!mMsgQueue.isEmpty() && mMsgQueue.element().getId() == mSendMsgId) {
                        mMsgQueue.poll();
                        if (!mMsgQueue.isEmpty()) {
                            Message nextMsg = mMsgQueue.element();
                            MessageSendingOptions options = new MessageSendingOptions();
                            options.setNeedReadReceipt(true);
                            JMessageClient.sendMessage(nextMsg, options);
                            mSendMsgId = nextMsg.getId();
                        }
                    }
                    holder.picture.setAlpha(1.0f);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.progressTv.setVisibility(View.GONE);
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        addMsgToList(customMsg);
                    } else if (status != 0) {
                        holder.resend.setVisibility(View.VISIBLE);
                    }

                    Message message = mConv.getMessage(msg.getId());
                    mMsgList.set(mMsgList.indexOf(msg), message);
                }
            });
        }
    }


}
