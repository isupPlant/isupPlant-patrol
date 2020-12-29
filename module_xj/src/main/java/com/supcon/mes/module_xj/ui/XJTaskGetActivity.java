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
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.DateFilterController;
import com.supcon.mes.middleware.controller.SystemCodeJsonController;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.inter.SystemCode;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.XJTaskCacheUtil;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.model.api.XJTaskAPI;
import com.supcon.mes.module_xj.model.api.XJTaskStateAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskGroupEntity;
import com.supcon.mes.module_xj.model.contract.XJTaskContract;
import com.supcon.mes.module_xj.model.contract.XJTaskStateContract;
import com.supcon.mes.module_xj.presenter.XJRunningTaskPresenter;
import com.supcon.mes.module_xj.presenter.XJTaskStatePresenter;
import com.supcon.mes.module_xj.ui.adapter.XJTaskGroupAdapter;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/1/9
 * Email:wangshizhan@supcom.com
 */
@Router(value = Constant.Router.XJ_TASK_GET)
@Controller(value = {SystemCodeJsonController.class, DateFilterController.class})
@Presenter(value = {XJRunningTaskPresenter.class, XJTaskStatePresenter.class})
@SystemCode(entityCodes = {
        Constant.SystemCode.PATROL_taskState

})
public class XJTaskGetActivity extends BaseRefreshRecyclerActivity<XJTaskGroupEntity>
        implements XJTaskContract.View,
        XJTaskStateContract.View {

   // private static final String XJ_TASK_STAFF_KEY = "PATROL_1_0_0_patrolTask_potrolTaskList_LISTPT_ASSO_3a556662_35fb_4884_a6ab_1aff5d055ac7";
    @BindByTag("titleText")
    TextView titleText;
    @BindByTag("contentView")
    RecyclerView contentView;
    @BindByTag("leftBtn")
    CustomImageButton leftBtn;
    @BindByTag("rightBtn")
    CustomImageButton rightBtn;
    @BindByTag("xjTaskGetBtn")
    TextView xjTaskGetBtn;
    private XJTaskGroupAdapter mXJTaskGroupAdapter;
    private Map<String, Object> queryMap = new HashMap<>();
    private List<XJTaskEntity> mXJTaskEntities = new ArrayList<>();
    private boolean isAll = false;

    @Override
    protected void onInit() {
        super.onInit();
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);

//        String activityRouter = getIntent().getStringExtra(Constant.IntentKey.ACTIVITY_ROUTER);
//        if(!TextUtils.isEmpty(activityRouter)){
//            SupPlantApplication.exitMain();
//        }

    }

    @Override
    protected IListAdapter<XJTaskGroupEntity> createAdapter() {
        mXJTaskGroupAdapter = new XJTaskGroupAdapter(context);
        return mXJTaskGroupAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_task_get;
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText(getString(R.string.xj_task_get));
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.ic_top_all);
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(8, context)));
        contentView.setAdapter(mXJTaskGroupAdapter);

        initQueryMap();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        leftBtn.setOnClickListener(v -> onBackPressed());

        RxView.clicks(rightBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (!isAll) {
                rightBtn.setImageResource(R.drawable.ic_top_all_p);
                selectAll();
                isAll = true;
            } else {
                rightBtn.setImageResource(R.drawable.ic_top_all);
                unselectAll();
                isAll = false;
            }
        });


        refreshListController.setOnRefreshListener(() -> {
            mXJTaskEntities.clear();
            getXJTask(1, queryMap);
        });
//        refreshListController.setOnRefreshPageListener(new OnRefreshPageListener() {
//            @Override
//            public void onRefresh(int pageIndex) {
//                LogUtil.d("pageIndex:"+pageIndex);
//
//                getXJTask(pageIndex, queryMap);
//            }
//        });


        getController(DateFilterController.class).setDateSelectListener((start, end) -> {
            LogUtil.d("start:" + start);
            LogUtil.d("end:" + end);

            if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
                queryMap.remove(Constant.BAPQuery.XJ_START_TIME_1);
                queryMap.remove(Constant.BAPQuery.XJ_START_TIME_2);
            } else {

                queryMap.put(Constant.BAPQuery.XJ_START_TIME_1, start);
                queryMap.put(Constant.BAPQuery.XJ_START_TIME_2, end);
            }
            refreshListController.refreshBegin();
        });
//        getController(DateFilterController.class).setDateChecked(Constant.Date.TODAY);

        RxView.clicks(xjTaskGetBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> updateTaskState());
    }

    private void selectAll() {

        if (mXJTaskGroupAdapter.getList() == null) {
            return;
        }
        for (XJTaskGroupEntity xjTaskGroupEntity : mXJTaskGroupAdapter.getList()) {

            for (XJTaskEntity xjTaskEntity : xjTaskGroupEntity.taskEntities) {
                xjTaskEntity.isChecked = true;
            }

        }

        mXJTaskGroupAdapter.notifyDataSetChanged();
    }

    private void unselectAll() {

        if (mXJTaskGroupAdapter.getList() == null) {
            return;
        }
        for (XJTaskGroupEntity xjTaskGroupEntity : mXJTaskGroupAdapter.getList()) {

            for (XJTaskEntity xjTaskEntity : xjTaskGroupEntity.taskEntities) {
                xjTaskEntity.isChecked = false;
            }

        }

        mXJTaskGroupAdapter.notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
    private void updateTaskState() {
        List<XJTaskGroupEntity> taskGroupEntities = mXJTaskGroupAdapter.getList();
        if (taskGroupEntities == null || taskGroupEntities.size() == 0) {
            ToastUtils.show(context, getString(R.string.xj_task_get_warning1));
            return;
        }

        List<XJTaskEntity> taskEntities = new ArrayList<>();
        Flowable.fromIterable(taskGroupEntities)
                .flatMap((Function<XJTaskGroupEntity, Publisher<XJTaskEntity>>)
                        xjTaskGroupEntity -> Flowable.fromIterable(xjTaskGroupEntity.taskEntities))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(xjTaskEntity -> {
                    if (xjTaskEntity.isChecked) {
                        taskEntities.add(xjTaskEntity);
                    }
                }, throwable -> {
                }, () -> {
                    LogUtil.d("size:" + taskEntities.size());

                    StringBuilder idList = new StringBuilder();
                    if (taskEntities.size() != 0) {
                        for (XJTaskEntity xjTaskEntity : taskEntities) {
                            idList.append(xjTaskEntity.id);
                            idList.append(",");
                        }
                        idList.replace(idList.length() - 1, idList.length(), "");
                        Map<String, Object> queryMap = new HashMap<>();
                        queryMap.put("idList", idList.toString());
                        queryMap.put("changeState", "PATROL_taskState/issued");
                        presenterRouter.create(XJTaskStateAPI.class).updateTaskState(queryMap);
                        onLoading(getString(R.string.xj_task_geting));
                    }
                });


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

    private void getXJTask(int pageNo, Map<String, Object> queryMap) {

        presenterRouter.create(XJTaskAPI.class).getTaskList(pageNo, 500, queryMap);

    }

    @Override
    public void getTaskListSuccess(CommonBAPListEntity entity) {

        List<XJTaskEntity> taskEntities = entity.result;

        if (taskEntities == null) {

            refreshListController.refreshComplete(null);

            return;
        }

        mXJTaskEntities.addAll(taskEntities);
        if (entity.hasNext) {
            getXJTask(entity.nextPage, queryMap);

        } else {
            createTaskGroups(mXJTaskEntities);
        }


    }

    @SuppressLint("CheckResult")
    private void createTaskGroups(List<XJTaskEntity> taskEntities) {

        Map<String, List<XJTaskEntity>> taskMap = new LinkedHashMap<>();
        List<XJTaskGroupEntity> xjTaskGroupEntities = new ArrayList<>();

        Flowable.fromIterable(taskEntities)
                .subscribeOn(Schedulers.newThread())
                .filter(xjTaskEntity -> {

                    if (xjTaskEntity.workRoute == null) {
                        return false;
                    }

                    String key = xjTaskEntity.workRoute.code + "" + DateUtil.dateFormat(xjTaskEntity.startTime);

                    if (!taskMap.containsKey(key)) {
                        taskMap.put(key, new ArrayList<>());
                    }

                    if (XJTaskCacheUtil.check(xjTaskEntity.tableNo)) {//清除历史数据
                        XJTaskCacheUtil.remove(xjTaskEntity.tableNo);
                    }

                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(xjTaskEntity -> {

                    xjTaskEntity.viewType = 1;//下载上传视图类型
                    String key = xjTaskEntity.workRoute.code + "" + DateUtil.dateFormat(xjTaskEntity.startTime);
                    List<XJTaskEntity> tasks = taskMap.get(key);
                    tasks.add(0, xjTaskEntity);
                }, throwable -> {

                }, () -> {

                    for (String key : taskMap.keySet()) {

                        XJTaskGroupEntity xjTaskGroupEntity = new XJTaskGroupEntity();
                        List<XJTaskEntity> xjTaskEntities = taskMap.get(key);
                        if (xjTaskEntities == null || xjTaskEntities.size() == 0) {
                            continue;
                        }

                        xjTaskGroupEntity.taskEntities = xjTaskEntities;

                        XJTaskEntity taskEntity = xjTaskEntities.get(0);

                        if (taskEntity.attrMap != null) {

                            for(String keyset : taskEntity.attrMap.keySet()) {
                                String value = (String)  taskEntity.attrMap.get(keyset);
                                if (!TextUtils.isEmpty(value)){
                                    xjTaskGroupEntity.staffName =value;
                                }
                            }
                        }

                        xjTaskGroupEntity.date = taskEntity.startTime;

                        if (taskEntity.workRoute != null) {
                            xjTaskGroupEntity.name = taskEntity.workRoute.name;
                        }

                        if (taskEntity.patrolType != null) {
                            xjTaskGroupEntity.typeValue = taskEntity.patrolType.value;
                        }
                        xjTaskGroupEntity.spanCount = 3;
                        xjTaskGroupEntities.add(xjTaskGroupEntity);
                    }

                    refreshListController.refreshComplete(xjTaskGroupEntities);

                });


    }

    @Override
    public void getTaskListFailed(String errorMsg) {
        refreshListController.refreshComplete(null);
    }

    @Override
    public void updateTaskStateSuccess() {
        onLoadSuccess();
        refreshListController.refreshBegin();
        EventBus.getDefault().post(new RefreshEvent());
    }

    @Override
    public void updateTaskStateFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
//        ToastUtils.show(context, ErrorMsgHelper.msgParse(errorMsg));
    }
}
