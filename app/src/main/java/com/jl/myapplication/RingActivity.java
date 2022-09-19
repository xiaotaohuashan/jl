package com.jl.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jl.core.log.LogUtils;
import com.jl.core.ringview.RingBean;
import com.jl.core.ringview.RingView;

import java.util.ArrayList;
import java.util.List;

public class RingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        RingView ringView = findViewById(R.id.ringview);

        List<Integer> list = new ArrayList<>();
        list.add(R.color.teal_200);
        list.add(R.color.teal_700);
        list.add(R.color.purple_200);
        list.add(R.color.purple_500);
        list.add(R.color.purple_700);
        list.add(R.color.black);
        list.add(R.color.purple_200);
        list.add(R.color.purple_700);

        List<RingBean> rateList = new ArrayList<>();
        rateList.add(new RingBean("2022-08:",0.2f));
        rateList.add(new RingBean("2022-07:",7.3f));
        rateList.add(new RingBean("2022-06:",21.7f));
        rateList.add(new RingBean("2022-05:",11.7f));
        rateList.add(new RingBean("2022-04:",22.7f));
        rateList.add(new RingBean("2022-03:",15.9f));
        rateList.add(new RingBean("2022-02:",11.5f));
        rateList.add(new RingBean("2022-01:",9.0f));

        ringView.setShow(list,rateList,false,true);
        LogUtils.i(rateList.get(2).getName());
    }
}