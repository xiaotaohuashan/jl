package com.jl.core.utils;


import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.base.fragment.BaseFragment;


/**
 * fragment工具类
 * Created by lenovo on 2017/9/20.
 */

public class FragmentUtils {

    public static synchronized <T extends Fragment> T selectFragment(BaseActivity activity, Fragment currentFragment, Fragment fragment, @IdRes int rootId) {
        if (activity == null || fragment == null) {
            return null;
        }

        if (currentFragment != fragment) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

            if (currentFragment == null) {
                if (fragment.isAdded()) {
                    transaction.show(fragment).commit();
                } else {
                    transaction.add(rootId, fragment).show(fragment).commit();
                }
            } else if (fragment.isAdded()) {
                transaction.hide(currentFragment).show(fragment).commit();
            } else {
                transaction.hide(currentFragment).add(rootId, fragment).show(fragment).commit();
            }
        }
        return (T) fragment;
    }

    public static synchronized <T extends Fragment> T selectFragmentFragment(BaseFragment baseFrament, Fragment currentFragment, Fragment fragment, @IdRes int rootId) {
        if (baseFrament == null || fragment == null) {
            return null;
        }
        if (currentFragment != fragment) {
            FragmentTransaction transaction = baseFrament.getChildFragmentManager().beginTransaction();
            if (currentFragment == null) {
                if (fragment.isAdded()) {
                    transaction.show(fragment).commit();
                } else {
                    transaction.add(rootId, fragment).show(fragment).commit();
                }
            } else if (fragment.isAdded()) {
                transaction.hide(currentFragment).show(fragment).commit();
            } else {
                transaction.hide(currentFragment).add(rootId, fragment).show(fragment).commit();
            }
        }
        return (T) fragment;
    }
}
