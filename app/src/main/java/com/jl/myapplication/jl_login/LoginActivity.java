package com.jl.myapplication.jl_login;

import static com.jl.myapplication.App.getContext;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.log.LogUtils;
import com.jl.core.network.httpInterface.DefaultObserver;
import com.jl.core.social.SocialCenter;
import com.jl.core.social.SocialCenterCallback;
import com.jl.core.utils.AesEncryptUtil;
import com.jl.core.utils.JsonUtils;
import com.jl.core.utils.SettingsUtil;
import com.jl.core.utils.StringUtil;
import com.jl.myapplication.App;
import com.jl.myapplication.MainActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityLoginBinding;
import com.jl.myapplication.model.HelpStoreParamForPage;
import com.jl.myapplication.model.LoginBean;
import com.jl.myapplication.model.PasswordBean;


public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        if (!StringUtil.isEmpty(SettingsUtil.getTrueName()) && !StringUtil.isEmpty(SettingsUtil.getPassword()) && !StringUtil.isEmpty(SettingsUtil.getUserType())) {
            loginPw(mActivity, SettingsUtil.getTrueName(), SettingsUtil.getPassword(), SettingsUtil.getUserType());
        }
        Intent it = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(it);
        finish();

    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        mBinding.loginProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mBinding.loginWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFastClick()) {
                    wxLogin();
                }
            }
        });
    }

    /**
     * 密码登录
     */
    public void loginPw(Context context, final String phoneStr, String passWordStr, String type) {
        if (StringUtil.isEmpty(phoneStr)) {
            Toast.makeText(context, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phoneStr.length() != 11) {
            Toast.makeText(context, "手机号格式不对", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(passWordStr)) {
            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(type)) {
            Toast.makeText(context, "请选择类型", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingDialog();
        PasswordBean passwordBean = new PasswordBean();
        passwordBean.username = phoneStr;
        String s;
        try {
            s = AesEncryptUtil.encrypt(passWordStr);
            if (!StringUtil.isEmpty(s) && s.length() > 2) {
                String a = s.substring(s.length() - 1);
                String b = s.substring(s.length() - 3);
                if (a.equals("\n")) {
                    passwordBean.password = s.substring(0, s.length() - 1);
                } else {
                    passwordBean.password = s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String userType;
        try {
            userType = AesEncryptUtil.encrypt(type);
            if (!StringUtil.isEmpty(userType) && userType.length() > 2) {
                String a = userType.substring(userType.length() - 1);
                String b = userType.substring(userType.length() - 3);
                if (a.equals("\n")) {
                    passwordBean.type = userType.substring(0, userType.length() - 1);
                } else {
                    passwordBean.type = userType;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        App.getService().getLoginService().loginPW(passwordBean, new DefaultObserver() {
            @Override
            public void onSuccess(int code, String message, JsonElement data, JsonObject all_data) {
                if (code == 216) {
                    LoginBean loginBean = JsonUtils.fromJson(data, LoginBean.class);
//                    if (loginBean != null) {
//                        SaveInfo.loginBean = loginBean;
//                    }
                } else {
                    if (code == 424) {
                        Toast.makeText(getContext(), "账号或密码错误", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onResponseMsg(String message) {
                super.onResponseMsg(message);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoadingDialog();
            }
        });

    }

    //发送短信
    public void sendNewGoodsSms() {
        HelpStoreParamForPage bean = new HelpStoreParamForPage();
        bean.setPhone("15817367531");
        App.getService().getLoginService().getPhoneCode(bean, new DefaultObserver() {
            @Override
            public void onSuccess(int code, String message, JsonElement data, JsonObject all_data) {
                if (code == 210) {

                } else {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUnSuccessFinish() {
                super.onUnSuccessFinish();

            }

            @Override
            public void onResponseMsg(String message) {
                super.onResponseMsg(message);
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    public void wxLogin() {
        SocialCenter.getInstance().setSocialCenterCallback(new SocialCenterCallback() {
            @Override
            public void onWechatLoginSuccess(String code) {
                LogUtils.i("code" + code);
                weiXinLogin(code,"type");

            }

            @Override
            public void onWechatLoginFailed() {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
            }

        });
        SocialCenter.getInstance().wechatLogin();
    }

    /**
     * 微信登录
     */
    public void weiXinLogin(String code,String type) {
//        ActiveUser activeUser = new ActiveUser();
//        String userType;
//        try {
//            userType  = AesEncryptUtil.encrypt(type);
//            if(!StringUtil.isEmpty(userType) && userType.length() > 2){
//                String a = userType.substring(userType.length() - 1);
//                String b = userType.substring(userType.length() - 3);
//                if (a.equals("\n")){
//                    activeUser.setType(userType.substring(0,userType.length() - 1));
//                }else {
//                    activeUser.setType(userType);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        activeUser.setCode(code);
//        App.getService().getLoginService().userAuthLogin(activeUser, new DefaultObserver() {
//            @Override
//            public void onSuccess(int code, String message, JsonElement data, JsonObject all_data) {
//                if (code == 470) {
//                    //账号与微信未关联
//                    UserAuthLoginBean loginBean = JsonUtils.fromJson(data, UserAuthLoginBean.class);
//                    Intent intent = new Intent(LoginActivity.this, BringPhoneActivity.class);
//                    intent.putExtra("loginBean", (Serializable) loginBean);
//                    intent.putExtra("mType", mType);
//                    startActivity(intent);
//                } else if (code == 216) {
//
//
//                } else if(code == 469){
//                    Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
//                }else{
//
//                }
//            }
//        });
    }
}
