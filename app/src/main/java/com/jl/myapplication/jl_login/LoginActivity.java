package com.jl.myapplication.jl_login;

import static com.jl.myapplication.App.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.MainActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityLoginBinding;
import com.jl.myapplication.jl_login.activity.OwnerResetPasswordActivity;
import com.jl.myapplication.jl_me.activity.AboutUseActivity;
import com.jl.myapplication.model.HelpStoreParamForPage;
import com.jl.myapplication.model.LoginBean;
import com.jl.myapplication.model.PasswordBean;


public class LoginActivity extends BaseActivity {
    private CountDownTimer countDownTimer;
    private ActivityLoginBinding mBinding;
    private boolean isVerifyLogin = true;
    private boolean dataCheckFlag = true;
    private static final long COUNT_DOWN_PERIOD = 60 * 1000L;
    private boolean countFlag = false;
    private static final int VERIFY_ENABLE = 0;
    private static final int VERIFY_DISABLE = 1;
    private boolean isPasswordVisible = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        Intent it = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(it);
        finish();
        countDownTimer = new CountDownTimer(COUNT_DOWN_PERIOD + 600, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.loginGetVerifyCodeTv.setText(String.format(getString(R.string.verify_count_down_text), millisUntilFinished / 1000));
                getVerifyCodeStatus(VERIFY_DISABLE);
                countFlag = true;
            }

            @Override
            public void onFinish() {
                mBinding.loginGetVerifyCodeTv.setText("获取验证码");
                getVerifyCodeStatus(VERIFY_ENABLE);
                countFlag = false;
            }
        };
    }

    private void getVerifyCodeStatus(int state) {
        if (state == VERIFY_ENABLE) {
            mBinding.loginGetVerifyCodeTv.setTextColor(getResources().getColor(R.color.login_verify_enable));
            mBinding.loginGetVerifyCodeTv.setBackground(getResources().getDrawable(R.drawable.button_edge));
            mBinding.loginGetVerifyCodeTv.setEnabled(true);
        } else {
            mBinding.loginGetVerifyCodeTv.setTextColor(getResources().getColor(R.color.login_verify_disable));
            mBinding.loginGetVerifyCodeTv.setBackground(getResources().getDrawable(R.drawable.foreman_change_uncheck));
            mBinding.loginGetVerifyCodeTv.setEnabled(false);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
        mBinding.loginProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, AboutUseActivity.class);
                startActivity(intent);
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
        mBinding.loginPhonePasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!isVerifyLogin) {
//                    mBinding.loginPhoneVerifyTv.setText("密码登录");
                mBinding.loginLayoutVerifyCode.setVisibility(View.GONE);
                mBinding.loginLayoutPsw.setVisibility(View.VISIBLE);
                mBinding.loginBtn.setText("登录");
                isVerifyLogin = false;
                dataCheckFlag = false;
//                } else {
//                    mBinding.loginPhoneVerifyTv.setText("验证码登录");
//                    mBinding.loginLayoutVerifyCode.setVisibility(View.GONE);
//                    mBinding.loginLayoutPsw.setVisibility(View.VISIBLE);
//                    mBinding.loginBtn.setText("登录");
//                    dataCheckFlag = false;
//                }
//                isVerifyLogin = !isVerifyLogin;
                passwordLoginButtonisGreen();
//                verifyCodeLoginButtonisGreen();
            }
        });
        mBinding.loginPhoneVerifyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!isVerifyLogin) {
//                    mBinding.loginPhoneVerifyTv.setText("密码登录");
//                    mBinding.loginLayoutPsw.setVisibility(View.GONE);
//                    mBinding.loginLayoutVerifyCode.setVisibility(View.VISIBLE);
//                    mBinding.loginBtn.setText("登录/注册");
//                    dataCheckFlag = true;
//                    passwordLoginButtonisGreen();
//                } else {
//                    mBinding.loginPhoneVerifyTv.setText("验证码登录");


                mBinding.loginLayoutPsw.setVisibility(View.GONE);
                mBinding.loginLayoutVerifyCode.setVisibility(View.VISIBLE);
                mBinding.loginBtn.setText("登录/注册");
                isVerifyLogin = true;
                dataCheckFlag = true;
//                passwordLoginButtonisGreen();
//                }
//                isVerifyLogin = !isVerifyLogin;
                verifyCodeLoginButtonisGreen();
            }
        });
        mBinding.loginGetVerifyCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchVerify();
            }
        });

        LoginWatcher watcher = new LoginWatcher();
        mBinding.loginPhoneEt.addTextChangedListener(watcher);
        mBinding.loginPswEt.addTextChangedListener(watcher);
        mBinding.loginVerifyCodeEt.addTextChangedListener(watcher);

        mBinding.loginBtn.setEnabled(false);
        mBinding.loginBtn.setClickable(false);
        mBinding.loginPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (mBinding.loginPhoneEt.getText().toString().length()==11 && isVerifyLogin && mBinding.loginVerifyCodeEt.getText().toString().length() > 0 ||
                        mBinding.loginPhoneEt.getText().toString().length()==11 && !isVerifyLogin && mBinding.loginPswEt.getText().toString().length() > 0){
                    mBinding.loginBtn.setBackgroundResource(R.drawable.login_green_shape);
                    mBinding.loginBtn.setEnabled(true);
                    mBinding.loginBtn.setClickable(true);
                }else {
                    mBinding.loginBtn.setBackgroundResource(R.drawable.login_grey_shape);
                    mBinding.loginBtn.setEnabled(false);
                    mBinding.loginBtn.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.loginPswEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                passwordLoginButtonisGreen();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.loginVerifyCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                verifyCodeLoginButtonisGreen();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.loginPhoneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    mBinding.loginPhoneIv.setImageDrawable(getResources().getDrawable(R.mipmap.eyes_open));
                    isPasswordVisible = false;
                    mBinding.loginPswEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mBinding.loginPhoneIv.setImageDrawable(getResources().getDrawable(R.mipmap.eyes_close));
                    isPasswordVisible = true;
                    mBinding.loginPswEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });
        mBinding.loginForgetPswTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToActivity(OwnerResetPasswordActivity.class);
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

    // 密码登录时登录按钮是否可点击
    private void passwordLoginButtonisGreen(){
        if (mBinding.loginPhoneEt.getText().toString().length()==11 && !isVerifyLogin && mBinding.loginPswEt.getText().toString().length() > 0){
            mBinding.loginBtn.setBackgroundResource(R.drawable.login_green_shape);
            mBinding.loginBtn.setEnabled(true);
            mBinding.loginBtn.setClickable(true);
        }else {
            mBinding.loginBtn.setBackgroundResource(R.drawable.login_grey_shape);
            mBinding.loginBtn.setEnabled(false);
            mBinding.loginBtn.setClickable(false);
        }
    }

    // 验证码登录时登录按钮是否可点击
    private void verifyCodeLoginButtonisGreen(){
        if (mBinding.loginPhoneEt.getText().toString().length()==11 && isVerifyLogin && mBinding.loginVerifyCodeEt.getText().toString().length() > 0){
            mBinding.loginBtn.setBackgroundResource(R.drawable.login_green_shape);
            mBinding.loginBtn.setEnabled(true);
            mBinding.loginBtn.setClickable(true);
        }else {
            mBinding.loginBtn.setBackgroundResource(R.drawable.login_grey_shape);
            mBinding.loginBtn.setEnabled(false);
            mBinding.loginBtn.setClickable(false);
        }
    }

    private void doLogin() {
        String phoneNumber = mBinding.loginPhoneEt.getText().toString();
        if (!StringUtil.checkPhoneNumber(phoneNumber)) {
            ToastUtils.show(this, "请输入正确的手机号码");
            return;
        }

        if (dataCheckFlag) {
            String verifyCode = mBinding.loginVerifyCodeEt.getText().toString();
            if (verifyCode.length() != 6) {
                ToastUtils.show(this, "请输入6位验证码");
                return;
            }
            Intent it = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(it);
            finish();
        } else {
            String password = mBinding.loginPswEt.getText().toString();
            if (password.length() < 8 || password.length() > 16) {
                ToastUtils.show(this, "请输入8-16位密码");
                return;
            }
            SettingsUtil.setTrueName(phoneNumber);
            SettingsUtil.setPassword(password);
            Intent it = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(it);
            finish();
        }

    }

    private void fetchVerify() {
        ToastUtils.show("验证码发送成功");
        countDownTimer.start();
    }

    private class LoginWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StringUtil.checkPhoneNumber(mBinding.loginPhoneEt.getText().toString()) && !countFlag) {
                getVerifyCodeStatus(VERIFY_ENABLE);
            } else {
                getVerifyCodeStatus(VERIFY_DISABLE);
            }
        }
    }

}
