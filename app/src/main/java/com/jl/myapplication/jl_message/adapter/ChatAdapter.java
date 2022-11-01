package com.jl.myapplication.jl_message.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.timepicker.TimeFormat;
import com.jl.myapplication.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
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
    //当前第0项消息的位置
    private int mStart;
    //发送图片消息的队列
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private Dialog mDialog;
    private boolean mHasLastPage = false;

    public ChatAdapter(Activity context, Conversation conv) {
        this.mContext = context;
        mActivity = context;
        this.mConv = conv;
    }

    @Override
    public int getCount() {
        return mMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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


        switch (msg.getContentType()) {
            case text:
                TextContent textContent = (TextContent) msg.getContent();
                String extraBusiness = textContent.getStringExtra("businessCard");
                if (extraBusiness != null) {
                    holder.txtContent.setVisibility(View.GONE);
                    holder.ll_businessCard.setVisibility(View.VISIBLE);
//                    mController.handleBusinessCard(msg, holder, position);
                } else {
                    holder.ll_businessCard.setVisibility(View.GONE);
                    holder.txtContent.setVisibility(View.VISIBLE);
//                    mController.handleTextMsg(msg, holder, position);
                }
                break;
            case image:
//                mController.handleImgMsg(msg, holder, position);
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
        if (msg.getDirect() == MessageDirect.send && !msg.getContentType().equals(ContentType.prompt)
                && msg.getContentType() != ContentType.custom   && msg.getContentType() != ContentType.video) {
            if (msg.getUnreceiptCnt() == 0) {
                if (msg.getTargetType() == ConversationType.group) {
                    holder.text_receipt.setText("全部已读");
                } else if (!((UserInfo) msg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                    holder.text_receipt.setText("已读");
                }
//                holder.text_receipt.setTextColor(mContext.getResources().getColor(R.color.message_already_receipt));
            } else {
//                holder.text_receipt.setTextColor(mContext.getResources().getColor(R.color.message_no_receipt));
                if (msg.getTargetType() == ConversationType.group) {
                    holder.text_receipt.setText(msg.getUnreceiptCnt() + "人未读");
                    //群聊未读消息数点击事件
                    holder.text_receipt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else if (!((UserInfo) msg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
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
}
