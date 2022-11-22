package com.jl.myapplication.jl_message.activity;


import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.jl.myapplication.R;


public class SendFileActivity extends FragmentActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

    }

    public FragmentManager getSupportFragmentManger() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager();
    }


}
