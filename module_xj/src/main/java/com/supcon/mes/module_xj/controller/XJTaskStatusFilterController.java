package com.supcon.mes.module_xj.controller;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.controller.BaseViewController;
import com.supcon.mes.middleware.model.listener.OnSuccessListener;

/**
 * Created by wangshizhan on 2020/4/8
 * Email:wangshizhan@supcom.com
 */
public class XJTaskStatusFilterController extends BaseViewController {

    @BindByTag("dateFilterGroup")
    RadioGroup dateFilterGroup;

    @BindByTag("dateFilterXJUncheck")
    RadioButton dateFilterXJUncheck;

    @BindByTag("dateFilterXJchecked")
    RadioButton dateFilterXJchecked;

    @BindByTag("dateFilterXJAll")
    RadioButton dateFilterXJAll;


    private OnSuccessListener<Integer> mSuccessListener;

    public XJTaskStatusFilterController(View rootView) {
        super(rootView);
    }

    @Override
    public void initListener() {
        super.initListener();

        dateFilterXJAll.setOnClickListener(v -> checkData(0));
        dateFilterXJUncheck.setOnClickListener(v -> checkData(1));

        dateFilterXJchecked.setOnClickListener(v -> checkData(2));



    }


    private void checkData(int position){

        if(mSuccessListener!=null){
            mSuccessListener.onSuccess(position);
        }

    }


    public void setOnSuccessListener(OnSuccessListener<Integer> listener){
        this.mSuccessListener = listener;
    }

}
