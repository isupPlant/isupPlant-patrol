package com.supcon.mes.module_defectmanage.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.GetDefectListAPI;
import com.supcon.mes.module_defectmanage.ui.fragment.DefectOnlineFragment;
import com.supcon.mes.module_defectmanage.ui.fragment.DefectOutlineFragment;
import com.supcon.mes.module_defectmanage.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;



@Router(value = Utils.AppCode.DEFECT_MANAGEMENT_OFF_LINE_LIST)
public class DefectOfflineListActivity extends BaseControllerActivity {
    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("rightBtn_text")
    Button rightBtn_text;
    @BindByTag("titleText")
    TextView titleText;
    @BindByTag("viewpager")
    ViewPager viewpager;
    @BindByTag("tablayout")
    TabLayout tablayout;

    String tableNo, areaCode;
    DefectOutlineFragment defectOutlineFragment;
    DefectOnlineFragment defectOnlineFragment;

    private List<Fragment> pages = new ArrayList<>();


    @Override
    protected int getLayoutID() {
        return R.layout.ac_defect_off_line_list;
    }

    @Override
    protected void initData() {
        super.initData();

        //情况分类：1、从巡检传过来的数据2、从列表中过来的某一条数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tableNo = bundle.getString(Constant.IntentKey.XJ_TASK_TABLENO);
            areaCode = bundle.getString(Constant.IntentKey.XJ_AREA_CODE);
        }



        defectOnlineFragment.setInfo(tableNo, areaCode);
        defectOutlineFragment.setInfo(tableNo, areaCode);
    }

    @Override
    protected void initView() {
        super.initView();

        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);

        titleText.setText(R.string.defect_off_line_list_title);
        rightBtn_text.setVisibility(View.VISIBLE);
        rightBtn_text.setText(getString(R.string.defect_choose));

        defectOutlineFragment = new DefectOutlineFragment();
        defectOnlineFragment = new DefectOnlineFragment();
        pages.add(defectOutlineFragment);
        pages.add(defectOnlineFragment);
        viewpager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewpager.setCurrentItem(0);
        viewpager.setOffscreenPageLimit(1);
        tablayout.setupWithViewPager(viewpager);

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    rightBtn_text.setVisibility(View.VISIBLE);
                } else {
                    rightBtn_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        String[] titleList = getResources().getStringArray(R.array.defect_list_tab);
        for (int i = 0; i < tablayout.getTabCount(); i++) {
            tablayout.getTabAt(i).setText(titleList[i]);
        }
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

        Disposable disposableRight = RxView.clicks(rightBtn_text)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    defectOutlineFragment.resetLltButtom();
                });
    }



//    @Override
//    public void defectEntryBatchSuccess(BAP5CommonEntity entity) {
//        //删除上传的数据
//        List<DefectModelEntity> list = new ArrayList<>();
//        for (DefectModelEntity value : checkedMap.values()) {
//            list.add(value);
//        }
//        DatabaseManager.getDao().getDefectModelEntityDao().deleteInTx(list);
//
//
//        onLoadSuccess();
//        ToastUtils.show(context, context.getString(R.string.submit_success));
//    }
//
//    @Override
//    public void defectEntryBatchFailed(String errorMsg) {
//        onLoadSuccess();
//        //提示用户保存在本地，但是不能重复保存啊,数据库的id是怎么回事
//        ToastUtils.show(context, context.getString(R.string.defect_submit_failed_save_to_local));
//    }


    private class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return pages.get(i);
        }

        @Override
        public int getCount() {
            return pages.size();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenterRouter.create(GetDefectListAPI.class).getDefectList(tableNo, areaCode, 1);
    }



//    @Override
//    public void uploadMultiFilesSuccess(ArrayList entity) {
//        closeLoader();
//
//        if (entity != null) {
//            ArrayList<FileEntity> filelist = entity;
//            List<FileUploadDefectEntity> uploadFileFormMapArrayList = null;
//            if (filelist != null && filelist.size() > 0) {
//                uploadFileFormMapArrayList  = HandleUtils.converFileToUploadFile(filelist);
//            }
//
//            List<DefectModelEntity> list = new ArrayList<>();
//            for (DefectModelEntity value : checkedMap.values()) {
//                if (!StringUtil.isBlank(value.fileJson) && uploadFileFormMapArrayList != null) {
//                    String json = value.getFileJson();
//                    List<FileUploadDefectEntity> defectFileList = new ArrayList<>();
//                    for (FileUploadDefectEntity fileUploadDefectEntity : uploadFileFormMapArrayList) {
//                        if (StringUtil.contains(json, fileUploadDefectEntity.getFilename())) {
//                            //
//                            defectFileList.add(fileUploadDefectEntity);
//                        }
//                    }
//                    value.setDefectFile(defectFileList);
//                }
//                list.add(value);
//            }
//
//            onLoading();
//            presenterRouter.create(AddDefectAPI.class).defectEntryBatch(list);
//        }
//    }
//
//    @Override
//    public void uploadMultiFilesFailed(String errorMsg) {
//        closeLoader();
//        ToastUtils.show(context, errorMsg);
//    }
}
