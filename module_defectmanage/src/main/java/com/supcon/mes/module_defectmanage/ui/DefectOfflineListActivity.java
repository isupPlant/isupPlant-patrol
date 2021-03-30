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
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.GetDefectListAPI;
import com.supcon.mes.module_defectmanage.ui.fragment.DefectOnlineFragment;
import com.supcon.mes.module_defectmanage.ui.fragment.DefectOfflineFragment;
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
    DefectOfflineFragment defectOutlineFragment;
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
        rightBtn_text.setText(getString(R.string.middleware_choose_all));

        defectOutlineFragment = new DefectOfflineFragment();
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
                    if (StringUtil.equalsIgnoreCase(rightBtn_text.getText().toString(), getString(R.string.middleware_choose_all))) {
                        rightBtn_text.setText(getString(R.string.defect_check_plan_cancel_all));
                        defectOutlineFragment.setAllChoose(true);
                    } else {
                        rightBtn_text.setText(getString(R.string.middleware_choose_all));
                        defectOutlineFragment.setAllChoose(false);
                    }
                });
    }


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


    public void setRightButton(boolean allChoose) {
        if (allChoose) {
            rightBtn_text.setText(getString(R.string.defect_check_plan_cancel_all));
        } else {
            rightBtn_text.setText(getString(R.string.middleware_choose_all));
        }
    }
}
