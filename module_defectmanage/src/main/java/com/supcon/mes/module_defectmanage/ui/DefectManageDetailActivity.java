package com.supcon.mes.module_defectmanage.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomDateView;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.FileEntity;
import com.supcon.mes.middleware.ui.view.FileListView;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.bean.DefectOnlineEntity;
import com.supcon.mes.module_defectmanage.util.Utils;

import java.util.ArrayList;


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
        file_list.setEntityCode("DefectManage_6.0.0.1_problemManage");

        setFileList((ArrayList<FileEntity>) entity.getDefectFile());
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
}