package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.common.view.view.loader.base.OnLoaderFinishListener;
import com.supcon.mes.aic_vib.controller.AICVibController;
import com.supcon.mes.aic_vib.controller.AICVibServiceController;
import com.supcon.mes.aic_vib.service.AICVibService;
import com.supcon.mes.av160.controller.AV160Controller;
import com.supcon.mes.mbap.beans.SheetEntity;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.mbap.view.CustomSheetDialog;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.mbap.view.CustomVerticalEditText;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.constant.TemperatureMode;
import com.supcon.mes.middleware.constant.VibMode;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.bean.ObjectEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.util.AnimationUtil;
import com.supcon.mes.middleware.util.SBTUtil;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_xj.IntentRouter;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.controller.XJCameraController;
import com.supcon.mes.module_xj.model.event.WorkItemLocationEvent;
import com.supcon.mes.module_xj.model.event.XJAreaRefreshEvent;
import com.supcon.mes.module_xj.model.event.XJWorkRefreshEvent;
import com.supcon.mes.module_xj.ui.adapter.XJWorkAdapter;
import com.supcon.mes.mogu_viber.controller.MGViberController;
import com.supcon.mes.sb2.model.event.ThermometerEvent;
import com.supcon.mes.testo_805i.controller.InfraredEvent;
import com.supcon.mes.testo_805i.controller.TestoController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/1/10
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_WORK_ITEM)
@Controller(value = {TestoController.class, SystemCodeJsonController.class, XJCameraController.class})
@SystemCode(entityCodes = {
//        Constant.SystemCode.PATROL_editType,
//        Constant.SystemCode.PATROL_valueType,
        Constant.SystemCode.PATROL_passReason,
        Constant.SystemCode.PATROL_realValue

})
public class XJWorkActivity extends BaseRefreshRecyclerActivity<XJWorkEntity> {

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("leftBtn")
    CustomImageButton leftBtn;

    @BindByTag("rightBtn")
    CustomImageButton rightBtn;

//    @BindByTag("rightBtn2")
//    CustomImageButton rightBtn2;

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("eamSpinner")
    Spinner eamSpinner;

    XJWorkAdapter mXJWorkAdapter;

    XJAreaEntity mXJAreaEntity;

    private Map<String, String> passReasonMap, realValueMap;

    private SinglePickController<String> mSingPicker;

    private String thermometervalue = ""; // 全局测温值

    private MGViberController mMGViberController;
    private CustomDialog mMGViberDialog;
    private AICVibServiceController mAICVibController;
    private AV160Controller mAV160Controller;
    private CustomDialog mTesto805iDialog;
    private int mPosition;
//    private int tempMode;
//    private int vibMode;
    ObjectEntity mDevice = null;
    List<String> deviceNames = new ArrayList<>();
    private List<XJWorkEntity> mWorkEntities = new ArrayList<>();

    private boolean needRefresh = false;//重录之后，需要刷新列表

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_work;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
        String xjAreaEntityStr = getIntent().getStringExtra(Constant.IntentKey.XJ_AREA_ENTITY_STR);
        if(xjAreaEntityStr!=null){
            mXJAreaEntity = GsonUtil.gsonToBean(xjAreaEntityStr, XJAreaEntity.class);
        }

        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(false);


        int tempMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0);
        int vibMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.VIB_MODE, 0);

        if(tempMode == TemperatureMode.AIC.getCode() || vibMode == VibMode.AIC.getCode()){

            if(mAICVibController == null) {
                AICVibService.start(context);
                mAICVibController = new AICVibServiceController(AICVibServiceController.getLayoutView(context), true);
                mAICVibController.onInit();
                mAICVibController.initView();
                mAICVibController.initListener();
                mAICVibController.initData();
                registerController(AICVibController.class.getSimpleName(), mAICVibController);
            }
            else{
                mAICVibController.initData();
            }
        }

        if(tempMode == TemperatureMode.SBT.getCode() && SBTUtil.isSupportTemp()){
            mXJWorkAdapter.setSb2ThermometerHelper();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        if(mMGViberController!=null){
            mMGViberController.onDestroy();
        }

        if(mAICVibController!=null){
            mAICVibController.onDestroy();
        }

        if(mAV160Controller!=null){
            mAV160Controller.onDestroy();
        }

        int finishNum = 0;
        for(XJWorkEntity xjWorkEntity : mXJAreaEntity.works){
            if (xjWorkEntity.isFinished){
                finishNum++;
            }
        }

        mXJAreaEntity.finishNum = finishNum;
        if(mXJAreaEntity.finishNum == mXJAreaEntity.works.size()){
            mXJAreaEntity.isFinished = true;
        }
        else{
            mXJAreaEntity.isFinished = false;
        }

        EventBus.getDefault().post(new XJAreaRefreshEvent(mXJAreaEntity));
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText(getString(R.string.xj_work));
        rightBtn.setImageResource(R.drawable.sl_xj_work_top_finish);

        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(1, context)));
        contentView.setAdapter(mXJWorkAdapter);

        mSingPicker = new SinglePickController<>(this);
        mSingPicker.setCanceledOnTouchOutside(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.IntentKey.XJ_AREA_ENTITY_STR, mXJAreaEntity.toString());
                        IntentRouter.go(context, Constant.Router.XJ_WORK_ITEM_VIEW, bundle);
                    }
                });

//        RxView.clicks(rightBtn2)
//                .throttleFirst(200, TimeUnit.MILLISECONDS)
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//                        showAllFinishDialog(0);
//                    }
//                });

        mXJWorkAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
//                LogUtil.d(""+childView.getTag());
                XJWorkEntity xjWorkEntity = (XJWorkEntity) obj;
                String tag = (String) childView.getTag();
                mPosition = position;
                switch (tag){

                    case "itemXJWorkFinish":
                        showAllFinishDialog(xjWorkEntity.eamLongId);
                        break;

                    case "itemXJWorkSkip":
                        skipEam(xjWorkEntity.eamLongId);
                        break;

                    case "itemXJWorkTempBtn":
                        showTempView(xjWorkEntity);
                        break;

                    case "itemXJWorkVibBtn":
                        showVibView(xjWorkEntity);
                        break;

                        //多选
                    case "itemXJWorkResultMultiSelect":
                        dialogMoreChoice(xjWorkEntity, position);
                        break;

                    case "itemXJWorkRemarkBtn":
                        showEditDialog(xjWorkEntity, position);
                        break;

                }


            }
        });

        eamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String deviceName = deviceNames.get(position);
                showWorks(deviceName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @SuppressLint("CheckResult")
    private void showEditDialog(XJWorkEntity xjWorkEntity, int position) {


        CustomDialog remarkDialog = new CustomDialog(context)
                .layout(R.layout.v_xj_remark_dialog)
                .bindClickListener(R.id.okBtn, v -> {}, true);

        CustomVerticalEditText iCustomView = remarkDialog.getDialog().findViewById(R.id.remarkInput);
        RxTextView.textChanges(iCustomView.editText())
                .skipInitialValue()
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        xjWorkEntity.remark = charSequence.toString();
                        xjWorkEntity.realRemark = charSequence.toString();
                    }
                });
        if(xjWorkEntity.remark!=null){
            iCustomView.setContent(xjWorkEntity.remark);
        }
        remarkDialog.show();
    }


    @Override
    protected IListAdapter<XJWorkEntity> createAdapter() {
        mXJWorkAdapter = new XJWorkAdapter(context);
        return mXJWorkAdapter;
    }

    @Override
    protected void initData() {
        super.initData();
        mXJWorkAdapter.setConclusions(getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_realValue));

        passReasonMap = getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_passReason);
        realValueMap =  getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_realValue);

        if(mXJAreaEntity!=null && mXJAreaEntity.works!=null){
            refreshWorkList();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        setDefaultAnimation();
        if(needRefresh){

            refreshWorkList();
            needRefresh = false;
        }

    }

    private void setDefaultAnimation() {
        AnimationUtil.setActivityDefaultAnimation(this);
    }

    @SuppressLint("CheckResult")
    private void initWorks(){
        List<XJWorkEntity> noEamWorks = new ArrayList<>();
        Flowable.fromIterable(mXJAreaEntity.works)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJWorkEntity>() {
                    @Override
                    public boolean test(XJWorkEntity xjWorkEntity) throws Exception {

                        if(xjWorkEntity.isFinished){
                            return false;
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJWorkEntity>() {
                    @Override
                    public void accept(XJWorkEntity xjWorkEntity) throws Exception {

                        if(xjWorkEntity.realRemark!=null){
                            xjWorkEntity.remark = xjWorkEntity.realRemark;
                        }
                        else{
                            xjWorkEntity.remark = "";
                        }

                        if(xjWorkEntity.eamId == null || xjWorkEntity.eamId.id == null){
                            noEamWorks.add(xjWorkEntity);
                        }
                        else {
                            if (mDevice == null || mDevice.id != xjWorkEntity.eamLongId) {
                                mDevice = xjWorkEntity.eamId;
                                XJWorkEntity workEntity = new XJWorkEntity();
                                workEntity.eamId = mDevice;
                                workEntity.eamLongId = mDevice.id;
                                workEntity.isEamView = true;
                                workEntity.eamName = mDevice.name;
                                workEntity.eamNum = deviceNames.size() + 1;
                                mWorkEntities.add(workEntity);

                                deviceNames.add(workEntity.eamId.name);
                            }
                            mWorkEntities.add(xjWorkEntity);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
//                        refreshListController.refreshComplete(mWorkEntities);
                        if(noEamWorks.size() != 0){
                            XJWorkEntity workEntity = new XJWorkEntity();
                            workEntity.isEamView =true;
                            workEntity.eamNum = deviceNames.size() + 1;

                            if(deviceNames.size() == 0){
                                workEntity.eamName = "区域巡检";
                            }
                            else{
                                workEntity.eamName = "无设备巡检";

                            }
                            deviceNames.add(workEntity.eamName);
                            mWorkEntities.add(workEntity);
                            mWorkEntities.addAll(noEamWorks);
                        }

                        if(deviceNames.size() == 0){
                            ((ViewGroup)eamSpinner.getParent()).setVisibility(View.GONE);
                            refreshListController.refreshComplete(mWorkEntities);
//                            if(mWorkEntities.size() == 0){
//                                refreshListController.refreshComplete(null);
//                                return;
//                            }
//                            XJWorkEntity workEntity = new XJWorkEntity();
//                            workEntity.isEamView =true;
//                            workEntity.eamNum =1;
//                            mWorkEntities.add(0, workEntity);
//
//                            refreshListController.refreshComplete(mWorkEntities);
                        }
                        else {

                            if(deviceNames.size()==1){
                                ((ViewGroup)eamSpinner.getParent()).setVisibility(View.GONE);
                                refreshListController.refreshComplete(mWorkEntities);
                            }
                            else{
                                deviceNames.add(0, getString(R.string.xj_work_eam_all));
                                ((ViewGroup)eamSpinner.getParent()).setVisibility(View.VISIBLE);
                                ArrayAdapter<String> eamSpinnerAdapter = new ArrayAdapter<>(context, R.layout.ly_spinner_item_dark, deviceNames);  //创建一个数组适配器
                                eamSpinnerAdapter.setDropDownViewResource(R.layout.ly_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
                                eamSpinner.setAdapter(eamSpinnerAdapter);
                            }


                        }
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void showWorks(String deviceName) {

        boolean isAll;
        isAll = getString(R.string.xj_work_eam_all).equals(deviceName);

        List<XJWorkEntity> workEntities = new ArrayList<>();
        Flowable.fromIterable(mWorkEntities)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJWorkEntity>() {
                    @Override
                    public boolean test(XJWorkEntity xjWorkEntity) throws Exception {

                        if(isAll || xjWorkEntity.eamId!=null && xjWorkEntity.eamId.name!=null && xjWorkEntity.eamId.name.equals(deviceName)){
                            return true;
                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJWorkEntity>() {
                    @Override
                    public void accept(XJWorkEntity xjWorkEntity) throws Exception {
                        workEntities.add(xjWorkEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        refreshListController.refreshComplete(workEntities);
                    }
                });
    }


    private void showVibView(XJWorkEntity xjWorkEntity) {
        int vibMode  = SharedPreferencesUtils.getParam(context, Constant.SPKey.VIB_MODE, 0);
        if (vibMode == VibMode.AIC.getCode()) {
            showAICDialog(mPosition, xjWorkEntity, false);
        } else if (vibMode == VibMode.AV160D.getCode()) {
            showAV160Dialog(mPosition, xjWorkEntity);
        } else if (vibMode == VibMode.SU100.getCode()) {
            showMGViberDialog(mPosition, xjWorkEntity);
        } else if (SBTUtil.isSupportTemp()) {

        }
    }

    private void showAV160Dialog(int position, XJWorkEntity xjWorkItemEntity) {


        if(mAV160Controller == null) {

            mAV160Controller = new AV160Controller(AV160Controller.getLayoutView(context), true);
            mAV160Controller.onInit();
            mAV160Controller.initView();
            mAV160Controller.initListener();
            mAV160Controller.initData();
            registerController(AV160Controller.class.getSimpleName(), mAV160Controller);
        }
        else{
            mAV160Controller.reset();
        }
        mAV160Controller.start();

        mAV160Controller.setOnAV160DataSelectListener(new AV160Controller.OnAV160DataSelectListener() {
            @Override
            public void onDataSelect(String data) {
                mAV160Controller.stop();
                if(TextUtils.isEmpty(data)){
                    return;
                }
                xjWorkItemEntity.concluse = data;
                mXJWorkAdapter.notifyItemChanged(position);
            }
        });

    }

    private void showMGViberDialog(int position, XJWorkEntity xjWorkItemEntity) {

        if(mMGViberDialog == null) {
            mMGViberDialog = new CustomDialog(context)
                    .layout(R.layout.ly_viber_dialog)
                    .bindClickListener(R.id.viberFinishBtn, null, true);
            mMGViberController = new MGViberController(mMGViberDialog.getDialog().getWindow().getDecorView());
            mMGViberController.onInit();
            mMGViberController.initView();
            mMGViberController.initListener();
            mMGViberController.initData();
        }
        else{
            mMGViberController.reset();
        }

        mMGViberDialog.bindClickListener(R.id.viberFinishBtn, v->{

            mMGViberController.stopTest();

            String data = mMGViberController.getData();
            if(TextUtils.isEmpty(data)){
                return;
            }
            xjWorkItemEntity.concluse = data;
            mXJWorkAdapter.notifyItemChanged(position);

        }, true).show();

    }


    private void showTempView(XJWorkEntity xjWorkEntity){
        int tempMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0);
        if (tempMode == TemperatureMode.AIC.getCode()) {
            showAICDialog(mPosition, xjWorkEntity, true);
        } else if (tempMode == TemperatureMode.TESTO.getCode()) {
           getController(TestoController.class).startService();
            showTesto805iDialog(mPosition, xjWorkEntity);
        } else if (tempMode == TemperatureMode.SBT.getCode()) {

        } else if (SBTUtil.isSupportTemp()) {

        }

    }

    private void showTesto805iDialog(int position, XJWorkEntity xjWorkItemEntity) {
        thermometervalue = null;
        mTesto805iDialog = new CustomDialog(context);
        mTesto805iDialog.layout(R.layout.v_testo_805i_dialog)
                .bindClickListener(R.id.startBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomTextView temperatureVal = mTesto805iDialog.getDialog().findViewById(R.id.temperatureVal);
                        temperatureVal.setContent("正在初始化，请确保设备打开（黄灯闪烁）;绿灯即可开启测温");
                        getController(TestoController.class).startService();
                    }
                }, false)
                .bindClickListener(R.id.okBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(thermometervalue!=null){
                            xjWorkItemEntity.concluse = thermometervalue;
                            mXJWorkAdapter.notifyItemChanged(position);
                            thermometervalue = null;
                        }
                    }
                }, true)
                .show();

    }

    private void showAICDialog(int position, XJWorkEntity xjWorkItemEntity, boolean isTempTest) {


//        if(mAICVibController==null) {
//
//            mAICVibController = ((XJWorkActivity)getActivity()).getAICVibController();
//
//            if(mAICVibController == null){
//                mAICVibController = new AICVibServiceController(AICVibServiceController.getLayoutView(context), true);
//
//                mAICVibController.onInit();
//
//                mAICVibController.initView();
//                mAICVibController.initListener();
//                mAICVibController.initData();
//            }
//
//        }

        mAICVibController.setOnDataSelectListener(new AICVibServiceController.OnDataSelectListener() {
            @Override
            public void onDataSelect(String data) {
                LogUtil.d("onDataSelect:"+data);
                if(TextUtils.isEmpty(data)){
                    return;
                }
                xjWorkItemEntity.concluse = data;
                mXJWorkAdapter.notifyItemChanged(position);
            }
        });
        mAICVibController.reset();
        mAICVibController.show(isTempTest);
    }



    @SuppressLint("CheckResult")
    public void showAllFinishDialog(long eamId) {


        new CustomDialog(context)
                .twoButtonAlertDialog(eamId == 0?getString(R.string.xj_work_finish_warning2):getString(R.string.xj_work_finish_warning1))
                .bindView(R.id.grayBtn, getString(R.string.no))
                .bindView(R.id.redBtn, getString(R.string.yes))
                .bindClickListener(R.id.grayBtn, null, true)
                .bindClickListener(R.id.redBtn, v -> doAllFinish(eamId), true)
                .show();
    }

    boolean isCanEndAll = true;
    @SuppressLint("CheckResult")
    private void doAllFinish(long eamId) {

        if(mWorkEntities.size() == 0){
            ToastUtils.show(context, getString(R.string.xj_work_finish_warning3));
            return;
        }
        List<XJWorkEntity> xjWorkEntities = new ArrayList<>();
        isCanEndAll = true;
        Flowable.fromIterable(mWorkEntities)
                .filter(new Predicate<XJWorkEntity>() {
                    @Override
                    public boolean test(XJWorkEntity xjWorkItemEntity) throws Exception {

                        return !xjWorkItemEntity.isEamView && (eamId == xjWorkItemEntity.eamLongId) && !xjWorkItemEntity.isFinished;
                    }
                })
                .subscribe(xjWorkItemEntity -> {


                            String msg = null;

                            xjWorkItemEntity.concluse = TextUtils.isEmpty(xjWorkItemEntity.concluse) ? xjWorkItemEntity.defaultVal : xjWorkItemEntity.concluse; //若列表无滚动直接一键完成，默认值不会回填到结果
                            if (TextUtils.isEmpty(xjWorkItemEntity.concluse)) {
                                StringBuilder sb = new StringBuilder();
                                if (xjWorkItemEntity.eamLongId == 0){
                                    sb.append("");
                                }else {
                                    sb.append("[").append(xjWorkItemEntity.eamId.name).append("]");
                                }
                                sb.append("巡检内容“" + xjWorkItemEntity.content + "”需要填写结果");
//                                ToastUtils.show(context,  sb.toString());
                                msg = sb.toString();
                            }
                            else if ( xjWorkItemEntity.isPhone && !xjWorkItemEntity.isRealPhoto) {
                                StringBuilder sb = new StringBuilder();
                                if (xjWorkItemEntity.eamLongId == 0){
                                    sb.append("");
                                }else {
                                    sb.append("[").append(xjWorkItemEntity.eamId.name).append("]");
                                }
                                sb.append("巡检内容“" + xjWorkItemEntity.content + "”需要拍照");
//                                ToastUtils.show(context,  sb.toString());
                                msg = sb.toString();
                            }

                            if(!TextUtils.isEmpty(msg)){

                                if(isCanEndAll) {
                                    WorkItemLocationEvent workItemLocationEvent = new WorkItemLocationEvent(mXJWorkAdapter.getList().indexOf(xjWorkItemEntity));
                                    workItemLocationEvent.setMsg(msg);
                                    EventBus.getDefault().post(workItemLocationEvent);
                                }
                                isCanEndAll = false;
                            }

//                            if(TextUtils.isEmpty(xjWorkItemEntity.concluse) && TextUtils.isEmpty(xjWorkItemEntity.defaultVal)){
//
//                                if(isCanEndAll) {
//                                    EventBus.getDefault().post(new WorkItemLocationEvent(mXJWorkAdapter.getList().indexOf(xjWorkItemEntity)));
//                                }
//                                isCanEndAll = false;
//                            }
                            xjWorkEntities.add(xjWorkItemEntity);
                        },
                        throwable -> {},
                        () -> {

                            if(!isCanEndAll){
                                return;
                            }

                            try {
                                for (XJWorkEntity xjWorkItemEntity:xjWorkEntities) {

                                    if(doFinish(xjWorkItemEntity)){
//                                        EventBus.getDefault().post(new XJWorkRefreshEvent(mXJWorkAdapter.getList().indexOf(xjWorkItemEntity), true));
                                    }
                                }

                                if(xjWorkEntities.size() == mXJAreaEntity.works.size()){
//                                    onLoadSuccess("操作完成");
//                                    mXJAreaEntity.isFinished = true;
//                                    finish();

                                    onLoadSuccessAndExit("操作完成", new OnLoaderFinishListener() {
                                        @Override
                                        public void onLoaderFinished() {
                                            finish();
                                        }
                                    });
                                }
                                else{

                                    refreshWorkList();

//                                    EventBus.getDefault().post(new XJWorkRefreshEvent());
                                }



                            } catch (Exception e) {
                                onLoadFailed("完成操作失败！" + e.getMessage());
                                e.printStackTrace();
                            } finally {

                            }

                        });



    }

    private void refreshWorkList() {

        mDevice = null;
        deviceNames.clear();
        mWorkEntities.clear();
        initWorks();
    }

    /**
     * @param
     * @return
     * @description 一键完成巡检项
     * @author zhangwenshuai1 2018/12/29
     */
    private boolean doFinish(XJWorkEntity xjWorkItemEntity) {
        if (/*!OLXJConstant.MobileWiLinkState.EXEMPTION_STATE.equals(xjWorkItemEntity.linkState) && */!xjWorkItemEntity.isFinished) { //免检项过滤掉，因为在后续的循环中被免检的项没有从当前列表中移除

            xjWorkItemEntity.completeDate =new Date().getTime();
            xjWorkItemEntity.isFinished = true;
            xjWorkItemEntity.taskDetailState =  SystemCodeManager.getInstance().getSystemCodeEntity("PATROL_taskDetailState/checked");
            xjWorkItemEntity.staffId = SupPlantApplication.getAccountInfo().staffId;
            //处理结论自动判定
            if (xjWorkItemEntity.isAutoJudge) {
                xjWorkItemEntity.conclusionName = TextUtils.isEmpty(xjWorkItemEntity.conclusionName) ? realValueMap.get("PATROL_realValue/normal") : xjWorkItemEntity.conclusionName;
                xjWorkItemEntity.conclusionID = TextUtils.isEmpty(xjWorkItemEntity.conclusionID) ? "PATROL_realValue/normal" : xjWorkItemEntity.conclusionID;
                xjWorkItemEntity.realValue = SystemCodeManager.getInstance().getSystemCodeEntity(xjWorkItemEntity.conclusionID);
            }


        }
        return true;
    }


    /**
     * 跳过该设备所有巡检项
     * @param eamId
     */
    @SuppressLint("CheckResult")
    public void skipEam(Long eamId){

        if(eamId == null){
            return;
        }

        List<XJWorkEntity> workItemEntities = new ArrayList<>();

        Flowable.fromIterable(mWorkEntities)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJWorkEntity>() {
                    @Override
                    public void accept(XJWorkEntity xjWorkItemEntity) throws Exception {

                        if(xjWorkItemEntity.isPass && eamId == xjWorkItemEntity.eamLongId)
                            workItemEntities.add(xjWorkItemEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                        if(workItemEntities.size()==0){
                            ToastUtils.show(context, "没有可以跳过的巡检项！");
                        }
                        else{
                            showAllSkipReasonPicker(workItemEntities);
                        }

                    }
                });

    }

    /**
     *@author zhangwenshuai1
     *@date 2018/4/4
     *@description  设备整体跳过原因筛选框
     *
     */
    @SuppressLint("CheckResult")
    private void showAllSkipReasonPicker(List<XJWorkEntity> xjWorkItemEntities){
        if(passReasonMap == null || passReasonMap.size() <= 0){

            return;
        }

        List<String> values = new ArrayList<>();
        values.addAll(passReasonMap.values());

        mSingPicker.list(values).listener((index, item) -> {

            String id = null;

            for(String key : passReasonMap.keySet()){
                String value = passReasonMap.get(key);
                if(value!=null && value.equals(item)){
                    id = key;
                    break;
                }

            }

            String finalId = id;
            Flowable.fromIterable(xjWorkItemEntities)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<XJWorkEntity>() {
                        @Override
                        public void accept(XJWorkEntity xjWorkEntity) throws Exception {
                            xjWorkEntity.passReason = SystemCodeManager.getInstance().getSystemCodeEntity(finalId);
                            xjWorkEntity.isRealPass = true;
                            xjWorkEntity.isFinished = true;
                            xjWorkEntity.taskDetailState = SystemCodeManager.getInstance().getSystemCodeEntity("PATROL_taskDetailState/skip");//跳检
                            xjWorkEntity.completeDate = new Date().getTime();
                            xjWorkEntity.staffId = SupPlantApplication.getAccountInfo().staffId;
                            xjWorkEntity.conclusionID = null;
                            xjWorkEntity.realValue = null;
                            xjWorkEntity.conclusionName = null;
                            xjWorkEntity.concluse = null;
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            mXJAreaEntity.finishNum = xjWorkItemEntities.size();
                            if(xjWorkItemEntities.size() == mXJAreaEntity.works.size()){
                                mXJAreaEntity.isFinished = true;
                                back();
                            }
                            else{

                                refreshWorkList();
                            }
                            ToastUtils.show(context, String.format(getResources().getString(R.string.xj_work_skip_toast), ""+(xjWorkItemEntities.size())));
//                            ((XJWorkActivity)context).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            });


                        }
                    });



        }).show();
    }

    /**
     * @description 多选
     * @author zhangwenshuai1
     * @date 2018/5/2
     *
     */
    private void dialogMoreChoice(XJWorkEntity xjWorkItemEntity, int xjPosition) {

        XJInputTypeEntity xjInputTypeEntity = SupPlantApplication.dao().getXJInputTypeEntityDao().queryBuilder().
                where(XJInputTypeEntityDao.Properties.Id.eq(xjWorkItemEntity.inputStandardId.id)).unique();

        if (xjInputTypeEntity.candidateValue == null || xjInputTypeEntity.candidateValue.isEmpty()){
            SnackbarHelper.showError(rootView,"无结果候选值");
            return;
        }


        String[] items = xjInputTypeEntity.candidateValue == null ? null : xjInputTypeEntity.candidateValue.split(",");  //候选值列表
        List<SheetEntity> list = new ArrayList<>();
        for (String item : items){
            SheetEntity sheetEntity = new SheetEntity();
            sheetEntity.name = item;
            list.add(sheetEntity);
        }

        List<Boolean> checkedList = new ArrayList<>();
        for(String s: items){
            if(xjWorkItemEntity.concluse!=null && xjWorkItemEntity.concluse.contains(s)){
                checkedList.add(true);
            }
            else{
                checkedList.add(false);
            }
        }

        new CustomSheetDialog(context)
                .multiSheet("多选列表",list, checkedList)
                .setOnItemChildViewClickListener((childView, position, action, obj) -> {

                    List<SheetEntity> sheetEntities = GsonUtil.jsonToList(obj.toString(), SheetEntity.class);

                    if (sheetEntities != null && sheetEntities.size() > 0 ){

                        xjWorkItemEntity.concluse = "";
                        for (SheetEntity sheetEntity : sheetEntities){
                            xjWorkItemEntity.concluse += sheetEntity.name + ",";
                        }

                        xjWorkItemEntity.concluse = xjWorkItemEntity.concluse.substring(0,xjWorkItemEntity.concluse.length()-1);

                        mXJWorkAdapter.notifyItemChanged(xjPosition);

                    }
                }).show();

    }


    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAreaUpdate(XJWorkRefreshEvent workRefreshEvent) {


        Flowable.timer(0, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    int position = workRefreshEvent.getPosition();
                    boolean isFinish = workRefreshEvent.isFinish();

                    if(isFinish){
                        mXJWorkAdapter.notifyItemRemoved(position);
                     }
                    else if(workRefreshEvent.getXJWorkEntity()!=null){//来自巡检已完成重录
                        XJWorkEntity workEntity = workRefreshEvent.getXJWorkEntity();
                        for(XJWorkEntity xjWorkEntity : mXJAreaEntity.works){
                            if(workEntity.id.equals(xjWorkEntity.id)){
                                mXJAreaEntity.works.set(mXJAreaEntity.works.indexOf(xjWorkEntity), workEntity);
                            }
                        }

                        needRefresh = true;
                    }
                    else{
                        mXJWorkAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 快速定位未完成巡检项
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocation(WorkItemLocationEvent event) {
        int pos = event.getLocation();
        LogUtil.d("onLocation:"+event.getLocation());

        if(pos == 0){
            return;
        }
        XJWorkEntity xjWorkEntity = mWorkEntities.get(pos);
        if(xjWorkEntity == null){
            return;
        }
        if(!TextUtils.isEmpty(event.getMsg())){
            ToastUtils.show(context, event.getMsg());
        }

//        ToastUtils.show(context, String.format(getResources().getString(R.string.xj_work_unfinish_warning), xjWorkEntity.content));
        contentView.scrollToPosition(pos);
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) contentView.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(pos, 0);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getThermometerVal(ThermometerEvent thermometerEvent){
        LogUtil.i("ThermometerEvent",thermometerEvent.getThermometerVal());
        thermometervalue = thermometerEvent.getThermometerVal().replace("℃","");

        if(mPosition!=-1 && mXJWorkAdapter.getList()!=null && mXJWorkAdapter.getList().size() >= mPosition+1){
            XJWorkEntity xjWorkItemEntity = mXJWorkAdapter.getItem(mPosition);
            xjWorkItemEntity.concluse = thermometervalue;
            mXJWorkAdapter.notifyItemChanged(mPosition);
            thermometervalue = null;
        }

    }

    /**
     * test测温
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InfraredEvent event) {
        if (TextUtils.isEmpty(event.tem))
            return;

        if(mTesto805iDialog!=null) {

            String tem = thermometervalue = event.tem;
            String tag = event.tag;
            LogUtil.d(this.getClass().getSimpleName(),tem + "-" + tag);

            CustomTextView temperatureVal = mTesto805iDialog.getDialog().findViewById(R.id.temperatureVal);
            if (temperatureVal != null)
                temperatureVal.setContent(tem);
        }

    }

}
