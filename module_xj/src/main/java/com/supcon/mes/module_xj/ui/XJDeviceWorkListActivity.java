package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.ptr.PtrFrameLayout;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.common.view.view.picker.DateTimePicker;
import com.supcon.mes.aic_vib.controller.AICVibServiceController;
import com.supcon.mes.aic_vib.service.AICVibService;
import com.supcon.mes.av160.controller.AV160Controller;
import com.supcon.mes.mbap.beans.SheetEntity;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.controllers.DatePickController;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomDateView;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.mbap.view.CustomSheetDialog;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.mbap.view.CustomVerticalEditText;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.constant.TemperatureMode;
import com.supcon.mes.middleware.constant.VibMode;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.bean.AccountInfo;
import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;
import com.supcon.mes.middleware.model.bean.ContactEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntityDao;
import com.supcon.mes.middleware.model.bean.ObjectEntity;
import com.supcon.mes.middleware.model.bean.PopupWindowEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.ui.view.CustomPopupWindow;
import com.supcon.mes.middleware.util.AnimationUtil;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SBTUtil;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_scan.model.event.CodeResultEvent;
import com.supcon.mes.module_scan.util.scanCode.CodeUtlis;
import com.supcon.mes.module_xj.IntentRouter;
import com.supcon.mes.module_xj.controller.XJWorkCameraController;
import com.supcon.mes.module_xj.model.api.XJTaskSubmitAPI;
import com.supcon.mes.module_xj.model.bean.DeviceDCSEntity;
import com.supcon.mes.module_xj.model.contract.DeviceDCSParamQueryContract;
import com.supcon.mes.module_xj.model.contract.XJTaskSubmitContract;
import com.supcon.mes.module_xj.presenter.DeviceDCSParamQueryPresenter;
import com.supcon.mes.module_xj.presenter.XJTaskSubmitPresenter;
import com.supcon.mes.module_xj.ui.adapter.XJDeviceWorkAdapter;
import com.supcon.mes.module_xj.util.XLinearLayoutManager;
import com.supcon.mes.mogu_viber.controller.MGViberController;
import com.supcon.mes.nfc.model.bean.NFCEntity;
import com.supcon.mes.patrol.R;
import com.supcon.mes.sb2.model.event.ThermometerEvent;
import com.supcon.mes.testo_805i.controller.InfraredEvent;
import com.supcon.mes.testo_805i.controller.TestoController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

//import com.supcon.mes.module_xj.ui.adapter.XJDeviceWorkAdapter;

/**
 * Created by matiechao on 2021/5/24
 * 设备巡检
 */
@Router(Constant.Router.MPS_DEVICE_WORK_LIST)
@Controller(value = {TestoController.class,
        SystemCodeJsonController.class,
        XJWorkCameraController.class,
//        OnlineCameraController.class
//        ExpertUHFRFIDController.class
})
@Presenter({XJTaskSubmitPresenter.class, DeviceDCSParamQueryPresenter.class})
@SystemCode(entityCodes = {
        Constant.SystemCode.PATROL_passReason,
        Constant.SystemCode.PATROL_realValue

})
public class XJDeviceWorkListActivity extends BaseRefreshRecyclerActivity<XJWorkEntity> implements
        XJTaskSubmitContract.View, DeviceDCSParamQueryContract.View {


    @BindByTag("leftBtn")
    CustomImageButton leftBtn;
    @BindByTag("titleText")
    TextView titleText;
    @BindByTag("titleSetting")
    ImageView titleSetting;
    @BindByTag("rightBtn")
    CustomImageButton rightBtn;
    @BindByTag("titleLayout")
    RelativeLayout titleLayout;
    @BindByTag("cdCheckTime")
    CustomDateView cdCheckTime;
    @BindByTag("ctCheckPeople")
    CustomTextView ctCheckPeople;
    @BindByTag("ceMaterielNum")
    CustomEditText ceMaterielNum;
    @BindByTag("btnBegin")
    Button btnBegin;
    @BindByTag("contentView")
    RecyclerView contentView;
    @BindByTag("refreshFrameLayout")
    PtrFrameLayout refreshFrameLayout;
    @BindByTag("xjBegin")
    LinearLayout xjBegin;
    @BindByTag("llXjCommit")
    LinearLayout llXjCommit;
    @BindByTag("btnUpload")
    Button btnUpload;
    @BindByTag("btnSave")
    Button btnSave;
    @BindByTag("eamSpinner")
    Spinner eamSpinner;


    XJDeviceWorkAdapter mXJWorkAdapter;
    ExpertController expertViberController;
    XJTaskAreaEntity mXJAreaEntity;
    ObjectEntity mDevice = null;


    boolean isCanEndAll = true;

    private Map<String, String> passReasonMap, realValueMap;
    private List<PopupWindowEntity> mPopupWindowEntityList = new ArrayList<>();
    private CustomPopupWindow mCustomPopupWindow;
    private SinglePickController<String> mSingPicker;
    private String thermometervalue = ""; // 全局测温值
    private MGViberController mMGViberController;
    private CustomDialog mMGViberDialog;
    private AICVibServiceController mAICVibController;
    private AV160Controller mAV160Controller;
    private CustomDialog mTesto805iDialog;
    private int mPosition;
    List<String> deviceNames = new ArrayList<>();
    List<XJWorkEntity> xjWorkEntityList = new ArrayList<>();
    List<XJWorkEntity> mXjWorkDataList = new ArrayList<>();
    List<XJWorkEntity> localXJList = new ArrayList<>();
    private TextView tempTv;
    private ImageView viberStatusIv;
    private boolean isAll = true;
    private List<DeviceEntity> deviceEntityList = new ArrayList<>();
    private List<String> exceptionIds = new ArrayList<>();
    //    private ExpertUHFRFIDController mExpertUHFRFIDController;
    boolean isContainsDevice = false;
    private DatePickController datePickController;
    private boolean localExist = false;

    String xjNameLocalData = "";
    private int viewTag = 0;
    private boolean showBegin = false;
    String scanEamCode = "";


    /**
     * 红外或UHF RFID（超高频）通用签到处理
     *
     * @param codeResultEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCodeReciver(CodeResultEvent codeResultEvent) {
        String scanResultCode = codeResultEvent.scanResult;
        scanEamCode = scanResultCode;
        if (CodeUtlis.NFC_TYPE.equals(codeResultEvent.type)) {
            NFCEntity nfcEntity = GsonUtil.gsonToBean(scanResultCode, NFCEntity.class);
            LogUtil.d(nfcEntity.toString());
            if (nfcEntity.content != null) {
                scanEamCode = nfcEntity.content;
                dealSign(nfcEntity.content.trim());
            }
        } else if (CodeUtlis.UHF_TYPE.equals(codeResultEvent.type)) {
            dealSign(scanResultCode);
        } else if (CodeUtlis.HW_TYPE.equals(codeResultEvent.type)) {
            dealSign(scanResultCode);
        }
    }

    /**
     * 人员选择
     *
     * @param selectDataEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectDataEvent(SelectDataEvent selectDataEvent) {
        LogUtil.i("selectDataEvent===" + selectDataEvent);
        if (selectDataEvent.getEntity() instanceof ContactEntity) {
            ContactEntity contactEntity = (ContactEntity) selectDataEvent.getEntity();
            ctCheckPeople.setContent(contactEntity.name);
            SharedPreferencesUtils.setParam(context, Constant.BAPQuery.STAFF_ID, contactEntity.staffId);
        }
    }


    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_device;
    }


    @Override
    protected void onInit() {
        super.onInit();
//        onLoading(getString(R.string.xj_task_uploading));
        EventBus.getDefault().register(this);
        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(false);
        int tempMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0);
        int vibMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.VIB_MODE, 0);
        if (tempMode == TemperatureMode.EXPERT.getCode() || vibMode == VibMode.EXPERT.getCode()) {
            if (expertViberController == null) {
                View contentView = ExpertController.createContentView(context);
                contentView.findViewById(R.id.viberFinishBtn).setOnClickListener(v -> expertViberController.hide());
                expertViberController = new ExpertController(contentView, true);
                expertViberController.onInit();
                registerController(ExpertController.class.getSimpleName(), expertViberController);
            } else {
                expertViberController.initData();
            }
        }
        if (tempMode == TemperatureMode.AIC.getCode() || vibMode == VibMode.AIC.getCode()) {
            AICVibService.start(context);
        }

        if (tempMode == TemperatureMode.SBT.getCode() && SBTUtil.isSupportTemp()) {
            mXJWorkAdapter.setSb2ThermometerHelper();
        }
//        ((ViewGroup) eamSpinner.getParent()).setVisibility(View.GONE);
//        xjEmptyView.setVisibility(View.VISIBLE);
//        contentView.setVisibility(View.GONE);
//        xjEmptyView.setEmptyContent("请用NFC、超高频、红外扫描获取数据");

    }


    /**
     * 根据content设备编号在巡检项数据中筛选设备后更新adapter
     *
     * @param content 设备id
     */
    @SuppressLint("CheckResult")
    private void dealSign(String content) {
        mXjWorkDataList.clear();
        getLocalData();
        if (localXJList.size() > 0) {
            for (XJWorkEntity entity : localXJList) {
                LogUtil.i("entity------------id========" + entity.eamId.id);
                if (content.trim().equals(entity.eamId.id.toString())) {
                    mXjWorkDataList.add(entity);
                }
            }
            if (mXjWorkDataList.size() > 0) {
                switchView(1);
                refreshListController.refreshComplete(mXjWorkDataList);
                return;
            }
        }
        if (xjWorkEntityList != null && xjWorkEntityList.size() > 0) {
            Flowable.fromIterable(xjWorkEntityList)
                    .subscribeOn(Schedulers.newThread())
                    .filter(xjWorkEntity -> xjWorkEntity.eamId != null && xjWorkEntity.eamId.id != null)
                    .filter(xjWorkEntity -> xjWorkEntity.eamId.id.toString().equals(content))
                    .filter(xjWorkEntity -> !xjWorkEntity.isFinished && xjWorkEntity.isRun)
                    .filter(xjWorkEntity -> xjWorkEntity.spotCheck)//是否点检。
                    .filter(xjWorkEntity -> !exceptionIds.contains(String.valueOf(xjWorkEntity.id)))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(xjWorkEntity -> {
                        isContainsDevice = true;
                        //|| xjWorkEntity.content.trim().equals(workEntity.content.trim())
                        for (XJWorkEntity workEntity : mXjWorkDataList) {
                            if (xjWorkEntity.id.equals(workEntity.id)) {
                                return;
                            }
                        }
//                        if (!mXjWorkDataList.contains(xjWorkEntity)) {
//                            noEamWorks.add(xjWorkEntity);
//                        }
                        if (mDevice == null || mDevice.id != xjWorkEntity.eamLongId) {
                            mDevice = xjWorkEntity.eamId;
                            XJWorkEntity workEntity = new XJWorkEntity();
                            workEntity.eamId = mDevice;
                            workEntity.eamLongId = mDevice.id;
                            workEntity.isEamView = true;
                            workEntity.eamName = mDevice.name;
                            workEntity.eamNum = deviceNames.size() + 1;

                            mXjWorkDataList.add(workEntity);
                            deviceNames.add(workEntity.eamId.name);
                        }
                        if (!mXjWorkDataList.contains(xjWorkEntity)) {
                            mXjWorkDataList.add(xjWorkEntity);
                        } else {
                            ToastUtils.show(context, R.string.xj_device_content_already);
                        }

                    }, throwable -> {

                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            if (isContainsDevice) {
                                if (deviceNames.size() == 0) {
                                    if (mXjWorkDataList.size() == 0) {
                                        ToastUtils.show(context, R.string.xj_device_work_empty);
                                    }
                                    switchView(0);
                                } else if (deviceNames.size() == 1) {
                                    switchView(1);
                                } else {
                                    switchView(1);
                                }
                            } else {
                                ToastUtils.show(context, R.string.xj_device_work_query_empty);
                            }
                        }
                    });
        } else {
            refreshListController.refreshComplete(null);
            ToastUtils.show(context, getString(R.string.xj_work_finish_warning3));
        }
    }


    /**
     * 切换界面显示
     */
    private void switchView(int tag) {
        //开始巡检
        if (tag == 0) {
            mDevice = null;
            viewTag = 0;
            rightBtn.setImageResource(R.drawable.ic_scan_history);
            xjBegin.setVisibility(View.VISIBLE);
            refreshFrameLayout.setVisibility(View.GONE);
            ((ViewGroup) eamSpinner.getParent()).setVisibility(View.GONE);
        }
        //扫描结果展示
        if (tag == 1) {
            viewTag = 1;
            rightBtn.setImageResource(R.drawable.ic_scan_history);
            refreshFrameLayout.setVisibility(View.VISIBLE);
            xjBegin.setVisibility(View.GONE);
            llXjCommit.setVisibility(View.VISIBLE);
            ((ViewGroup) eamSpinner.getParent()).setVisibility(View.GONE);
            refreshListController.refreshComplete(mXjWorkDataList);
        }
        //巡检历史记录展示
        if (tag == 2) {
            refreshFrameLayout.setVisibility(View.VISIBLE);
            xjNameLocalData = SharedPreferencesUtils.getParam(context, Constant.SPKey.XJ_DEVICE_NAMES, "");
            if (!TextUtils.isEmpty(xjNameLocalData)) {
                deviceNames = GsonUtil.jsonToList(xjNameLocalData, String.class);
            }
            viewTag = 2;
            rightBtn.setImageResource(R.drawable.ic_xj_work_upload);
            xjBegin.setVisibility(View.GONE);
            llXjCommit.setVisibility(View.GONE);
            if (!deviceNames.contains(XJDeviceWorkListActivity.this.getString(R.string.xj_work_eam_all))) {
                deviceNames.add(0, XJDeviceWorkListActivity.this.getString(R.string.xj_work_eam_all));
            }
            ((ViewGroup) eamSpinner.getParent()).setVisibility(View.VISIBLE);
            ArrayAdapter<String> eamSpinnerAdapter = new ArrayAdapter<>(context, R.layout.ly_spinner_item_dark, deviceNames);  //创建一个数组适配器
            eamSpinnerAdapter.setDropDownViewResource(R.layout.ly_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
            eamSpinner.setAdapter(eamSpinnerAdapter);
            eamSpinner.setSelection(isAll ? 0 : 1);
        }
    }


    @Override
    protected void initView() {
        super.initView();
        switchView(0);
        titleText.setText(R.string.xj_work_eam_check);
//        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, getString(R.string.middleware_no_data)));
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
//        titleTextMiddle.setVisibility(View.VISIBLE);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.ic_scan_history);
//        rightBtn.setImageResource(R.drawable.ic_xj_multi_check);
//        rightBtn.setImageResource(R.drawable.ic_scan_history);
        LinearLayoutManager mLayoutManager = new XLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contentView.setLayoutManager(mLayoutManager);
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(1, context)));
        contentView.setAdapter(mXJWorkAdapter);
        mSingPicker = new SinglePickController<>(this);
        mSingPicker.setCanceledOnTouchOutside(true);

        datePickController = new DatePickController((Activity) context);
        datePickController.setCycleDisable(false);
        datePickController.setCanceledOnTouchOutside(true);
        datePickController.setSecondVisible(true);
        datePickController.textSize(18);

    }


    @Override
    public void onBackPressed() {
        if (viewTag == 0) {
            super.onBackPressed();
        } else {
            if (showBegin) {
                switchView(0);
            } else {
                if (viewTag == 1) {
                    switchView(0);
                } else {
                    dealSign(scanEamCode);
                    switchView(1);
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        //显示本地缓存数据
        RxView.clicks(rightBtn)
                .throttleFirst(200, TimeUnit.MICROSECONDS)
                .subscribe(o -> {
                    if (viewTag == 2) {
                        showAllFinishDialog();
                    } else {
                        showLocalData();
                    }
                });
        //完成点检任务
        RxView.clicks(btnUpload)
                .throttleFirst(200, TimeUnit.MICROSECONDS)
                .subscribe(o -> {
                    showAllFinishDialog();
                });

        //保存继续扫描
        RxView.clicks(btnSave)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    deviceWorkFinish();
                });


        RxView.clicks(leftBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> onBackPressed());

        //请检时间选择监听
        cdCheckTime.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {

                    return;
                }
                datePickController.listener(new DateTimePicker.OnYearMonthDayTimePickListener() {
                    @Override
                    public void onDateTimePicked(String year, String month, String day, String hour, String minute, String second) {
                        String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                        cdCheckTime.setContent(dateStr);
                        //更换表头中请检时间的值
//                        mHeadEntity.setApplyTime(DateUtil.dateFormat(dateStr,"yyyy-MM-dd HH:mm:ss")+"");
                    }
                }).show(new Date().getTime());
            }
        });

        //请检人选择监听
        ctCheckPeople.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action != -1) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constant.IntentKey.IS_MULTI, false);
                    bundle.putBoolean(Constant.IntentKey.IS_SELECT, true);
                    bundle.putString(Constant.IntentKey.SELECT_TAG, ctCheckPeople.getTag() + "");
                    com.supcon.mes.middleware.IntentRouter.go(context, Constant.Router.CONTACT_SELECT, bundle);
                } else {
                    LogUtil.i("Object====" + obj);
                    AccountInfo entity = new AccountInfo();
                    entity.setId(entity.id);

                }
            }
        });


        //开始扫描
        RxView.clicks(btnBegin)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (!TextUtils.isEmpty(ceMaterielNum.getContent())) {
                        goSMZX();
                    } else {
                        ToastUtils.show(context, R.string.xj_device_please_inputname);
                    }
                });
        mXJWorkAdapter.setOnItemChildViewClickListener((childView, position, action, obj) -> {
            XJWorkEntity xjWorkEntity = (XJWorkEntity) obj;
            String tag = (String) childView.getTag();
            mPosition = position;
            switch (tag) {
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


    /**
     * 测温
     *
     * @param xjWorkEntity
     */
    private void showTempView(XJWorkEntity xjWorkEntity) {
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


    /**
     * 设备筛选
     *
     * @param deviceName
     */
    @SuppressLint("CheckResult")
    private void showWorks(String deviceName) {
        isAll = getString(R.string.xj_work_eam_all).equals(deviceName);
        if (isAll) {
            refreshListController.refreshComplete(mXjWorkDataList);
            return;
        }
        List<XJWorkEntity> workEntities = new ArrayList<>();
        Flowable.fromIterable(mXjWorkDataList)
                .subscribeOn(Schedulers.newThread())
                .filter(xjWorkEntity -> xjWorkEntity.eamId != null && xjWorkEntity.eamId.name != null && xjWorkEntity.eamId.name.equals(deviceName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workEntities::add, throwable -> {
                }, () -> refreshListController.refreshComplete(workEntities));
    }

    /**
     * 跳转扫描页面
     */
    private void goSMZX() {
        AnimationUtil.setBottomInAnimation(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.IntentKey.MODULE, Constant.Router.MPS_DEVICE_WORK_LIST);
        IntentRouter.go(context, Constant.Router.SMZX_MODULE, bundle);
    }

    /**
     * 巡检项待提交视图
     */
    private void showLocalData() {
        mXjWorkDataList.clear();
        getLocalData();
        if (localXJList.size() > 0) {
            for (XJWorkEntity entity : localXJList) {
                if (entity.isEamView) {
                    mXjWorkDataList.add(entity);
                }
                if (entity.isFinished) {
                    mXjWorkDataList.add(entity);
                }
            }
            LogUtil.i("XJDeviceWorkListActivity_______mXjWorkDataList=====" + mXjWorkDataList);
            if (viewTag == 0) {
                showBegin = true;
            } else {
                showBegin = false;
            }
            switchView(2);
            refreshListController.refreshComplete(mXjWorkDataList);
        } else {
            ToastUtils.show(context, R.string.xj_device_local_empty);
        }

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
            default:
        }
    }


    private void showAllJumpDialog() {
        new CustomDialog(context)
                .twoButtonAlertDialog(getString(R.string.xj_work_jump_all))
                .bindView(R.id.grayBtn, getString(R.string.no))
                .bindView(R.id.redBtn, getString(R.string.yes))
                .bindClickListener(R.id.grayBtn, null, true)
                .bindClickListener(R.id.redBtn, v -> {
                    if (mXjWorkDataList != null && mXjWorkDataList.size() > 0) {
                        XJDeviceWorkListActivity.this.skipEam(0L);
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
//                    remoteLocalRecord();
                    if (mXjWorkDataList != null && mXjWorkDataList.size() > 0) {
                        presenterRouter.create(XJTaskSubmitAPI.class).uploadXJWorkFile(mXjWorkDataList, true, ceMaterielNum.getContent().trim(), DateUtil.dateFormat(cdCheckTime.getContent()), System.currentTimeMillis());
                        onLoading(getString(R.string.xj_task_uploading));
                    }
                }, true)
                .show();
    }

    @SuppressLint("CheckResult")
    private void showEditDialog(XJWorkEntity xjWorkEntity, int position) {
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
    protected IListAdapter<XJWorkEntity> createAdapter() {
        mXJWorkAdapter = new XJDeviceWorkAdapter(context);
        return mXJWorkAdapter;
    }

    @Override
    protected void initData() {
        super.initData();
        xjWorkEntityList = SupPlantApplication.dao().getXJWorkEntityDao().queryBuilder().list();
        mXJWorkAdapter.setConclusions(getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_realValue));
        passReasonMap = getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_passReason);
        realValueMap = getController(SystemCodeJsonController.class).getCodeMap(Constant.SystemCode.PATROL_realValue);
        checkDeviceState();
        String time = DateUtil.dateTimeFormat(0);
        cdCheckTime.setContent(time);
        AccountInfo accountInfo = SupPlantApplication.getAccountInfo();
        ctCheckPeople.setContent(accountInfo.staffName);
        SharedPreferencesUtils.setParam(context, Constant.BAPQuery.STAFF_ID, accountInfo.staffId);
    }

    public void checkDeviceState() {
        if (mXJAreaEntity != null && mXJAreaEntity.works != null) {
            deviceEntityList = new ArrayList<>();
            for (XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works) {
                if (xjWorkEntity.eamId != null || xjWorkEntity.eamId.id != null) {
                    //根据设备eamId获取CommonDeviceEntity
                    try {
                        DeviceEntity commonDeviceEntity = SupPlantApplication.dao().getDeviceEntityDao().queryBuilder()
                                .where(DeviceEntityDao.Properties.Code.eq(xjWorkEntity.eamId.code)).unique();
                        if (commonDeviceEntity != null) {
                            deviceEntityList.add(commonDeviceEntity);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
    }


    /**
     * 测振
     *
     * @param xjWorkEntity
     */
    private void showVibView(XJWorkEntity xjWorkEntity) {
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

    private void showAV160Dialog(int position, XJWorkEntity xjWorkItemEntity) {


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

    private void showMGViberDialog(int position, XJWorkEntity xjWorkItemEntity) {
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


    private void showTesto805iDialog(int position, XJWorkEntity xjWorkItemEntity) {
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

    private void showAICDialog(int position, XJWorkEntity xjWorkItemEntity, boolean isTempTest) {


        if (mAICVibController == null) {

            mAICVibController = new AICVibServiceController(AICVibServiceController.getLayoutView(context), true);
            mAICVibController.onInit();
            mAICVibController.initView();
            mAICVibController.initListener();
            mAICVibController.initData();
        } else {
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

    //long eamId
    @SuppressLint("CheckResult")
    private void deviceWorkFinish() {
        if (mXjWorkDataList.size() == 0) {
            ToastUtils.show(context, getString(R.string.xj_work_finish_warning3));
            return;
        }
        List<XJWorkEntity> xjWorkEntities = new ArrayList<>();
        List<XJWorkEntity> failWorkList = new ArrayList<>();
        isCanEndAll = true;
        Flowable.fromIterable(mXjWorkDataList)
                .subscribe(xjWorkItemEntity -> {
                            if (xjWorkItemEntity.isEamView) {
                                xjWorkEntities.add(xjWorkItemEntity);
                            } else {
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
                                }
                                if (msg == null) {
                                    xjWorkEntities.add(xjWorkItemEntity);
                                }
                                if (!TextUtils.isEmpty(msg)) {
                                    failWorkList.add(xjWorkItemEntity);
                                }
                                isCanEndAll = xjWorkEntities.size() > 1 ? true : false;
                            }
                        },
                        throwable -> {
                        },
                        () -> {
                            if (xjWorkEntities.size() > 1) {
                                String diaLogMsg = "";
                                if (!isCanEndAll) {
                                    diaLogMsg = getString(R.string.xj_device_scan_again);
                                } else {
                                    diaLogMsg = failWorkList.size() > 0 ? getString(R.string.xj_device_continue_submit) : getString(R.string.xj_work_finish_warning1);
                                }
                                new CustomDialog(context)
                                        .twoButtonAlertDialog(diaLogMsg)
                                        .bindView(R.id.grayBtn, getString(R.string.no))
                                        .bindView(R.id.redBtn, getString(R.string.yes))
                                        .bindClickListener(R.id.grayBtn, null, true)
                                        .bindClickListener(R.id.redBtn, v -> saveCommitData(xjWorkEntities, failWorkList), true)
                                        .show();
                            } else {
                                ToastUtils.show(context,getString(R.string.xj_device_submit_content_empty));
                            }

                        });
    }


    /**
     * 保存完成的巡检数据
     *
     * @param xjWorkEntities
     */
    @SuppressLint("CheckResult")
    private void saveCommitData(List<XJWorkEntity> xjWorkEntities, @Nullable List<XJWorkEntity> failWorkList) {
        try {
            localExist = false;
            for (XJWorkEntity xjWorkItemEntity : xjWorkEntities) {
                if (!xjWorkItemEntity.isEamView) {
                    doFinish(xjWorkItemEntity);
                }
            }
            if (failWorkList.size() > 0) {
                xjWorkEntities.addAll(failWorkList);
            }
            if (getLocalData().size() > 0) {
                ListIterator iterable = localXJList.listIterator();
                while (iterable.hasNext()) {
                    XJWorkEntity xjWorkIterable = (XJWorkEntity) iterable.next();
                    for (XJWorkEntity entity : xjWorkEntities) {
                        if (xjWorkIterable.isEamView && entity.isEamView) {
                            if (!entity.eamId.id.equals(xjWorkIterable.eamId.id)) {
                                break;
                            } else {
                                localExist = true;
                            }
                        }
                        if (!entity.isEamView && entity.eamId.id.equals(xjWorkIterable.eamId.id)) {
                            if (entity.content.equals(xjWorkIterable.content) && entity.isFinished) {
                                iterable.remove();
                                iterable.add(entity);
                                break;
                            }
                        }
                    }
                }
            }
            if (!localExist) {
                localXJList.addAll(xjWorkEntities);
            }
            SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_CACHE, localXJList.toString());
            saveDeviceNames();
            ToastUtils.show(context, getString(R.string.operate_succeed));
            goSMZX();
            switchView(0);

        } catch (Exception e) {
            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_operater_fail) + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 保存设备名称筛选数据
     */
    private void saveDeviceNames() {
        xjNameLocalData = SharedPreferencesUtils.getParam(context, Constant.SPKey.XJ_DEVICE_NAMES, "");
        if (TextUtils.isEmpty(xjNameLocalData)) {
            SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_NAMES, deviceNames.toString());
            return;
        } else {
            deviceNames = GsonUtil.jsonToList(xjNameLocalData, String.class);
        }
        for (XJWorkEntity entity : mXjWorkDataList) {
            if (entity.isFinished) {
                if (!deviceNames.contains(entity.eamId.name)) {
                    deviceNames.add(entity.eamId.name);
                    SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_NAMES, deviceNames.toString());
                    return;
                }

            }
        }
    }


    /**
     * 获取本地存储在SharedPreferences中的巡检项数据
     *
     * @return
     */
    private List<XJWorkEntity> getLocalData() {
        String spLocalData = SharedPreferencesUtils.getParam(context, Constant.SPKey.XJ_DEVICE_CACHE, "");
        if (!TextUtils.isEmpty(spLocalData)) {
            localXJList = GsonUtil.jsonToList(spLocalData, XJWorkEntity.class);
        }
        return localXJList;
    }


    /**
     * @param
     * @return
     * @description 一键完成巡检项
     * @author zhangwenshuai1 2018/12/29
     */
    private boolean doFinish(XJWorkEntity xjWorkItemEntity) {
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
        List<XJWorkEntity> workItemEntities = new ArrayList<>();
        Flowable.fromIterable(mXjWorkDataList)
                .filter(xjWorkEntity -> xjWorkEntity.isPass && !xjWorkEntity.isEamView)
                .filter(xjWorkEntity -> eamId == xjWorkEntity.eamLongId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(xjWorkItemEntity -> {
                    workItemEntities.add(xjWorkItemEntity);
                }, throwable -> {
                }, () -> {
                    if (workItemEntities.size() == 0) {
                        ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_jump_xj));
                    } else {
                        showAllSkipReasonPicker(workItemEntities);
                    }
                });

    }

    /**
     * @author zhangwenshuai1
     * @date 2018/4/4
     * @description 设备整体跳过原因筛选框
     */
    @SuppressLint("CheckResult")
    private void showAllSkipReasonPicker(List<XJWorkEntity> xjWorkItemEntities) {
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
                        mXjWorkDataList.removeAll(xjWorkItemEntities);
                        refreshListController.refreshComplete(mXjWorkDataList);
                        if (viewTag == 2) {
                            SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_CACHE, mXjWorkDataList.toString());
                        }
                        ToastUtils.show(context, String.format(getResources().getString(R.string.xj_work_skip_toast), "" + (xjWorkItemEntities.size())));
                    });


        }).show();
    }

    /**
     * @description 多选
     * @author zhangwenshuai1
     * @date 2018/5/2
     */
    private void dialogMoreChoice(XJWorkEntity xjWorkItemEntity, int xjPosition) {

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getThermometerVal(ThermometerEvent thermometerEvent) {
        LogUtil.i("ThermometerEvent", thermometerEvent.getThermometerVal());
        thermometervalue = thermometerEvent.getThermometerVal().replace("℃", "");

        if (mPosition != -1 && mXJWorkAdapter.getList() != null && mXJWorkAdapter.getList().size() >= mPosition + 1) {
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
            onLoadFailed(getString(R.string.xj_patrol_upload));
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
    public void uploadXJWorkFileSuccess(Long entity) {
        onLoadSuccess(context.getResources().getString(R.string.xj_patrol_upload_succeed));
    }

    @Override
    public void uploadXJWorkFileFailed(String errorMsg) {
        LogUtil.i("巡检项上传失败-----返回内容" + errorMsg);
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

    @Override
    public void uploadXJDataSuccess(Long id) {
        LogUtil.i("巡检项上传成功-----返回内容" + id);
        LogUtil.i("上传成功");
        onLoadSuccess(context.getResources().getString(R.string.xj_patrol_upload_succeed));
        //清空本地存储的待上传记录
        if (viewTag == 2) {
            SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_CACHE, "");
            SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_NAMES, "");
        } else {
            remoteLocalRecord();
        }
        switchView(0);
    }

    /**
     * 删除本地对应设备的保存记录(设备名称记录巡检内容记录)
     */
    @SuppressLint("CheckResult")
    private void remoteLocalRecord() {
        getLocalData();
        xjNameLocalData = SharedPreferencesUtils.getParam(context, Constant.SPKey.XJ_DEVICE_NAMES, "");

        if (!TextUtils.isEmpty(xjNameLocalData)) {
            deviceNames = GsonUtil.jsonToList(xjNameLocalData, String.class);
        }
        if (localXJList.size() > 0) {
            Flowable.fromIterable(localXJList)
                    .filter(localEntity -> localEntity.eamId.id.toString().trim().equals(scanEamCode))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(localEntity -> {
                        localXJList.remove(localEntity);
                        if (deviceNames.contains(localEntity.eamId.name)) {
                            deviceNames.remove(localEntity.eamId.name);
                        }
                    }, throwable -> {
                    }, () -> {
                        LogUtil.i("删除后的本地数据为：" + localXJList);
                        SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_CACHE, localXJList.toString());
                        SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_DEVICE_NAMES, deviceNames.toString());
                    });
        }


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
        mXJWorkAdapter.notifyDataSetChanged();
    }

    @Override
    public void getDeviceDCSParamsFailed(String errorMsg) {
        LogUtil.e("errorMsg:" + errorMsg);
    }


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
    }


}
