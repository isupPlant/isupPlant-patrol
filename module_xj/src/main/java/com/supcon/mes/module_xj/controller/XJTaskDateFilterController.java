package com.supcon.mes.module_xj.controller;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.controller.BaseViewController;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.listener.DateSelectListener;
import com.supcon.mes.middleware.util.TimeUtil;

/**
 * Created by wangshizhan on 2019/11/14
 * Email:wangshizhan@supcom.com
 */
public class XJTaskDateFilterController extends BaseViewController {

    @BindByTag("dateFilterGroup")
    RadioGroup dateFilterGroup;

    @BindByTag("dateFilterAll")
    RadioButton dateFilterAll;

    @BindByTag("dateFilterToday")
    RadioButton dateFilterToday;

    @BindByTag("dateFilterThisWeek")
    RadioButton dateFilterThisWeek;

    @BindByTag("dateFilterThisMonth")
    RadioButton dateFilterThisMonth;


    DateSelectListener mDateSelectListener;


    public XJTaskDateFilterController(View rootView) {
        super(rootView);
    }

    @Override
    public void initView() {
        super.initView();
    }



    @Override
    public void initListener() {
        super.initListener();


        if(dateFilterAll!=null)
            dateFilterAll.setOnClickListener(v -> getDates(Constant.Date.ALL));

        if(dateFilterToday!=null)
            dateFilterToday.setOnClickListener(v -> getDates(Constant.Date.TODAY));

        if(dateFilterThisWeek!=null)
            dateFilterThisWeek.setOnClickListener(v -> getDates(Constant.Date.THIS_WEEK));

        if(dateFilterThisMonth!=null)
            dateFilterThisMonth.setOnClickListener(v -> getDates(Constant.Date.THIS_MONTH));



    }


    private void getDates(String timePeriod){
        String[] dates= TimeUtil.getTimePeriod(timePeriod);

        String start = dates[0];
        String end = dates[1];

        if(mDateSelectListener!=null){
            mDateSelectListener.onDateSelect(start, end);
        }
    }



    public void setDateSelectListener(DateSelectListener dateSelectListener) {
        mDateSelectListener = dateSelectListener;
    }



}
