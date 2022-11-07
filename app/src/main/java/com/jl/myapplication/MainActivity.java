package com.jl.myapplication;

import static com.jl.myapplication.App.REQUEST_CODE_SCAN;

import androidx.fragment.app.Fragment;


import android.Manifest;
import android.content.Intent;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.ActivityManager;
import com.jl.core.utils.FragmentUtils;
import com.jl.myapplication.databinding.ActivityMainBinding;
import com.jl.myapplication.jl_message.fragment.MessageFragment;
import com.jl.myapplication.jl_home.fragment.MeFragment;
import com.jl.myapplication.jl_home.fragment.ShoppingCarFragment;
import com.jl.myapplication.jl_home.fragment.HomeFragment;
import com.jl.myapplication.jl_home.fragment.OrganizationFragment;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding mBinding;
    private HomeFragment mHomeFragment;
    private OrganizationFragment mOrganizationFragment;
    private MessageFragment mMessageFragment;
    private ShoppingCarFragment mWaitFragment;
    private MeFragment mMeFragment;
    private Fragment mCurFragment;
    private long mkeyTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        createFragment();
        mBinding.tabHome.setSelected(true);
        switchFragment(mHomeFragment);
        requestPermission();
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.tabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment(view);
                switchFragment(mMessageFragment);
            }
        });

        mBinding.tabOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment(view);
                switchFragment(mOrganizationFragment);
            }
        });

        mBinding.tabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment(view);
                switchFragment(mHomeFragment);
            }
        });

        mBinding.tabWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment(view);
                switchFragment(mWaitFragment);
            }
        });

        mBinding.tabMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment(view);
                switchFragment(mMeFragment);
            }
        });
    }

    private void createFragment() {
        mHomeFragment = new HomeFragment();
        mMessageFragment = new MessageFragment();
        mOrganizationFragment = new OrganizationFragment();
        mMessageFragment = new MessageFragment();
        mWaitFragment = new ShoppingCarFragment();
        mMeFragment = new MeFragment();
    }

    public void setFragmentPosition(int position) {
        if (position == 0) {
            closeFragment(mBinding.tabHome);
            switchFragment(mHomeFragment);
        } else if (position == 1) {
            closeFragment(mBinding.tabOrganization);
            switchFragment(mOrganizationFragment);
        } else if (position == 2) {
            closeFragment(mBinding.tabWait);
            switchFragment(mMessageFragment);
        } else if (position == 3) {
            closeFragment(mBinding.tabMessage);
            switchFragment(mWaitFragment);
        } else if (position == 4) {
            closeFragment(mBinding.tabMe);
            switchFragment(mMeFragment);
        }
    }

    public void closeFragment (View view){
        mBinding.tabHome.setSelected(false);
        mBinding.tabOrganization.setSelected(false);
        mBinding.tabWait.setSelected(false);
        mBinding.tabMessage.setSelected(false);
        mBinding.tabMe.setSelected(false);
    }
    /**
     * 选择Fragment
     *
     * @param fragment
     */
    public void switchFragment (Fragment fragment){
        mCurFragment = FragmentUtils.selectFragment(this, mCurFragment, fragment, R.id.layout_content);
    }

    public boolean onKeyDown ( int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                mkeyTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                ActivityManager.getInstance().finishAll();
                Process.killProcess(Process.myPid());
            }
        }
        return false;
    }

    private void requestPermission() {
        if (AndPermission.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO)) {
            return;
        }else {
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE,Permission.CAMERA,Permission.RECORD_AUDIO)
                    .onGranted(permissions -> {

                    })
                    .onDenied(permissions -> {
                        // Storage permission are not allowed.
                    })
                    .start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
//            if (data != null) {
//
//                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                result.setText("扫描结果为：" + content);
//            }
        }
    }
}