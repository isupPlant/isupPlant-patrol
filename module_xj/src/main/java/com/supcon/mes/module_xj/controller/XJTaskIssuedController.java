package com.supcon.mes.module_xj.controller;

import android.content.Context;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.listener.OnAPIResultListener;
import com.supcon.mes.module_xj.model.api.XJTaskAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.contract.XJTaskContract;
import com.supcon.mes.module_xj.presenter.XJRunningTaskPresenter;
import com.supcon.mes.module_xj.presenter.XJTaskPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangshizhan on 2020/4/9
 * Email:wangshizhan@supcom.com
 */
@Presenter(XJRunningTaskPresenter.class)
public class XJTaskIssuedController extends BaseDataController implements XJTaskContract.View {

    private Map<String, Object> queryMap = new HashMap<>();

//    private List<XJTaskEntity> mNoIssuedTasks = new ArrayList<>();

    private OnAPIResultListener<Integer> mOnResultListener;

    public XJTaskIssuedController(Context context) {
        super(context);
    }


    @Override
    public void onInit() {
        super.onInit();
        initQueryMap();
    }


    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        presenterRouter.create(XJTaskAPI.class).getTaskList(1, 1, queryMap);
    }

    private void initQueryMap() {

//        String[] dates= TimeUtil.getTimePeriod(Constant.Date.TODAY);
//
//        String start = dates[0];
//
//        queryMap.put(Constant.BAPQuery.XJ_START_TIME_1, start);

        queryMap.put(Constant.BAPQuery.XJ_TASK_STATE, "PATROL_taskState/issued");//PATROL_taskState/notIssued
    }


    @Override
    public void getTaskListSuccess(CommonBAPListEntity entity) {

        List<XJTaskEntity> taskEntities = entity.result;

        if(taskEntities!=null && taskEntities.size()!=0){


        }
        if(mOnResultListener!=null){
            mOnResultListener.onSuccess(entity.totalCount);
        }


    }

    @Override
    public void getTaskListFailed(String errorMsg) {

    }


    public void setOnResultListener(OnAPIResultListener<Integer> onResultListener) {
        mOnResultListener = onResultListener;
    }

//    public List<XJTaskEntity> getNoIssuedTasks() {
//        return mNoIssuedTasks;
//    }
}
