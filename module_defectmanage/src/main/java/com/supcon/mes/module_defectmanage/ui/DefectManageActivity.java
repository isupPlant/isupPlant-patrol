package com.supcon.mes.module_defectmanage.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomDateView;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.mbap.view.CustomVerticalEditText;
import com.supcon.mes.middleware.IntentRouter;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.MyPickerController;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.RegionExEntity;
import com.supcon.mes.middleware.model.api.AddFileListAPI;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.BapPageResultEntity;
import com.supcon.mes.middleware.model.bean.BaseCodeIdNameEntity;
import com.supcon.mes.middleware.model.bean.BaseIntIdNameEntity;
import com.supcon.mes.middleware.model.bean.ContactEntity;
import com.supcon.mes.middleware.model.bean.DepartmentEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntityDao;
import com.supcon.mes.middleware.model.bean.FileEntity;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.model.contract.AddFileListContract;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.presenter.AddFileListPresenter;
import com.supcon.mes.middleware.ui.view.AddFileListView;
import com.supcon.mes.middleware.ui.view.FileListView;
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.AddDefectAPI;
import com.supcon.mes.module_defectmanage.model.api.GetDefectSourceListAPI;
import com.supcon.mes.module_defectmanage.model.api.RefDefectAPI;
import com.supcon.mes.module_defectmanage.model.bean.DefectListNumEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntityDao;
import com.supcon.mes.module_defectmanage.model.bean.DefectSourceEntity;
import com.supcon.mes.module_defectmanage.model.bean.FileUploadDefectEntity;
import com.supcon.mes.module_defectmanage.model.contract.AddDefectContract;
import com.supcon.mes.module_defectmanage.model.contract.GetDefectSourceListContract;
import com.supcon.mes.module_defectmanage.model.contract.RefDefectContract;
import com.supcon.mes.module_defectmanage.presenter.AddDefectPresenter;
import com.supcon.mes.module_defectmanage.presenter.GetDefectSourceListPresenter;
import com.supcon.mes.module_defectmanage.presenter.RefDefectPresenter;
import com.supcon.mes.module_defectmanage.util.DatabaseManager;
import com.supcon.mes.module_defectmanage.util.HandleUtils;
import com.supcon.mes.module_defectmanage.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;


@Presenter(value = {AddDefectPresenter.class, AddFileListPresenter.class, GetDefectSourceListPresenter.class, RefDefectPresenter.class})
@Controller(value = {SystemCodeJsonController.class})
@SystemCode(entityCodes = {Constant.SystemCode.DefectManage_problemClass, Constant.SystemCode.DefectManage_problemLevel,
Utils.SystemCode.DefectManage_problemState})
@Router(value = Constant.AppCode.DEFECT_MANAGEMENT_ADD)
public class DefectManageActivity extends BaseControllerActivity implements AddDefectContract.View, AddFileListContract.View
, GetDefectSourceListContract.View, RefDefectContract.View {

    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("name")
    CustomEditText name;
    @BindByTag("describe")
    CustomVerticalEditText describe;
    @BindByTag("source")
    CustomTextView source;
    @BindByTag("type")
    CustomTextView type;
//    @BindByTag("level")
//    CustomTextView level;
    @BindByTag("devicename")
    CustomTextView devicename;
    @BindByTag("equip_department")
    CustomTextView equip_department;
    @BindByTag("assessor")
    CustomTextView assessor;
    @BindByTag("discover")
    CustomTextView discover;
    @BindByTag("address")
    CustomTextView address;
    @BindByTag("findtime")
    CustomDateView findtime;
//    @BindByTag("planhandletime")
//    CustomDateView planhandletime;
    @BindByTag("leak_name")
    CustomEditText leak_name;
    @BindByTag("leak_status")
    CustomTextView leak_status;
    @BindByTag("isdeviceView")
    CustomTextView isdeviceView;
    @BindByTag("leak_number")
    CustomTextView leak_number;
    @BindByTag("leak_time")
    CustomDateView leak_time;
    @BindByTag("file_list")
    AddFileListView file_list;
    @BindByTag("saveBtn")
    Button saveBtn;
    @BindByTag("submitBtn")
    Button submitBtn;
    @BindByTag("leak_ly")
    LinearLayout leak_ly;
    @BindByTag("rightBtn")
    ImageButton rightBtn;
    @BindByTag("titleText")
    TextView titleText;

    private SinglePickController mSinglePickController;
    private MyPickerController mDatePickController;

    SystemCodeEntity selectedType/*, selectLevel**/;
    Boolean haslist;//是否挂牌
    BaseCodeIdNameEntity selectSource;
//    BaseCodeIdNameEntity selectedEamInfo;

    BaseIntIdNameEntity selectedDepartment;
    BaseIntIdNameEntity selectedAssor, selectedFinder;

    BaseCodeIdNameEntity  selectedArea = new BaseCodeIdNameEntity();
    long findTimeLong, /*handleTimeLong,**/ leakTimeLong;
    DefectModelEntity defectModelEntity;
    DeviceEntity selectedDevice;
//    List<DeviceEntity> deviceEntities;
    String tableNo;
    boolean isDevice = false;//巡检过来的缺陷才有的字段

    String chooseType;
    ArrayList<DefectSourceEntity> sourceTypeList;
    String TAG_AREA = "TAG_AREA";
    String TAG_DEVICE_REF = "deviceName";
    boolean isFromAll = false;//从首页入口来的
    List<DefectListNumEntity> numEntityList;
    String selectedListedNum;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_defect_add;
    }

    @Override
    protected void initData() {
        super.initData();

        selectSource = new BaseCodeIdNameEntity();

        Long dataId = null;
        String areaCode = null,areaName = null;
        //情况分类：1、从巡检传过来的数据2、从列表中过来的某一条数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dataId = bundle.getLong(Constant.INTENT_EXTRA_ID);
            tableNo = bundle.getString(Constant.IntentKey.XJ_TASK_TABLENO);
            areaCode= bundle.getString(Constant.IntentKey.XJ_AREA_CODE);
            areaName = bundle.getString(Constant.IntentKey.XJ_AREA_NAME);
            isDevice = bundle.getBoolean(Constant.IntentKey.XJ_IS_DEVICE);
        }

        if (dataId != null && dataId.longValue() > 0) {
            //说明是从列表中过来的
            rightBtn.setVisibility(View.GONE);
            List<DefectModelEntity> list = DatabaseManager.getDao().getDefectModelEntityDao().queryBuilder()
                    .where(DefectModelEntityDao.Properties.DbId.eq(dataId)).list();
            if (list != null && list.size() > 0) {
                defectModelEntity = list.get(0);
            }

            if (defectModelEntity.getDefectSource() != null) {
                selectSource.setCode(defectModelEntity.getDefectSource());
                source.setVisibility(View.GONE);
            }
        } else {
            //从另外的实体来的进行初始化
            defectModelEntity = new DefectModelEntity();
            //如果有区域说明就是巡检
            if (!StringUtil.isBlank(tableNo) || !StringUtil.isBlank(areaCode)) {
                selectSource.setCode("OSI");
                selectSource.setName(getString(R.string.defect_source_osi));
                source.setVisibility(View.GONE);

            } else {
                rightBtn.setVisibility(View.GONE);
                isFromAll = true;
                presenterRouter.create(GetDefectSourceListAPI.class).getDefectSourceList(1);//如果是分页的就要改了目前就三种方式;
            }
            //如果是从巡检过来的就设置为巡检，不显示巡检来源；
        }

        //这里的逻辑要修改：缺陷过来的与其他地方的不一样
        if (StringUtil.isBlank(areaCode)) {

        } else {
            if (isDevice) {

                if (areaCode != null) {
                    HandleUtils.setDeviceIdList(areaCode);//存储一下
                    List<DeviceEntity> deviceEntities = getDeviceList(areaCode);

                    if (deviceEntities != null && deviceEntities.size() > 0) {
                        selectedDevice = deviceEntities.get(0);
                        devicename.setContent(selectedDevice.getName());
                    }
                }
            } else {

                selectedArea = new BaseCodeIdNameEntity();
                selectedArea.setCode(areaCode);
                selectedArea.setName(areaName);
                address.setContent(areaName);
            }
        }

        if (defectModelEntity == null) {
            finish();
        }

        presenterRouter.create(RefDefectAPI.class).listedRefQuery();
        initDatePickController();
        initSinglePickController();

        if (dataId == null || dataId.longValue() ==0) {
            initByEmpty();
        } else {
            initByEditInfo();
        }

        setViewByDeviceFlag();
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);

        titleText.setText(R.string.defect_title_add);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_list));

        file_list.setFileListView(new FileListView.FileListViewListener() {
            @Override
            public void loadFile(String type) {
                switch (type) {
                    case FileListView.LOADING:
                        onLoading(context.getString(R.string.download_attachment));
                        break;
                    case FileListView.LOADSUCCESS:
                        onLoadSuccess("");
                        break;
                    case FileListView.LOADFAIL:
                        onLoadFailed(context.getString(R.string.download_attachment_failed));
                        break;
                    case FileListView.DELETEING:
                        onLoading(context.getString(R.string.delete_attachment));
                        break;
                    case FileListView.DELETEFAIL:
                        onLoadFailed(context.getString(R.string.delete_attachment_failed));
                        break;
                    default:
                        break;
                }
            }
        });
        file_list.setModuleCode("HierarchicalMod");
        file_list.setEntityCode("Task_5.0.0.00_task");
    }

    @Override
    protected void initListener() {
        super.initListener();

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        devicename.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    selectedDevice = null;
                } else {
                    //转换为小狄的
//                    if (isFromAll) {
                        //
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.IntentKey.SELECT_TAG, TAG_DEVICE_REF);
                        com.supcon.mes.middleware.IntentRouter.go(context, Constant.Router.DEVICE_REFER, bundle);
//                    } else {
//                        ArrayList<DeviceSelected> selectedList = new ArrayList<>();
//                        if (deviceEntities == null || deviceEntities.size() < 1) {
//                            return;
//                        }
//                        for (DeviceEntity deviceEntity : deviceEntities) {
//                            DeviceSelected deviceSelected = new DeviceSelected();
//                            deviceSelected.id = deviceEntity.id;
//                            deviceSelected.name = deviceEntity.name;
//                            selectedList.add(deviceSelected);
//                        }
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable(Constant.INTENT_EXTRA_OBJECT, selectedList);
//                        bundle.putInt(Constant.INTENT_EXTRA_INT, 1);
//                        bundle.putString(Constant.INTENT_EXTRA_OBJECT_CHAT, "device");
//                        IntentRouter.go(context, Constant.Router.SELECT, bundle);
//                    }

                }
            }
        });

        assessor.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                choosePerson("assor_person");
            }
        });

        discover.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                choosePerson("find_person");
            }
        });

        equip_department.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                chooseDepartment("find_department");
            }
        });


        findtime.setOnChildViewClickListener((childView, action, obj) -> {
            if (action == -1) {
            } else {
                mDatePickController
                        .listener((year, month, day, hour, minute, second) -> {

                            String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                            findTimeLong = DateUtil.dateFormat(dateStr, "yyyy-MM-dd HH:mm:ss");
                            findtime.setDate(DateUtil.dateFormat(findTimeLong, "yyyy-MM-dd HH:mm:ss"));
                        })
                        .show(findTimeLong);
            }
        });

//        planhandletime.setOnChildViewClickListener((childView, action, obj) -> {
//            if (action == -1) {
//            } else {
//                mDatePickController
//                        .listener((year, month, day, hour, minute, second) -> {
//
//                            String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
//                            handleTimeLong = DateUtil.dateFormat(dateStr, "yyyy-MM-dd HH:mm:ss");
//                            planhandletime.setDate(DateUtil.dateFormat(handleTimeLong, "yyyy-MM-dd HH:mm:ss"));
//                        })
//                        .show(handleTimeLong);
//            }
//        });

        leak_time.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                } else {
                    mDatePickController
                            .listener((year, month, day, hour, minute, second) -> {

                                String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                                leakTimeLong = DateUtil.dateFormat(dateStr, "yyyy-MM-dd HH:mm:ss");
                                leak_time.setDate(DateUtil.dateFormat(leakTimeLong, "yyyy-MM-dd HH:mm:ss"));
                            })
                            .show(leakTimeLong);
                }
            }
        });

        leak_number.setOnChildViewClickListener((childView, action, obj) -> {
            if (action == -1) {
                selectedListedNum = "";
            } else {
                if (numEntityList == null || numEntityList.size() == 0) {
                    ToastUtils.show(context, getString(R.string.defect_listnumber_list_is_null));
                    return;
                }
                SinglePickController<String> stringSinglePickController = mSinglePickController
                        .list(numEntityList)
                        .listener((index, item) -> {
                            DefectListNumEntity listNumEntity = numEntityList.get(index);
                            leak_number.setContent(listNumEntity.getListedNumber());
                            selectedListedNum = listNumEntity.getListedNumber();
                        });
                stringSinglePickController.show(source.getContent());
            }
        });

        source.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    //什么情况下会这样？？？
                    selectSource = null;
                } else {
                    if (sourceTypeList == null || sourceTypeList.size() == 0) {
                        ToastUtils.show(context, getString(R.string.defect_source_list_is_null));
                        return;
                    }
                    //跳转到选择页面
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(Constant.INTENT_EXTRA_OBJECT, sourceTypeList);
//                    bundle.putInt(Constant.INTENT_EXTRA_INT, 1);
//                    bundle.putString(Constant.INTENT_EXTRA_OBJECT_CHAT, TAG_SOURCE_TYPE);
//                    IntentRouter.go(context, Constant.Router.SELECT, bundle);

                    SinglePickController<String> stringSinglePickController = mSinglePickController
                            .list(sourceTypeList)
                            .listener((index, item) -> {
                                DefectSourceEntity sourceEntity = sourceTypeList.get(index);
                                source.setContent(sourceEntity.getName());
                                selectSource = new BaseCodeIdNameEntity();
                                selectSource.setCode(sourceEntity.get_code());
                                selectSource.setName(sourceEntity.getName());
                            });
                    stringSinglePickController.show(source.getContent());
                }
            }
        });

        isdeviceView.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    //什么情况下会这样？？？
                    isDevice = false;
                    setViewByDeviceFlag();
                } else {
                    List<String> nameList = new ArrayList<>();
                    nameList.add(getString(R.string.defect_no));
                    nameList.add(getString(R.string.defect_yes));

                    SinglePickController<String> stringSinglePickController = mSinglePickController
                            .list(nameList)
                            .listener((index, item) -> {
                                if (index == 0) {
                                    isDevice = false;
                                } else {
                                    isDevice = true;
                                }
                                setViewByDeviceFlag();
                            });
                    stringSinglePickController.show(isdeviceView.getContent());
                }
            }
        });

        address.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    selectedArea = null;
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.INTENT_EXTRA_INT, 1);
                    bundle.putString(Constant.INTENT_EXTRA_OBJECT_CHAT, TAG_AREA);
                    IntentRouter.go(context, Constant.Router.SELECT_REGION, bundle);
                }
            }
        });

        leak_status.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    haslist = false;
                } else {
                    //如果选择的是泄漏的话
                    List<String> nameList = new ArrayList<>();
                    nameList.add(getString(R.string.defect_no));
                    nameList.add(getString(R.string.defect_yes));

                    SinglePickController<String> stringSinglePickController = mSinglePickController
                            .list(nameList)
                            .listener((index, item) -> {
                                if (index == 0) {
                                    haslist = false;
                                } else {
                                    haslist = true;
                                }
                                leak_status.setContent((String) item);
                            });
                    stringSinglePickController.show(leak_status.getContent());
                }
            }
        });


        type.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    selectedType = null;
                } else {
                    //如果选择的是泄漏的话
                    final List<SystemCodeEntity> systemCodeEntityList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemClass);
                    if (systemCodeEntityList == null || systemCodeEntityList.size() < 1) {
                        ToastUtils.show(context, getString(R.string.defect_data_is_null));
                        return;
                    }

                    List<String> nameList = new ArrayList<>();
                    for (SystemCodeEntity systemCodeEntity : systemCodeEntityList) {
                        nameList.add(systemCodeEntity.getValue());
                    }

                    SinglePickController<String> stringSinglePickController = mSinglePickController
                            .list(nameList)
                            .listener((index, item) -> {
                                selectedType = systemCodeEntityList.get(index);
                                type.setContent((String) item);

                                //如果是泄漏的话就要隐藏一下
                                swithLeakly(selectedType.code);
                            });
                    stringSinglePickController.show(type.getContent());
                }
            }
        });

//        level.setOnChildViewClickListener(new OnChildViewClickListener() {
//            @Override
//            public void onChildViewClick(View childView, int action, Object obj) {
//                if (action == -1) {
//                    selectLevel = null;
//                    leak_ly.setVisibility(View.GONE);
//                } else {
//                    //如果选择的是泄漏的话
//                    final List<SystemCodeEntity> systemCodeEntityList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemLevel);
//                    if (systemCodeEntityList == null || systemCodeEntityList.size() < 1) {
//                        ToastUtils.show(context, getString(R.string.defect_data_is_null));
//                        return;
//                    }
//
//                    List<String> nameList = new ArrayList<>();
//                    for (SystemCodeEntity systemCodeEntity : systemCodeEntityList) {
//                        nameList.add(systemCodeEntity.getValue());
//                    }
//
//                    SinglePickController<String> stringSinglePickController = mSinglePickController
//                            .list(nameList)
//                            .listener((index, item) -> {
//                                selectLevel = systemCodeEntityList.get(index);
//                                level.setContent((String) item);
//                            });
//                    stringSinglePickController.show(level.getContent());
//                }
//            }
//        });

        Disposable disposable = RxView.clicks(submitBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    submit();
                });

        Disposable disposableSave = RxView.clicks(saveBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    saveToLocal();
                });

        Disposable disposableList = RxView.clicks(rightBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.IntentKey.XJ_TASK_TABLENO, tableNo);
                    bundle.putString(Constant.IntentKey.XJ_AREA_CODE, selectedArea.getCode());
                    IntentRouter.go(context, Utils.AppCode.DEFECT_MANAGEMENT_OFF_LINE_LIST, bundle);
                });
    }

    private void initSinglePickController() {
        mSinglePickController = new SinglePickController<String>((Activity) context);
        mSinglePickController.textSize(18);
        mSinglePickController.setCanceledOnTouchOutside(true);
    }

    private void initDatePickController() {
        mDatePickController = new MyPickerController(this);
        mDatePickController.textSize(18);
        mDatePickController.setCycleDisable(false);
        mDatePickController.setSecondVisible(true);
        mDatePickController.setCanceledOnTouchOutside(true);
    }

    /**
     * 根据是不是设备的切换UI
     */
    private void setViewByDeviceFlag() {
        if (isDevice) {
            address.setNecessary(false);
            devicename.setNecessary(true);
            isdeviceView.setContent(getString(R.string.defect_yes));
        } else {
            address.setNecessary(true);
            devicename.setNecessary(false);
            isdeviceView.setContent(getString(R.string.defect_no));
        }
    }

    /**
     * 按照已经保存的数据初始化
     */
    private void initByEditInfo() {
        //UI全部初始化

        name.setContent(defectModelEntity.getName());
        describe.setContent(defectModelEntity.getHiddenApperance());
        //有几个传过来的字段 没有

        //
        selectedType = new SystemCodeEntity();
        List<SystemCodeEntity> systemCodeEntityList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemClass);
        if (systemCodeEntityList != null && systemCodeEntityList.size() > 0) {
            for (SystemCodeEntity systemCodeEntity : systemCodeEntityList) {
                if (StringUtil.contains(systemCodeEntity.getCode(), defectModelEntity.defectType)) {
                    selectedType = systemCodeEntity;
                    type.setContent(systemCodeEntity.getValue());
                    swithLeakly(systemCodeEntity.code);
                }
            }
        }

//        selectLevel = new SystemCodeEntity();
//        List<SystemCodeEntity> levelList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemLevel);
//        if (levelList != null && levelList.size() > 0) {
//            for (SystemCodeEntity systemCodeEntity : levelList) {
//                if (StringUtil.contains(systemCodeEntity.getCode(), defectModelEntity.problemLevel)) {
//                    selectLevel = systemCodeEntity;
//                    level.setContent(systemCodeEntity.getValue());
//                }
//            }
//        }

        selectedDepartment = new BaseIntIdNameEntity();
        selectedDepartment.setName(defectModelEntity.eamDeptName);
        selectedDepartment.setId(defectModelEntity.eamDeptId);
        equip_department.setContent(defectModelEntity.eamDeptName);

        findtime.setContent(defectModelEntity.findTime);
        if (!StringUtil.isBlank(defectModelEntity.findTime)) {
            findTimeLong = DateUtil.dateFormat(defectModelEntity.findTime, "yyyy-MM-dd HH:mm:ss");
        }

//        planhandletime.setContent(defectModelEntity.eliminateTime);
//        if (!StringUtil.isBlank(defectModelEntity.eliminateTime)) {
//            handleTimeLong = DateUtil.dateFormat(defectModelEntity.eliminateTime, "yyyy-MM-dd HH:mm:ss");
//        }

        assessor.setContent(defectModelEntity.assessorName);
        selectedAssor = new BaseIntIdNameEntity();
        selectedAssor.setId(defectModelEntity.assessorId);
        selectedAssor.setName(defectModelEntity.assessorName);

        discover.setContent(defectModelEntity.finderName);
        selectedFinder = new BaseIntIdNameEntity();
        selectedFinder.setId(defectModelEntity.finderId);
        selectedFinder.setName(defectModelEntity.finderName);

        //泄漏的
        haslist = defectModelEntity.listed;
        if (haslist == null || !haslist) {
            leak_status.setContent(getString(R.string.defect_yes));
        } else {
            leak_status.setContent(getString(R.string.defect_no));
        }
        leak_time.setContent(defectModelEntity.listedTime);
        if (!StringUtil.isBlank(defectModelEntity.listedTime)) {
            leakTimeLong = DateUtil.dateFormat(defectModelEntity.listedTime, "yyyy-MM-dd HH:mm:ss");
        }

        leak_name.setContent(defectModelEntity.leakName);
        leak_number.setContent(defectModelEntity.listedNumber);

        if (!StringUtil.isBlank(defectModelEntity.fileJson)) {
            ArrayList<FileEntity> fileList = (ArrayList<FileEntity>) GsonUtil.jsonToList(defectModelEntity.fileJson, FileEntity.class);
            if (fileList != null && fileList.size() > 0) {
                file_list.addAllList(fileList);
            }
        }

        //
        selectedArea = new BaseCodeIdNameEntity();
        selectedArea.setName(defectModelEntity.areaName);
        selectedArea.setCode(defectModelEntity.areaCode);

        //从全局变量中获取，如果第一次进来的时候就会去获取
//        deviceEntities = getDeviceList(HandleUtils.getDeviceIdList());
//        if (deviceEntities == null || deviceEntities.size() == 0) {
//            devicename.setVisibility(View.GONE);
//        }
        if (devicename.getVisibility() == View.VISIBLE) {
            selectedDevice = new DeviceEntity();
            selectedDevice.setCode(defectModelEntity.getEamCode());
            selectedDevice.setName(defectModelEntity.eamName);
            devicename.setContent(selectedDevice.name);
        }

        address.setContent(selectedArea.getName());
    }

    private void swithLeakly(String code) {
        if (StringUtil.contains(code, "Leak")) {
            leak_ly.setVisibility(View.VISIBLE);
        } else {
            leak_ly.setVisibility(View.GONE);
        }
    }

    private void initByEmpty() {
        selectedAssor = new BaseIntIdNameEntity();
        Long id = SupPlantApplication.getAccountInfo().staffId;
        selectedAssor.setId(id);
        selectedAssor.setName(SupPlantApplication.getAccountInfo().staffName);

        selectedFinder = new BaseIntIdNameEntity();
        selectedFinder.setId(id);
        selectedFinder.setName(SupPlantApplication.getAccountInfo().staffName);

        assessor.setValue(SupPlantApplication.getAccountInfo().staffName);
        discover.setValue(SupPlantApplication.getAccountInfo().staffName);

        findtime.setDate(DateUtil.dateFormat(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
        Calendar calendar = Calendar.getInstance();
        findTimeLong = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR, 8);
//        handleTimeLong = calendar.getTimeInMillis();
//        planhandletime.setDate(DateUtil.dateFormat(handleTimeLong, "yyyy-MM-dd HH:mm:ss"));

        leakTimeLong = calendar.getTimeInMillis();
        leak_time.setDate(DateUtil.dateFormat(leakTimeLong, "yyyy-MM-dd HH:mm:ss"));

        address.setContent(selectedArea.getName());
        isdeviceView.setContent(getString(R.string.defect_no));
    }

    /**
     * 保存到本地数据库
     */
    private void saveToLocal() {
        //如果是从数据库中取出来的是不是就不用new一个对象
//        putValueToEntity(false);
        putValueToEntity(true);
        if (!defectModelEntity.isValid) {
            return;
        }

        saveFileToString();
        DatabaseManager.getDao().getDefectModelEntityDao().insertOrReplace(defectModelEntity);

        if (StringUtil.isBlank(tableNo)) {
            finish();
        } else {
            ToastUtils.show(context, getString(R.string.defect_save_local_success));
        }
    }

    /**
     * 把文件存储到本地json
     */
    private void saveFileToString() {
        if (file_list.getList() != null) {
            List<FileEntity> fileList = file_list.getList();
            String json = GsonUtil.gsonString(fileList);
            defectModelEntity.fileJson = json;
        }
    }

    /**
     * 提交数据，提交失败后，提示是否保存在本地，保存在本地的数据都可以编辑
     */
    private void submit() {
        putValueToEntity(true);

        if (!defectModelEntity.isValid) {
            return;
        }
        //如果有附件的话 还要添加附件的逻辑
        if (file_list.getLocalPathList() == null || file_list.getLocalPathList().size() == 0) {
            onLoading();
            presenterRouter.create(AddDefectAPI.class).defectEntry(defectModelEntity);
        } else {
            onLoading(getString(R.string.load_submit) + "...");
            upLoadFile();
        }

    }

    private void putValueToEntity(boolean flag) {
        if (defectModelEntity == null) {
            defectModelEntity = new DefectModelEntity();
        }
        defectModelEntity.isValid = isValid(flag);
        defectModelEntity.name = name.getContent();
        defectModelEntity.hiddenApperance = describe.getContent();

        if (selectSource != null ) {
            defectModelEntity.defectSource = selectSource.getCode();
        }

        if (selectedType != null) {
            defectModelEntity.defectType =  selectedType.getCode();
            if (StringUtil.contains(selectedType.getCode(), "Leak")){
                //如果是泄露 把其他字段传过去
                defectModelEntity.leakName = leak_name.getContent();
                defectModelEntity.listed = haslist;
                defectModelEntity.listedTime = DateUtil.dateTimeFormat(leakTimeLong);
                defectModelEntity.listedNumber = selectedListedNum;
            }
        }

//        if (selectLevel != null) {
//            defectModelEntity.problemLevel =  selectLevel.getCode();
//        }

        //:{
        //            "id":70290724115712,
        //            "code":"ZGSSB002",
        //            "name":"总公司设备002",
        //            "areaNum":"1111111"
        //        }
        //
        //     "problemSource":{
        //            "id":76595634910464,
        //            "code":"",
        //            "name":"巡检"
        //        }

        if (selectedDevice != null) {
            LogUtil.e(selectedDevice.getName());
            defectModelEntity.eamCode = selectedDevice.getCode();
            defectModelEntity.eamName = selectedDevice.getName();
        }

        if (selectedDepartment != null) {
            defectModelEntity.eamDeptId = selectedDepartment.getId();
            defectModelEntity.eamDeptName = selectedDepartment.getName();
        }

        if (selectedAssor != null) {
            defectModelEntity.assessorId = selectedAssor.getId();
            defectModelEntity.assessorName = selectedAssor.getName();
        }

        if (selectedFinder != null) {
            defectModelEntity.finderId = selectedFinder.getId();
            defectModelEntity.finderName = selectedFinder.getName();
        }

        if (selectedArea != null) {
            defectModelEntity.areaId = selectedArea.getId();
            defectModelEntity.areaCode = selectedArea.getCode();
            defectModelEntity.areaName = selectedArea.getName();
        }

        if (findTimeLong > 0) {
            defectModelEntity.findTime = DateUtil.dateTimeFormat(findTimeLong);
        }

//        if (handleTimeLong > 0) {
//            defectModelEntity.eliminateTime = DateUtil.dateTimeFormat(handleTimeLong);
//        }

        if (defectModelEntity.tableNo == null) {
            defectModelEntity.tableNo = tableNo;
        }

        if (defectModelEntity.dbId == null) {
            Long id = Calendar.getInstance().getTimeInMillis();
            defectModelEntity.dbId = new Long(id);
        }

        if (SupPlantApplication.getAccountInfo() != null) {
            defectModelEntity.cid = SupPlantApplication.getAccountInfo().companyId;
        }


        //附件的问题还没有
    }

    /**
     * 数据有效性校验
     * @return
     */
    private boolean isValid(boolean needToastTip) {
        //名称
        if (StringUtil.isBlank(name.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_name_is_null));
            return false;
        }

        //描述
        if (StringUtil.isBlank(describe.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_describe_is_null));
            return false;
        }

        //缺陷来源
        if (selectedDepartment == null) {
            toastTip(needToastTip, getString(R.string.defect_equip_department_is_null));
            return false;
        }

        //类型
        if (StringUtil.isBlank(type.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_type_is_null));
            return false;
        }

//        //等级
//        if (StringUtil.isBlank(level.getContent())) {
//            toastTip(needToastTip, getString(R.string.defect_level_is_null));
//            return false;
//        }
//
        //等级
        if (selectSource == null) {
            toastTip(needToastTip, getString(R.string.defect_source_is_null));
            return false;
        }

//        //装置
//        if (StringUtil.isBlank(equip_department.getContent())) {
//            toastTip(needToastTip, getString(R.string.defect_equip_department_is_null));
//            return false;
//        }
        //评估人
        if (StringUtil.isBlank(assessor.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_assesor_is_null));
            return false;
        }
        //发现人
        if (StringUtil.isBlank(discover.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_discover_is_null));
            return false;
        }
        //设备不能为空
        if (isDevice && StringUtil.isBlank(devicename.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_device_is_null));
            return false;
        }
        //地点
        if (!isDevice && StringUtil.isBlank(address.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_address_is_null));
            return false;
        }
        //时间
        if (StringUtil.isBlank(findtime.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_find_time_is_null));
            return false;
        }

//        //消缺时间
//        if (StringUtil.isBlank(planhandletime.getContent())) {
//            toastTip(needToastTip, getString(R.string.defect_plan_handle_time_is_null));
//            return false;
//        }

        return true;
    }

    private void toastTip(boolean needToastTip, String tipString) {
        if (!needToastTip) {
            return;
        }

        ToastUtils.show(context, tipString);
    }

    @Override
    public void defectEntrySuccess(BAP5CommonEntity entity) {
        onLoadSuccess(context.getString(R.string.submit_success));

        DatabaseManager.getDao().getDefectModelEntityDao().deleteInTx(defectModelEntity);
//        if (tableNo == null) {

        if (isFromAll) {
            RefreshEvent refreshEvent = new RefreshEvent();
            refreshEvent.setTag("refreshDefectAdd");
            EventBus.getDefault().post(refreshEvent);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                back();
            }
        }).start();
//        }
    }

    @Override
    public void defectEntryFailed(String errorMsg) {
//        onLoadFailed(context.getString(R.string.defect_submit_failed) + errorMsg + context.getString(R.string.defect_submit_failed_save_to_local));
        onLoadFailed(context.getString(R.string.defect_submit_failed) + errorMsg);
        if (!isFromAll) {
            defectModelEntity.setDefectFile(null);//如果提交失败了 就要重新上传
            //提示用户保存在本地，但是不能重复保存啊,数据库的id是怎么回事
            saveFileToString();
            //把数据在本地数据库中更新
            DatabaseManager.getDao().getDefectModelEntityDao().update(defectModelEntity);
        }

    }

    @Override
    public void defectEntryBatchSuccess(BAP5CommonEntity entity) {

    }

    @Override
    public void defectEntryBatchFailed(String errorMsg) {

    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void choosePerson(String type) {
        chooseType = type;
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.IntentKey.IS_MULTI, false);
        bundle.putBoolean(Constant.IntentKey.IS_SELECT, true);
        IntentRouter.go(context, Constant.Router.CONTACT_SELECT, bundle);
    }

    private void chooseDepartment(String type) {
        chooseType = type;
        IntentRouter.go(context, Constant.Router.DEPART_SELECT);
    }

    /**
     * @param event
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSelectSourceType(SelectDataEvent event) {
        if (StringUtil.equalsIgnoreCase(event.getSelectTag(), TAG_AREA)) {
            ArrayList<RegionExEntity> onelist = (ArrayList<RegionExEntity>) event.getEntity();
            if (onelist != null) {
                RegionExEntity selectEntity = onelist.get(0);
                selectedArea = new BaseCodeIdNameEntity();
                selectedArea.setId(selectEntity.getId());
                selectedArea.setCode(selectEntity.get_code());
                selectedArea.setName(selectEntity.getRegionName());

                address.setContent(selectEntity.getRegionName());
            }
        } else if (StringUtil.equalsIgnoreCase(event.getSelectTag(), TAG_DEVICE_REF)) {
            DeviceEntity deviceEntity = (DeviceEntity) event.getEntity();
            if (deviceEntity != null) {
                selectedDevice = deviceEntity;
                devicename.setContent(deviceEntity.getName());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSelect(SelectDataEvent event) {
        if (event.getEntity() instanceof DepartmentEntity) {
            DepartmentEntity selectDepartmentEntity = (DepartmentEntity) event.getEntity();
            switch (chooseType) {
                case "find_department":
                    if (selectedDepartment == null) {
                        selectedDepartment = new BaseIntIdNameEntity();
                    }
                    selectedDepartment.setId(selectDepartmentEntity.id);
                    selectedDepartment.setName(selectDepartmentEntity.name);

                    equip_department.setValue(selectDepartmentEntity.name);
                    break;
                default:
                    break;
            }
        } else if (event.getEntity() instanceof ContactEntity) {
            ContactEntity selectContactEntity = (ContactEntity) event.getEntity();
            switch (chooseType) {
                case "find_person":
                    selectedFinder.setId(selectContactEntity.staffId);
                    selectedFinder.setName(selectContactEntity.name);

                    discover.setValue(selectContactEntity.name);
//                    findDepartment.setId(selectContactEntity.getDepartmentCurrent().id);
//                    findDepartment.setName(selectContactEntity.getDepartmentCurrent().name);
//                    the_department.setValue(selectContactEntity.getDepartmentCurrent().name);
                    break;
                case "assor_person":
                    selectedAssor.setId(selectContactEntity.staffId);
                    selectedAssor.setName(selectContactEntity.name);

                    assessor.setValue(selectContactEntity.name);
//                    dutyDepartment.setId(selectContactEntity.getDepartmentCurrent().id);
//                    dutyDepartment.setName(selectContactEntity.getDepartmentCurrent().name);
//                    cd_charge_department.setValue(selectContactEntity.getDepartmentCurrent().name);
                    break;
                default:
                    break;

            }
        } else if (StringUtil.equalsIgnoreCase(event.getSelectTag(), TAG_DEVICE_REF)){
            if (event instanceof SelectDataEvent) {
                DeviceEntity selectEntity = (DeviceEntity) event.getEntity();
                if (selectEntity != null) {
                    selectedDevice = selectEntity;
                    devicename.setContent(selectedDevice.name);
                }
            }
        }
    }

    //上传附件
    private void upLoadFile() {
        presenterRouter.create(AddFileListAPI.class).uploadMultiFiles(file_list.getLocalPathList());
    }


    @Override
    public void uploadMultiFilesSuccess(ArrayList entity) {
        if (entity != null) {
            ArrayList<FileEntity> filelist = entity;
            if (filelist != null && filelist.size() > 0) {
                List<FileUploadDefectEntity> uploadFileFormMapArrayList = HandleUtils.converFileToUploadFile(filelist);
                String fileListJson = GsonUtil.gsonString(uploadFileFormMapArrayList);
                defectModelEntity.setDefectFile(fileListJson);
            }

            presenterRouter.create(AddDefectAPI.class).defectEntry(defectModelEntity);
        }
    }



    @Override
    public void uploadMultiFilesFailed(String errorMsg) {
        onLoadFailed(errorMsg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            file_list.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        file_list.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private List<DeviceEntity> getDeviceList(String idList) {
        if (!TextUtils.isEmpty(idList)) {
            List<DeviceEntity> deviceEntityList = new ArrayList<>();
            Object[] eamIdList = idList.split(",");
//            for (String eamCode : eamIdList) {
                //根据设备eamId获取CommonDeviceEntity
                try {
                    List<DeviceEntity> listall = SupPlantApplication.dao().getDeviceEntityDao().queryBuilder()
                            .where(DeviceEntityDao.Properties.Code.in(eamIdList)).list();
//                    DeviceEntity commonDeviceEntity = SupPlantApplication.dao().getDeviceEntityDao().queryBuilder()
//                            .where(DeviceEntityDao.Properties.Code.eq(eamCode)).unique();
                    if (listall != null && listall.size() > 0) {
                        deviceEntityList.addAll(listall);
                    }
                } catch (Exception e) {

                }
//            }
            return deviceEntityList;
        }
        return null;
    }


    @Override
    public void getDefectSourceListSuccess(BAP5CommonEntity entity) {
        if (entity == null) {
            return;
        }
        BapPageResultEntity pageResultEntity = (BapPageResultEntity) entity.data;
        if (pageResultEntity == null) {
            return;
        }
        sourceTypeList = (ArrayList<DefectSourceEntity>) pageResultEntity.getResult();
    }

    @Override
    public void getDefectSourceListFailed(String errorMsg) {

    }

    @Override
    public void listedRefQuerySuccess(BapPageResultEntity entity) {
        if (entity != null) {
            numEntityList = entity.getResult();
        }
    }

    @Override
    public void listedRefQueryFailed(String errorMsg) {

    }
}
