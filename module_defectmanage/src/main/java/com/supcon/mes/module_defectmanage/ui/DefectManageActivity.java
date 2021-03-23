package com.supcon.mes.module_defectmanage.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.supcon.mes.middleware.model.api.AddFileListAPI;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.BaseCodeIdNameEntity;
import com.supcon.mes.middleware.model.bean.BaseIdValueEntity;
import com.supcon.mes.middleware.model.bean.BaseIntIdNameEntity;
import com.supcon.mes.middleware.model.bean.ContactEntity;
import com.supcon.mes.middleware.model.bean.DataUtil;
import com.supcon.mes.middleware.model.bean.DepartmentEntity;
import com.supcon.mes.middleware.model.bean.FileEntity;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.model.contract.AddFileListContract;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.presenter.AddFileListPresenter;
import com.supcon.mes.middleware.ui.view.AddFileListView;
import com.supcon.mes.middleware.ui.view.FileListView;
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.AddDefectAPI;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntityDao;
import com.supcon.mes.module_defectmanage.model.bean.FileUploadDefectEntity;
import com.supcon.mes.module_defectmanage.model.contract.AddDefectContract;
import com.supcon.mes.module_defectmanage.presenter.AddDefectPresenter;
import com.supcon.mes.module_defectmanage.util.DatabaseManager;
import com.supcon.mes.module_defectmanage.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;


@Presenter(value = {AddDefectPresenter.class, AddFileListPresenter.class})
@Controller(value = {SystemCodeJsonController.class})
@SystemCode(entityCodes = {Constant.SystemCode.DefectManage_problemClass, Constant.SystemCode.DefectManage_problemLevel})
@Router(value = Constant.AppCode.DEFECT_MANAGEMENT_ADD)
public class DefectManageActivity extends BaseControllerActivity implements AddDefectContract.View, AddFileListContract.View {

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
    @BindByTag("level")
    CustomTextView level;
    @BindByTag("devicename")
    CustomTextView devicename;
    @BindByTag("equip_department")
    CustomTextView equip_department;
    @BindByTag("assessor")
    CustomTextView assessor;
    @BindByTag("discover")
    CustomTextView discover;
    @BindByTag("address")
    CustomEditText address;
    @BindByTag("findtime")
    CustomDateView findtime;
    @BindByTag("planhandletime")
    CustomDateView planhandletime;
    @BindByTag("leak_name")
    CustomEditText leak_name;
    @BindByTag("leak_status")
    CustomTextView leak_status;
    @BindByTag("leak_number")
    CustomEditText leak_number;
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

    SystemCodeEntity selectedType, selectLevel;
    BaseCodeIdNameEntity selectSource;
    BaseCodeIdNameEntity selectedEamInfo;

    BaseIntIdNameEntity selectedDepartment;
    BaseIntIdNameEntity selectedAssor, selectedFinder;

    BaseCodeIdNameEntity  selectedArea;
    long findTimeLong, handleTimeLong;
    DefectModelEntity defectModelEntity;
    Long tableNo;

    String chooseType;


    @Override
    protected int getLayoutID() {
        return R.layout.ac_defect_add;
    }

    @Override
    protected void initData() {
        super.initData();

        //情况分类：1、从巡检传过来的数据2、从列表中过来的某一条数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Long dataId = bundle.getLong(Constant.INTENT_EXTRA_ID);
            tableNo = bundle.getLong(Constant.INTENT_EXTRA_OBJECT_CHAT);
            if (dataId != null && dataId.longValue() > 0) {
                //说明是从列表中过来的
                rightBtn.setVisibility(View.GONE);
                List<DefectModelEntity> list = DatabaseManager.getDao().getDefectModelEntityDao().queryBuilder()
                        .where(DefectModelEntityDao.Properties.DbId.eq(dataId)).list();
                if (list != null && list.size() > 0) {
                    defectModelEntity = list.get(0);
                }
            } else {
                //从另外的实体来的进行初始化
                defectModelEntity = new DefectModelEntity();

            }
        }

        if (defectModelEntity == null) {
            finish();
        }

        initDatePickController();
        initSinglePickController();

        if (tableNo != null && tableNo.longValue() > 0) {
            initByEmpty();
        } else {
            initByEditInfo();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);

        titleText.setText(R.string.defect_add_file);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_all_p));

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

        planhandletime.setOnChildViewClickListener((childView, action, obj) -> {
            if (action == -1) {
            } else {
                mDatePickController
                        .listener((year, month, day, hour, minute, second) -> {

                            String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                            handleTimeLong = DateUtil.dateFormat(dateStr, "yyyy-MM-dd HH:mm:ss");
                            planhandletime.setDate(DateUtil.dateFormat(handleTimeLong, "yyyy-MM-dd HH:mm:ss"));
                        })
                        .show(handleTimeLong);
            }
        });

        source.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    //什么情况下会这样？？？
                    selectSource = null;
                } else {
//                //跳转到哪个页面去到底
//                if (regionEntityList == null || regionEntityList.size() == 0) {
//                    //选中的不展示；如果列表为空就不能跳转
//                    return;
//                }
//                clickTag = tag_target;
//
//                startByDeviceController = true;
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(Constant.INTENT_EXTRA_OBJECT, regionEntityList);
//                bundle.putString(Constant.INTENT_EXTRA_ID, context.getString(R.string.psc_device_region_choose));
//                IntentRouter.go(context, Constant.Router.NODE_TREE, bundle);
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

        level.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if (action == -1) {
                    selectLevel = null;
                    leak_ly.setVisibility(View.GONE);
                } else {
                    //如果选择的是泄漏的话
                    final List<SystemCodeEntity> systemCodeEntityList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemLevel);
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
                                selectLevel = systemCodeEntityList.get(index);
                                level.setContent((String) item);
                                if (selectLevel != null && StringUtil.contains(selectLevel.code, "Leak")) {
                                    leak_ly.setVisibility(View.VISIBLE);
                                } else {
                                    leak_ly.setVisibility(View.GONE);
                                }
                            });
                    stringSinglePickController.show(level.getContent());
                }
            }
        });

        Disposable disposable = RxView.clicks(submitBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    submit();
                });

        Disposable disposableSave = RxView.clicks(saveBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    saveToLocal();

                    if (tableNo == null) {
                        finish();
                    }
                });

        Disposable disposableList = RxView.clicks(rightBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constant.INTENT_EXTRA_OBJECT_CHAT, tableNo);
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

        selectLevel = new SystemCodeEntity();
        List<SystemCodeEntity> levelList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemLevel);
        if (levelList != null && levelList.size() > 0) {
            for (SystemCodeEntity systemCodeEntity : levelList) {
                if (StringUtil.contains(systemCodeEntity.getCode(), defectModelEntity.problemLevel)) {
                    selectLevel = systemCodeEntity;
                    level.setContent(systemCodeEntity.getValue());
                }
            }
        }

        selectedDepartment = new BaseIntIdNameEntity();
        selectedDepartment.setName(defectModelEntity.eamDeptName);
        selectedDepartment.setId(defectModelEntity.eamDeptId);
        equip_department.setContent(defectModelEntity.eamDeptName);

        findtime.setContent(defectModelEntity.findTime);
        if (!StringUtil.isBlank(defectModelEntity.findTime)) {
            findTimeLong = DateUtil.dateFormat(defectModelEntity.findTime, "yyyy-MM-dd HH:mm:ss");
        }

        planhandletime.setContent(defectModelEntity.eliminateTime);
        if (!StringUtil.isBlank(defectModelEntity.eliminateTime)) {
            handleTimeLong = DateUtil.dateFormat(defectModelEntity.eliminateTime, "yyyy-MM-dd HH:mm:ss");
        }

        assessor.setContent(defectModelEntity.assessorName);
        selectedAssor = new BaseIntIdNameEntity();
        selectedAssor.setId(defectModelEntity.assessorId);
        selectedAssor.setName(defectModelEntity.assessorName);

        discover.setContent(defectModelEntity.finderName);
        selectedFinder = new BaseIntIdNameEntity();
        selectedFinder.setId(defectModelEntity.finderId);
        selectedFinder.setName(defectModelEntity.finderName);
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
        handleTimeLong = calendar.getTimeInMillis();
        planhandletime.setDate(DateUtil.dateFormat(handleTimeLong, "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 保存到本地数据库
     */
    private void saveToLocal() {
        //如果是从数据库中取出来的是不是就不用new一个对象
        putValueToEntity(false);
        saveFileToString();
        DatabaseManager.getDao().getDefectModelEntityDao().insertOrReplace(defectModelEntity);

        if (tableNo == null || tableNo.longValue() < 1) {
            finish();
        } else {
            ToastUtils.show(context, getString(R.string.middleware_save_succeed));
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

//        if (selectedType != null) {
//            defectModelEntity.defectSource = selectSource.getCode();
//        }

        if (selectedType != null) {
            defectModelEntity.defectType =  selectedType.getCode();
        }

        if (selectLevel != null) {
            defectModelEntity.problemLevel =  selectLevel.getCode();
        }

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

        defectModelEntity.eamCode = "ZGSSB002";
        defectModelEntity.defectSource = "OSI";
//        if (selectedEamInfo != null) {
//            defectModelEntity.eamCode = selectedEamInfo.getCode();
//        }


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

        defectModelEntity.areaId = 1005L;
//        if (selectedArea != null) {
//            defectModelEntity.areaCode = selectedArea.getCode();
//        }

        if (findTimeLong > 0) {
            defectModelEntity.findTime = DateUtil.dateTimeFormat(findTimeLong);
        }

        if (handleTimeLong > 0) {
            defectModelEntity.eliminateTime = DateUtil.dateTimeFormat(handleTimeLong);
        }

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

//        //缺陷来源
//        if (StringUtil.isBlank(source.getContent())) {
//            toastTip(needToastTip, getString(R.string.defect_source_is_null));
//            return false;
//        }

        //类型
        if (StringUtil.isBlank(type.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_type_is_null));
            return false;
        }

        //等级
        if (StringUtil.isBlank(level.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_level_is_null));
            return false;
        }

        //等级
        if (StringUtil.isBlank(level.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_level_is_null));
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
        //地点
        if (StringUtil.isBlank(address.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_address_is_null));
            return false;
        }
        //时间
        if (StringUtil.isBlank(findtime.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_find_time_is_null));
            return false;
        }

        //消缺时间
        if (StringUtil.isBlank(planhandletime.getContent())) {
            toastTip(needToastTip, getString(R.string.defect_plan_handle_time_is_null));
            return false;
        }

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
        onLoadSuccess();
        ToastUtils.show(context, context.getString(R.string.submit_success));

//        if (tableNo == null) {
            finish();
//        }
    }

    @Override
    public void defectEntryFailed(String errorMsg) {
        closeLoader();
        //提示用户保存在本地，但是不能重复保存啊,数据库的id是怎么回事
        ToastUtils.show(context, errorMsg + context.getString(R.string.defect_submit_failed_save_to_local));

        saveFileToString();
        //把数据在本地数据库中更新
        DatabaseManager.getDao().getDefectModelEntityDao().update(defectModelEntity);
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
        }
    }

    //上传附件
    private void upLoadFile() {
        presenterRouter.create(AddFileListAPI.class).uploadMultiFiles(file_list.getLocalPathList());
    }


    @Override
    public void uploadMultiFilesSuccess(ArrayList entity) {
        closeLoader();
        if (entity != null) {
            ArrayList<FileEntity> filelist = entity;
            if (filelist != null && filelist.size() > 0) {
                List<FileUploadDefectEntity> uploadFileFormMapArrayList = converFileToUploadFile(filelist);
                defectModelEntity.setList(uploadFileFormMapArrayList);
            }

            submit();
        }
    }

    private List<FileUploadDefectEntity> converFileToUploadFile(List<FileEntity> fileEntities) {
        List<FileUploadDefectEntity> resultList = new ArrayList<>();
        if (fileEntities != null && fileEntities.size() > 0) {
            for (FileEntity fileEntity : fileEntities) {
                FileUploadDefectEntity entity = new FileUploadDefectEntity();
                entity.setFileIcon(fileEntity.getFileIcon());
                entity.setFilename(fileEntity.getFilename());
                entity.setPath(fileEntity.getPath());
                resultList.add(entity);
            }
        }
        return resultList;
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
}
