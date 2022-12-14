package com.jl.myapplication.jl_message.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.jl.core.utils.BitmapUtil;
import com.jl.core.utils.DateUtils;
import com.jl.core.utils.DeviceUtils;
import com.jl.core.utils.FileUtils;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.R;
import com.jl.myapplication.jl_message.DialogCreator;
import com.jl.myapplication.jl_message.FileHelper;
import com.jl.myapplication.jl_message.HandleResponseCode;
import com.jl.myapplication.jl_message.activity.DownLoadActivity;
import com.jl.myapplication.jl_message.activity.MapPickerActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.ChatRoomInfo;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

public class ChatAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {
    private int mOffset = 18;
    private Activity mActivity;
    private Context mContext;
    private Conversation mConv;
    private List<Message> mMsgList = new ArrayList<Message>();//??????????????????
    private UserInfo mUserInfo;
    public Animation mSendingAnim;
    //??????
    private final int TYPE_SEND_TXT = 0;
    private final int TYPE_RECEIVE_TXT = 1;
    // ??????
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //??????
    private final int TYPE_SEND_FILE = 4;
    private final int TYPE_RECEIVE_FILE = 5;
    // ??????
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;
    // ??????
    private final int TYPE_SEND_LOCATION = 8;
    private final int TYPE_RECEIVER_LOCATION = 9;
    //???????????????
    private final int TYPE_GROUP_CHANGE = 10;
    //??????
    private final int TYPE_SEND_VIDEO = 11;
    private final int TYPE_RECEIVE_VIDEO = 12;
    //???????????????
    private final int TYPE_CUSTOM_TXT = 13;

    private int mWidth;

    //???????????????????????????
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private Dialog mDialog;
    private boolean mHasLastPage = false;
    private Map<Integer, UserInfo> mUserInfoMap = new HashMap<>();
    private ContentLongClickListener mLongClickListener;
    private int mSendMsgId;
    private float mDensity;// ??????????????????
    private List<Integer> mIndexList = new ArrayList<Integer>();//????????????
    private int nextPlayPosition = 0;
    private boolean autoPlay = false;
    private final MediaPlayer mp = new MediaPlayer();
    private AnimationDrawable mVoiceAnimation;
    private int mPosition = -1;// ???mSetData?????????????????????????????????????????????
    private boolean mSetData = false;
    private FileInputStream mFIS;
    private FileDescriptor mFD;
    private boolean mIsEarPhoneOn;

    public ChatAdapter(@Nullable List<Message> data, Activity context, Conversation conv, ContentLongClickListener longClickListener) {
        super(data);
        mContext = context;
        mActivity = context;
        mConv = conv;
        // ??????????????????
        this.mMsgList = data;
        // ??????????????????
        reverse(mMsgList);
        // ????????????
        mUserInfo = (UserInfo) mConv.getTargetInfo();
        // ????????????????????????
        mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.jmui_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        mSendingAnim.setInterpolator(lin);

        mLongClickListener = longClickListener;

        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.mDensity = dm.density;
        //????????????
        setMultiTypeDelegate(new MultiTypeDelegate<Message>() {
            @Override
            protected int getItemType(Message msg) {
                //???????????????????????????????????????????????????????????????????????????
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
        });
        //????????????
        getMultiTypeDelegate().registerItemType(TYPE_SEND_TXT, R.layout.jmui_chat_item_send_text)
                .registerItemType(TYPE_RECEIVE_TXT,R.layout.jmui_chat_item_receive_text)
                .registerItemType(TYPE_SEND_IMAGE, R.layout.jmui_chat_item_send_image)
                .registerItemType(TYPE_RECEIVER_IMAGE, R.layout.jmui_chat_item_receive_image)
                .registerItemType(TYPE_SEND_FILE, R.layout.jmui_chat_item_send_file)
                .registerItemType(TYPE_RECEIVE_FILE, R.layout.jmui_chat_item_receive_file)
                .registerItemType(TYPE_SEND_VOICE, R.layout.jmui_chat_item_send_voice)
                .registerItemType(TYPE_RECEIVER_VOICE, R.layout.jmui_chat_item_receive_voice)
                .registerItemType(TYPE_SEND_LOCATION, R.layout.jmui_chat_item_send_location)
                .registerItemType(TYPE_RECEIVER_LOCATION, R.layout.jmui_chat_item_receive_location)
                .registerItemType(TYPE_SEND_VIDEO, R.layout.jmui_chat_item_send_video)
                .registerItemType(TYPE_RECEIVE_VIDEO, R.layout.jmui_chat_item_receive_video)
                .registerItemType(TYPE_CUSTOM_TXT, R.layout.jmui_chat_item_group_change);
    }

    // ??????????????????
    private void reverse(List<Message> list) {
        if (list.size() > 0) {
            Collections.reverse(list);
        }
    }

    public Message getMessage(int position) {
        return mMsgList.get(position);
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

    //??????????????????????????????,??????????????????event?????????????????????????????????????????????????????????
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

    public void addMsgFromReceiptToList(Message msg) {
        mMsgList.add(msg);
        msg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    notifyDataSetChanged();
                } else {
                    HandleResponseCode.onHandle(mContext, i, false);
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void addMsgListToList(List<Message> singleOfflineMsgList) {
        mMsgList.addAll(singleOfflineMsgList);
        notifyDataSetChanged();
    }


    @Override
    protected void convert(BaseViewHolder holder, Message msg) {
        //?????????????????????????????????
        if (msg.getDirect() == MessageDirect.receive && !msg.haveRead()) {
            msg.setHaveRead(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                }
            });
        }

        // ????????????
        long nowDate = msg.getCreateTime();
        if (holder.getAdapterPosition() > 0){
            long lastDate = mMsgList.get(holder.getAdapterPosition() - 1).getCreateTime();
            // ???????????????????????????????????????????????????????????????
            if (nowDate - lastDate > 300000) {
                DateUtils timeFormat = new DateUtils(mContext, nowDate);
                holder.setText(R.id.jmui_send_time_txt, timeFormat.getDetailTime());
                holder.setVisible(R.id.jmui_send_time_txt, true);
            } else {
                holder.setVisible(R.id.jmui_send_time_txt, false);
            }
        }else {
            DateUtils timeFormat = new DateUtils(mContext, nowDate);
            holder.setText(R.id.jmui_send_time_txt, timeFormat.getDetailTime());
            holder.setVisible(R.id.jmui_send_time_txt, true);
        }

        //????????????
        final UserInfo userInfo = msg.getFromUser();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int status, String desc, Bitmap bitmap) {
                    if (status == 0) {
                        holder.setImageBitmap(R.id.jmui_avatar_iv, bitmap);
                    } else {
                        holder.setImageResource(R.id.jmui_avatar_iv, R.drawable.jmui_head_icon);
                    }
                }
            });
        } else {
            holder.setImageResource(R.id.jmui_avatar_iv, R.drawable.jmui_head_icon);
        }

        // ???????????????????????????????????????
        holder.setOnClickListener(R.id.jmui_avatar_iv,new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
        //????????????
        int itemViewType = holder.getItemViewType();
        //????????????
        switch (itemViewType){
            case TYPE_SEND_TXT:
            case TYPE_RECEIVE_TXT:
                handleTextMsg(msg,holder);
                break;
            case TYPE_SEND_IMAGE:
            case TYPE_RECEIVER_IMAGE:
                handleImgMsg(msg, holder);
                break;
            case TYPE_SEND_FILE:
            case TYPE_RECEIVE_FILE:
                FileContent fileContent = (FileContent) msg.getContent();
                String extra = fileContent.getStringExtra("video");
                if (!TextUtils.isEmpty(extra)) {
                    handleVideo(msg, holder);
                } else {
                    handleFileMsg(msg, holder);
                }
                break;
            case TYPE_SEND_VOICE:
            case TYPE_RECEIVER_VOICE:
                handleVoiceMsg(msg, holder);
                break;
            case TYPE_SEND_LOCATION:
            case TYPE_RECEIVER_LOCATION:
                break;
            case TYPE_SEND_VIDEO:
            case TYPE_RECEIVE_VIDEO:
            case TYPE_CUSTOM_TXT:
            default:
                return;
        }
    }

    public void handleTextMsg(final Message msg, final BaseViewHolder holder) {
        final String content = ((TextContent) msg.getContent()).getText();
        holder.setText(R.id.jmui_msg_content,  content);
        holder.setOnLongClickListener(R.id.jmui_msg_content,mLongClickListener);
        // ?????????????????????????????????????????????
        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    holder.setVisible(R.id.jmui_fail_resend_ib, true);//??????????????????
                    holder.setVisible(R.id.jmui_sending_iv, false);//?????????????????????
                    holder.setVisible(R.id.text_receipt, false);//????????????
                    break;
                case send_going:
                    // ?????????
                    holder.setVisible(R.id.jmui_fail_resend_ib, false);//??????????????????
                    holder.setVisible(R.id.jmui_sending_iv, true);//?????????????????????
                    holder.setVisible(R.id.text_receipt, false);//????????????
                    //??????????????????????????????????????????????????????????????????Callback
                    if (!msg.isSendCompleteCallbackExists()) {
                        msg.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(final int status, final String desc) {
                                holder.setVisible(R.id.jmui_sending_iv, false);//?????????????????????
                                if (status == 803008) {
                                    CustomContent customContent = new CustomContent();
                                    customContent.setBooleanValue("blackList", true);
                                    Message customMsg = mConv.createSendMessage(customContent);
                                    addMsgToList(customMsg);
                                } else if (status == 803005) {
                                    holder.setVisible(R.id.jmui_fail_resend_ib, true);//??????????????????
                                    ToastUtils.show(mContext, "????????????, ?????????????????????");
                                } else if (status != 0) {
                                    holder.setVisible(R.id.jmui_fail_resend_ib, true);//??????????????????
                                    HandleResponseCode.onHandle(mContext, status, false);
                                }
                            }
                        });
                    }
                    break;
                case send_success:
                    holder.setVisible(R.id.jmui_fail_resend_ib, false);//??????????????????
                    holder.setVisible(R.id.jmui_sending_iv, false);//?????????????????????
                    holder.setVisible(R.id.text_receipt, true);//????????????
                    break;
                case send_fail:
                    holder.setVisible(R.id.jmui_fail_resend_ib, true);//??????????????????
                    holder.setVisible(R.id.jmui_sending_iv, false);//?????????????????????
                    holder.setVisible(R.id.text_receipt, true);//????????????
                    break;
                default:
            }
        }
        holder.setOnClickListener(R.id.jmui_fail_resend_ib, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showResendDialog(holder, msg);
            }
        });
    }

    // ????????????
    public void handleImgMsg(final Message msg, final BaseViewHolder holder) {
        final ImageContent imgContent = (ImageContent) msg.getContent();
        final String jiguang = imgContent.getStringExtra("jiguang");
        // ?????????????????????
        final String path = imgContent.getLocalThumbnailPath();
        if (path == null) {
            //???????????????????????????
            imgContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        ImageView imageView = setPictureScale(jiguang, msg, file.getPath(), holder.getView(R.id.jmui_picture_iv));
                        Glide.with(mContext).load(file).into(imageView);
                    }
                }
            });
        } else {
            if (holder.getView(R.id.jmui_picture_iv) != null){
                ImageView imageView = setPictureScale(jiguang, msg, path, holder.getView(R.id.jmui_picture_iv));
                Glide.with(mContext).load(new File(path)).into(imageView);
            }
        }
        // ????????????
        if (msg.getDirect() == MessageDirect.receive) {
            switch (msg.getStatus()) {
                case receive_fail:
                    holder.setImageResource(R.id.jmui_picture_iv,R.drawable.jmui_fetch_failed);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_fail_resend_ib).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imgContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                                @Override
                                public void onComplete(int i, String s, File file) {
                                    if (i == 0) {
                                        ToastUtils.show(mContext, "????????????");
                                        holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                                        notifyDataSetChanged();
                                    } else {
                                        ToastUtils.show(mContext, "????????????" + s);
                                    }
                                }
                            });
                        }
                    });
                    break;
                default:
            }
            // ???????????????????????????????????????
        } else {
            //????????????
            switch (msg.getStatus()) {
                case created:
                    holder.getView(R.id.jmui_picture_iv).setEnabled(false);
                    holder.getView(R.id.jmui_fail_resend_ib).setEnabled(false);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    holder.setText(R.id.jmui_progress_tv,"0%");
                    break;
                case send_success:
                    holder.getView(R.id.jmui_picture_iv).setEnabled(true);
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.text_receipt).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_picture_iv).setAlpha(1.0f);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.getView(R.id.jmui_fail_resend_ib).setEnabled(true);
                    holder.getView(R.id.jmui_picture_iv).setEnabled(true);
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_picture_iv).setAlpha(1.0f);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    holder.getView(R.id.jmui_picture_iv).setEnabled(false);
                    holder.getView(R.id.jmui_fail_resend_ib).setEnabled(false);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    sendingImage(msg, holder);
                    break;
                default:
                    holder.getView(R.id.jmui_picture_iv).setAlpha(0.75f);
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_sending_iv).startAnimation(mSendingAnim);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
                    holder.setText(R.id.jmui_progress_tv,"0%");
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    //????????????????????????????????????????????????
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
        if (holder.getView(R.id.jmui_picture_iv) != null) {
            // ??????????????????
            holder.getView(R.id.jmui_picture_iv).setOnLongClickListener(mLongClickListener);

        }
        if (msg.getDirect().equals(MessageDirect.send) && holder.getView(R.id.jmui_fail_resend_ib) != null) {
            holder.getView(R.id.jmui_fail_resend_ib).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showResendDialog(holder, msg);
                }
            });
        }
    }

    /**
     * ????????????????????????
     *
     * @param path      ????????????
     * @param imageView ???????????????View
     */
    private ImageView setPictureScale(String extra, Message message, String path, final ImageView imageView) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);


        //????????????????????????
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

    public class BtnOrTxtListener implements View.OnClickListener {

        private int position;
        private BaseViewHolder holder;

        public BtnOrTxtListener(int index,  final BaseViewHolder holder) {
            this.position = index;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            Message msg = mMsgList.get(position);
            MessageDirect msgDirect = msg.getDirect();
            switch (msg.getContentType()) {
                case voice:
                    if (!DeviceUtils.isExternalStorageAvailable()) {
                        Toast.makeText(mContext, "??????????????????",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (mVoiceAnimation != null) {
                        mVoiceAnimation.stop();
                    }
                    // ?????????????????????????????????Item ???????????????
                    if (mp.isPlaying() && mPosition == position) {
                        if (msgDirect == MessageDirect.send) {
                            holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_voice_send);
                        } else {
                            holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_voice_receive);
                        }
                        mVoiceAnimation = (AnimationDrawable)((ImageView) holder.getView(R.id.jmui_voice_iv)).getDrawable();
                        pauseVoice(msgDirect, holder.getView(R.id.jmui_voice_iv));
                        // ??????????????????
                    } else if (msgDirect == MessageDirect.send) {
                        holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_voice_send);
                        mVoiceAnimation = (AnimationDrawable)((ImageView) holder.getView(R.id.jmui_voice_iv)).getDrawable();

                        // ?????????????????????????????????
                        if (mSetData && mPosition == position) {
                            mVoiceAnimation.start();
                            mp.start();
                            // ?????????????????????????????????????????????
                        } else {
                            playVoice(position, holder, true);
                        }
                        // ????????????????????????????????????????????????????????????
                    } else {
                        try {
                            // ?????????????????????????????????
                            if (mSetData && mPosition == position) {
                                if (mVoiceAnimation != null) {
                                    mVoiceAnimation.start();
                                }
                                mp.start();
                                // ?????????????????????????????????
                            } else {
                                // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                if (msg.getContent().getBooleanExtra("isRead") == null
                                        || !msg.getContent().getBooleanExtra("isRead")) {
                                    autoPlay = true;
                                    playVoice(position, holder, false);
                                    // ?????????????????????????????????
                                } else {
                                    holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_voice_receive);
                                    mVoiceAnimation = (AnimationDrawable)((ImageView) holder.getView(R.id.jmui_voice_iv)).getDrawable();
                                    playVoice(position, holder, false);
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case image:
//                    if (holder.getView(R.id.jmui_picture_iv) != null && v.getId() == holder.getView(R.id.jmui_picture_iv).getId()) {
//                        Intent intent = new Intent();
//                        String targetId = "";
//                        intent.putExtra("msgId", msg.getId());
//                        Object targetInfo = mConv.getTargetInfo();
//                        switch (mConv.getType()) {
//                            case single:
//                                targetId = ((UserInfo) targetInfo).getUserName();
//                                break;
//                            case group:
//                                targetId = String.valueOf(((GroupInfo) targetInfo).getGroupID());
//                                break;
//                            case chatroom:
//                                targetId = String.valueOf(((ChatRoomInfo) targetInfo).getRoomID());
//                                intent.putExtra(BrowserViewPagerActivity.MSG_JSON, msg.toJson());
//                                intent.putExtra(BrowserViewPagerActivity.MSG_LIST_JSON, getImsgMsgListJson());
//                                break;
//                            default:
//                        }
//                        intent.putExtra(App.CONV_TYPE, mConv.getType());
//                        intent.putExtra(App.TARGET_ID, targetId);
//                        intent.putExtra(App.TARGET_APP_KEY, mConv.getTargetAppKey());
//                        intent.putExtra("msgCount", mMsgList.size());
//                        intent.putIntegerArrayListExtra(App.MsgIDs, getImgMsgIDList());
//                        intent.putExtra("fromChatActivity", true);
//                        intent.setClass(mContext, BrowserViewPagerActivity.class);
//                        mContext.startActivity(intent);
//                    }
                    break;
                case location:
                    if (holder.getView(R.id.jmui_picture_iv) != null && v.getId() == holder.getView(R.id.jmui_picture_iv).getId()) {
                        Intent intent = new Intent(mContext, MapPickerActivity.class);
                        LocationContent locationContent = (LocationContent) msg.getContent();
                        intent.putExtra("latitude", locationContent.getLatitude().doubleValue());
                        intent.putExtra("longitude", locationContent.getLongitude().doubleValue());
                        intent.putExtra("locDesc", locationContent.getAddress());
                        intent.putExtra("sendLocation", false);
                        mContext.startActivity(intent);
                    }
                    break;
                case file:
                    if (msg.getStatus() == MessageStatus.send_going ||  msg.getStatus() == MessageStatus.receive_going) {
                        return;
                    }
                    final FileContent content = (FileContent) msg.getContent();
                    String path = content.getLocalPath();
                    if (path == null) {
                        return;
                    }
                    File file = new File(path);
                    if (file == null) {
                        return;
                    }
                    if (file.exists()) {
                        Intent intent = FileUtils.getViewIntent(mContext, file);
                        try {
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(mContext, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String fileUrl;
                    }
                    break;
            }

        }
    }

    private void pauseVoice(MessageDirect msgDirect, ImageView voice) {
        if (msgDirect == MessageDirect.send) {
            voice.setImageResource(R.drawable.send_3);
        } else {
            voice.setImageResource(R.drawable.jmui_receive_3);
        }
        mp.pause();
        mSetData = true;
    }

    private void browseDocument(String fileName, String path) {
        try {
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mime = mimeTypeMap.getMimeTypeFromExtension(ext);
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mime);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "??????????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    public void handleVoiceMsg(final Message msg, final BaseViewHolder holder) {
        final VoiceContent content = (VoiceContent) msg.getContent();
        final MessageDirect msgDirect = msg.getDirect();
        int length = content.getDuration();
        String lengthStr = length + "\"";
        if (holder.getView(R.id.jmui_voice_length_tv) != null){
            holder.setText(R.id.jmui_voice_length_tv,lengthStr);
        }
        //??????????????????????????????????????????????????????????????????
        int width = (int) (-0.04 * length * length + 4.526 * length + 75.214);
        ((TextView)holder.getView(R.id.jmui_msg_content)).setWidth((int) (width * mDensity));
        //???????????????position
        holder.getView(R.id.jmui_msg_content).setOnLongClickListener(mLongClickListener);
        if (msgDirect == MessageDirect.send) {
            holder.setImageResource(R.id.jmui_voice_iv,R.drawable.send_3);
            switch (msg.getStatus()) {
                case created:
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    break;
                case send_success:
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    holder.getView(R.id.text_receipt).setVisibility(View.VISIBLE);
                    break;
                case send_fail:
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    sendingTextOrVoice(holder, msg);
                    break;
                default:
            }
        } else switch (msg.getStatus()) {
            case receive_success:
                holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_receive_3);
                // ???????????????????????????
                if (msg.getContent().getBooleanExtra("isRead") == null
                        || !msg.getContent().getBooleanExtra("isRead")) {
                    mConv.updateMessageExtra(msg, "isRead", false);
                    holder.getView(R.id.jmui_read_status_iv).setVisibility(View.VISIBLE);
//                    if (mIndexList.size() > 0) {
//                        if (!mIndexList.contains(position)) {
//                            addToListAndSort(position);
//                        }
//                    } else {
//                        addToListAndSort(position);
//                    }
//                    if (nextPlayPosition == position && autoPlay) {
//                        playVoice(position, holder, false);
//                    }
                } else if (msg.getContent().getBooleanExtra("isRead")) {
                    holder.getView(R.id.jmui_read_status_iv).setVisibility(View.GONE);
                }
                break;
            case receive_fail:
                holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_receive_3);
                // ????????????????????????????????????
                content.downloadVoiceFile(msg,
                        new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int status, String desc, File file) {

                            }
                        });
                break;
            case receive_going:
                break;
            default:
        }

        if (holder.getView(R.id.jmui_fail_resend_ib) != null) {
            holder.getView(R.id.jmui_fail_resend_ib).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (msg.getContent() != null) {
                        showResendDialog(holder, msg);
                    } else {
                        Toast.makeText(mContext, "??????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        holder.getView(R.id.jmui_msg_content).setOnClickListener(new BtnOrTxtListener(holder.getAdapterPosition(), holder));
    }

    public void handleLocationMsg(final Message msg, final BaseViewHolder holder) {
        final LocationContent content = (LocationContent) msg.getContent();
        String path = content.getStringExtra("path");

        holder.setText(R.id.jmui_loc_desc,content.getAddress());
        if (msg.getDirect() == MessageDirect.receive) {
            switch (msg.getStatus()) {
                case receive_going:
                    break;
                case receive_success:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap locationBitmap = createLocationBitmap(content.getLongitude(), content.getLatitude());
                            if (locationBitmap != null) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.setVisible(R.id.location_view,true);
                                        holder.setImageBitmap(R.id.jmui_picture_iv,locationBitmap);
                                    }
                                });
                            }
                        }
                    }).start();
                    break;
                case receive_fail:
                    break;
            }
        } else {
            if (path != null && holder.getView(R.id.jmui_picture_iv) != null) {
                try {
                    File file = new File(path);
                    if (file.exists() && file.isFile()) {
                        Glide.with(mContext).load(file).into((ImageView) holder.getView(R.id.jmui_picture_iv));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            switch (msg.getStatus()) {
                case created:
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    if (null != mUserInfo/* && !mUserInfo.isFriend()*/) {
                        holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    }
                    break;
                case send_going:
                    sendingTextOrVoice(holder, msg);
                    break;
                case send_success:
                    holder.getView(R.id.text_receipt).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    break;
            }
        }
        if (holder.getView(R.id.jmui_picture_iv) != null) {
            holder.getView(R.id.jmui_picture_iv).setOnClickListener(new BtnOrTxtListener(holder.getAdapterPosition(), holder));
            holder.getView(R.id.jmui_picture_iv).setOnLongClickListener(mLongClickListener);

        }

        if (holder.getView(R.id.jmui_fail_resend_ib) != null) {
            holder.getView(R.id.jmui_fail_resend_ib).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (msg.getContent() != null) {
                        showResendDialog(holder, msg);
                    } else {
                        Toast.makeText(mContext, "??????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //???????????????????????????
    private void sendingTextOrVoice(final BaseViewHolder holder, final Message msg) {
        holder.getView(R.id.text_receipt).setVisibility(View.GONE);
        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
        holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
        holder.getView(R.id.jmui_sending_iv).startAnimation(mSendingAnim);
        //??????????????????????????????????????????????????????????????????Callback
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        addMsgToList(customMsg);
                    } else if (status == 803005) {
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                        ToastUtils.show(mContext, "????????????, ?????????????????????");
                    } else if (status != 0) {
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }
    }

    //???????????????
    public void showResendDialog(final BaseViewHolder holder, final Message msg) {
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

    // ???????????????????????????
    private void resendTextOrVoice(final BaseViewHolder holder, Message msg) {
        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
        holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
        holder.getView(R.id.jmui_sending_iv).startAnimation(mSendingAnim);

        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(msg, options);
    }

    // ??????????????????
    private void resendImage(final BaseViewHolder holder, Message msg) {
        holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
        holder.getView(R.id.jmui_sending_iv).startAnimation(mSendingAnim);
        holder.getView(R.id.jmui_picture_iv).setAlpha(0.75f);
        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
        holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
        try {
            // ??????????????????
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String progressStr = (int) (progress * 100) + "%";
                            holder.setText(R.id.jmui_progress_tv,progressStr);
                        }
                    });
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        holder.getView(R.id.jmui_sending_iv).clearAnimation();
                        holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                        holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                        holder.getView(R.id.jmui_picture_iv).setAlpha(1.0f);
                        if (status != 0) {
                            HandleResponseCode.onHandle(mContext, status, false);
                            holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
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

    // ??????????????????
    private void resendFile(final BaseViewHolder holder, final Message msg) {
        if (holder.getView(R.id.jmui_send_file_ll) != null)
            holder.setBackgroundColor(R.id.jmui_send_file_ll,Color.parseColor("#86222222"));
        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
        holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
        try {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String progressStr = (int) (progress * 100) + "%";
                            holder.setText(R.id.jmui_progress_tv,progressStr);
                        }
                    });
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                        //????????????api21??????????????????????????????????????????????????????.??????api????????????ContextCompat.getDrawable
                        holder.getView(R.id.jmui_send_file_ll).setBackground(mContext.getDrawable(R.drawable.jmui_msg_send_bg));
                        if (status != 0) {
                            HandleResponseCode.onHandle(mContext, status, false);
                            holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
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

    private void sendingImage(final Message msg, final BaseViewHolder holder) {
        holder.getView(R.id.jmui_picture_iv).setAlpha(0.75f);
        holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
        holder.getView(R.id.jmui_sending_iv).startAnimation(mSendingAnim);
        holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
        holder.setText(R.id.jmui_progress_tv,"0%");
        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
        //???????????????????????????????????????????????????Callback
        if (!msg.isContentUploadProgressCallbackExists()) {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double v) {
                    String progressStr = (int) (v * 100) + "%";
                    holder.setText(R.id.jmui_progress_tv,progressStr);
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
                    holder.getView(R.id.jmui_picture_iv).setAlpha(1.0f);
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        addMsgToList(customMsg);
                    } else if (status != 0) {
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    }

                    Message message = mConv.getMessage(msg.getId());
                    mMsgList.set(mMsgList.indexOf(msg), message);
                }
            });
        }
    }

    public void addMsgToList(Message msg) {
        mMsgList.add(msg);
        notifyDataSetChanged();
    }

    private void addToListAndSort(int position) {
        mIndexList.add(position);
        Collections.sort(mIndexList);
    }

    public void playVoice(final int position, final BaseViewHolder holder, final boolean isSender) {
        // ???????????????????????????
        mPosition = position;
        Message msg = mMsgList.get(position);
        if (autoPlay) {
            mConv.updateMessageExtra(msg, "isRead", true);
            holder.getView(R.id.jmui_read_status_iv).setVisibility(View.GONE);
            if (mVoiceAnimation != null) {
                mVoiceAnimation.stop();
                mVoiceAnimation = null;
            }
            holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_voice_receive);
            mVoiceAnimation = (AnimationDrawable)((ImageView) holder.getView(R.id.jmui_voice_iv)).getDrawable();
        }
        try {
            mp.reset();
            VoiceContent vc = (VoiceContent) msg.getContent();
            mFIS = new FileInputStream(vc.getLocalPath());
            mFD = mFIS.getFD();
            mp.setDataSource(mFD);
            if (mIsEarPhoneOn) {
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mp.prepare();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVoiceAnimation.start();
                    mp.start();
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mVoiceAnimation.stop();
                    mp.reset();
                    mSetData = false;
                    if (isSender) {
                        holder.setImageResource(R.id.jmui_voice_iv,R.drawable.send_3);
                    } else {
                        holder.setImageResource(R.id.jmui_voice_iv,R.drawable.jmui_receive_3);
                    }
                    if (autoPlay) {
                        int curCount = mIndexList.indexOf(position);
                        if (curCount + 1 >= mIndexList.size()) {
                            nextPlayPosition = -1;
                            autoPlay = false;
                        } else {
                            nextPlayPosition = mIndexList.get(curCount + 1);
                            notifyDataSetChanged();
                        }
                        mIndexList.remove(curCount);
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(mContext, "????????????, ??????????????????",
                    Toast.LENGTH_SHORT).show();
            VoiceContent vc = (VoiceContent) msg.getContent();
            vc.downloadVoiceFile(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        Toast.makeText(mContext, "????????????",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "??????????????????",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } finally {
            try {
                if (mFIS != null) {
                    mFIS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static abstract class ContentLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            return true;
        }

        public abstract void onContentLongClick(int position, View view);
    }

    //?????????????????????????????????api????????????????????????
    private Bitmap createLocationBitmap(Number longitude, Number latitude) {
        String mapUrl = "http://api.map.baidu.com/staticimage?width=160&height=90&center="
                + longitude + "," + latitude + "&zoom=18";
        try {
            URL url = new URL(mapUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setConnectTimeout(5000);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSendMsgs(Message msg) {
        if (msg != null) {
            mMsgList.add(msg);
            mMsgQueue.offer(msg);
        }

        if (mMsgQueue.size() > 0) {
            Message message = mMsgQueue.element();
            sendNextImgMsg(message);
            notifyDataSetChanged();
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param msg ????????????
     */
    private void sendNextImgMsg(Message msg) {
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(msg, options);
        msg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                //??????
                mMsgQueue.poll();
                //????????????????????????????????????????????????
                if (!mMsgQueue.isEmpty()) {
                    sendNextImgMsg(mMsgQueue.element());
                }
                notifyDataSetChanged();
            }
        });
    }

    //?????????
    public void handleVideo(final Message msg, final BaseViewHolder holder) {
        FileContent fileContent = (FileContent) msg.getContent();
        String videoPath = fileContent.getLocalPath();
        if (videoPath != null) {
            File dir = new File(App.THUMP_PICTURE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String thumbPath;
            if (msg.getServerMessageId() == 0) {
                switch (msg.getTargetType()) {
                    case single:
                        thumbPath = dir + "/" + msg.getTargetType() + "_" + ((UserInfo) msg.getTargetInfo()).getUserID() + "_" + msg.getId();
                        break;
                    case group:
                        thumbPath = dir + "/" + msg.getTargetType() + "_" + ((GroupInfo) msg.getTargetInfo()).getGroupID() + "_" + msg.getId();
                        break;
                    case chatroom:
                        thumbPath = dir + "/" + msg.getTargetType() + "_" + ((ChatRoomInfo) msg.getTargetInfo()).getRoomID() + "_" + msg.getId();
                        break;
                    default:
                        Glide.with(mContext).load(R.drawable.video_not_found).into((ImageView) holder.getView(R.id.jmui_picture_iv));
                        return;
                }
            } else {
                thumbPath = dir + "/" + msg.getServerMessageId();
            }
            String path = BitmapUtil.extractThumbnail(videoPath, thumbPath);
            setPictureScale(null, msg, path, (ImageView) holder.getView(R.id.jmui_picture_iv));
            Glide.with(mContext).load(new File(path)).into((ImageView) holder.getView(R.id.jmui_picture_iv));
        } else {
            Glide.with(mContext).load(R.drawable.video_not_found).into((ImageView) holder.getView(R.id.jmui_picture_iv));
        }
        holder.getView(R.id.jmui_picture_iv).setEnabled(false);
        holder.getView(R.id.jmui_fail_resend_ib).setEnabled(false);
        holder.getView(R.id.text_receipt).setVisibility(View.GONE);
        holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
        holder.setText(R.id.jmui_progress_tv,"0%");
        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    holder.getView(R.id.message_item_video_play).setVisibility(View.GONE);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    if (null != mUserInfo) {
                        holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    }
                    break;
                case send_success:
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_picture_iv).setAlpha(1.0f);
                    holder.getView(R.id.text_receipt).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    holder.getView(R.id.message_item_video_play).setVisibility(View.VISIBLE);
                    break;
                case send_fail:
                    holder.getView(R.id.jmui_sending_iv).clearAnimation();
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_picture_iv).setAlpha(1.0f);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    holder.getView(R.id.message_item_video_play).setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.message_item_video_play).setVisibility(View.GONE);
                    sendingImage(msg, holder);
                    break;
                default:
                    holder.getView(R.id.jmui_picture_iv).setAlpha(0.75f);
                    holder.getView(R.id.jmui_sending_iv).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_sending_iv).startAnimation(mSendingAnim);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
                    holder.getView(R.id.message_item_video_play).setVisibility(View.GONE);
                    holder.setText(R.id.jmui_progress_tv,"0%");
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    //????????????????????????????????????????????????
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
                    break;
            }

            holder.getView(R.id.jmui_fail_resend_ib).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showResendDialog(holder, msg);
                }
            });

        } else {
            switch (msg.getStatus()) {
                case receive_going:
                    holder.getView(R.id.message_item_video_play).setVisibility(View.VISIBLE);
                    break;
                case receive_fail:
                    holder.getView(R.id.message_item_video_play).setVisibility(View.VISIBLE);
                    break;
                case receive_success:
                    holder.getView(R.id.message_item_video_play).setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

        }
        holder.getView(R.id.jmui_picture_iv).setOnClickListener(new BtnOrTxtListener(holder.getAdapterPosition(), holder));
        holder.getView(R.id.jmui_picture_iv).setOnLongClickListener(mLongClickListener);
    }

    public void handleFileMsg(final Message msg, final BaseViewHolder holder) {
        final FileContent content = (FileContent) msg.getContent();
        if (holder.getView(R.id.jmui_msg_content) != null) {
            holder.setText(R.id.jmui_msg_content,content.getFileName());
        }
        Number fileSize = content.getNumberExtra("fileSize");
        if (fileSize != null && holder.getView(R.id.jmui_send_file_size) != null) {
            String size = FileUtils.getFileSize(fileSize);
            holder.setText(R.id.jmui_send_file_size,size);
        }
        String fileType = content.getStringExtra("fileType");
        Drawable drawable;
        if (fileType != null && (fileType.equals("mp4") || fileType.equals("mov") || fileType.equals("rm") ||
                fileType.equals("rmvb") || fileType.equals("wmv") || fileType.equals("avi") ||
                fileType.equals("3gp") || fileType.equals("mkv"))) {
            drawable = mContext.getResources().getDrawable(R.drawable.jmui_video);
        } else if (fileType != null && (fileType.equals("wav") || fileType.equals("mp3") || fileType.equals("wma") || fileType.equals("midi"))) {
            drawable = mContext.getResources().getDrawable(R.drawable.jmui_audio);
        } else if (fileType != null && (fileType.equals("ppt") || fileType.equals("pptx") || fileType.equals("doc") ||
                fileType.equals("docx") || fileType.equals("pdf") || fileType.equals("xls") ||
                fileType.equals("xlsx") || fileType.equals("txt") || fileType.equals("wps"))) {
            drawable = mContext.getResources().getDrawable(R.drawable.jmui_document);
            //.jpeg .jpg .png .bmp .gif
        } else if (fileType != null && (fileType.equals("jpeg") || fileType.equals("jpg") || fileType.equals("png") ||
                fileType.equals("bmp") || fileType.equals("gif"))) {
            drawable = mContext.getResources().getDrawable(R.drawable.image_file);
        } else {
            drawable = mContext.getResources().getDrawable(R.drawable.jmui_other);
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        if (holder.getView(R.id.iv_document) != null)
            holder.setImageBitmap(R.id.iv_document,bitmapDrawable.getBitmap());
        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
                    holder.setText(R.id.jmui_progress_tv,"0%");
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    if (null != mUserInfo) {
                        holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
                        holder.setText(R.id.jmui_progress_tv,"0%");
                        holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    }
                    break;
                case send_going:
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    if (!msg.isContentUploadProgressCallbackExists()) {
                        msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                            @Override
                            public void onProgressUpdate(double v) {
                                String progressStr = (int) (v * 100) + "%";
                                holder.setText(R.id.jmui_progress_tv,progressStr);
                            }
                        });
                    }
                    if (!msg.isSendCompleteCallbackExists()) {
                        msg.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int status, String desc) {
                                holder.getView(R.id.jmui_send_file_ll).setBackground(mContext.getDrawable(R.drawable.jmui_msg_send_bg));
                                holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                                if (status == 803008) {
                                    CustomContent customContent = new CustomContent();
                                    customContent.setBooleanValue("blackList", true);
                                    Message customMsg = mConv.createSendMessage(customContent);
                                    addMsgToList(customMsg);
                                } else if (status != 0) {
                                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    break;
                case send_success:
                    holder.getView(R.id.text_receipt).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_send_file_ll).setBackground(mContext.getDrawable(R.drawable.jmui_msg_send_bg));
                    holder.getView(R.id.file_already_send).setVisibility(View.VISIBLE);
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.GONE);
                    break;
                case send_fail:
                    holder.getView(R.id.file_already_send).setVisibility(View.VISIBLE);
                    holder.setText(R.id.file_already_send,"????????????");
                    holder.getView(R.id.text_receipt).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_send_file_ll).setBackground(mContext.getDrawable(R.drawable.jmui_msg_send_bg));
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_fail_resend_ib).setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (msg.getStatus()) {
                case receive_going:
                    holder.getView(R.id.jmui_send_file_ll).setBackgroundColor(Color.parseColor("#86222222"));
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
                    holder.setText(R.id.jmui_send_file_load,"");
                    if (!msg.isContentDownloadProgressCallbackExists()) {
                        msg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
                            @Override
                            public void onProgressUpdate(double v) {
                                if (v < 1) {
                                    String progressStr = (int) (v * 100) + "%";
                                    holder.setText(R.id.jmui_progress_tv,progressStr);
                                } else {
                                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                                    holder.getView(R.id.jmui_send_file_ll).setBackground(mContext.getDrawable(R.drawable.jmui_msg_receive_bg));
                                }

                            }
                        });
                    }
                    break;
                case receive_fail://???????????????????????????????????????
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    //????????????????????????????????????????????????????????????
                    //mContext.getDrawable(R.drawable.jmui_msg_receive_bg)
                    //???????????????????????? NoSuchMethodError ??????setBackground???????????????????????????
                    //ContextCompat.getDrawable(mContext, R.drawable.jmui_msg_receive_bg)
                    holder.getView(R.id.jmui_send_file_ll).setBackground(ContextCompat.getDrawable(mContext, R.drawable.jmui_msg_receive_bg));
                    holder.setText(R.id.jmui_send_file_load,"?????????");
                    break;
                case receive_success:
                    holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                    holder.getView(R.id.jmui_send_file_ll).setBackground(mContext.getDrawable(R.drawable.jmui_msg_receive_bg));
                    if (mConv.getType() != ConversationType.chatroom) {
                        holder.setText(R.id.jmui_send_file_load,"?????????");
                    }
                    break;
            }
        }

        if (holder.getView(R.id.jmui_send_file_load) != null) {
            holder.getView(R.id.jmui_send_file_load).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (msg.getDirect() == MessageDirect.send) {
                        showResendDialog(holder, msg);
                    } else {
                        holder.getView(R.id.jmui_send_file_ll).setBackgroundColor(Color.parseColor("#86222222"));
                        holder.setText(R.id.jmui_progress_tv,"0%");
                        holder.getView(R.id.jmui_progress_tv).setVisibility(View.VISIBLE);
                        if (!msg.isContentDownloadProgressCallbackExists()) {
                            msg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
                                @Override
                                public void onProgressUpdate(double v) {
                                    String progressStr = (int) (v * 100) + "%";
                                    holder.setText(R.id.jmui_progress_tv,progressStr);
                                }
                            });
                        }
                        content.downloadFile(msg, new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int status, String desc, File file) {
                                holder.getView(R.id.jmui_progress_tv).setVisibility(View.GONE);
                                holder.getView(R.id.jmui_send_file_ll).setBackground(mContext.getDrawable(R.drawable.jmui_msg_receive_bg));
                                if (status != 0) {
                                    holder.setText(R.id.jmui_send_file_load,"?????????");
                                    Toast.makeText(mContext, R.string.download_file_failed,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, R.string.download_file_succeed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        holder.getView(R.id.jmui_send_file_ll).setOnLongClickListener(mLongClickListener);
        holder.getView(R.id.jmui_send_file_ll).setOnClickListener(new BtnOrTxtListener(holder.getAdapterPosition(), holder));
    }

}
