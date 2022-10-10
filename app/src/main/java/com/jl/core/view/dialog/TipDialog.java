package com.jl.core.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.jl.core.utils.ScreenUtil;
import com.jl.myapplication.R;


/**
 * Created by chenhe_han on 2018/5/18.
 * 公司：
 * 描述：
 */

public class TipDialog extends BaseDialog {
    private static TipDialog mTipDialog;
    //标题
    private TextView mTipTitleText;
    //内容
    private TextView mTipContentText;
    private TextView mCancelBtn;
    private TextView mConfirmBtn;

    private IOnClickListener mClickListener;

    private String mTitle;
    private String mContent;
    private String mCancelString;
    private String mConfirmString;


    public static TipDialog getInstance(FragmentManager manager) {
        mTipDialog = new TipDialog();
        mTipDialog.show(manager, TipDialog.class.getCanonicalName());
        return mTipDialog;
    }

    @Override
    protected void initArguments() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_tip_layout;
    }

    @Override
    protected void initData() {
        mTipTitleText = mRootView.findViewById(R.id.tip_title_text);
        mTipContentText = mRootView.findViewById(R.id.tip_content_text);
        mCancelBtn = mRootView.findViewById(R.id.tip_cancel_button);
        mConfirmBtn = mRootView.findViewById(R.id.tip_confirm_button);
        if (!TextUtils.isEmpty(mTitle)) {
            mTipTitleText.setText(mTitle);
        }
        if (!TextUtils.isEmpty(mContent)) {
            mTipContentText.setText(mContent);
        }
        if (!TextUtils.isEmpty(mCancelString)) {
            mCancelBtn.setText(mCancelString);
        }
        if (!TextUtils.isEmpty(mConfirmString)) {
            mConfirmBtn.setText(mConfirmString);
        }
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.clickCancel(mTipDialog);
                } else {
                    dismiss();
                }
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.clickConfirm(mTipDialog);
                } else {
                    dismiss();
                }
            }
        });
    }

    public TipDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public TipDialog setContent(String content) {
        mContent = content;
        return this;
    }

    public TipDialog setCancelText(String cancelString) {
        mCancelString = cancelString;
        return this;
    }

    public TipDialog setConfirmText(String confirmString) {
        mConfirmString = confirmString;
        return this;
    }

    public TipDialog setOnClickListener(IOnClickListener listener) {
        this.mClickListener = listener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        attributes.verticalMargin = 0.25f;
        attributes.width = (int) (ScreenUtil.screenWidth() * 0.8f);
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public interface IOnClickListener {
        void clickCancel(TipDialog dialog);

        void clickConfirm(TipDialog dialog);
    }
}
