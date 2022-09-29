package com.jl.myapplication;

import androidx.fragment.app.Fragment;


import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.ActivityManager;
import com.jl.core.utils.FragmentUtils;
import com.jl.myapplication.databinding.ActivityMainBinding;
import com.jl.myapplication.home.fragment.MessageFragment;
import com.jl.myapplication.home.fragment.MeFragment;
import com.jl.myapplication.home.fragment.WaitFragment;
import com.jl.myapplication.home.fragment.HomeFragment;
import com.jl.myapplication.home.fragment.OrganizationFragment;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding mBinding;
    private HomeFragment mHomeFragment;
    private OrganizationFragment mOrganizationFragment;
    private MessageFragment mMessageFragment;
    private WaitFragment mWaitFragment;
    private MeFragment mMeFragment;
    private Fragment mCurFragment;
    private long mkeyTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        mBinding = getBindView();
        createFragment();
        mBinding.tabMe.setSelected(true);
        switchFragment(mMeFragment);

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
        mMessageFragment = new MessageFragment();
        mOrganizationFragment = new OrganizationFragment();
        mMessageFragment = new MessageFragment();
        mWaitFragment = new WaitFragment();
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
}