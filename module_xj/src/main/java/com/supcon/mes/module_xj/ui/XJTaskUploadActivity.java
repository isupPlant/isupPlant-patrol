package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnRefreshListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.DateFilterController;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.model.listener.DateSelectListener;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.XJCacheUtil;
import com.supcon.mes.module_xj.IntentRouter;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.model.api.XJLocalTaskAPI;
import com.supcon.mes.module_xj.model.api.XJTaskStateAPI;
import com.supcon.mes.module_xj.model.api.XJTaskSubmitAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.bean.XJTaskGroupEntity;
import com.supcon.mes.module_xj.model.contract.XJLocalTaskContract;
import com.supcon.mes.module_xj.model.contract.XJTaskStateContract;
import com.supcon.mes.module_xj.model.contract.XJTaskSubmitContract;
import com.supcon.mes.module_xj.presenter.XJLocalTaskPresenter;
import com.supcon.mes.module_xj.presenter.XJTaskStatePresenter;
import com.supcon.mes.module_xj.presenter.XJTaskSubmitPresenter;
import com.supcon.mes.module_xj.ui.adapter.XJTaskGroupAdapter;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/1/9
 * Email:wangshizhan@supcom.com
 */
@Router(value = Constant.Router.XJ_TASK_UPLOAD)
@Controller(value = {DateFilterController.class})
@Presenter(value = {XJLocalTaskPresenter.class, XJTaskSubmitPresenter.class})
public class XJTaskUploadActivity extends BaseRefreshRecyclerActivity<XJTaskGroupEntity> implements XJLocalTaskContract.View, XJTaskSubmitContract.View {

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("leftBtn")
    CustomImageButton leftBtn;

    @BindByTag("rightBtn")
    CustomImageButton rightBtn;

    @BindByTag("xjTaskUploadBtn")
    TextView xjTaskUploadBtn;

    private XJTaskGroupAdapter mXJTaskGroupAdapter;
    private Map<String, Object> queryMap = new HashMap<>();
    private static final String  XJ_TASK_STAFF_KEY = "PATROL_1_0_0_patrolTask_potrolTaskList_LISTPT_ASSO_3a556662_35fb_4884_a6ab_1aff5d055ac7";
    private List<XJTaskEntity> mUploadTasks = new ArrayList<>();
    private boolean isAll = false;

    @Override
    protected void onInit() {
        super.onInit();
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);

    }

    @Override
    protected IListAdapter<XJTaskGroupEntity> createAdapter() {
        mXJTaskGroupAdapter = new XJTaskGroupAdapter(context);
        return mXJTaskGroupAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_task_upload;
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this,R.color.themeColor);
        titleText.setText(getString(R.string.xj_task_upload));
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.ic_top_all);
        initQueryMap();
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(8, context)));
        contentView.setAdapter(mXJTaskGroupAdapter);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        leftBtn.setOnClickListener(v -> onBackPressed());


        RxView.clicks(rightBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(o -> {
            if(!isAll){
                rightBtn.setImageResource(R.drawable.ic_top_all_p);
                selectAll();
                isAll = true;
            }
            else{
                rightBtn.setImageResource(R.drawable.ic_top_all);
                unselectAll();
                isAll = false;
            }
        });

        refreshListController.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getXJTask(queryMap);
            }
        });


//        getController(DateFilterController.class).setDateSelectListener(new DateSelectListener() {
//            @Override
//            public void onDateSelect(String start, String end) {
//                LogUtil.d("start:"+start);
//                LogUtil.d("end:"+end);
//
//                if(TextUtils.isEmpty(start) || TextUtils.isEmpty(end)){
//                    queryMap.remove(Constant.BAPQuery.XJ_START_TIME_1);
//                    queryMap.remove(Constant.BAPQuery.XJ_START_TIME_2);
//                }
//                else {
//
//                    queryMap.put(Constant.BAPQuery.XJ_START_TIME_1, start);
//                    queryMap.put(Constant.BAPQuery.XJ_START_TIME_2, end);
//                }
//                refreshListController.refreshBegin();
//            }
//        });
//        getController(DateFilterController.class).setDateChecked(Constant.Date.TODAY);

        RxView.clicks(xjTaskUploadBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {


                       uploadTask();

                    }
                });
    }


    private void selectAll() {

        if(mXJTaskGroupAdapter.getList() == null){
            return;
        }
        for(XJTaskGroupEntity xjTaskGroupEntity : mXJTaskGroupAdapter.getList()){

            for(XJTaskEntity xjTaskEntity : xjTaskGroupEntity.taskEntities){
                xjTaskEntity.isChecked = true;
            }

        }

        mXJTaskGroupAdapter.notifyDataSetChanged();
    }

    private void unselectAll() {

        if(mXJTaskGroupAdapter.getList() == null){
            return;
        }
        for(XJTaskGroupEntity xjTaskGroupEntity : mXJTaskGroupAdapter.getList()){

            for(XJTaskEntity xjTaskEntity : xjTaskGroupEntity.taskEntities){
                xjTaskEntity.isChecked = false;
            }

        }

        mXJTaskGroupAdapter.notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
    private void uploadTask() {

        mUploadTasks.clear();
        List<XJTaskGroupEntity> taskGroupEntities = mXJTaskGroupAdapter.getList();
        if(taskGroupEntities==null || taskGroupEntities.size() == 0){

            ToastUtils.show(context, getString(R.string.xj_task_get_warning1));
            return;
        }


        Flowable.fromIterable(taskGroupEntities)
                .flatMap(new Function<XJTaskGroupEntity, Publisher<XJTaskEntity>>() {
                    @Override
                    public Publisher<XJTaskEntity> apply(XJTaskGroupEntity xjTaskGroupEntity) throws Exception {
                        return Flowable.fromIterable(xjTaskGroupEntity.taskEntities);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJTaskEntity>() {
                    @Override
                    public void accept(XJTaskEntity xjTaskEntity) throws Exception {
                        if (xjTaskEntity.isChecked) {
                            mUploadTasks.add(xjTaskEntity);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtil.d("size:"+mUploadTasks.size());



                        if(mUploadTasks.size()!=0){
                            presenterRouter.create(XJTaskSubmitAPI.class).uploadFile(mUploadTasks, false);
                            onLoading(getString(R.string.xj_task_uploading));
                        }


                    }
                })
        ;


    }

    private void initQueryMap() {
//        String[] dates= TimeUtil.getTimePeriod(Constant.Date.TODAY);
//
//        String start = dates[0];
//        String end = dates[1];
//
//        queryMap.put(Constant.BAPQuery.XJ_START_TIME_1, DateUtil.dateTimeFormat(System.currentTimeMillis()));
//        queryMap.put(Constant.BAPQuery.XJ_START_TIME_2, end);

        queryMap.put(Constant.BAPQuery.XJ_TASK_STATE, "PATROL_taskState/notIssued");//PATROL_taskState/notIssued
    }

    private void getXJTask(Map<String, Object> queryMap) {

        presenterRouter.create(XJLocalTaskAPI.class).getLocalTask(queryMap);

    }

    @SuppressLint("CheckResult")
    private void createTaskGroups(List<XJTaskEntity> taskEntities) {

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

                        if(!xjTaskEntity.isFinished){
                            return false;
                        }

                        String key = xjTaskEntity.workRoute.code+""+ DateUtil.dateFormat(xjTaskEntity.startTime);

                        if(!taskMap.containsKey(key)){
                            taskMap.put(key, new ArrayList<>());
                        }

                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJTaskEntity>() {
                    @Override
                    public void accept(XJTaskEntity xjTaskEntity) throws Exception {

                        xjTaskEntity.viewType = 1;//下载上传视图类型
                        String key = xjTaskEntity.workRoute.code+""+ DateUtil.dateFormat(xjTaskEntity.startTime);
                        List<XJTaskEntity> tasks = taskMap.get(key);
                        tasks.add(0, xjTaskEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                        if(taskMap == null || taskMap.size() == 0){

                            refreshListController.refreshComplete(null);
                            return;
                        }
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

                            xjTaskGroupEntity.date = taskEntity.startTime;

                            if(taskEntity.workRoute!=null){
                                xjTaskGroupEntity.name = taskEntity.workRoute.name;
                            }

                            if(taskEntity.patrolType!=null){
                                xjTaskGroupEntity.typeValue = taskEntity.patrolType.value;
                            }
                            xjTaskGroupEntity.spanCount = 3;
                            xjTaskGroupEntities.add(xjTaskGroupEntity);
                        }
                        Collections.sort(xjTaskGroupEntities,new Comparator<XJTaskGroupEntity>() {
                            @Override
                            public int compare(XJTaskGroupEntity o1, XJTaskGroupEntity o2) {
                                return (int) (o2.date - o1.date);
                            }
                        });
                        refreshListController.refreshComplete(xjTaskGroupEntities);

                    }
                });
    }


    @SuppressLint("CheckResult")
    @Override
    public void getLocalTaskSuccess(List entity) {
        List<XJTaskEntity> taskEntities = entity;

        if(taskEntities.size() == 0){

            refreshListController.refreshComplete(null);

            return;
        }


       createTaskGroups(taskEntities);


    }

    @Override
    public void getLocalTaskFailed(String errorMsg) {
        refreshListController.refreshComplete(null);
    }

    @Override
    public void saveLocalTaskSuccess() {

    }

    @Override
    public void saveLocalTaskFailed(String errorMsg) {

    }

    @Override
    public void uploadFileSuccess(String path) {

        LogUtil.d(""+path);

        if(TextUtils.isEmpty(path)){
            onLoadFailed("巡检数据上传失败！");
            return;
        }

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("filePath", path.replace("\\", "/"));
        queryMap.put("uploadTaskResultDTOs",new ArrayList<>());
        presenterRouter.create(XJTaskSubmitAPI.class).uploadXJData(false, queryMap);
    }


    @Override
    public void uploadFileFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

    @Override
    public void uploadXJDataSuccess() {
        onLoadSuccess();
        for(XJTaskEntity xjTaskEntity : mUploadTasks){
            XJCacheUtil.remove(xjTaskEntity.tableNo);
        }
        refreshListController.refreshBegin();

        EventBus.getDefault().post(new RefreshEvent());
    }

    @Override
    public void uploadXJDataFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }
}
