package com.jl.myapplication.jl_message.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jl.myapplication.App.TARGET_APP_KEY;
import static com.jl.myapplication.App.TARGET_ID;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.EmoticonsKeyboardUtils;
import com.jl.core.utils.FileUtils;
import com.jl.core.utils.PicSelectUtil;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.R;
import com.jl.myapplication.config.RequestCode;
import com.jl.myapplication.databinding.ActivityChatBinding;
import com.jl.myapplication.jl_me.activity.AboutUseActivity;
import com.jl.myapplication.jl_message.AppBean;
import com.jl.myapplication.jl_message.AppsAdapter;
import com.jl.myapplication.jl_message.ImageEvent;
import com.jl.myapplication.jl_message.TipItem;
import com.jl.myapplication.jl_message.TipView;
import com.jl.myapplication.jl_message.adapter.ChatAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.exceptions.JMFileSizeExceedException;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import retrofit2.http.Url;

// ????????????
public class ChatActivity extends BaseActivity {
    private ActivityChatBinding mBinding;
    // ????????????
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
        setTitle("????????????");
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        mTargetId = intent.getStringExtra(TARGET_ID);
        mTargetAppKey = intent.getStringExtra(TARGET_APP_KEY);
        // ??????????????????
        mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        if (mConv == null) {
            mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
        }
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatAdapter(mConv.getMessagesFromNewest(0, 18), this, mConv, longClickListener);
        mBinding.recyclerView.setAdapter(mAdapter);

        ArrayList<AppBean> mAppBeanList = new ArrayList<>();
        mAppBeanList.add(new AppBean(R.mipmap.icon_photo, "??????"));
        mAppBeanList.add(new AppBean(R.mipmap.icon_camera, "??????"));
        mAppBeanList.add(new AppBean(R.mipmap.icon_file, "??????"));
        mAppBeanList.add(new AppBean(R.mipmap.icon_loaction, "??????"));
//        mAppBeanList.add(new AppBean(R.mipmap.businesscard, "??????"));
//        mAppBeanList.add(new AppBean(R.mipmap.icon_audio, "??????"));
//        mAppBeanList.add(new AppBean(R.mipmap.icon_voice, "??????"));
        AppsAdapter adapter = new AppsAdapter(this, mAppBeanList);
        mBinding.gvApps.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        String targetId = getIntent().getStringExtra(TARGET_ID);
        if (null != targetId) {
            String appKey = getIntent().getStringExtra(TARGET_APP_KEY);
            // ??????????????????
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
                if (mcgContent.equals("")) {
                    return;
                }
                Message msg;
                TextContent content = new TextContent(mcgContent);
                // ??????????????????
                msg = mConv.createSendMessage(content);
                // ??????IM????????????
                JMessageClient.sendMessage(msg);
                mAdapter.addMsgToList(msg);
                mBinding.etChat.setText("");
            }
        });
        //??????????????????
        mBinding.btnVoiceOrText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.btn_voice_or_text) {
                    requestPermission();
                }
            }
        });
        mBinding.btnMultimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.gvApps.getVisibility() == GONE) {
                    mBinding.gvApps.setVisibility(VISIBLE);
                } else {
                    mBinding.gvApps.setVisibility(GONE);
                }
            }
        });
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
            //?????????????????????
            if ((msg.getContentType() == ContentType.text)) {
                //?????????
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
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
                                            Toast.makeText(ChatActivity.this, "?????????", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                        Intent intent = new Intent(ChatActivity.this, AboutUseActivity.class);
                                        App.forwardMsg.clear();
                                        App.forwardMsg.add(msg);
                                        startActivity(intent);
                                    } else {
                                        //??????
                                        mConv.deleteMessage(msg.getId());
                                        mAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                    //?????????
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
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
                                            Toast.makeText(ChatActivity.this, "?????????", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                        //??????
                                        if (msg.getContentType() == ContentType.text || msg.getContentType() == ContentType.image ||
                                                (msg.getContentType() == ContentType.file && (msg.getContent()).getStringExtra("video") != null)) {
                                            Intent intent = new Intent(ChatActivity.this, AboutUseActivity.class);
                                            App.forwardMsg.clear();
                                            App.forwardMsg.add(msg);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(ChatActivity.this, "?????????????????????,??????,?????????", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 2) {
                                        //??????
                                        mConv.retractMessage(msg, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 855001) {
                                                    Toast.makeText(ChatActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                                                } else if (i == 0) {
                                                    mAdapter.delMsgRetract(msg);
                                                }
                                            }
                                        });
                                    } else {
                                        //??????
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
            } else {
                //?????????????????????????????????????????????
                //?????????
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 1) {
                                        //??????
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
                    //?????????
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mBinding.llMain, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
                            .addItem(new TipItem("??????"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 1) {
                                        //??????
                                        mConv.retractMessage(msg, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 855001) {
                                                    Toast.makeText(ChatActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
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
                                        //??????
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

    public void setVideoText() {
        EmoticonsKeyboardUtils.hintKbTwo(this);
        if (mBinding.rlInput.isShown()) {
            mBinding.btnVoiceOrText.setImageResource(R.drawable.btn_voice_or_text_keyboard);
            mBinding.rlInput.setVisibility(GONE);
            mBinding.btnVoice.setVisibility(VISIBLE);
        } else {
            mBinding.rlInput.setVisibility(VISIBLE);
            mBinding.btnVoice.setVisibility(GONE);
            mBinding.btnVoiceOrText.setImageResource(R.drawable.btn_voice_or_text);
        }
    }

    // ????????????
    public void requestPermission() {
        if (AndPermission.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO)) {
            setVideoText();
            mBinding.btnVoice.initConv(mConv, mAdapter, mBinding.recyclerView);
        } else {
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.RECORD_AUDIO)
                    .onGranted(permissions -> {
                        setVideoText();
                        mBinding.btnVoice.initConv(mConv, mAdapter, mBinding.recyclerView);
                    })
                    .onDenied(permissions -> {
                        ToastUtils.show("??????????????????");
                    })
                    .start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ImageEvent event) {
        Intent intent;
        switch (event.getFlag()) {
            //??????
            case App.IMAGE_MESSAGE:
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(permissions -> {
                            //type 0??????  1??????
                            PicSelectUtil.chooseMultiplePic(this, RequestCode.PICK_IMAGE, 9, 1);
                        })
                        .onDenied(permissions -> {
                            ToastUtils.show("????????????????????????????????????????????????????????????");
                        })
                        .start();
                break;
            //??????
            case App.TAKE_PHOTO_MESSAGE:
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(permissions -> {
                            //type 0??????  1??????
                            PicSelectUtil.chooseMultiplePic(this, RequestCode.PICK_IMAGE, 9, 0);
                        })
                        .onDenied(permissions -> {
                            ToastUtils.show("????????????????????????????????????????????????????????????");
                        })
                        .start();
                break;
            //??????
            case App.TAKE_LOCATION:
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    AndPermission.with(this)
                            .runtime()
                            .permission(Permission.ACCESS_FINE_LOCATION)
                            .onGranted(permissions -> {
                                Intent intent1 = new Intent(mContext, MapPickerActivity.class);
                                intent1.putExtra(App.CONV_TYPE, mConv.getType());
                                intent1.putExtra(App.TARGET_ID, mTargetId);
                                intent1.putExtra(App.TARGET_APP_KEY, mTargetAppKey);
                                intent1.putExtra("sendLocation", true);
                                startActivityForResult(intent1, App.REQUEST_CODE_SEND_LOCATION);
                            })
                            .onDenied(permissions -> {
                                Toast.makeText(this, "??????????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
                            })
                            .start();
                } else {
                    intent = new Intent(mContext, MapPickerActivity.class);
                    intent.putExtra(App.CONV_TYPE, mConv.getType());
                    intent.putExtra(App.TARGET_ID, mTargetId);
                    intent.putExtra(App.TARGET_APP_KEY, mTargetAppKey);
                    intent.putExtra("sendLocation", true);
                    startActivityForResult(intent, App.REQUEST_CODE_SEND_LOCATION);
                }
                break;
            //??????
            case App.FILE_MESSAGE:
//                if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "????????????????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
//
//                } else {
//                    intent = new Intent(mContext, SendFileActivity.class);
//                    intent.putExtra(App.TARGET_ID, mTargetId);
//                    intent.putExtra(App.TARGET_APP_KEY, mTargetAppKey);
//                    intent.putExtra(App.CONV_TYPE, mConv.getType());
//                    startActivityForResult(intent, App.REQUEST_CODE_SEND_FILE);
//                }

                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("*/*");      //   /*/ ?????????????????????????????????
                //intent.setType(???audio/*???) //????????????

                //intent.setType(???video/*???) //???????????? ???mp4 3gp ???android????????????????????????

                //intent.setType(???video/*;image/*???)//???????????????????????????
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent1, 100);

                break;
            //??????
            case App.BUSINESS_CARD:
            case App.TACK_VIDEO:
            case App.TACK_VOICE:
                ToastUtils.show(mContext, "????????????????????????");
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCode.PICK_IMAGE://4
                if (data == null) {
                    return;
                }
                List<LocalMedia> pic = PictureSelector.obtainMultipleResult(data);
                for (int i = 0; i < pic.size(); i++) {
                    //??????????????????????????????
                    File file = new File(pic.get(i).getCompressPath());
                    ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                            if (responseCode == 0) {
                                Message msg = mConv.createSendMessage(imageContent);
                                handleSendMsg(msg);
                            }
                        }
                    });
                }
                break;
            case 100:
                if (data == null) {
                    return;
                }
                Uri uri = data.getData();
                String path = FileUtils.getPath(this, uri);
                if (TextUtils.isEmpty(path)) {
                    Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                String type = path.substring(path.lastIndexOf("."));
                File file = new File(path);
                switch (type) {
                    case ".png":
                    case ".jpg":
                    case ".jpeg":
                    case ".gif":
                        try {
                            Message msg = mConv.createSendImageMessage(file);
                            handleSendMsg(msg);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
//                    case ".3gp":
//                    case ".mpg":
//                    case ".mpeg":
//                    case ".mpe":
//                    case ".mp4":
//                    case ".avi":
//                        break;
                    default:
                        try {
                            Message msg = mConv.createSendFileMessage(file, "");
                            handleSendMsg(msg);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(mContext, mContext.getString(R.string.jmui_file_not_found_toast),
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (JMFileSizeExceedException e) {
                            e.printStackTrace();
                        }
                }
                break;
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param msg
     */
    private void handleSendMsg(Message msg) {
        mAdapter.setSendMsgs(msg);
        setToBottom();
    }

    public void setToBottom() {
        mBinding.recyclerView.clearFocus();
        mBinding.recyclerView.post(new Runnable() {
            @Override
            public void run() {
                mBinding.recyclerView.scrollToPosition(mBinding.recyclerView.getAdapter().getItemCount() - 1);
            }
        });
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        UserInfo userInfo = (UserInfo) conv.getTargetInfo();
        String targetId = userInfo.getUserName();
        String appKey = userInfo.getAppKey();
        if (targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
            List<Message> singleOfflineMsgList = event.getOfflineMessageList();
            if (singleOfflineMsgList != null && singleOfflineMsgList.size() > 0) {
                setToBottom();
                mAdapter.addMsgListToList(singleOfflineMsgList);
            }
        }
    }
}
