package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.App;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.expert_uhf.controller.ExpertUHFRFIDController;
import com.supcon.mes.expert_uhf.helper.InventoryBuffer;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntityDao;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.inter.IMap;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.util.SBTUtil;
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.middleware.util.XJTaskCacheUtil;
import com.supcon.mes.module_scan.controller.ScanDriverController;
import com.supcon.mes.module_scan.model.event.CodeResultEvent;
import com.supcon.mes.module_scan.util.scanCode.CodeUtlis;
import com.supcon.mes.module_xj.IntentRouter;
import com.supcon.mes.patrol.R;
import com.supcon.mes.module_xj.controller.XJLocalTaskController;
import com.supcon.mes.module_xj.model.api.XJUpdateStatusAPI;
import com.supcon.mes.module_xj.model.contract.XJTaskSubmitContract;
import com.supcon.mes.module_xj.model.contract.XJUpdateStatusContract;
import com.supcon.mes.module_xj.model.event.XJAreaRefreshEvent;
import com.supcon.mes.module_xj.model.event.XJTempTaskUploadRefreshEvent;
import com.supcon.mes.module_xj.presenter.XJTaskSubmitPresenter;
import com.supcon.mes.module_xj.presenter.XJUpdateTaskStatusPresenter;
import com.supcon.mes.module_xj.ui.adapter.XJAreaAdapter;
import com.supcon.mes.nfc.model.bean.NFCEntity;
import com.supcon.mes.sb2.model.event.BarcodeEvent;
import com.supcon.mes.sb2.model.event.SB2AttachEvent;
import com.supcon.mes.sb2.model.event.UhfRfidEvent;
import com.supcon.mes.sb2.util.EM55UHFRFIDHelper;
import com.supcon.mes.sb2.util.SoundHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by wangshizhan on 2020/4/13
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_TASK_DETAIL)
@Controller(value = {SystemCodeJsonController.class, XJLocalTaskController.class,
        ExpertUHFRFIDController.class})
@Presenter(value = {XJTaskSubmitPresenter.class, XJUpdateTaskStatusPresenter.class})
@SystemCode(entityCodes = {
        Constant.SystemCode.PATROL_signInType,
        Constant.SystemCode.PATROL_passReason
})
public class XJTaskDetailActivity extends BaseControllerActivity implements XJTaskSubmitContract.View
        , IMap,
        XJUpdateStatusContract.View {

    @BindByTag("xjTaskDetailRouteName")
    TextView xjTaskDetailRouteName;

    @BindByTag("xjTaskDetailTaskState")
    TextView xjTaskDetailTaskState;

    @BindByTag("xjTaskDetailTableNo")
    TextView xjTaskDetailTableNo;

    @BindByTag("xjTaskDetailStaff")
    TextView xjTaskDetailStaff;

    @BindByTag("xjTaskDetailDate")
    TextView xjTaskDetailDate;

    @BindByTag("xjTaskDetailTaskBtn")
    Button xjTaskDetailTaskBtn;

    @BindByTag("xjTaskDetailContentView")
    RecyclerView xjTaskDetailContentView;

    @BindByTag("xjTaskDetailParent")
    RelativeLayout xjTaskDetailParent;

    @BindByTag("xjTaskDetailCloseBtn")
    ImageView xjTaskDetailCloseBtn;

    @BindByTag("xjTaskDetailScanBtn")
    ImageView xjTaskDetailScanBtn;

    Map<String, String> signTypeInfoMap;     //????????????
    private XJTaskEntity mXJTaskEntity;
    private XJAreaAdapter mXJAreaAdapter;
    private ScanDriverController driverController;
    private EM55UHFRFIDHelper em55UHFRFIDHelper;
    private ExpertUHFRFIDController mExpertUHFRFIDController;
    private int enterPosition = -1;
    private SoundHelper mSoundHelper = new SoundHelper();
    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_task_detail;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
        Window win = this.getWindow();
        setFinishOnTouchOutside(false);
        win.setWindowAnimations(R.style.fadeStyle); //????????????????????????
        win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //???????????????????????????
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //???????????????
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });

//        String taskStr = getIntent().getStringExtra(Constant.IntentKey.XJ_TASK_ENTITY_STR);
//
//
//        if(!TextUtils.isEmpty(taskStr)){
//            mXJTaskEntity = GsonUtil.gsonToBean(taskStr, XJTaskEntity.class);
//        }

        String taskNo = getIntent().getStringExtra(Constant.IntentKey.XJ_TASK_NO_STR);
        String taskStr = getIntent().getStringExtra(Constant.IntentKey.XJ_TASK_ENTITY_STR);
        if (!TextUtils.isEmpty(taskNo)) {
            //??????????????????
            taskStr = XJTaskCacheUtil.getString(taskNo);
        }
//        String taskStr = BundleSaveUtil.instance.getValue(Constant.IntentKey.XJ_TASK_ENTITY_STR);
//
//        if (!TextUtils.isEmpty(taskStr)) {
//            mXJTaskEntity = GsonUtil.gsonToBean(taskStr, XJTaskEntity.class);
        mXJTaskEntity = GsonUtil.gsonToBean(taskStr, XJTaskEntity.class);
        if (SBTUtil.isSupportUHF() && SharedPreferencesUtils.getParam(context, Constant.SPKey.UHF_ENABLE, false)) {
            em55UHFRFIDHelper = EM55UHFRFIDHelper.getInstance();
            openDevice();
        }
    }

    @Override
    protected void initControllers() {
        super.initControllers();

        driverController = new ScanDriverController.Builder()
                .supportEAMModuleType()  //????????????
                .setSerise(false)
                .supportNFCDriver()
                .supportHwDriver()
//                .supportUHFDriver()
                .supportCameraDriver()
                .build(context);
        registerController(ScanDriverController.class.getSimpleName(), driverController);
        driverController.openScan();
        mExpertUHFRFIDController = getController(ExpertUHFRFIDController.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        driverController.onNewIntent(intent);
    }

    @SuppressLint("CheckResult")
    private void openDevice() {

        if (em55UHFRFIDHelper == null) {
            return;
        }
        em55UHFRFIDHelper.inventoryStop();
        em55UHFRFIDHelper.close();
        Flowable.just(true)
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                        return em55UHFRFIDHelper.open();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (!(App.getAppContext().store.lastElement() instanceof XJTaskDetailActivity)) {
                                return;
                            }
                            em55UHFRFIDHelper.inventoryStart();
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_uhf_succeed));
                        } else {
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_uhf_fail));
                        }
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (em55UHFRFIDHelper != null && !em55UHFRFIDHelper.isStart()) {
            em55UHFRFIDHelper.inventoryStart();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceAttached(SB2AttachEvent sb2AttachEvent) {

        if (em55UHFRFIDHelper != null) {
            em55UHFRFIDHelper.inventoryStop();
            em55UHFRFIDHelper.close();
        }

        if (sb2AttachEvent.isAttached()) {
            openDevice();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mExpertUHFRFIDController.start(new ExpertUHFRFIDController.UHFResult() {
            @Override
            public void result(Pair<String, InventoryBuffer.InventoryTagMap> result) {
                if (TextUtils.isEmpty(result.first)) {
                    char[] str = result.second.strEPC.replaceAll(" ", "").toCharArray();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (char c : str)
                        if (c >= '0' && c <= '9' && stringBuilder.length() < 8)
                            stringBuilder.append(c);
                    EventBus.getDefault().post(new UhfRfidEvent(stringBuilder.toString()));
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        mExpertUHFRFIDController.stop();
        if (em55UHFRFIDHelper != null && em55UHFRFIDHelper.isStart())
            em55UHFRFIDHelper.inventoryStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (em55UHFRFIDHelper != null) {
            em55UHFRFIDHelper.close();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        updateView();
    }

    @Override
    protected void initData() {
        super.initData();

        signTypeInfoMap = getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_signInType);
    }

    private void updateView() {
        if (mXJTaskEntity != null) {
            if (mXJTaskEntity.workRoute != null)
                xjTaskDetailRouteName.setText(mXJTaskEntity.workRoute.name);
            xjTaskDetailTableNo.setText(mXJTaskEntity.tableNo);
            xjTaskDetailTaskState.setText(mXJTaskEntity.isFinished ? getString(R.string.xj_task_checked) : getString(R.string.xj_task_uncheck));
            xjTaskDetailTaskState.setTextColor(getResources().getColor(R.color.xjBtnColor));

            if (mXJTaskEntity.isFinished) {
                xjTaskDetailTaskBtn.setVisibility(View.GONE);
                xjTaskDetailTaskState.setTextColor(getResources().getColor(R.color.xjBtnColor));
                xjTaskDetailTaskState.setBackgroundResource(R.drawable.sh_xj_blue_stroke);
            } else if (mXJTaskEntity.realStartTime != 0) {
                xjTaskDetailTaskState.setTextColor(getResources().getColor(R.color.xjBtnRedColor));
                xjTaskDetailTaskState.setBackgroundResource(R.drawable.sh_xj_red_stroke);
                xjTaskDetailTaskBtn.setVisibility(View.VISIBLE);
                xjTaskDetailTaskBtn.setText(getString(R.string.xj_task_end));
                xjTaskDetailTaskBtn.setBackgroundResource(R.drawable.sl_xj_task_red);
            } else {
                xjTaskDetailTaskState.setTextColor(getResources().getColor(R.color.xjBtnRedColor));
                xjTaskDetailTaskState.setBackgroundResource(R.drawable.sh_xj_red_stroke);
                xjTaskDetailTaskBtn.setVisibility(View.VISIBLE);
                xjTaskDetailTaskBtn.setText(getString(R.string.xj_task_start));
                xjTaskDetailTaskBtn.setBackgroundResource(R.drawable.sl_xj_task_blue);
            }

            xjTaskDetailDate.setText(DateUtil.dateFormat(mXJTaskEntity.startTime, "MM-dd HH:mm")
                    + " - " + DateUtil.dateFormat(mXJTaskEntity.endTime, "MM-dd HH:mm"));

            if (mXJTaskEntity.attrMap != null)
                for (String key : mXJTaskEntity.attrMap.keySet()) {
                    String value = (String) mXJTaskEntity.attrMap.get(key);
                    if (!TextUtils.isEmpty(value)) {
                        xjTaskDetailStaff.setText("" + value);
                    }
                }
            else if (mXJTaskEntity.isTemp) {
                xjTaskDetailStaff.setText(mXJTaskEntity.staffName);
            }
            initArea();
        }
    }

    private void initArea() {


        if (mXJTaskEntity.areas == null) {

            List<XJAreaEntity> areaEntities = SupPlantApplication.dao().getXJAreaEntityDao().queryBuilder()
                    .where(XJAreaEntityDao.Properties.WorkRouteId.eq(mXJTaskEntity.workRoute.id))
                    .where(XJAreaEntityDao.Properties.Valid.eq(true))
                    .where(XJAreaEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                    .orderAsc(XJAreaEntityDao.Properties.Sort)
                    .list();


            String s = GsonUtil.gsonString(areaEntities);

            List<XJTaskAreaEntity> xjTaskAreaEntities = GsonUtil.jsonToList(s, XJTaskAreaEntity.class);
            mXJTaskEntity.areas = xjTaskAreaEntities;
            XJTaskCacheUtil.insertTasksAreas(mXJTaskEntity);

            mXJTaskEntity.areas = SupPlantApplication.dao().getXJTaskAreaEntityDao().queryBuilder()
                    .where(XJTaskAreaEntityDao.Properties.TableNo.eq(mXJTaskEntity.tableNo)).list();

        }

        if (mXJTaskEntity.areas.size() == 0) {

            ToastUtils.show(context, getString(R.string.xj_area_empty_warning));

        }

        mXJAreaAdapter = new XJAreaAdapter(context);

        xjTaskDetailContentView.setLayoutManager(new LinearLayoutManager(context));
        mXJAreaAdapter.exceptionIds = mXJTaskEntity.exceptinWorkIds;
        mXJAreaAdapter.setList(mXJTaskEntity.areas);
        xjTaskDetailContentView.setAdapter(mXJAreaAdapter);

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        RxView.clicks(xjTaskDetailCloseBtn)
                .subscribe(o -> back());

        RxView.clicks(xjTaskDetailScanBtn)
                .subscribe(o -> {
                    driverController.openCameraScan();
                });

        if (mXJAreaAdapter != null) {
            mXJAreaAdapter.setOnItemChildViewClickListener((childView, position, action, obj) -> {
                XJTaskAreaEntity xjAreaEntity = (XJTaskAreaEntity) obj;

                if (mXJTaskEntity.realStartTime == 0) {
                    ToastUtils.show(context, getString(R.string.xj_area_sign_warning1));
                    return;
                }

                if (mXJTaskEntity.areas == null || mXJTaskEntity.areas.size() == 0) {
                    ToastUtils.show(context, getString(R.string.xj_area_sign_warning2));
                    showDialog();
                    return;
                }

                if (xjAreaEntity.isSigned || mXJTaskEntity.isFinished) {
                    goArea(xjAreaEntity);
                } else {
                    showSignReason(xjAreaEntity);
                }

            });


            RxView.clicks(xjTaskDetailTaskBtn)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(o -> {
                        if (mXJTaskEntity.realStartTime == 0) {

                            if (mXJTaskEntity.areas == null || mXJTaskEntity.areas.size() == 0) {
                                //                   ToastUtils.show(context, getString(R.string.xj_area_sign_warning2));
                                showDialog();
                                return;
                            }
                            if (mXJTaskEntity.advanceStartTime != null) {
                                double advanceStartTime = mXJTaskEntity.advanceStartTime * (60 * 60 * 1000);
                                long currentTime = System.currentTimeMillis();
                                if ((mXJTaskEntity.startTime - advanceStartTime) > currentTime) {

                                    ToastUtils.show(this, context.getResources().getString(R.string.xj_no_allow_advance) + new DecimalFormat("0.00").format(mXJTaskEntity.advanceStartTime) + context.getResources().getString(R.string.hour_begins));
                                    return;
                                }
                            }
                            if (mXJTaskEntity.delayStartTime != null) {
                                double delayStartTime = mXJTaskEntity.delayStartTime * (60 * 60 * 1000);
                                long currentTime = System.currentTimeMillis();
                                if ((mXJTaskEntity.startTime + delayStartTime) < currentTime) {
                                    ToastUtils.show(this, context.getResources().getString(R.string.xj_no_allow_delay) + new DecimalFormat("0.00").format(mXJTaskEntity.delayStartTime) + context.getResources().getString(R.string.hour_begins));
                                    return;
                                }
                            }
                            mXJTaskEntity.realStartTime = System.currentTimeMillis();
                            XJTaskCacheUtil.putString(mXJTaskEntity.toString());
                            //????????????
                            xjTaskDetailTaskBtn.setBackgroundResource(R.drawable.sl_xj_task_red);
                            xjTaskDetailTaskBtn.setText(getString(R.string.xj_task_end));
                            if (mXJTaskEntity.id != null) {//???????????????????????????????????????
                                presenterRouter.create(XJUpdateStatusAPI.class).updateXJTaskStatus(mXJTaskEntity.id, "PATROL_taskState/running");
                            }
                        } else {
                            if (mXJTaskEntity.delayEndTime != null) {
                                double delayEndTime = mXJTaskEntity.delayEndTime * (60 * 60 * 1000);
                                long currentTime = System.currentTimeMillis();

                                if ((mXJTaskEntity.endTime + delayEndTime) < currentTime) {
                                    ToastUtils.show(this, context.getResources().getString(R.string.xj_no_allow_delay) + new DecimalFormat("0.00").format(mXJTaskEntity.delayEndTime) + context.getResources().getString(R.string.hour_end));
                                    return;
                                }
                            }
                            showFinishDialog();
                        }
//                            if (mXJTaskEntity.areas == null || mXJTaskEntity.areas.size() == 0) {
//                                //                   ToastUtils.show(context, getString(R.string.xj_area_sign_warning2));
//                                showDialog();
//                                return;
//                            }
//
//                            mXJTaskEntity.realStartTime = System.currentTimeMillis();
//                            XJTaskCacheUtil.putString(mXJTaskEntity.tableNo, mXJTaskEntity.toString());
//                            //????????????
//                            xjTaskDetailTaskBtn.setBackgroundResource(R.drawable.sl_xj_task_red);
//                            xjTaskDetailTaskBtn.setText(getString(R.string.xj_task_end));
//
//                            presenterRouter.create(XJUpdateStatusAPI.class).updateXJTaskStatus(mXJTaskEntity.id, "PATROL_taskState/running");
//                        } else {
//                            showFinishDialog();
//                        }

                    });
        }
    }

    public void showDialog() {
        new CustomDialog(context)
                .twoButtonAlertDialog(getString(R.string.xj_area_sign_warning2))
                .bindView(R.id.redBtn, context.getResources().getString(R.string.xj_patrol_sure))
                .bindView(R.id.grayBtn, context.getResources().getString(R.string.xj_patrol_cancel))
                .bindClickListener(R.id.redBtn, v1 -> {
                    IntentRouter.go(context, Constant.AppCode.COM_DataManage);
                    back();
                }, true)
                .bindClickListener(R.id.grayBtn, v -> {

                }, true)
                .show();


    }

    private void showFinishDialog() {
        mXJTaskEntity.isFinished = checkAreaFinishState(mXJTaskEntity.areas);
        new CustomDialog(context)
                .twoButtonAlertDialog(mXJTaskEntity.isFinished ? getString(R.string.xj_task_finish_warning2) : getString(R.string.xj_task_finish_warning1))
                .bindClickListener(R.id.grayBtn, v -> {
                }, true)
                .bindClickListener(R.id.redBtn, v -> {

                    onLoading(getString(R.string.xj_task_finish_toast1));

                    mXJTaskEntity.isFinished = true;
                    mXJTaskEntity.realEndTime = new Date().getTime();

                    XJTaskCacheUtil.putStringAsync(mXJTaskEntity.toString(), new XJTaskCacheUtil.Callback() {
                        @Override
                        public void apply() {
                            LogUtil.d("493 ????????????");
                        }
                    });
                    EventBus.getDefault().post(new RefreshEvent());
                    onLoadSuccessAndExit(getString(R.string.xj_task_finish_toast2), () -> back());
//                    presenterRouter.create(OLXJTaskStatusAPI.class).endTasks(String.valueOf(olxjTaskEntity.id), "????????????", true);
                }, true)
                .show();
    }

    private boolean checkAreaFinishState(List<XJTaskAreaEntity> areaEntities) {


        if (areaEntities == null || areaEntities.size() == 0) {
            return true;
        }

        for (XJTaskAreaEntity xjAreaEntity : areaEntities) {
            if (!xjAreaEntity.isFinished && xjAreaEntity.getTotalNum(mXJTaskEntity.exceptinWorkIds) > 0) {
                return false;
            }
        }

        return true;

    }

    /**
     * @author zhangwenshuai1
     * @date 2018/4/9
     * @description ??????????????????????????????
     */
    private void showSignReason(XJTaskAreaEntity xjAreaEntity) {
        if (xjAreaEntity.isSigned && xjAreaEntity.cardTime != 0) {
            goArea(xjAreaEntity);
        } else {
            if (signTypeInfoMap == null || signTypeInfoMap.size() <= 0) {
                ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_reason));
                return;
            }

            List<String> values = new ArrayList<>();
            values.addAll(signTypeInfoMap.values());

            new SinglePickController<String>(this).list(values).listener((index, item) -> {


                for (String key : signTypeInfoMap.keySet()) {

                    String value = signTypeInfoMap.get(key);

                    if (value.equals(item)) {
                        xjAreaEntity.signInReason = SystemCodeManager.getInstance().getSystemCodeEntity(key);
                        break;
                    }
                }


                xjAreaEntity.isSigned = true;
                xjAreaEntity.payCardType = SystemCodeManager.getInstance().getSystemCodeEntity("PATROL_payCardType/signIn");
                xjAreaEntity.cardTime = new Date().getTime();
//                XJTaskCacheUtil.putStringAsync(mXJTaskEntity.toString(), new XJTaskCacheUtil.Callback() {
//                    @Override
//                    public void apply() {
//                        LogUtil.d("557 ????????????");
//                    }
//                });
//                SupPlantApplication.dao().getXJTaskAreaEntityDao().update(xjAreaEntity);
                XJTaskCacheUtil.insertTasksArea(xjAreaEntity);
                goArea(xjAreaEntity);

            }).show();
        }

    }

    /**
     * @param
     * @description ?????????????????????
     * @author zhangwenshuai1
     * @date 2018/6/15
     */
    private void goArea(XJTaskAreaEntity xjAreaEntity) {
        enterPosition = getEnterPosition(xjAreaEntity);
        doGoArea(xjAreaEntity);
    }

    private int getEnterPosition(XJTaskAreaEntity areaEntity) {

        int index = 0;
        for (XJTaskAreaEntity olxjAreaEntity : mXJTaskEntity.areas) {
            if (olxjAreaEntity.id != null && areaEntity.id != null && olxjAreaEntity.id.equals(areaEntity.id)) {
                return index;
            }
            index++;
        }
        return index;
    }

    private void doGoArea(XJTaskAreaEntity xjAreaEntity) {
        Bundle bundle = new Bundle();
//        LogUtil.e("ciruy", TextUtils.isEmpty(mXJTaskEntity.exceptinWorkIds)?"":mXJTaskEntity.exceptinWorkIds);
        bundle.putString(Constant.IntentKey.XJ_AREA_ENTITY_STR, String.valueOf(xjAreaEntity.id));
        bundle.putString(Constant.IntentKey.XJ_TASK_NO_STR, mXJTaskEntity.tableNo);
        bundle.putString(Constant.IntentKey.XJ_AREA_EXCEPTION_IDS, mXJTaskEntity.exceptinWorkIds);
//        BundleSaveUtil.instance
//                .put(Constant.IntentKey.XJ_AREA_ENTITY_STR, xjAreaEntity.toString())
//                .put(Constant.IntentKey.XJ_TASK_ENTITY_STR, mXJTaskEntity.toString())
//                .put(Constant.IntentKey.XJ_AREA_EXCEPTION_IDS, mXJTaskEntity.exceptinWorkIds);
//        bundle.putBoolean(Constant.IntentKey.BUNDLE_TEMP_SAVE, true);
        if (xjAreaEntity.isFinished || mXJTaskEntity.isFinished) {
            bundle.putBoolean(Constant.IntentKey.XJ_IS_FROM_TASK, true);
            bundle.putBoolean(Constant.IntentKey.XJ_IS_FINISHED, mXJTaskEntity.isFinished);
            IntentRouter.go(context, Constant.Router.XJ_WORK_ITEM_VIEW, bundle);
        } else
            IntentRouter.go(context, Constant.Router.XJ_WORK_ITEM, bundle);
    }

    @SuppressLint("SupportAnnotationUsage")
    @CheckResult
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAreaUpdate(XJAreaRefreshEvent areaEntity) {
        if (enterPosition != -1) {
            XJTaskAreaEntity xjTaskAreaEntity = SupPlantApplication.dao().getXJTaskAreaEntityDao()
                    .queryBuilder()
                    .where(XJTaskAreaEntityDao.Properties.Id.eq(mXJTaskEntity.areas.get(enterPosition).id))
                    .where(XJTaskAreaEntityDao.Properties.TableNo.eq(mXJTaskEntity.tableNo))
                    .unique();
            mXJTaskEntity.areas.set(enterPosition, xjTaskAreaEntity);
            mXJAreaAdapter.notifyDataSetChanged();

        }
    }


    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTempTaskUploadRefresh(XJTempTaskUploadRefreshEvent event) {
        if (event.getId() != 0) {
            mXJTaskEntity.id = event.getId();
            XJTaskCacheUtil.putStringAsync(mXJTaskEntity.toString(), new XJTaskCacheUtil.Callback() {
                @Override
                public void apply() {
                    LogUtil.d("493 ????????????");
                }
            });
        }
        if (enterPosition != -1) {
            XJTaskAreaEntity xjTaskAreaEntity = SupPlantApplication.dao().getXJTaskAreaEntityDao()
                    .queryBuilder()
                    .where(XJTaskAreaEntityDao.Properties.Id.eq(mXJTaskEntity.areas.get(enterPosition).id))
                    .where(XJTaskAreaEntityDao.Properties.TableNo.eq(mXJTaskEntity.tableNo))
                    .unique();
            mXJTaskEntity.areas.set(enterPosition, xjTaskAreaEntity);
            mXJAreaAdapter.notifyDataSetChanged();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCodeReciver(CodeResultEvent codeResultEvent) {
        String resultCode = codeResultEvent.scanResult;
        if (CodeUtlis.NFC_TYPE.equals(codeResultEvent.type)) {
            NFCEntity nfcEntity = GsonUtil.gsonToBean(resultCode, NFCEntity.class);
            dealSign(nfcEntity.content);
        } else if (CodeUtlis.UHF_TYPE.equals(codeResultEvent.type)) {
            dealSign(resultCode);
        } else if (CodeUtlis.HW_TYPE.equals(codeResultEvent.type)) {
            dealSign(resultCode);
        } else if (CodeUtlis.QR_TYPE.equals(codeResultEvent.type)) {
            dealSign(resultCode);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanBarCode(BarcodeEvent barcodeEvent) {
        LogUtil.i("BarcodeEvent", barcodeEvent.getScanCode());
        dealSign(barcodeEvent.getScanCode());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUhfRfidEpcCode(UhfRfidEvent uhfRfidEvent) {
        LogUtil.d("EPC:" + uhfRfidEvent.getEpcCode());
        if (uhfRfidEvent.getEpcCode() != null) {
            if (mExpertUHFRFIDController != null) {
                mExpertUHFRFIDController.stop();
                mExpertUHFRFIDController.onDestroy();
            }
            if (em55UHFRFIDHelper != null) {
                em55UHFRFIDHelper.inventoryStop();
                em55UHFRFIDHelper.close();
            }
            dealSign(uhfRfidEvent.getEpcCode());
        }
    }
//
//    /**
//     * @param
//     * @description NFC??????
//     * @author zhangwenshuai1
//     * @date 2018/6/28
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getNFC(NFCEvent nfcEvent) {
//        LogUtil.d("NFC_TAG", nfcEvent.getNfc());
//        Map<String, Object> nfcJson = Util.gsonToMaps(nfcEvent.getNfc());
//        dealSign((String) nfcJson.get("id"));
//    }

//    private Set<String> scannedCodeSet = new HashSet<>();

    /**
     * @param
     * @return
     * @description ?????????UHF RFID?????????????????????????????????
     * @author zhangwenshuai1 2018/8/2
     */
    private void dealSign(String code) {
        int index = 0;
        if (mXJTaskEntity.areas == null || mXJTaskEntity.areas.size() == 0) {
            showDialog();
            return;
        }

        if (mXJTaskEntity.realStartTime == 0) {
            ToastUtils.show(context, getString(R.string.xj_area_sign_warning1));
            return;
        }
        boolean isExist = false;
        for (XJTaskAreaEntity areaEntity : mXJTaskEntity.areas) {
            if (code.trim().equals(areaEntity.signCode)) {
                isExist = true;
                updateXJAreaEntity(areaEntity);//update??????
                LogUtil.i("BarcodeEvent1", code);
                if (enterPosition != index) {
                    doGoArea(areaEntity);  //??????
                    enterPosition = index;
                }
                break;
            }
            index++;
        }
        if (!isExist) {
            ToastUtils.show(context, getString(R.string.xj_patrol_code_not_match));
        }
    }

    /**
     * @param
     * @description ??????????????????????????????????????????
     * @author yangkai2
     */

    @Override
    public void updateXJTaskStatusSuccess(BAP5CommonEntity entity) {

    }

    @Override
    public void updateXJTaskStatusFailed(String errorMsg) {

    }

    /**
     * @param
     * @description update??????????????????
     * @author zhangwenshuai1
     * @date 2018/6/15
     */
    private void updateXJAreaEntity(XJTaskAreaEntity xjAreaEntity) {
        xjAreaEntity.payCardType = SystemCodeManager.getInstance().getSystemCodeEntity("PATROL_payCardType/card");
        xjAreaEntity.isSigned = true;
        xjAreaEntity.cardTime = new Date().getTime();
//        SupPlantApplication.dao().getXJTaskAreaEntityDao().update(xjAreaEntity);
        XJTaskCacheUtil.insertTasksArea(xjAreaEntity);
    }


    @Override
    public void uploadFileSuccess(String entity) {

    }

    @Override
    public void uploadFileFailed(String errorMsg) {

    }

    @Override
    public void uploadXJWorkFileSuccess(Long entity) {

    }

    @Override
    public void uploadXJWorkFileFailed(String errorMsg) {

    }

    @Override
    public void uploadXJDataSuccess(Long id) {

    }

    @Override
    public void uploadXJDataFailed(String errorMsg) {

    }


}
