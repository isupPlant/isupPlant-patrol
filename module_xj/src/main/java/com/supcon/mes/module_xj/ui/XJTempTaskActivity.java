package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.view.loader.base.OnLoaderFinishListener;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.util.XJCacheUtil;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/5/25
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_TEMP_TASK)
public class XJTempTaskActivity extends BaseControllerActivity {

    @BindByTag("leftBtn")
    CustomImageButton leftBtn;

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("xjTempTaskRouteSelect")
    CustomTextView xjTempTaskRouteSelect;

    @BindByTag("xjTempTaskTimeSelect")
    CustomTextView xjTempTaskTimeSelect;

    @BindByTag("xjTempAreaList")
    CustomTextView xjTempAreaList;

    private long deploymentId;
    private XJTaskEntity mXJTaskEntity;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_temp_task;
    }

    @Override
    protected void onInit() {
        super.onInit();
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText("临时巡检");

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        RxView.clicks(leftBtn)
                .throttleFirst(200 , TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        onBackPressed();
                    }
                });
    }


    @Override
    public void onBackPressed() {

        new CustomDialog(context)
                .twoButtonAlertDialog(mXJTaskEntity.areas!=null? getString(R.string.xj_temp_task_exit_warning) : getString(R.string.xj_temp_task_exit_warning2))
                .bindView(R.id.grayBtn,"留下")
                .bindView(R.id.redBtn,"离开")
                .bindClickListener(R.id.grayBtn, v -> {}, true)
                .bindClickListener(R.id.redBtn, v -> super.onBackPressed()
                , true)
                .show();

    }
}
