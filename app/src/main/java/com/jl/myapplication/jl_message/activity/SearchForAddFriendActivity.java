package com.jl.myapplication.jl_message.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivitySearchForAddFriendBinding;
import com.jl.myapplication.jl_message.InfoModel;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by ${chenyn} on 2017/3/13.
 */

public class SearchForAddFriendActivity extends BaseActivity {
    private ActivitySearchForAddFriendBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_for_add_friend;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        mBinding.btnSearch.setEnabled(false);
        setTitle("搜索用户");
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.etSearchUser.addTextChangedListener(new TextChange());
        mBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKbTwo();
                String searchUserName = mBinding.etSearchUser.getText().toString();
                if (!TextUtils.isEmpty(searchUserName)) {
                    // 极光IM获取用户信息
                    JMessageClient.getUserInfo(searchUserName, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                            if (responseCode == 0) {
                                InfoModel.getInstance().friendInfo = info;
                                mBinding.searchResult.setVisibility(View.VISIBLE);
                                mBinding.searchAddBtn.setVisibility(View.VISIBLE);
                                //这个接口会在本地寻找头像文件,不存在就异步拉取
                                File avatarFile = info.getAvatarFile();
                                if (avatarFile != null) {
                                    InfoModel.getInstance().setBitmap(BitmapFactory.decodeFile(avatarFile.getAbsolutePath()));
                                } else {
                                    InfoModel.getInstance().setBitmap(BitmapFactory.decodeResource(getResources(), com.luck.picture.lib.R.drawable.ic_camera));
                                }
                                    mBinding.searchName.setText(TextUtils.isEmpty(info.getNickname()) ? info.getUserName() : info.getNickname());
                            } else {
                                ToastUtils.show(SearchForAddFriendActivity.this, "该用户不存在");
                                mBinding.searchResult.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        mBinding.searchAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //聊天
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(App.TARGET_ID, InfoModel.getInstance().friendInfo.getUserName());
                intent.putExtra(App.TARGET_APP_KEY, InfoModel.getInstance().friendInfo.getAppKey());
                intent.setClass(SearchForAddFriendActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        mBinding.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.etSearchUser.setText("");
            }
        });
    }

    private class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            boolean feedback = mBinding.etSearchUser.getText().length() > 0;
            if (feedback) {
                mBinding.ivClear.setVisibility(View.VISIBLE);
                mBinding.btnSearch.setEnabled(true);
            } else {
                mBinding.ivClear.setVisibility(View.GONE);
                mBinding.btnSearch.setEnabled(false);
            }
        }
    }

    // 隐藏软键盘
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
