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
import com.supcon.mes.middleware.model.bean.BaseIntIdNameEntity;
import com.supcon.mes.middleware.model.bean.ContactEntity;
import com.supcon.mes.middleware.model.bean.DepartmentEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntity;
import com.supcon.mes.middleware.model.bean.DeviceEntityDao;
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
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.AddDefectAPI;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntityDao;
import com.supcon.mes.module_defectmanage.model.bean.DefectOnlineEntity;
import com.supcon.mes.module_defectmanage.model.bean.DeviceSelected;
import com.supcon.mes.module_defectmanage.model.bean.FileUploadDefectEntity;
import com.supcon.mes.module_defectmanage.model.contract.AddDefectContract;
import com.supcon.mes.module_defectmanage.presenter.AddDefectPresenter;
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



@Router(value = Utils.AppCode.DEFECT_MANAGEMENT_ON_LINE_DETAIL)
public class DefectManageDetailActivity extends BaseControllerActivity  {

    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("name")
    CustomTextView name;
    @BindByTag("describe")
    CustomTextView describe;
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
    CustomTextView findtime;
    @BindByTag("planhandletime")
    CustomTextView planhandletime;
    @BindByTag("leak_name")
    CustomEditText leak_name;
    @BindByTag("leak_status")
    CustomTextView leak_status;
    @BindByTag("leak_number")
    CustomEditText leak_number;
    @BindByTag("leak_time")
    CustomDateView leak_time;

    @BindByTag("leak_ly")
    LinearLayout leak_ly;
    @BindByTag("rightBtn")
    ImageButton rightBtn;
    @BindByTag("titleText")
    TextView titleText;
    @BindByTag("file_list")
    FileListView file_list;

    DefectOnlineEntity entity;

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
            entity = (DefectOnlineEntity) bundle.getSerializable(Constant.INTENT_EXTRA_OBJECT);
        }

        if (entity == null) {
            finish();
        }

    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);

        titleText.setText(R.string.defect_add_file);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_all_p));

        source.setContent(R.string.defect_source_osi);

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


    }


}
