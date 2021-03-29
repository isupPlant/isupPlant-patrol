package com.supcon.mes.module_defectmanage.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomDateView;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.FileEntity;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.ui.view.FileListView;
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.bean.DefectOnlineEntity;
import com.supcon.mes.module_defectmanage.util.Utils;

import java.util.ArrayList;
import java.util.List;


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
    CustomTextView address;
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
    @BindByTag("status")
    CustomTextView status;

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
        return R.layout.ac_defect_detail;
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

        initByEntity();
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);

        titleText.setText(R.string.defect_detail);
        rightBtn.setVisibility(View.GONE);

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


    private void setFileList(ArrayList<FileEntity> list) {
        if(list!=null&&list.size()!=0){
            file_list.setVisibility(View.VISIBLE);
            file_list.setList(list);
        }
    }

    private void initByEntity() {
        describe.setContent(entity.getHiddenApperance());
        if (entity.getDefectSource() != null) {
            source.setContent(entity.getDefectSource().getName());
        }
        if (entity.getDefectState() != null) {
            status.setContent(entity.getDefectState().getName());
        }
        if (entity.getDefectType() != null) {
            type.setContent(entity.getDefectType().getName());
        }
        if (entity.getProblemLevel() != null) {
            level.setContent(entity.getProblemLevel().getName());
        }

        devicename.setContent(getString(R.string.defect_source_osi));
        if (entity.getEam() != null) {
            devicename.setContent(entity.getEam().getName());
        }
        if (entity.getEamDept() != null) {
            equip_department.setContent(entity.getEamDept().getName());
        }
        if (entity.getAssessor() != null) {
            assessor.setContent(entity.getAssessor().getName());
        }
        if (entity.getFinder() != null) {
            discover.setContent(entity.getFinder().getName());
        }

        if (entity.getArea() != null) {
            address.setContent(entity.getArea().getName());
        }
        findtime.setContent(entity.getFindTime() + "");

        name.setContent(entity.getName());

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
        file_list.setModuleCode("DefectManage");
        file_list.setEntityCode("DefectManage_6.0.0.1_problemManage");

        setFileList((ArrayList<FileEntity>) entity.getDefectFile());

        //
        if (entity.getDefectType() != null && StringUtil.contains(entity.getDefectType().getCode(), "Leak")) {
            showLeakllt();
        }
    }

    private void showLeakllt() {
        leak_ly.setVisibility(View.VISIBLE);
        leak_name.setEditable(false);
        leak_name.setNecessary(false);
        leak_name.setHint("");

        leak_status.setEditable(false);
        leak_status.setNecessary(false);

        leak_number.setEditable(false);
        leak_number.setNecessary(false);
        leak_number.setHint("");

        leak_time.setEditable(false);
        leak_time.setNecessary(false);

        if (entity.getListed() != null && entity.getListed()) {
            leak_status.setContent(getString(R.string.yes));
        } else {
            leak_status.setContent(getString(R.string.no));
        }

        if (!StringUtil.isBlank(entity.getListedTime())) {
            leak_time.setContent(entity.getListedTime());
        }

        if (!StringUtil.isBlank(entity.getLeakName())) {
            leak_name.setContent(entity.getLeakName());
        }

        if (!StringUtil.isBlank(entity.getListedNumber())) {
            leak_number.setContent(entity.getListedNumber());
        }
    }
}
