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
import com.jl.myapplication.home.fragment.CommodityFragment;
import com.jl.myapplication.home.fragment.CustomerFragment;
import com.jl.myapplication.home.fragment.DataFragment;
import com.jl.myapplication.home.fragment.NotificationFragment;
import com.jl.myapplication.home.fragment.TaskFragment;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private NotificationFragment notificationFragment;
    private TaskFragment taskFragment;
    private CommodityFragment shoppingInfoFragment;
    private DataFragment mDataFragment;
    private CustomerFragment memberFragment;
    private Fragment mCurFragment;
    private long mkeyTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        binding = getBindView();
        createFragment();
        binding.tabMember.setSelected(true);
        switchFragment(memberFragment);

    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tab_notification:
//                closeFragment(v);
//                switchFragment(notificationFragment);
//                break;
//            case R.id.tab_task:
//                closeFragment(v);
//                switchFragment(taskFragment);
//                break;
//            case R.id.tab_shopping:
//                closeFragment(v);
//                switchFragment(shoppingInfoFragment);
//                break;
//            case R.id.tab_message:
//                closeFragment(v);
//                switchFragment(mDataFragment);
//                break;
//            case R.id.tab_member:
//                closeFragment(v);
//                switchFragment(memberFragment);
//                break;
//        }
//    }

    public void setFragmentPosition(int position){
        if (position == 0){
            closeFragment(binding.tabNotification);
            switchFragment(notificationFragment);
        }else if (position == 1){
            closeFragment(binding.tabTask);
            switchFragment(taskFragment);
        }else if (position == 2){
            closeFragment(binding.tabShopping);
            switchFragment(shoppingInfoFragment);
        }else if (position == 3){
            closeFragment(binding.tabMessage);
            switchFragment(mDataFragment);
        }else if (position == 4){
            closeFragment(binding.tabMember);
            switchFragment(memberFragment);
        }
    }

    private void createFragment() {
        notificationFragment = new NotificationFragment();
        taskFragment = new TaskFragment();
        shoppingInfoFragment = new CommodityFragment();
        mDataFragment = new DataFragment();
        memberFragment = new CustomerFragment();
    }

    public void closeFragment(View view) {
        binding.tabNotification.setSelected(false);
        binding.tabTask.setSelected(false);
        binding.tabShopping.setSelected(false);
        binding.tabMessage.setSelected(false);
        binding.tabMember.setSelected(false);
        view.setSelected(true);
//        ActAnimationUtils.ImgViewAlphaSize(view,5);
    }

    /**
     * 选择Fragment
     *
     * @param fragment
     */
    public void switchFragment(Fragment fragment) {
        mCurFragment = FragmentUtils.selectFragment(this, mCurFragment, fragment, R.id.layout_content);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
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