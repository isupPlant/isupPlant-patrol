package com.supcon.mes.module_xj_temp.controller;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.controller.BaseViewController;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.common.view.view.custom.ICustomView;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.view.CustomDateView;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.middleware.controller.MyPickerController;
import com.supcon.mes.middleware.model.listener.DateSelectListener;
import com.supcon.mes.patrol.R;

/**
 * Created by wangshizhan on 2019/11/14
 * Email:wangshizhan@supcom.com
 */
public class XJTempTimeController extends BaseViewController {

    String customDateStart = "", customDateEnd = "";

    DateSelectListener mDateSelectListener;

    CustomDialog mCustomDialog;
    CustomDateView customStartTimeView;
    CustomDateView customEndTimeView;

    private MyPickerController mDatePickController;

    public XJTempTimeController(View rootView) {
        super(rootView);
    }

    @Override
    public void initView() {
        super.initView();
        initDatePickController();
    }



    @Override
    public void initListener() {
        super.initListener();

    }

    private void initDatePickController() {
        mDatePickController = new MyPickerController((Activity) context);
        mDatePickController.textSize(18);
        mDatePickController.setCycleDisable(false);
        mDatePickController.setSecondVisible(false);
        mDatePickController.setCanceledOnTouchOutside(true);
        mDatePickController.setDateOnly(false);
    }


    public void showCustomDialog() {

        if(mCustomDialog == null) {

            mCustomDialog = new CustomDialog(context)
                    .layout(R.layout.v_dialog_custom_time)
                    .bindChildListener(R.id.customStartTimeView, new OnChildViewClickListener() {
                        @Override
                        public void onChildViewClick(View childView, int action, Object obj) {
                            if (action == -1) {
                                customDateStart = "";
                                ((ICustomView)childView).setContent("");
                            } else {
                                mDatePickController
                                        .listener((year, month, day, hour, minute, second) -> {

                                            String dateStr = year + "-" + month + "-" + day+" "+hour+":"+minute+":00" ;
                                            LogUtil.i(dateStr);

                                            if(!TextUtils.isEmpty(customDateEnd)){
                                                long startDate = DateUtil.dateFormat(customDateStart, "yyyy-MM-dd HH:mm:ss");
                                                long endDate = DateUtil.dateFormat(dateStr, "yyyy-MM-dd HH:mm:ss");

                                                if(startDate > endDate){
                                                    ToastUtils.show(context, context.getString(R.string.middleware_select_time_warning));
                                                    return;
                                                }
                                            }

                                            customDateStart = dateStr;

                                            customStartTimeView.setContent(customDateStart);
                                            customStartTimeView.invalidate();
                                        })
                                        .show(DateUtil.dateFormat(customDateStart, "yyyy-MM-dd HH:mm:ss"));
                            }
                        }
                    })
                    .bindChildListener(R.id.customEndTimeView, new OnChildViewClickListener() {
                        @Override
                        public void onChildViewClick(View childView, int action, Object obj) {
                            if (action == -1) {
                                customDateEnd = "";
                                ((ICustomView)childView).setContent("");
                            } else {
                                mDatePickController
                                        .listener((year, month, day, hour, minute, second) -> {

                                            String dateStr = year + "-" + month + "-" + day+" "+hour+":"+minute+":00" ; ;
                                            LogUtil.i(dateStr);

                                            if(!TextUtils.isEmpty(customDateStart)){
                                                long startDate = DateUtil.dateFormat(customDateStart, "yyyy-MM-dd HH:mm:ss");
                                                long endDate = DateUtil.dateFormat(dateStr, "yyyy-MM-dd HH:mm:ss");

                                                if(startDate > endDate){
                                                    ToastUtils.show(context, context.getString(R.string.middleware_select_time_warning));
                                                    return;
                                                }
                                            }


                                            customDateEnd = dateStr;

                                            customEndTimeView.setContent(customDateEnd);
                                            customEndTimeView.invalidate();
                                        })
                                        .show(DateUtil.dateFormat(customDateEnd, "yyyy-MM-dd HH:mm:ss"));
                            }
                        }
                    })
                    .bindClickListener(R.id.customTimeViewOkBtn, v -> {

                        if (TextUtils.isEmpty(customDateStart)) {
                            ToastUtils.show(context,context.getResources().getString(R.string.xj_patrol_selete_start_time));
                            return;
                        }

                        if (TextUtils.isEmpty(customDateEnd)) {
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_selete_end_time));
                            return;
                        }

                        if (mDateSelectListener != null) {
                            mDateSelectListener.onDateSelect(customDateStart, customDateEnd);
                        }

                        mCustomDialog.dismiss();

                    }, false)
                    .bindClickListener(R.id.customTimeViewCancelBtn, v -> {

                        customDateStart = "";
                        customDateEnd = "";
                        ((ICustomView)mCustomDialog.getDialog().findViewById(R.id.customEndTimeView)).setContent("");
                        ((ICustomView)mCustomDialog.getDialog().findViewById(R.id.customStartTimeView)).setContent("");
                        if (mDateSelectListener != null) {
                            mDateSelectListener.onDateSelect(customDateStart, customDateEnd);
                        }

                    }, true);

            customStartTimeView = mCustomDialog.getDialog().findViewById(R.id.customStartTimeView);
            customEndTimeView = mCustomDialog.getDialog().findViewById(R.id.customEndTimeView);

        }

        mCustomDialog.show();


    }

    public void setDateSelectListener(DateSelectListener dateSelectListener) {
        mDateSelectListener = dateSelectListener;
    }

}
