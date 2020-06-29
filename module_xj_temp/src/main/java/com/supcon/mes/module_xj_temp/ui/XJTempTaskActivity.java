package com.supcon.mes.module_xj_temp.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.constant.ViewAction;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.Area;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.model.listener.DateSelectListener;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.middleware.util.XJCacheUtil;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.event.XJTempTaskAddEvent;
import com.supcon.mes.module_xj_temp.IntentRouter;
import com.supcon.mes.module_xj_temp.R;
import com.supcon.mes.module_xj_temp.controller.XJTempTimeController;
import com.supcon.mes.module_xj_temp.ui.adapter.XJTempAreaAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/5/25
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_TEMP_TASK)
@Controller(XJTempTimeController.class)
public class XJTempTaskActivity extends BaseControllerActivity {

    @BindByTag("leftBtn")
    CustomImageButton leftBtn;

    @BindByTag("rightBtn")
    CustomImageButton rightBtn;

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("xjTempTaskRouteSelect")
    CustomTextView xjTempTaskRouteSelect;

    @BindByTag("xjTempTaskTimeSelect")
    CustomTextView xjTempTaskTimeSelect;

    @BindByTag("xjTempAreaList")
    RecyclerView xjTempAreaList;

    private XJTempAreaAdapter mXJTempAreaAdapter;
    private XJTaskEntity mXJTaskEntity;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_temp_task;
    }

    @Override
    protected void onInit() {
        super.onInit();
        mXJTaskEntity = new XJTaskEntity();
        mXJTaskEntity.areas = new ArrayList<>();
        mXJTaskEntity.isTemp = true;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSelect(SelectDataEvent event) {
        if("selectRoute".equals(event.getSelectTag())){
            mXJTaskEntity.workRoute = (XJRouteEntity) event.getEntity();
            mXJTaskEntity.patrolType = SystemCodeManager.getInstance().getSystemCodeEntity(mXJTaskEntity.workRoute.patrolType.id);
//            mXJTaskEntity.workRoute.name = mXJTaskEntity.workRoute.name+"Temp";
            mXJTaskEntity.tableNo = "Temp"+new Date().getTime();
            mXJTaskEntity.staffName = SupPlantApplication.getAccountInfo().staffName;

            xjTempTaskRouteSelect.setContent(mXJTaskEntity.workRoute.name+getResources().getString(R.string.xj_temp_task));

            List<XJAreaEntity> areaEntities = SupPlantApplication.dao().getXJAreaEntityDao().queryBuilder()
                    .where(XJAreaEntityDao.Properties.WorkRouteId.eq(mXJTaskEntity.workRoute.id))
                    .where(XJAreaEntityDao.Properties.Valid.eq(true))
                    .where(XJAreaEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                    .list();

            mXJTaskEntity.areas.addAll(areaEntities);
            mXJTempAreaAdapter.setList(mXJTaskEntity.areas);
            mXJTempAreaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText("新增临时巡检");
        rightBtn.setImageResource(R.drawable.sl_top_submit);
        rightBtn.setVisibility(View.VISIBLE);

        xjTempAreaList.setLayoutManager(new LinearLayoutManager(context));
        xjTempAreaList.addItemDecoration(new SpaceItemDecoration(1));
        mXJTempAreaAdapter = new XJTempAreaAdapter(context, mXJTaskEntity.areas);
        xjTempAreaList.setAdapter(mXJTempAreaAdapter);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        RxView.clicks(leftBtn)
                .throttleFirst(200 , TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        onBackPressed();
                    }
                });

        RxView.clicks(rightBtn)
                .throttleFirst(200 , TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        if(mXJTaskEntity.workRoute == null){
                            ToastUtils.show(context, "请选择巡检线路");
                            return;
                        }

                        if(mXJTaskEntity.startTime == 0||mXJTaskEntity.endTime == 0){
                            ToastUtils.show(context, "请选择巡检开始和结束时间");
                            return;
                        }
                        boolean isHaveAreas=false;
                        for(XJAreaEntity xjAreaEntity:mXJTaskEntity.areas){
                            if (xjAreaEntity.isChecked){
                                isHaveAreas=true;
                            }
                        }
                        if(mXJTaskEntity.areas == null || mXJTaskEntity.areas.size() == 0||!isHaveAreas){
                            ToastUtils.show(context, "请选择巡检区域");
                            return;
                        }

                        for(int i = mXJTaskEntity.areas.size()-1; i>=0; i--){

                            XJAreaEntity xjAreaEntity = mXJTaskEntity.areas.get(i);
                            if(!xjAreaEntity.isChecked){
                                mXJTaskEntity.areas.remove(i);
                            }

                        }

                        XJCacheUtil.putString(mXJTaskEntity.tableNo, mXJTaskEntity.toString());
                        EventBus.getDefault().post(new XJTempTaskAddEvent(mXJTaskEntity));
                        back();
                    }
                });

        RxView.clicks(xjTempTaskTimeSelect)
                .throttleFirst(200 , TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        getController(XJTempTimeController.class).showCustomDialog();
                    }
                });

        xjTempTaskTimeSelect.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if(action == ViewAction.CONTENT_CLEAN.value()){
                    mXJTaskEntity.startTime = 0;
                    mXJTaskEntity.endTime = 0;
                }
                else{
                    getController(XJTempTimeController.class).showCustomDialog();
                }
            }
        });

        xjTempTaskRouteSelect.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if(action == ViewAction.CONTENT_CLEAN.value()){
                    mXJTaskEntity.workRoute = null;
                    mXJTaskEntity.patrolType = null;
                    mXJTaskEntity.tableNo = null;
                    mXJTaskEntity.areas.clear();
                    mXJTempAreaAdapter.notifyDataSetChanged();
                }
                else{
                    IntentRouter.go(context, Constant.Router.XJ_ROUTE_LIST);
                }
            }
        });

        getController(XJTempTimeController.class).setDateSelectListener(new DateSelectListener() {
            @Override
            public void onDateSelect(String start, String end) {

                if(TextUtils.isEmpty(start)){
                    mXJTaskEntity.startTime = 0;
                }
                else{
                    mXJTaskEntity.startTime = DateUtil.dateFormat(start, "yyyy-MM-dd HH:mm:ss");
                }

                if(TextUtils.isEmpty(end)){
                    mXJTaskEntity.endTime = 0;
                }
                else{
                    mXJTaskEntity.endTime = DateUtil.dateFormat(end, "yyyy-MM-dd HH:mm:ss");
                }


                if (!TextUtils.isEmpty(start)&&!TextUtils.isEmpty(end)) {
                    StringBuilder stringBuilder = new StringBuilder(start);
                    stringBuilder.append("--");
                    stringBuilder.append(end);
                    xjTempTaskTimeSelect.setContent(stringBuilder.toString());
                }else{
                    xjTempTaskTimeSelect.setContent("");
                }

            }
        });
//
//        mXJTempAreaAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
//            @Override
//            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
//
//            }
//        });
    }


    @Override
    public void onBackPressed() {

        new CustomDialog(context)
                .twoButtonAlertDialog(mXJTaskEntity.areas!=null? getString(R.string.xj_temp_task_exit_warning) : getString(R.string.xj_temp_task_exit_warning2))
                .bindView(R.id.grayBtn,"留下")
                .bindView(R.id.redBtn,"离开")
                .bindClickListener(R.id.grayBtn, v -> {}, true)
                .bindClickListener(R.id.redBtn, v -> super.onBackPressed()
                , true)
                .show();

    }
}
