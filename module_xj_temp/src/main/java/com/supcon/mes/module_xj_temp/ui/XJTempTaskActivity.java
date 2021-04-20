package com.supcon.mes.module_xj_temp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.constant.ViewAction;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskRouteEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntityDao;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.model.listener.DateSelectListener;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.middleware.util.XJTaskCacheUtil;
import com.supcon.mes.module_xj.model.event.XJTempTaskAddEvent;
import com.supcon.mes.module_xj_temp.IntentRouter;
import com.supcon.mes.patrol.R;
import com.supcon.mes.module_xj_temp.controller.XJTempTimeController;
import com.supcon.mes.module_xj_temp.ui.adapter.XJTempAreaAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/5/25
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.AppCode.MPS_TempPatrol)
@Controller(XJTempTimeController.class)
public class XJTempTaskActivity extends BaseControllerActivity {

    @BindByTag("leftBtn")
    CustomImageButton leftBtn;

    @BindByTag("rightBtn")
    CustomImageButton rightBtn;

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
    private long eamId;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_temp_task;
    }

    @Override
    protected void onInit() {
        super.onInit();
        eamId = getIntent().getLongExtra(Constant.IntentKey.SBDA_ONLINE_EAMID, -1);
        mXJTaskEntity = new XJTaskEntity();
        mXJTaskEntity.areas = new ArrayList<>();
        mXJTaskEntity.isTemp = true;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSelect(SelectDataEvent event) {
        if ("selectRoute".equals(event.getSelectTag())) {
            String s= GsonUtil.gsonString(event.getEntity());
            XJTaskRouteEntity xjTaskRouteEntity=GsonUtil.gsonToBean(s,XJTaskRouteEntity.class);
            mXJTaskEntity.workRoute = (xjTaskRouteEntity) ;
            mXJTaskEntity.patrolType = SystemCodeManager.getInstance().getSystemCodeEntity(mXJTaskEntity.workRoute.patrolType.id);
//            mXJTaskEntity.workRoute.name = mXJTaskEntity.workRoute.name+"Temp";
       //     mXJTaskEntity.tableNo = getResources().getString(R.string.xj_temp_task);
            mXJTaskEntity.tableNo = "Temp"+new Date().getTime();
            mXJTaskEntity.staffName = SupPlantApplication.getAccountInfo().staffName;

            xjTempTaskRouteSelect.setContent(mXJTaskEntity.workRoute.name + getResources().getString(R.string.xj_temp_task));

            List<XJAreaEntity> xjTaskAreaEntities = SupPlantApplication.dao().getXJAreaEntityDao().queryBuilder()
                    .where(XJAreaEntityDao.Properties.WorkRouteId.eq(mXJTaskEntity.workRoute.id))
                    .where(XJAreaEntityDao.Properties.Valid.eq(true))
                    .where(XJAreaEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                    .orderAsc(XJAreaEntityDao.Properties.Sort)
                    .list();


            String areaString=GsonUtil.gsonString(xjTaskAreaEntities);
            List<XJTaskAreaEntity> areaEntities=GsonUtil.jsonToList(areaString,XJTaskAreaEntity.class);

            //过滤没有巡检项的巡检区域，不显示
            List<XJTaskAreaEntity> areas=new ArrayList<>();
            for (int i = 0; i <areaEntities.size(); i++) {
                List<XJWorkEntity> xjWorkEntities = SupPlantApplication.dao().getXJWorkEntityDao().queryBuilder()
                        .where(XJWorkEntityDao.Properties.AreaLongId.eq(areaEntities.get(i).id))
                        .where(XJWorkEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                        .orderAsc(XJWorkEntityDao.Properties.Sort)
                        .list();
                if (xjWorkEntities != null && xjWorkEntities.size() >0) {
                    areas.add(areaEntities.get(i));
                }
            }
            areaEntities=areas;

            List<XJTaskAreaEntity> xjAreaData = null;
            //过滤设备相关巡检区域
            if (eamId != -1) {
                xjAreaData = getDeviceXJData(areaEntities);
            } else {
                xjAreaData = areaEntities;
            }
            mXJTaskEntity.areas.clear();
            mXJTaskEntity.areas.addAll(xjAreaData);
            mXJTempAreaAdapter.setList(mXJTaskEntity.areas);
            mXJTempAreaAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取设备巡检项，巡检区域
     *
     * @param areaEntities
     * @return
     */
    @SuppressLint("CheckResult")
    private List<XJTaskAreaEntity> getDeviceXJData(List<XJTaskAreaEntity> areaEntities) {
        List<XJTaskAreaEntity> deviceAreaEntity = new ArrayList<>();
        for (int i = 0; i < areaEntities.size(); i++) {
            List<XJWorkEntity> deviceWork = new ArrayList<>();
            List<XJWorkEntity> works = SupPlantApplication.dao().getXJWorkEntityDao().queryBuilder()
                    .where(XJWorkEntityDao.Properties.AreaLongId.eq(areaEntities.get(i).id))
                    .where(XJWorkEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                    .orderAsc(XJWorkEntityDao.Properties.Sort)
                    .list();
            for (int j = 0; j < works.size(); j++) {
                if (works.get(j).eamId != null && works.get(j).eamId.id != null && works.get(j).eamId.id == eamId) {
                    deviceWork.add(works.get(j));
                }
            }
            if (deviceWork.size() > 0) {
                deviceAreaEntity.add(areaEntities.get(i));
            }
        }
        return deviceAreaEntity;
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText(context.getResources().getString(R.string.xj_patrol_new_xj));
        rightBtn.setImageResource(R.drawable.sl_top_submit);
        rightBtn.setVisibility(View.VISIBLE);

        xjTempAreaList.setLayoutManager(new LinearLayoutManager(context));
        xjTempAreaList.addItemDecoration(new SpaceItemDecoration(1));
        mXJTempAreaAdapter = new XJTempAreaAdapter(context, mXJTaskEntity.areas);
        xjTempAreaList.setAdapter(mXJTempAreaAdapter);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        RxView.clicks(leftBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        onBackPressed();
                    }
                });

        RxView.clicks(rightBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        if (mXJTaskEntity.workRoute == null) {
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_selete_router));
                            return;
                        }

                        if (mXJTaskEntity.startTime == 0 || mXJTaskEntity.endTime == 0) {
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_selete_start_end_time));
                            return;
                        }
                        boolean isHaveAreas = false;
                        for (XJTaskAreaEntity xjAreaEntity : mXJTaskEntity.areas) {
                            if (xjAreaEntity.isChecked) {
                                isHaveAreas = true;
                            }
                        }
                        if (mXJTaskEntity.areas == null || mXJTaskEntity.areas.size() == 0 || !isHaveAreas) {
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_xj_area));
                            return;
                        }

                        for (int i = mXJTaskEntity.areas.size() - 1; i >= 0; i--) {

                            XJTaskAreaEntity xjAreaEntity = mXJTaskEntity.areas.get(i);
                            if (!xjAreaEntity.isChecked) {
                                mXJTaskEntity.areas.remove(i);
                            }

                        }
                        new CustomDialog(context)
                                .twoButtonAlertDialog(getString(R.string.xj_temp_task_sure_warning))
                                .bindView(R.id.grayBtn, context.getResources().getString(R.string.xj_patrol_cancel))
                                .bindView(R.id.redBtn, context.getResources().getString(R.string.xj_patrol_sure))
                                .bindClickListener(R.id.grayBtn, v -> {}, true)
                                .bindClickListener(R.id.redBtn, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                XJTaskCacheUtil.putString( mXJTaskEntity.toString());
                                                EventBus.getDefault().post(new XJTempTaskAddEvent(mXJTaskEntity));
                                                back();
                                            }
                                        }
                                        , true)
                                .show();
                    }
                });

        RxView.clicks(xjTempTaskTimeSelect)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        getController(XJTempTimeController.class).showCustomDialog();
                    }
                });

        xjTempTaskTimeSelect.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == ViewAction.CONTENT_CLEAN.value()) {
                    mXJTaskEntity.startTime = 0;
                    mXJTaskEntity.endTime = 0;
                } else {
                    getController(XJTempTimeController.class).showCustomDialog();
                }
            }
        });

        xjTempTaskRouteSelect.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == ViewAction.CONTENT_CLEAN.value()) {
                    mXJTaskEntity.workRoute = null;
                    mXJTaskEntity.patrolType = null;
                    mXJTaskEntity.tableNo = null;
                    mXJTaskEntity.areas.clear();
                    mXJTempAreaAdapter.notifyDataSetChanged();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constant.IntentKey.SBDA_ONLINE_EAMID, eamId);
                    IntentRouter.go(context, Constant.Router.XJ_ROUTE_LIST, bundle);
                }
            }
        });

        getController(XJTempTimeController.class).setDateSelectListener(new DateSelectListener() {
            @Override
            public void onDateSelect(String start, String end) {

                if (TextUtils.isEmpty(start)) {
                    mXJTaskEntity.startTime = 0;
                } else {
                    mXJTaskEntity.startTime = DateUtil.dateFormat(start, "yyyy-MM-dd HH:mm:ss");
                }

                if (TextUtils.isEmpty(end)) {
                    mXJTaskEntity.endTime = 0;
                } else {
                    mXJTaskEntity.endTime = DateUtil.dateFormat(end, "yyyy-MM-dd HH:mm:ss");
                }


                if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
                    StringBuilder stringBuilder = new StringBuilder(start);
                    stringBuilder.append("--");
                    stringBuilder.append(end);
                    xjTempTaskTimeSelect.setContent(stringBuilder.toString());
                } else {
                    xjTempTaskTimeSelect.setContent("");
                }

            }
        });
//
//        mXJTempAreaAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
//            @Override
//            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
//
//            }
//        });
    }


    @Override
    public void onBackPressed() {

        new CustomDialog(context)
                .twoButtonAlertDialog(mXJTaskEntity.areas != null &&mXJTaskEntity.areas .size()>0? getString(R.string.xj_temp_task_exit_warning) : getString(R.string.xj_temp_task_exit_warning2))
                .bindView(R.id.grayBtn, context.getResources().getString(R.string.xj_patrol_stay))
                .bindView(R.id.redBtn, context.getResources().getString(R.string.xj_patrol_go))
                .bindClickListener(R.id.grayBtn, v -> {
                }, true)
                .bindClickListener(R.id.redBtn, v -> super.onBackPressed()
                        , true)
                .show();

    }
}
