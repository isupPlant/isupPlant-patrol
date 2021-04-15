package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.mes.supcon.expert_ewg01p.config.ModuleConfig;
import com.mes.supcon.expert_ewg01p.config.ViberMode;
import com.mes.supcon.expert_ewg01p.controller.ExpertController;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnRefreshListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.aic_vib.controller.AICVibServiceController;
import com.supcon.mes.aic_vib.service.AICVibService;
import com.supcon.mes.av160.controller.AV160Controller;
import com.supcon.mes.mbap.beans.SheetEntity;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomSheetDialog;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.mbap.view.CustomVerticalEditText;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.constant.TemperatureMode;
import com.supcon.mes.middleware.constant.VibMode;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntityDao;
import com.supcon.mes.middleware.model.bean.ObjectEntity;
import com.supcon.mes.middleware.model.bean.PopupWindowEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.ui.view.CustomPopupWindow;
import com.supcon.mes.middleware.util.AnimationUtil;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SBTUtil;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.middleware.util.XJTaskCacheUtil;
import com.supcon.mes.module_xj.IntentRouter;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.controller.XJCameraController;
import com.supcon.mes.module_xj.model.api.DeviceDCSParamQueryAPI;
import com.supcon.mes.module_xj.model.api.XJTaskSubmitAPI;
import com.supcon.mes.module_xj.model.bean.DeviceDCSEntity;
import com.supcon.mes.module_xj.model.contract.DeviceDCSParamQueryContract;
import com.supcon.mes.module_xj.model.contract.XJTaskSubmitContract;
import com.supcon.mes.module_xj.model.event.WorkItemLocationEvent;
import com.supcon.mes.module_xj.model.event.XJAreaRefreshEvent;
import com.supcon.mes.module_xj.model.event.XJWorkRefreshEvent;
import com.supcon.mes.module_xj.presenter.DeviceDCSParamQueryPresenter;
import com.supcon.mes.module_xj.ui.adapter.XJWorkAdapter;
import com.supcon.mes.module_xj.ui.dialog.XJAbnormalSelectDialog;
import com.supcon.mes.module_xj.util.XLinearLayoutManager;
import com.supcon.mes.mogu_viber.controller.MGViberController;
import com.supcon.mes.sb2.model.event.ThermometerEvent;
import com.supcon.mes.testo_805i.controller.InfraredEvent;
import com.supcon.mes.testo_805i.controller.TestoController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/1/10
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_WORK_ITEM)
@Controller(value = {TestoController.class, SystemCodeJsonController.class, XJCameraController.class})
@Presenter({DeviceDCSParamQueryPresenter.class})
@SystemCode(entityCodes = {
//        Constant.SystemCode.PATROL_editType,
//        Constant.SystemCode.PATROL_valueType,
        Constant.SystemCode.PATROL_passReason,
        Constant.SystemCode.PATROL_realValue,
        Constant.SystemCode.PATROL_abnormalReason

})
public class XJWorkActivity extends BaseRefreshRecyclerActivity<XJTaskWorkEntity> implements
        XJTaskSubmitContract.View, DeviceDCSParamQueryContract.View {

    @BindByTag("titleTextMiddle")
    TextView titleTextMiddle;

    @BindByTag("leftBtn")
    ImageButton leftBtn;

    @BindByTag("rightBtn")
    ImageButton rightBtn;

    @BindByTag("rightBtn_sec")
    ImageButton rightBtn_sec;

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("eamSpinner")
    Spinner eamSpinner;

    XJWorkAdapter mXJWorkAdapter;

    ExpertController expertViberController;
    XJTaskAreaEntity mXJAreaEntity;
    ObjectEntity mDevice = null;
    List<String> deviceNames = new ArrayList<>();
    List<String> deviceNumber = new ArrayList<>();
    boolean isOneKeyJump = false;
    boolean isOneKeyOver = false;
    boolean isCanEndAll = true;
    private Map<String, String> passReasonMap, realValueMap;
    private List<PopupWindowEntity> mPopupWindowEntityList=new ArrayList<>();
    private CustomPopupWindow mCustomPopupWindow;
    private SinglePickController<String> mSingPicker;
    private String thermometervalue = ""; // 全局测温值
    private MGViberController mMGViberController;
    private CustomDialog mMGViberDialog;

    private AICVibServiceController mAICVibController;
    private AV160Controller mAV160Controller;
    private CustomDialog mTesto805iDialog;
    private int mPosition;
    private List<XJTaskWorkEntity> mWorkEntities = new ArrayList<>();
    private boolean needRefresh = false;//重录之后，需要刷新列表
    private TextView tempTv;
    private XJTaskEntity mXJTaskEntity;
    private ImageView viberStatusIv;
    private boolean isAll = true;
    private List<DeviceEntity> deviceEntityList = new ArrayList<>();

    private String exceptionIdsStr;
    private List<String> exceptionIds = new ArrayList<>();
    private Map<String, String> abnormalReasonMap;     //异常原因

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_work;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
        String xjAreaEntityStr = getIntent().getStringExtra(Constant.IntentKey.XJ_AREA_ENTITY_STR);
        exceptionIdsStr = getIntent().getStringExtra(Constant.IntentKey.XJ_AREA_EXCEPTION_IDS);
        //Todo: swap begin
        if(!TextUtils.isEmpty(exceptionIdsStr))
            exceptionIds = Arrays.asList(exceptionIdsStr.split(","));
        //Todo: swap end

        String taskNo = getIntent().getStringExtra(Constant.IntentKey.XJ_TASK_NO_STR);

        /*if (xjAreaEntityStr != null) {
            mXJAreaEntity = GsonUtil.gsonToBean(xjAreaEntityStr, XJAreaEntity.class);
        }*/

        if (!TextUtils.isEmpty(taskNo)) {

            String taskStr = XJTaskCacheUtil.getString(taskNo);
            mXJTaskEntity = GsonUtil.gsonToBean(taskStr, XJTaskEntity.class);
        }

        if (xjAreaEntityStr != null && mXJTaskEntity != null) {
            for(XJTaskAreaEntity areaEntity: mXJTaskEntity.areas){
                if(xjAreaEntityStr.equals(String.valueOf(areaEntity.id))){
                    mXJAreaEntity = areaEntity;
                    break;
                }
            }
            mXJAreaEntity.works= XJTaskCacheUtil.getTaskWork(taskNo,Long.valueOf(xjAreaEntityStr));
        }

        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(true);

        int tempMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0);
        int vibMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.VIB_MODE, 0);

        if (tempMode == TemperatureMode.EXPERT.getCode() || vibMode == VibMode.EXPERT.getCode()) {
            if (expertViberController == null) {
                View contentView = ExpertController.createContentView(context);
                contentView.findViewById(R.id.viberFinishBtn).setOnClickListener(v -> expertViberController.hide());
                expertViberController = new ExpertController(contentView, true);
                expertViberController.onInit();
//                expertViberController.initView();
//                expertViberController.initListener();
//                expertViberController.initData();
                registerController(ExpertController.class.getSimpleName(), expertViberController);
            } else {
                expertViberController.initData();
            }
        }
        if (tempMode == TemperatureMode.AIC.getCode() || vibMode == VibMode.AIC.getCode()) {

//            if (mAICVibController == null) {
            AICVibService.start(context);
//                mAICVibController = new AICVibServiceController(AICVibServiceController.getLayoutView(context), true);
//                mAICVibController.onInit();
//                registerController(AICVibController.class.getSimpleName(), mAICVibController);
//            }
        }

        if (tempMode == TemperatureMode.SBT.getCode() && SBTUtil.isSupportTemp()) {
            mXJWorkAdapter.setSb2ThermometerHelper();
        }
        if (mXJAreaEntity!=null&&!TextUtils.isEmpty(mXJAreaEntity.name)){
            titleTextMiddle.setText(mXJAreaEntity.name);
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (expertViberController != null) expertViberController.onStop();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        if (mMGViberController != null) {
            mMGViberController.onDestroy();
        }

        if (mAICVibController != null) {
            mAICVibController.onDestroy();
        }

        if (mAV160Controller != null) {
            mAV160Controller.onDestroy();
        }
//        if (expertViberController != null)
//            expertViberController.onDestroy();

        int finishNum = 0;
        for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
            if (xjWorkEntity.isFinished) {
                finishNum++;
            }
        }

        mXJAreaEntity.finishNum = finishNum;
        mXJAreaEntity.isFinished = mXJAreaEntity.finishNum == mXJAreaEntity.works.size();
        XJTaskCacheUtil.insertTasksArea(mXJAreaEntity);
        EventBus.getDefault().post(new XJAreaRefreshEvent());


/*        int finishNum = 0;
        for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
            if (xjWorkEntity.isFinished) {
                finishNum++;
            }
        }

        mXJAreaEntity.finishNum = finishNum;
        mXJAreaEntity.isFinished = mXJAreaEntity.finishNum == mXJAreaEntity.works.size();
//        SupPlantApplication.dao().getXJTaskAreaEntityDao().update(mXJAreaEntity);
        XJTaskCacheUtil.insertTasksArea(mXJAreaEntity);*/

    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleTextMiddle.setVisibility(View.VISIBLE);
//        titleTextMiddle.setText(getString(R.string.xj_work));

        rightBtn.setImageResource(R.drawable.sl_xj_work_top_finish);
        rightBtn_sec.setImageResource(R.drawable.sl_top_more);
        LinearLayoutManager mLayoutManager=new XLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contentView.setLayoutManager(mLayoutManager);
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(1, context)));
        contentView.setAdapter(mXJWorkAdapter);

        mSingPicker = new SinglePickController<>(this);
        mSingPicker.setCanceledOnTouchOutside(true);


    }



    private void initPopupWindowData() {
        createPopupWindowEntityData(context.getString(R.string.xj_work_over), R.drawable.ic_xj_work_finish,0);
        createPopupWindowEntityData(context.getString(R.string.xj_work_jump), R.drawable.ic_xj_work_skip,1);
        createPopupWindowEntityData(context.getString(R.string.input_defect), R.drawable.ic_input_defect,2);
        mCustomPopupWindow = new CustomPopupWindow(context, mPopupWindowEntityList);
    }


    public void createPopupWindowEntityData(String name,int iconId,int tag){
        PopupWindowEntity popupWindowEntity=new PopupWindowEntity();
        popupWindowEntity.setText(name);
        popupWindowEntity.setIconId(iconId);
        popupWindowEntity.setTag(tag);
        mPopupWindowEntityList.add(popupWindowEntity);
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
                .subscribe(o -> onBackPressed());


        RxView.clicks(rightBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.IntentKey.XJ_AREA_ENTITY_STR,  mXJAreaEntity.id+"");
                    bundle.putString(Constant.IntentKey.XJ_TASK_NO_STR, mXJTaskEntity.tableNo);

//                    BundleSaveUtil.instance
//                            .put(Constant.IntentKey.XJ_AREA_ENTITY_STR, mXJAreaEntity.toString())
//                            .put(Constant.IntentKey.XJ_TASK_ENTITY_STR, mXJTaskEntity.toString());
                    IntentRouter.go(context, Constant.Router.XJ_WORK_ITEM_VIEW, bundle);
                });

        RxView.clicks(rightBtn_sec)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    mCustomPopupWindow.setOnItemClick((parent, view, position, id) -> dealPosition(position));
                    mCustomPopupWindow.showPopupWindow(rightBtn_sec);
                });

        mXJWorkAdapter.setOnItemChildViewClickListener((childView, position, action, obj) -> {
            XJTaskWorkEntity xjWorkEntity = (XJTaskWorkEntity) obj;
            String tag = (String) childView.getTag();
            mPosition = position;
            switch (tag) {

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

                case "itemAbnormalReason":
                    showPopUp(xjWorkEntity);
                    break;

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

    private void dealPosition(int position) {
        if (mPopupWindowEntityList == null) {
            return;
        }
        PopupWindowEntity popupWindowEntity = mPopupWindowEntityList.get(position);
        switch (popupWindowEntity.getTag()) {
            case 0:
                mCustomPopupWindow.dismiss();
                showAllFinishDialog();
                break;

            case 1:
                mCustomPopupWindow.dismiss();
                showAllJumpDialog();
                break;
            case 2:
                Bundle bundle = new Bundle();
                StringBuilder idList = new StringBuilder();
                if (deviceEntityList!=null&&deviceEntityList.size()>0){
                    for (DeviceEntity deviceEntity : deviceEntityList) {
                        idList.append(deviceEntity.code);
                        idList.append(",");
                    }
                    if(!TextUtils.isEmpty(idList)){
                        idList.replace(idList.length() - 1, idList.length(), "");
                        bundle.putString(Constant.IntentKey.XJ_AREA_EAMLISTS,  idList.toString());
                    }
                }


                bundle.putString(Constant.IntentKey.XJ_AREA_CODE,  mXJAreaEntity.code);
                bundle.putString(Constant.IntentKey.XJ_AREA_NAME,  mXJAreaEntity.name);
                bundle.putString(Constant.IntentKey.XJ_TASK_TABLENO,  mXJTaskEntity.tableNo);
                IntentRouter.go(context, Constant.AppCode.DEFECT_MANAGEMENT_ADD, bundle);

//                if (!TextUtils.isEmpty(获取到的设备idListString)) {
//                    List<DeviceEntity>   deviceEntityList = new ArrayList<>();
//                    String[] eamIdList = 获取到的设备idListString.split(",");
//                    for (String  eamId : eamIdList) {
//                        //根据设备eamId获取CommonDeviceEntity
//                        try {
//                            DeviceEntity commonDeviceEntity = SupPlantApplication.dao().getDeviceEntityDao().queryBuilder()
//                                    .where(DeviceEntityDao.Properties.Id.eq(eamId)).unique();
//                            if (commonDeviceEntity != null) {
//                                if (commonDeviceEntity.state.id.equals("BaseSet_eamState/inUse")) {
//                                    if (!deviceEntityList.contains(commonDeviceEntity)) {
//                                        deviceEntityList.add(commonDeviceEntity);
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }

                break;
            default:
        }
    }

    private void showFinishDialog() {
        new CustomDialog(context)
                .twoButtonAlertDialog(checkAreaFinishState() ? getString(R.string.xj_task_finish_warning2) : getString(R.string.xj_task_finish_warning1))
                .bindClickListener(R.id.grayBtn, v -> {
                }, true)
                .bindClickListener(R.id.redBtn, v -> {
                    //将本区域数据变成已完成
                    for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
                        xjWorkEntity.isFinished = true;
                    }
                    //拼接单个区域上传数据
                    mXJTaskEntity.areas.clear();
                    mXJTaskEntity.areas.add(mXJAreaEntity);
                    List<XJTaskEntity> xjTaskEntityList = new ArrayList<>();
                    xjTaskEntityList.add(mXJTaskEntity);
                    presenterRouter.create(XJTaskSubmitAPI.class).uploadFile(xjTaskEntityList, true);
                    onLoading(getString(R.string.xj_task_uploading));
                }, true)
                .show();
    }

    private boolean checkAreaFinishState() {
        int finishNum = 0;
        for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
            if (xjWorkEntity.isFinished) {
                finishNum++;
            }
        }
        mXJAreaEntity.finishNum = finishNum;
        if (mXJAreaEntity.finishNum == mXJAreaEntity.works.size()) {
            mXJAreaEntity.isFinished = true;
            return true;
        } else {
            mXJAreaEntity.isFinished = false;
            return false;
        }
    }

    private void showAllJumpDialog() {
        new CustomDialog(context)
                .twoButtonAlertDialog(getString(R.string.xj_work_jump_all))
                .bindView(R.id.grayBtn, getString(R.string.no))
                .bindView(R.id.redBtn, getString(R.string.yes))
                .bindClickListener(R.id.grayBtn, null, true)
                .bindClickListener(R.id.redBtn, v -> {
                    if (mWorkEntities != null && mWorkEntities.size() > 0) {
                        isOneKeyJump = true;
                        XJWorkActivity.this.skipEam(0L);
                    }
                }, true)
                .show();

    }

    public void showAllFinishDialog() {
        new CustomDialog(context)
                .twoButtonAlertDialog(getString(R.string.xj_work_over_all))
                .bindView(R.id.grayBtn, getString(R.string.no))
                .bindView(R.id.redBtn, getString(R.string.yes))
                .bindClickListener(R.id.grayBtn, null, true)
                .bindClickListener(R.id.redBtn, v -> {
                    if (mWorkEntities != null && mWorkEntities.size() > 0) {
                        isOneKeyOver = true;
                        XJWorkActivity.this.doAllFinish(0);
                    }

                }, true)
                .show();
    }

    @SuppressLint("CheckResult")
    private void showEditDialog(XJTaskWorkEntity xjWorkEntity, int position) {


        CustomDialog remarkDialog = new CustomDialog(context)
                .layout(R.layout.v_xj_remark_dialog)
                .bindClickListener(R.id.okBtn, v -> {
                }, true);

        CustomVerticalEditText iCustomView = remarkDialog.getDialog().findViewById(R.id.remarkInput);
        RxTextView.textChanges(iCustomView.editText())
                .skipInitialValue()
                .subscribe(charSequence -> {
                    xjWorkEntity.remark = charSequence.toString();
                    xjWorkEntity.realRemark = charSequence.toString();
                    mXJWorkAdapter.notifyDataSetChanged();
                });
        if (xjWorkEntity.remark != null) {
            iCustomView.setContent(xjWorkEntity.remark);
        }
        remarkDialog.show();
    }

    @Override
    protected IListAdapter<XJTaskWorkEntity> createAdapter() {
        mXJWorkAdapter = new XJWorkAdapter(context);
        return mXJWorkAdapter;
    }

    @Override
    protected void initData() {
        super.initData();
        mXJWorkAdapter.setConclusions(getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_realValue));

        passReasonMap = getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_passReason);
        realValueMap = getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_realValue);
        abnormalReasonMap = getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_abnormalReason);
        checkDeviceState();
        refreshListController.setOnRefreshListener(() -> {
            if (mXJAreaEntity != null && mXJAreaEntity.works != null) {
                refreshWorkList();
            }
        });
    }

    public void checkDeviceState(){
        if (mXJAreaEntity!=null&& mXJAreaEntity.works!=null){
            deviceEntityList=new ArrayList<>();
            for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
                if (xjWorkEntity.eamId != null || xjWorkEntity.eamId.id != null) {
                    //根据设备eamId获取CommonDeviceEntity
                    try {
                        DeviceEntity commonDeviceEntity = SupPlantApplication.dao().getDeviceEntityDao().queryBuilder()
                                .where(DeviceEntityDao.Properties.Code.eq( xjWorkEntity.eamId.code)).unique();
                        if (commonDeviceEntity!=null){
                            if (!deviceEntityList.contains(commonDeviceEntity)) {
                                deviceEntityList.add(commonDeviceEntity);
                            }
                        }
                    }catch (Exception e){

                    }
                }
            }
        }
        initPopupWindowData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefaultAnimation();
        if (needRefresh) {
            refreshWorkList();
            needRefresh = false;
        }
    }

    private void setDefaultAnimation() {
        AnimationUtil.setActivityDefaultAnimation(this);
    }

    @SuppressLint("CheckResult")
    private void initWorks() {
        List<XJTaskWorkEntity> noEamWorks = new ArrayList<>();
        //遍历当前巡检区域下的所有巡检项,过滤掉所有未启动的巡检项以及例外的巡检内容
        Flowable.fromIterable(mXJAreaEntity.works)
                //Todo: 大金swap from
//                .filter(xjWorkEntity -> !xjWorkEntity.isFinished)
                //Todo: 大金swap to begin
                .filter(xjWorkEntity -> !xjWorkEntity.isFinished && xjWorkEntity.isRun)
                .filter(xjWorkEntity -> !exceptionIds.contains(String.valueOf(xjWorkEntity.id)))
                //Todo: 大金swap to end
                .subscribe(xjWorkEntity -> {
                    if (xjWorkEntity.realRemark != null) {
                        xjWorkEntity.remark = xjWorkEntity.realRemark;
                    } else {
                        xjWorkEntity.remark = "";
                    }
                    if (xjWorkEntity.eamId == null || xjWorkEntity.eamId.id == null) {
                        noEamWorks.add(xjWorkEntity);
                    } else {
                        if (mDevice == null || mDevice.id != xjWorkEntity.eamLongId) {
                            mDevice = xjWorkEntity.eamId;
                            XJTaskWorkEntity workEntity = new XJTaskWorkEntity();
                            workEntity.eamId = mDevice;
                            workEntity.eamLongId = mDevice.id;
                            workEntity.isEamView = true;
                            workEntity.eamName = mDevice.name;
                            workEntity.eamNum = deviceNames.size() + 1;
                            for (DeviceEntity deviceEntity:deviceEntityList){
                                if (deviceEntity.id==workEntity.eamLongId) {
                                    if (deviceEntity.areaNum != null) {
                                        workEntity.areaNum = deviceEntity.areaNum;
                                    }
                                }
                            }
                            mWorkEntities.add(workEntity);

                            deviceNames.add(workEntity.eamId.name);
                        }
                        mWorkEntities.add(xjWorkEntity);
                    }


                }, throwable -> {

                }, () -> {
                    if (noEamWorks.size() != 0) {
                        XJTaskWorkEntity workEntity = new XJTaskWorkEntity();
                        workEntity.isEamView = true;
                        workEntity.eamNum = deviceNames.size() + 1;
                        if (deviceNames.size() == 0) {
                            workEntity.eamName = "区域巡检";
                        } else {
                            workEntity.eamName = "无设备巡检";
                        }
                        deviceNames.add(workEntity.eamName);
                        mWorkEntities.add(workEntity);
                        mWorkEntities.addAll(noEamWorks);
                    }

                    if (deviceNames.size() == 0) {
                        ((ViewGroup) eamSpinner.getParent()).setVisibility(View.GONE);
                        refreshListController.refreshComplete(mWorkEntities);
                    } else if (deviceNames.size() == 1) {
                        ((ViewGroup) eamSpinner.getParent()).setVisibility(View.GONE);
                        refreshListController.refreshComplete(mWorkEntities);
                    } else {
                        deviceNames.add(0, getString(R.string.xj_work_eam_all));
                        ((ViewGroup) eamSpinner.getParent()).setVisibility(View.VISIBLE);
                        ArrayAdapter<String> eamSpinnerAdapter = new ArrayAdapter<>(context, R.layout.ly_spinner_item_dark, deviceNames);  //创建一个数组适配器
                        eamSpinnerAdapter.setDropDownViewResource(R.layout.ly_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
                        eamSpinner.setAdapter(eamSpinnerAdapter);
                        eamSpinner.setSelection(isAll ? 0 : 1);
                    }
                });

        for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
            if (xjWorkEntity.itemNumber != null && !TextUtils.isEmpty(xjWorkEntity.itemNumber)) {
                deviceNumber.add(xjWorkEntity.itemNumber.trim());
            }
        }
        if (deviceNumber.size() > 0) {
            presenterRouter.create(DeviceDCSParamQueryAPI.class).getDeviceDCSParams(deviceNumber);
        }
    }

    @SuppressLint("CheckResult")
    private void showWorks(String deviceName) {
//        LogUtil.e("ciruy", "showWorks:"+deviceName);
        isAll = getString(R.string.xj_work_eam_all).equals(deviceName);
        if(isAll){
            refreshListController.refreshComplete(mWorkEntities);
            return;
        }
        List<XJTaskWorkEntity> workEntities = new ArrayList<>();
        Flowable.fromIterable(mWorkEntities)
                .subscribeOn(Schedulers.newThread())
                .filter(xjWorkEntity -> xjWorkEntity.eamId != null && xjWorkEntity.eamId.name != null && xjWorkEntity.eamId.name.equals(deviceName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workEntities::add, throwable -> {
                }, () -> refreshListController.refreshComplete(workEntities));
    }

    private void showVibView(XJTaskWorkEntity xjWorkEntity) {
        int vibMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.VIB_MODE, 0);
        if (vibMode == VibMode.AIC.getCode()) {
            showAICDialog(mPosition, xjWorkEntity, false);
        } else if (vibMode == VibMode.AV160D.getCode()) {
            showAV160Dialog(mPosition, xjWorkEntity);
        } else if (vibMode == VibMode.SU100.getCode()) {
            showMGViberDialog(mPosition, xjWorkEntity);
        } else if (SBTUtil.isSupportTemp()) {

        } else if (vibMode == VibMode.EXPERT.getCode()) {
            SharedPreferencesUtils.setParam(context, ModuleConfig.IS_VIBER, true);
            expertViberController.initView();
            SharedPreferencesUtils.setParam(context, ModuleConfig.CURRENT_MODE,
                    ViberMode.DISTANCE.name());
            expertViberController.show();
            expertViberController.setOnFinishListener(result -> {

                xjWorkEntity.concluse = result.second;
                mXJWorkAdapter.notifyItemChanged(mPosition);
            });
            expertViberController.run(result -> {
                if (result.first != null) return;
            });
        }
    }

    private void showAV160Dialog(int position, XJTaskWorkEntity xjWorkItemEntity) {


        if (mAV160Controller == null) {
            mAV160Controller = new AV160Controller(AV160Controller.getLayoutView(context), true);
            mAV160Controller.onInit();
            mAV160Controller.initView();
            mAV160Controller.initListener();
            mAV160Controller.initData();
            registerController(AV160Controller.class.getSimpleName(), mAV160Controller);
        } else {
            mAV160Controller.reset();
        }
        mAV160Controller.start();
        mAV160Controller.setOnAV160DataSelectListener(data -> {
            mAV160Controller.stop();
            if (TextUtils.isEmpty(data)) {
                return;
            }
            xjWorkItemEntity.concluse = data;
            mXJWorkAdapter.notifyItemChanged(position);
        });

    }

    private void showMGViberDialog(int position, XJTaskWorkEntity xjWorkItemEntity) {
        if (mMGViberDialog == null) {
            mMGViberDialog = new CustomDialog(context)
                    .layout(R.layout.ly_viber_dialog)
                    .bindClickListener(R.id.viberFinishBtn, null, true);
            mMGViberController = new MGViberController(mMGViberDialog.getDialog().getWindow().getDecorView());
            mMGViberController.onInit();
            mMGViberController.initView();
            mMGViberController.initListener();
            mMGViberController.initData();
        } else {
            mMGViberController.reset();
        }

        mMGViberDialog.bindClickListener(R.id.viberFinishBtn, v -> {
            mMGViberController.stopTest();
            String data = mMGViberController.getData();
            if (TextUtils.isEmpty(data)) {
                return;
            }
            xjWorkItemEntity.concluse = data;
            mXJWorkAdapter.notifyItemChanged(position);
        }, true).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (expertViberController != null) expertViberController.onStop();
    }
    private void showTempView(XJTaskWorkEntity xjWorkEntity) {
        int tempMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0);
        if (tempMode == TemperatureMode.AIC.getCode()) {
            showAICDialog(mPosition, xjWorkEntity, true);
        } else if (tempMode == TemperatureMode.TESTO.getCode()) {
            getController(TestoController.class).startService();
            showTesto805iDialog(mPosition, xjWorkEntity);
        } else if (tempMode == TemperatureMode.SBT.getCode()) {

        } else if (SBTUtil.isSupportTemp()) {

        } else if (tempMode == TemperatureMode.EXPERT.getCode()) {
            SharedPreferencesUtils.setParam(context, ModuleConfig.IS_VIBER, false);
            expertViberController.initView();
//            if (expertViberController == null) {
//                expertViberController = new ExpertController(ExpertController.createContentView(context), true);
////        mMGViberController.setTemperatureNeed(true);
//                expertViberController.onInit();
//                expertViberController.setOnDataSelectListener(data ->
//                        LogUtil.d("onDataSelect:" + data));
//            }
            expertViberController.setOnFinishListener(result -> {
                xjWorkEntity.concluse = result.second;
                mXJWorkAdapter.notifyItemChanged(mPosition);
            });
            expertViberController.run(result -> {
                if (result.first != null) return;
            });
            expertViberController.show();
        }

    }

    private void showTesto805iDialog(int position, XJTaskWorkEntity xjWorkItemEntity) {
        thermometervalue = null;
        mTesto805iDialog = new CustomDialog(context);
        mTesto805iDialog.layout(R.layout.v_temp_805i_dialog)
                .bindClickListener(R.id.startBtn, v -> {
                    CustomTextView temperatureVal = mTesto805iDialog.getDialog().findViewById(R.id.temperatureVal);
                    temperatureVal.setContent(context.getResources().getString(R.string.xj_patrol_open_device_hint));
                    getController(TestoController.class).startService();
                }, false)
                .bindClickListener(R.id.okBtn, v -> {
                    if (thermometervalue != null) {
                        xjWorkItemEntity.concluse = thermometervalue;
                        mXJWorkAdapter.notifyItemChanged(position);
                        thermometervalue = null;
                    }
                }, true)
                .show();
        tempTv = mTesto805iDialog.getDialog().findViewById(R.id.viberStatus);
        viberStatusIv = mTesto805iDialog.getDialog().findViewById(R.id.viberStatusIv);
        ((ImageView) mTesto805iDialog.getDialog().findViewById(R.id.viberStatusIv)).setImageResource(R.drawable.ic_device_connect2);
        tempTv.setText(context.getResources().getString(R.string.xjj_patrol_service_start));

    }

    private void showAICDialog(int position, XJTaskWorkEntity xjWorkItemEntity, boolean isTempTest) {


        if(mAICVibController==null) {

            mAICVibController = new AICVibServiceController(AICVibServiceController.getLayoutView(context), true);
            mAICVibController.onInit();
            mAICVibController.initView();
            mAICVibController.initListener();
            mAICVibController.initData();
        }
        else{
            mAICVibController.reset();
        }

        mAICVibController.setOnDataSelectListener(data -> {
            LogUtil.d("onDataSelect:" + data);
            if (TextUtils.isEmpty(data)) {
                return;
            }
            xjWorkItemEntity.concluse = data;
            mXJWorkAdapter.notifyItemChanged(position);
        });

        mAICVibController.show(isTempTest);
    }

    @SuppressLint("CheckResult")
    public void showAllFinishDialog(long eamId) {
        new CustomDialog(context)
                .twoButtonAlertDialog(eamId == 0 ? getString(R.string.xj_work_finish_warning2) : getString(R.string.xj_work_finish_warning1))
                .bindView(R.id.grayBtn, getString(R.string.no))
                .bindView(R.id.redBtn, getString(R.string.yes))
                .bindClickListener(R.id.grayBtn, null, true)
                .bindClickListener(R.id.redBtn, v -> doAllFinish(eamId), true)
                .show();
    }

    @SuppressLint("CheckResult")
    private void doAllFinish(long eamId) {
        if (mWorkEntities.size() == 0) {
            ToastUtils.show(context, getString(R.string.xj_work_finish_warning3));
            return;
        }
        List<XJTaskWorkEntity> xjWorkEntities = new ArrayList<>();
        isCanEndAll = true;
        Flowable.fromIterable(mWorkEntities)
                .filter(xjWorkItemEntity -> {
                    if (isOneKeyOver) {
                        return !xjWorkItemEntity.isEamView && !xjWorkItemEntity.isFinished;
                    } else {
                        return !xjWorkItemEntity.isEamView && (eamId == xjWorkItemEntity.eamLongId) && !xjWorkItemEntity.isFinished;
                    }
                })
                .subscribe(xjWorkItemEntity -> {
                            String msg = null;
                            xjWorkItemEntity.concluse = TextUtils.isEmpty(xjWorkItemEntity.concluse) ? xjWorkItemEntity.defaultVal : xjWorkItemEntity.concluse; //若列表无滚动直接一键完成，默认值不会回填到结果
                            if (TextUtils.isEmpty(xjWorkItemEntity.concluse)) {
                                StringBuilder sb = new StringBuilder();
                                if (xjWorkItemEntity.eamLongId == 0) {
                                    sb.append("");
                                } else {
                                    sb.append("[").append(xjWorkItemEntity.eamId.name).append("]");
                                }
                                sb.append(context.getResources().getString(R.string.xj_patrol_xj_content)).append(xjWorkItemEntity.content).append(context.getResources().getString(R.string.xj_patrol_input_result));
                                msg = sb.toString();
                            } else if (xjWorkItemEntity.isPhone && !xjWorkItemEntity.isRealPhoto) {
                                StringBuilder sb = new StringBuilder();
                                if (xjWorkItemEntity.eamLongId == 0) {
                                    sb.append("");
                                } else {
                                    sb.append("[").append(xjWorkItemEntity.eamId.name).append("]");
                                }
                                sb.append(context.getResources().getString(R.string.xj_patrol_xj_content)).append(xjWorkItemEntity.content).append(context.getResources().getString(R.string.xj_patrol_take_photo));
                                msg = sb.toString();
                            }else if(xjWorkItemEntity.conclusionID.equals("PATROL_realValue/abnormal")&&xjWorkItemEntity.abnormalReason==null){
                                StringBuilder sb = new StringBuilder();
                                if (xjWorkItemEntity.eamLongId == 0) {
                                    sb.append("");
                                } else {
                                    sb.append("[").append(xjWorkItemEntity.eamId.name).append("]");
                                }
                                sb.append(context.getResources().getString(R.string.xj_patrol_xj_content)).append(xjWorkItemEntity.content).append(context.getResources().getString(R.string.xj_patrol_input_abnormal_reason));
                                msg = sb.toString();
                            }
                            if (!TextUtils.isEmpty(msg)) {
                                if (isCanEndAll) {
                                    WorkItemLocationEvent workItemLocationEvent = new WorkItemLocationEvent(mXJWorkAdapter.getList().indexOf(xjWorkItemEntity));
                                    workItemLocationEvent.setMsg(msg);
                                    EventBus.getDefault().post(workItemLocationEvent);
                                }
                                isCanEndAll = false;
                            }
                            xjWorkEntities.add(xjWorkItemEntity);
                        },
                        throwable -> {
                        },
                        () -> {
                            if (!isCanEndAll) {
                                return;
                            }
                            try {
                                for (XJTaskWorkEntity xjWorkItemEntity : xjWorkEntities) {
                                    doFinish(xjWorkItemEntity);
                                }
                                mXJAreaEntity.finishNum += xjWorkEntities.size();
                                XJTaskCacheUtil.putWorkAsync(mXJTaskEntity.tableNo, mXJAreaEntity.id, mXJAreaEntity.works, new XJTaskCacheUtil.Callback() {
                                    @Override
                                    public void apply() {
                                        LogUtil.d("保存巡检项成功");
                                    }
                                });
                                mXJAreaEntity.isFinished = mXJAreaEntity.finishNum == mXJAreaEntity.works.size();
                                XJTaskCacheUtil.insertTasksArea(mXJAreaEntity);
                                if (xjWorkEntities.size() == mXJAreaEntity.works.size()) {
                                    onLoadSuccessAndExit(context.getResources().getString(R.string.xj_patrol_over), this::finish);
                                } else {

                                    refreshWorkList();
                                }
                            } catch (Exception e) {
                                onLoadFailed(context.getResources().getString(R.string.xj_patrol_operater_fail) + e.getMessage());
                                e.printStackTrace();
                            }
                            isOneKeyOver = false;
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
    private boolean doFinish(XJTaskWorkEntity xjWorkItemEntity) {
        if (/*!OLXJConstant.MobileWiLinkState.EXEMPTION_STATE.equals(xjWorkItemEntity.linkState) && */!xjWorkItemEntity.isFinished) { //免检项过滤掉，因为在后续的循环中被免检的项没有从当前列表中移除

            xjWorkItemEntity.completeDate = new Date().getTime();
            xjWorkItemEntity.isFinished = true;
            xjWorkItemEntity.taskDetailState = SystemCodeManager.getInstance().getSystemCodeEntity("PATROL_taskDetailState/checked");
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
     *
     * @param eamId
     */
    @SuppressLint("CheckResult")
    public void skipEam(Long eamId) {

        if (eamId == null) {
            return;
        }

        List<XJTaskWorkEntity> workItemEntities = new ArrayList<>();

        Flowable.fromIterable(mWorkEntities)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(xjWorkItemEntity -> {
                    if (isOneKeyJump) {
                        if (xjWorkItemEntity.isPass) {
                            workItemEntities.add(xjWorkItemEntity);
                        }
                    } else {
                        if (xjWorkItemEntity.isPass && eamId == xjWorkItemEntity.eamLongId) {
                            workItemEntities.add(xjWorkItemEntity);
                        }
                    }
                }, throwable -> {

                }, () -> {

                    if (workItemEntities.size() == 0) {
                        ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_jump_xj));
                    } else {
                        showAllSkipReasonPicker(workItemEntities);
                    }
                    isOneKeyJump = false;
                });

    }

    /**
     * @author zhangwenshuai1
     * @date 2018/4/4
     * @description 设备整体跳过原因筛选框
     */
    @SuppressLint("CheckResult")
    private void showAllSkipReasonPicker(List<XJTaskWorkEntity> xjWorkItemEntities) {
        if (passReasonMap == null || passReasonMap.size() <= 0) {

            return;
        }

        List<String> values = new ArrayList<>();
        values.addAll(passReasonMap.values());

        mSingPicker.list(values).listener((index, item) -> {

            String id = null;

            for (String key : passReasonMap.keySet()) {
                String value = passReasonMap.get(key);
                if (value != null && value.equals(item)) {
                    id = key;
                    break;
                }

            }

            String finalId = id;
            Flowable.fromIterable(xjWorkItemEntities)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(xjWorkEntity -> {
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
                    }, throwable -> {

                    }, () -> {
                        mXJAreaEntity.finishNum += xjWorkItemEntities.size();
                        XJTaskCacheUtil.putWorkAsync(mXJTaskEntity.tableNo, mXJAreaEntity.id, mXJAreaEntity.works, new XJTaskCacheUtil.Callback() {
                            @Override
                            public void apply() {
                                LogUtil.d("保存巡检项成功");
                            }
                        });
                        mXJAreaEntity.isFinished = mXJAreaEntity.finishNum == mXJAreaEntity.works.size();
                        XJTaskCacheUtil.insertTasksArea(mXJAreaEntity);
                        if (xjWorkItemEntities.size() == mXJAreaEntity.works.size()) {
                            mXJAreaEntity.isFinished = true;
                            back();
                        } else {
                            refreshWorkList();
                        }
                        ToastUtils.show(context, String.format(getResources().getString(R.string.xj_work_skip_toast), "" + (xjWorkItemEntities.size())));
//                            ((XJWorkActivity)context).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            });


                    });


        }).show();
    }

    /**
     * @description 多选
     * @author zhangwenshuai1
     * @date 2018/5/2
     */
    private void dialogMoreChoice(XJTaskWorkEntity xjWorkItemEntity, int xjPosition) {

        XJInputTypeEntity xjInputTypeEntity = SupPlantApplication.dao().getXJInputTypeEntityDao().queryBuilder().
                where(XJInputTypeEntityDao.Properties.Id.eq(xjWorkItemEntity.inputStandardId.id)).unique();

        if (xjInputTypeEntity.candidateValue == null || xjInputTypeEntity.candidateValue.isEmpty()) {
            SnackbarHelper.showError(rootView, context.getResources().getString(R.string.xj_patrol_no_result));
            return;
        }


        String[] items = xjInputTypeEntity.candidateValue.split(",");  //候选值列表
        List<SheetEntity> list = new ArrayList<>();
        for (String item : items) {
            SheetEntity sheetEntity = new SheetEntity();
            sheetEntity.name = item;
            list.add(sheetEntity);
        }

        List<Boolean> checkedList = new ArrayList<>();
        for (String s : items) {
            if (xjWorkItemEntity.concluse != null && xjWorkItemEntity.concluse.contains(s)) {
                checkedList.add(true);
            } else {
                checkedList.add(false);
            }
        }

        new CustomSheetDialog(context)
                .multiSheet(context.getString(R.string.xj_patrol_multi_choose_list), list, checkedList)
                .setOnItemChildViewClickListener((childView, position, action, obj) -> {

                    List<SheetEntity> sheetEntities = GsonUtil.jsonToList(obj.toString(), SheetEntity.class);

                    if (sheetEntities != null && sheetEntities.size() > 0) {

                        xjWorkItemEntity.concluse = "";
                        for (SheetEntity sheetEntity : sheetEntities) {
                            xjWorkItemEntity.concluse += sheetEntity.name + ",";
                        }

                        xjWorkItemEntity.concluse = xjWorkItemEntity.concluse.substring(0, xjWorkItemEntity.concluse.length() - 1);

                        mXJWorkAdapter.notifyItemChanged(xjPosition);

                    }
                }).show();

    }


    XJAbnormalSelectDialog abnormalSelectDialog;
    //巡检项异常原因选择框
    private void showPopUp(XJTaskWorkEntity xjWorkEntity) {
        if (abnormalSelectDialog==null)
           abnormalSelectDialog=new XJAbnormalSelectDialog(context,context.getResources().getString(R.string.detailed_reasons)+xjWorkEntity.eamName==null?"":xjWorkEntity.eamName,abnormalReasonMap);
            if ( xjWorkEntity.abnormalReason!=null){
                abnormalSelectDialog.setPosition(xjWorkEntity.abnormalReason.value);
            }
            abnormalSelectDialog.show();
            abnormalSelectDialog.setOnSureListener((selectAbnormalId, season) -> {
            xjWorkEntity.abnormalReason = SystemCodeManager.getInstance().getSystemCodeEntity(selectAbnormalId);
            xjWorkEntity.reason = season;
        });
//
//        mCustomDialog
//
//                .bindClickListener(R.id.btn_sure, v -> {
//                        //TODO 这个判断值需要改
//                        if (selectAbnoralId.equals("其他")&&TextUtils.isEmpty(editReasonDescription.getContent())) {
//                            ToastUtils.show(context, context.getResources().getString(R.string.please_input_eason_description));
//                            return;
//                        }
//                        xjWorkEntity.abnormalReason = SystemCodeManager.getInstance().getSystemCodeEntity(finalId);
//                        xjWorkEntity.reason = editReasonDescription.getContent();
//                        mXJWorkAdapter.notifyDataSetChanged();
//                }, false)
//                .bindClickListener(R.id.btn_cancel, v ->{
//
//                }, true);
//


    }





    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAreaUpdate(XJWorkRefreshEvent workRefreshEvent) {
        Flowable.timer(0, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
//                    int position = workRefreshEvent.getPosition();
//                    boolean isFinish = workRefreshEvent.isFinish();
//
//                    if (isFinish) {
//                        mXJWorkAdapter.notifyItemRemoved(position);
//                    } else if (workRefreshEvent.getXJWorkEntity() != null) {//来自巡检已完成重录
//                        XJTaskWorkEntity workEntity = workRefreshEvent.getXJWorkEntity();
//                        for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
//                            if (workEntity.id.equals(xjWorkEntity.id)) {
//                                mXJAreaEntity.works.set(mXJAreaEntity.works.indexOf(xjWorkEntity), workEntity);
//                            }
//                        }

//                    } else {
                    mXJAreaEntity.works= XJTaskCacheUtil.getTaskWork(mXJTaskEntity.tableNo,mXJAreaEntity.id);
                    needRefresh = true;

                    //                      mXJWorkAdapter.notifyDataSetChanged();
                    // }
                });
    }

    /**
     * 快速定位未完成巡检项
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocation(WorkItemLocationEvent event) {
        int pos = event.getLocation();
        LogUtil.d("onLocation:" + event.getLocation());

        if (pos == 0) {
            return;
        }
        XJTaskWorkEntity xjWorkEntity = mWorkEntities.get(pos);
        if (xjWorkEntity == null) {
            return;
        }
        if (!TextUtils.isEmpty(event.getMsg())) {
            ToastUtils.show(context, event.getMsg());
        }

//        ToastUtils.show(context, String.format(getResources().getString(R.string.xj_work_unfinish_warning), xjWorkEntity.content));
        contentView.scrollToPosition(pos);
        XLinearLayoutManager mLayoutManager = (XLinearLayoutManager) contentView.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(pos, 0);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getThermometerVal(ThermometerEvent thermometerEvent) {
        LogUtil.i("ThermometerEvent", thermometerEvent.getThermometerVal());
        thermometervalue = thermometerEvent.getThermometerVal().replace("℃", "");

        if (mPosition != -1 && mXJWorkAdapter.getList() != null && mXJWorkAdapter.getList().size() >= mPosition + 1) {
            XJTaskWorkEntity xjWorkItemEntity = mXJWorkAdapter.getItem(mPosition);
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

        String tag = event.tag;
        if ("设备就绪，可按下测温".equals(tag)) {
            tempTv.setText(context.getResources().getString(R.string.xj_patrol_device_operate));
        } else if ("正在测温".equals(tag)) {
            if (!context.getResources().getString(R.string.xj_patrol_temperature).equals(tempTv.getText().toString()))
                tempTv.setText(tag);
        }
        if (TextUtils.isEmpty(event.tem))
            return;

        if (mTesto805iDialog != null) {
            String tem = thermometervalue = event.tem;
            LogUtil.d(this.getClass().getSimpleName(), tem + "-" + tag);

            CustomTextView temperatureVal = mTesto805iDialog.getDialog().findViewById(R.id.temperatureVal);
            if (temperatureVal != null)
                temperatureVal.setContent(tem);
        }

    }


    @Override
    public void uploadFileSuccess(String path) {

        LogUtil.d("" + path);

        if (TextUtils.isEmpty(path)) {
            onLoadFailed("巡检数据上传失败！");
            return;
        }

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("filePath", path.replace("\\", "/"));
        queryMap.put("uploadTaskResultDTOs", new ArrayList<>());
        presenterRouter.create(XJTaskSubmitAPI.class).uploadXJData(false, queryMap);
    }


    @Override
    public void uploadFileFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

    @Override
    public void uploadXJDataSuccess(Long id) {
        onLoadSuccess();
        onBackPressed();
    }

    @Override
    public void uploadXJDataFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

    @Override
    public void getDeviceDCSParamsSuccess(BAP5CommonListEntity entity) {
        List<DeviceDCSEntity> deviceDCSEntities = entity.data;
        if (deviceDCSEntities.size() == 0) {
            return;
        }
        for (int i = 0; i < deviceDCSEntities.size(); i++) {
            if (deviceDCSEntities.get(i).result) {
                for (int j = 0; j < mXJAreaEntity.works.size(); j++) {
                    if (deviceDCSEntities.get(i).name.trim().equals(mXJAreaEntity.works.get(j).itemNumber.trim())) {
                        mXJAreaEntity.works.get(j).concluse = deviceDCSEntities.get(i).value + "";
                    }
                }
            }
        }
//        LogUtil.e("ciruy", "isAll:"+isAll+",deviceNames:"+deviceNames.toString());
//        if (deviceNames.size() > 1) {
//            eamSpinner.setSelection(isAll?0:1);
//            LogUtil.e("ciruy", "eamSpinner:"+isAll);
//            showWorks(deviceNames.get(isAll?0:1));
//        } else {
        mXJWorkAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void getDeviceDCSParamsFailed(String errorMsg) {
        LogUtil.e("errorMsg:" + errorMsg);
    }
}
