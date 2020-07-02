package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.listener.OnRefreshListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.constant.TemperatureMode;
import com.supcon.mes.middleware.controller.CheckUserPermissionController;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.api.DeploymentAPI;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.DeploymentEntity;
import com.supcon.mes.middleware.model.contract.DeploymentContract;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.model.listener.DateSelectListener;
import com.supcon.mes.middleware.model.listener.OnAPIResultListener;
import com.supcon.mes.middleware.model.listener.OnSuccessListener;
import com.supcon.mes.middleware.presenter.DeploymentPresenter;
import com.supcon.mes.middleware.util.SBTUtil;
import com.supcon.mes.middleware.util.TimeUtil;
import com.supcon.mes.middleware.util.XJCacheUtil;
import com.supcon.mes.module_xj.IntentRouter;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.controller.XJLocalTaskController;
import com.supcon.mes.module_xj.controller.XJTaskDateFilterController;
import com.supcon.mes.module_xj.controller.XJTaskNoIssuedController;
import com.supcon.mes.module_xj.controller.XJTaskStatusFilterController;
import com.supcon.mes.module_xj.controller.XJTaskUploadController;
import com.supcon.mes.module_xj.model.api.XJTaskAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.bean.XJTaskGroupEntity;
import com.supcon.mes.module_xj.model.contract.XJTaskContract;
import com.supcon.mes.module_xj.model.event.XJTempTaskAddEvent;
import com.supcon.mes.module_xj.presenter.XJTaskPresenter;
import com.supcon.mes.module_xj.ui.adapter.XJTaskGroupAdapter;
import com.supcon.mes.sb2.config.SB2Config;
import com.supcon.mes.sb2.controller.SB2Controller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/1/9
 * Email:wangshizhan@supcom.com
 */
@Router(value = Constant.AppCode.MPS_Patrol)
@Controller(value = {SystemCodeJsonController.class, CheckUserPermissionController.class,
        XJTaskDateFilterController.class, XJTaskStatusFilterController.class,/*顶部筛选*/
        XJTaskNoIssuedController.class, XJTaskUploadController.class,/*获取，上传*/
        XJLocalTaskController.class})
@Presenter(value = {XJTaskPresenter.class, DeploymentPresenter.class})
@SystemCode(entityCodes = {
        Constant.SystemCode.PATROL_taskState,
        Constant.SystemCode.PATROL_editType,
        Constant.SystemCode.PATROL_signInType,
        Constant.SystemCode.PATROL_valueType,
        Constant.SystemCode.PATROL_taskDetailState,
        Constant.SystemCode.PATROL_realValue,
        Constant.SystemCode.PATROL_passReason,
        Constant.SystemCode.PATROL_routeType,
        Constant.SystemCode.PATROL_payCardType
})
public class XJTaskListActivity extends BaseRefreshRecyclerActivity<XJTaskGroupEntity> implements XJTaskContract.View, DeploymentContract.View {

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("leftBtn")
    CustomImageButton leftBtn;

    @BindByTag("rightBtn")
    CustomImageButton rightBtn;

    @BindByTag("xjTaskGetTaskBtn")
    TextView xjTaskGetTaskBtn;

    @BindByTag("xjTaskGetTaskNum")
    TextView xjTaskGetTaskNum;

    @BindByTag("xjTaskUploadTaskBtn")
    TextView xjTaskUploadTaskBtn;

    @BindByTag("xjTaskUploadTaskNum")
    TextView xjTaskUploadTaskNum;

    private Map<String, Object> queryMap = new HashMap<>();

    public static final String  XJ_TASK_STAFF_KEY = "PATROL_1_0_0_patrolTask_potrolTaskList_LISTPT_ASSO_3a556662_35fb_4884_a6ab_1aff5d055ac7";

    private XJTaskGroupAdapter mXJTaskGroupAdapter;

    private int taskStatusPosition = 0;
    private List<XJTaskEntity> mXJTaskEntities = new ArrayList<>();
    private long deploymentId ;
    private boolean needRefresh = false;

    @Override
    protected void onInit() {
        super.onInit();
        refreshListController.setAutoPullDownRefresh(false);
        refreshListController.setPullDownRefreshEnabled(true);

//        String activityRouter = getIntent().getStringExtra(Constant.IntentKey.ACTIVITY_ROUTER);
//        if(!TextUtils.isEmpty(activityRouter)){
//            SupPlantApplication.exitMain();
//        }
        String taskCache =  SharedPreferencesUtils.getParam(context, Constant.SPKey.XJ_TASKS_CACHE+taskStatusPosition, "");

        if(!TextUtils.isEmpty(taskCache)){
            mXJTaskEntities.addAll(GsonUtil.jsonToList(taskCache, XJTaskEntity.class));
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtils.setParam(context, Constant.SPKey.XJ_TASKS_CACHE+taskStatusPosition, GsonUtil.gsonString(mXJTaskGroupAdapter.getXJTaskEntity()));
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(RefreshEvent refreshEvent) {
        refreshListController.refreshBegin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTempTaskAdd(XJTempTaskAddEvent event) {
        needRefresh = true;
        mXJTaskEntities.add(0, event.getTempTaskEntity());
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();

        Flowable.timer(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        int code = 0;
//                        if(SBTUtil.isSBT()){
//                            code|= SB2Config.BARCORD;
//                        }

                        if(SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0) == TemperatureMode.SBT.getCode() && SBTUtil.isSupportTemp()){
                            code |= SB2Config.TEMPERATURE;
                        }

                        if(SharedPreferencesUtils.getParam(context, Constant.SPKey.UHF_ENABLE, false) && SBTUtil.isSupportUHF()){
                            code |= SB2Config.UHF;
                        }


                        SB2Controller sb2Controller = new SB2Controller(context);
                        sb2Controller.setConfigCode(code);
                        registerController(SB2Controller.class.getSimpleName(), sb2Controller);
                    }
                });

        if(mXJTaskEntities.size() == 0){
            refreshListController.refreshBegin();
        } else{
            createTaskGroups(mXJTaskEntities);
        }

    }

    @Override
    protected IListAdapter<XJTaskGroupEntity> createAdapter() {
        mXJTaskGroupAdapter = new XJTaskGroupAdapter(context);
        return mXJTaskGroupAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_task_list;
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText(getString(R.string.xj_task_list));

        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(1, context)));
        contentView.setAdapter(mXJTaskGroupAdapter);
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        getController(CheckUserPermissionController.class)
                .checkUserPermission(SupPlantApplication.getUserName(),"PATROL_1.0.0_patrolTask_tempTaskList","start_ml5tgr8")
                .setSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result){
                            rightBtn.setVisibility(View.VISIBLE);
                            presenterRouter.create(DeploymentAPI.class).getCurrentDeployment("tempTaskWF");

                        }
                    }
                });



        leftBtn.setOnClickListener(v -> onBackPressed());


        RxView.clicks(rightBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Bundle bundle = new Bundle();
                        bundle.putLong(Constant.IntentKey.DEPLOYMENT_ID, deploymentId);
                        IntentRouter.go(context, Constant.Router.XJ_TEMP_TASK, bundle);
                    }
                });

        initQueryMap();

//        refreshListController.setOnRefreshPageListener(new OnRefreshPageListener() {
//            @Override
//            public void onRefresh(int pageIndex) {
//                LogUtil.d("pageIndex:"+pageIndex);
//
//                if(taskStatusPosition == 0){
//                    getXJTask(pageIndex, queryMap);
//                }
//                else if(taskStatusPosition == 2 && pageIndex == 1){
//                    getCheckedTasks();
//                }
//                else{
//                    getXJTask(pageIndex, queryMap);
//                }
//
//
//            }
//        });

        refreshListController.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(taskStatusPosition == 0){
                    getXJTask(1, queryMap);
                }
                else if(taskStatusPosition == 2){
                    getCheckedTasks();
                }
                else{
                    getXJTask(1, queryMap);
                }
            }
        });


        RxView.clicks(xjTaskGetTaskBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        IntentRouter.go(context, Constant.Router.XJ_TASK_GET);
                    }
                });

        RxView.clicks(xjTaskUploadTaskBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        IntentRouter.go(context, Constant.Router.XJ_TASK_UPLOAD);
                    }
                });

        getController(XJTaskDateFilterController.class).setDateSelectListener(new DateSelectListener() {
            @Override
            public void onDateSelect(String start, String end) {
                LogUtil.d("start:"+start);
                LogUtil.d("end:"+end);

                if(TextUtils.isEmpty(start) || TextUtils.isEmpty(end)){
                    queryMap.remove(Constant.BAPQuery.XJ_START_TIME_1);
                    queryMap.remove(Constant.BAPQuery.XJ_START_TIME_2);
                }
                else {

                    queryMap.put(Constant.BAPQuery.XJ_START_TIME_1, start);
                    queryMap.put(Constant.BAPQuery.XJ_START_TIME_2, end);
                }
                refreshListController.refreshBegin();
            }
        });


        getController(XJTaskStatusFilterController.class).setOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                LogUtil.d("result:"+result);
                taskStatusPosition = result;
                refreshListController.refreshBegin();

            }
        });

        getController(XJTaskNoIssuedController.class).setOnResultListener(new OnAPIResultListener<Integer>() {
            @Override
            public void onFail(String errorMsg) {

            }

            @Override
            public void onSuccess(Integer result) {
                if(result!=null && result!=0){
                    xjTaskGetTaskNum.setVisibility(View.VISIBLE);
                    xjTaskGetTaskNum.setText(String.valueOf(result));
                }
                else{
                    xjTaskGetTaskNum.setVisibility(View.GONE);
                    xjTaskGetTaskNum.setText("");
                }
            }
        });

        getController(XJTaskUploadController.class).setOnResultListener(new OnAPIResultListener<Integer>() {
            @Override
            public void onFail(String errorMsg) {

            }

            @Override
            public void onSuccess(Integer result) {
                if(result!=null && result!=0){
                    xjTaskUploadTaskNum.setVisibility(View.VISIBLE);
                    xjTaskUploadTaskNum.setText(String.valueOf(result));
                }
                else{
                    xjTaskUploadTaskNum.setVisibility(View.GONE);
                    xjTaskUploadTaskNum.setText("");
                }
            }
        });

        mXJTaskGroupAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                XJTaskEntity xjTaskEntity = (XJTaskEntity) obj;

                if(xjTaskEntity == null){
                    return;
                }

                Bundle bundle = new Bundle();
                if(XJCacheUtil.check(context, xjTaskEntity.tableNo)){//检查本地缓存
                    bundle.putString(Constant.IntentKey.XJ_TASK_ENTITY_STR, XJCacheUtil.getString(xjTaskEntity.tableNo));
                }
                else{
                    bundle.putString(Constant.IntentKey.XJ_TASK_ENTITY_STR, xjTaskEntity.toString());
                }
                IntentRouter.go(context, Constant.Router.XJ_TASK_DETAIL, bundle);
            }
        });
    }

    private void getCheckedTasks() {

        List<XJTaskEntity> taskEntities = getController(XJTaskUploadController.class).getUploadTasks();
        if(taskEntities!=null && taskEntities.size()!=0){
            createTaskGroups(taskEntities);
        }
        else{
            refreshListController.refreshComplete(null);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        if(needRefresh){
            createTaskGroups(mXJTaskEntities);
            needRefresh = false;
        }


    }

    private void initQueryMap() {
        String[] dates= TimeUtil.getTimePeriod(Constant.Date.TODAY);

        String start = dates[0];
        String end = dates[1];

        queryMap.put(Constant.BAPQuery.XJ_START_TIME_1, start);
        queryMap.put(Constant.BAPQuery.XJ_START_TIME_2, end);

        queryMap.put(Constant.BAPQuery.XJ_TASK_STATE, "PATROL_taskState/issued");//PATROL_taskState/notIssued
    }

    private void getXJTask(int pageNo, Map<String, Object> queryMap) {

        presenterRouter.create(XJTaskAPI.class).getTaskList(pageNo, 20, queryMap);

    }

    @Override
    public void getTaskListSuccess(CommonBAPListEntity entity) {

        List<XJTaskEntity> taskEntities = entity.result;

        if(taskEntities==null){

            refreshListController.refreshComplete(null);

            return;
        }

        if(entity.pageNo == 1){
            mXJTaskEntities.clear();
            mXJTaskEntities.addAll(getXJTempTasks());
        }


        mXJTaskEntities.addAll(taskEntities);
        if(entity.hasNext){
            getXJTask(entity.nextPage, queryMap);
        }
        else{
            createTaskGroups(mXJTaskEntities);
        }
    }

    private List<XJTaskEntity> getXJTempTasks(){
        List<XJTaskEntity> tempTaskEntities = new ArrayList<>();
        List<String> tempTasks = XJCacheUtil.getTempTasks(context);
        for(String s : tempTasks){
            XJTaskEntity xjTaskEntity = GsonUtil.gsonToBean(XJCacheUtil.getString(s.replace(".0", "")), XJTaskEntity.class);
          if (taskStatusPosition==0){
              tempTaskEntities.add(xjTaskEntity);
          }else if (taskStatusPosition==1){
              if (!xjTaskEntity.isFinished){
                  tempTaskEntities.add(xjTaskEntity);
              }
          }else if(taskStatusPosition==2){
              if (xjTaskEntity.isFinished){
                  tempTaskEntities.add(xjTaskEntity);
              }
          }

        }

        return tempTaskEntities;
    }

    @SuppressLint("CheckResult")
    private void createTaskGroups(List<XJTaskEntity> taskEntities) {

        LogUtil.e("测试"+taskEntities.toString());
        Map<String, List<XJTaskEntity>> taskMap = new LinkedHashMap<>();
        List<XJTaskGroupEntity> xjTaskGroupEntities = new ArrayList<>();
        Flowable.fromIterable(taskEntities)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJTaskEntity>() {
                    @Override
                    public boolean test(XJTaskEntity xjTaskEntity) throws Exception {

                        if(xjTaskEntity.workRoute == null){
                            return false;
                        }

                        XJTaskEntity  taskEntity = getController(XJLocalTaskController.class).getLocalTask(xjTaskEntity.tableNo);
                        if(taskStatusPosition == 1 && taskEntity!=null && taskEntity.isFinished){//待检过滤
                            return false;
                        }

                        String key = xjTaskEntity.workRoute.code+""+ DateUtil.dateFormat(xjTaskEntity.startTime);

                        if(xjTaskEntity.isTemp){
                            List<XJTaskEntity> xjTaskEntities = new ArrayList<>();
                            xjTaskEntities.add(xjTaskEntity);
                            taskMap.put(xjTaskEntity.tableNo, xjTaskEntities);
                        }

                        else if(!taskMap.containsKey(key)){
                            taskMap.put(key, new ArrayList<>());
                        }

                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJTaskEntity>() {
                    @Override
                    public void accept(XJTaskEntity xjTaskEntity) throws Exception {

                        if(XJCacheUtil.check(context, xjTaskEntity.tableNo)){

                            XJTaskEntity taskEntity = getController(XJLocalTaskController.class).getLocalTask(xjTaskEntity.tableNo);

                            if(taskEntity!=null){

                                xjTaskEntity = taskEntity;
                            }
                            else
                            {
//                                XJCacheUtil.remove(xjTaskEntity.tableNo);
                            }
                        }

                        if(!xjTaskEntity.isTemp){
                            String key = xjTaskEntity.workRoute.code+""+ DateUtil.dateFormat(xjTaskEntity.startTime);
                            List<XJTaskEntity> tasks = taskMap.get(key);
                            tasks.add(0, xjTaskEntity);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                        for(String key: taskMap.keySet()){

                            XJTaskGroupEntity xjTaskGroupEntity = new XJTaskGroupEntity();
                            List<XJTaskEntity> xjTaskEntities = taskMap.get(key);
                            if(xjTaskEntities == null || xjTaskEntities.size() == 0){
                                continue;
                            }

                            xjTaskGroupEntity.taskEntities = xjTaskEntities;

                            XJTaskEntity taskEntity = xjTaskEntities.get(0);

                            if(taskEntity.attrMap != null && taskEntity.attrMap.containsKey(XJ_TASK_STAFF_KEY)){
                                xjTaskGroupEntity.staffName = (String) xjTaskEntities.get(0).attrMap.get(XJ_TASK_STAFF_KEY);
                            }
                            else if(taskEntity.isTemp){
                                xjTaskGroupEntity.staffName = taskEntity.staffName;
                            }

                            xjTaskGroupEntity.date = taskEntity.startTime;

                            if(taskEntity.workRoute!=null){
                                xjTaskGroupEntity.name = taskEntity.workRoute.name;
                            }

                            if(taskEntity.patrolType!=null){
                                xjTaskGroupEntity.typeValue = taskEntity.patrolType.value;
                            }

                            xjTaskGroupEntity.spanCount = 2;
                            xjTaskGroupEntities.add(xjTaskGroupEntity);
                        }
                        refreshListController.refreshComplete(xjTaskGroupEntities);

                    }
                });



    }

    @Override
    public void getTaskListFailed(String errorMsg) {
        createTaskGroups(mXJTaskEntities);
    }

    @Override
    public void getCurrentDeploymentSuccess(DeploymentEntity entity) {
        LogUtil.d("getCurrentDeploymentSuccess:"+entity.toString());
        deploymentId = entity.id;
    }

    @Override
    public void getCurrentDeploymentFailed(String errorMsg) {

    }
}
