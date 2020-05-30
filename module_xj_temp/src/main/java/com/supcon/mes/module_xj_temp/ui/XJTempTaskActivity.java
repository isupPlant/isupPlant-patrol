package com.supcon.mes.module_xj_temp.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.model.listener.DateSelectListener;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj_temp.R;
import com.supcon.mes.module_xj_temp.controller.XJTempTimeController;
import com.supcon.mes.module_xj_temp.ui.adapter.XJTempAreaAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/5/25
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_TEMP_TASK)
@Controller(XJTempTimeController.class)
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
    RecyclerView xjTempAreaList;

    private XJTempAreaAdapter mXJTempAreaAdapter;
    private XJTaskEntity mXJTaskEntity;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_temp_task;
    }

    @Override
    protected void onInit() {
        super.onInit();
        mXJTaskEntity = new XJTaskEntity();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSelect(SelectDataEvent event) {
        if("selectRoute".equals(event.getSelectTag())){
            mXJTaskEntity.workRoute = (XJRouteEntity) event.getEntity();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText("临时巡检");
        xjTempAreaList.setLayoutManager(new LinearLayoutManager(context));
        xjTempAreaList.addItemDecoration(new SpaceItemDecoration(1));
        mXJTempAreaAdapter = new XJTempAreaAdapter(context);
        xjTempAreaList.setAdapter(mXJTempAreaAdapter);
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

        RxView.clicks(xjTempTaskTimeSelect)
                .throttleFirst(200 , TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        getController(XJTempTimeController.class).showCustomDialog();
                    }
                });

        getController(XJTempTimeController.class).setDateSelectListener(new DateSelectListener() {
            @Override
            public void onDateSelect(String start, String end) {

                if(TextUtils.isEmpty(start)){
                    mXJTaskEntity.startTime = 0;
                }
                else{
                    mXJTaskEntity.startTime = DateUtil.dateFormat(start);
                }

                if(TextUtils.isEmpty(end)){
                    mXJTaskEntity.endTime = 0;
                }
                else{
                    mXJTaskEntity.endTime = DateUtil.dateFormat(end);
                }

                StringBuilder stringBuilder = new StringBuilder(start);
                stringBuilder.append("--");
                stringBuilder.append(end);
                xjTempTaskTimeSelect.setContent(stringBuilder.toString());


            }
        });

        RxView.clicks(xjTempTaskRouteSelect)
                .throttleFirst(200 , TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        getController(XJTempTimeController.class).showCustomDialog();
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
