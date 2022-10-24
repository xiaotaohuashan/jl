package com.jl.myapplication.jl_home.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.utils.DataCleanManager;
import com.jl.core.view.dialog.TwoButtonMsgDialogUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentMeBinding;
import com.jl.myapplication.jl_home.activity.MyCardActivity;
import com.jl.myapplication.jl_me.activity.AboutUseActivity;
import com.jl.myapplication.jl_me.activity.SettingActivity;
import com.jl.myapplication.jl_login.LoginActivity;

public class MeFragment extends BaseFragment {
    private FragmentMeBinding mBinding;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void setListener() {
        mBinding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwoButtonMsgDialogUtil.showDialog(getActivity(), "提示", "是否退出登录", "取消", "是", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TwoButtonMsgDialogUtil.dismiss();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
            }
        });
        mBinding.rlSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
        mBinding.clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCleanDialog();
            }
        });
        mBinding.tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AboutUseActivity.class);
                startActivity(intent);
            }
        });
        mBinding.rlMyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyCardActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initView() {
        mBinding = getBindView();

    }

    //清理缓存
    private void showCleanDialog() {
        final AlertDialog.Builder cleanDialog =
                new AlertDialog.Builder(getContext());
        cleanDialog.setTitle("清除缓存提醒");
        cleanDialog.setMessage("是否确认清除缓存?");
        cleanDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataCleanManager.clearAllCache(getContext());
                        mBinding.tvClearResult.setText("清除成功");
                    }
                });
        cleanDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        AlertDialog dialog = cleanDialog.create();
        dialog.show();
        Button btnPos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button btnNeg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnPos.setTextColor(Color.RED);
        btnNeg.setTextColor(Color.RED);

    }
}
