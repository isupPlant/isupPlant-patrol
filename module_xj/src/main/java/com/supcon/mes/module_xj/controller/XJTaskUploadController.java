package com.supcon.mes.module_xj.controller;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.mes.middleware.model.listener.OnAPIResultListener;
import com.supcon.mes.module_xj.model.api.XJLocalTaskAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.contract.XJLocalTaskContract;
import com.supcon.mes.module_xj.presenter.XJLocalTaskPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/4/9
 * Email:wangshizhan@supcom.com
 */
@Presenter(XJLocalTaskPresenter.class)
public class XJTaskUploadController extends BaseDataController implements XJLocalTaskContract.View {


    private List<XJTaskEntity> mUploadTasks = new ArrayList<>();

    private OnAPIResultListener<Integer> mOnResultListener;

    public XJTaskUploadController(Context context) {
        super(context);
    }


    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void initData() {
        super.initData();

    }


    @Override
    public void onResume() {
        super.onResume();
        presenterRouter.create(XJLocalTaskAPI.class).getLocalTask(new HashMap<>());
    }

    public void setOnResultListener(OnAPIResultListener<Integer> onResultListener) {
        mOnResultListener = onResultListener;
    }

    public List<XJTaskEntity> getUploadTasks() {
        return mUploadTasks;
    }

    @SuppressLint("CheckResult")
    @Override
    public void getLocalTaskSuccess(List entity) {
        mUploadTasks.clear();
        List<XJTaskEntity> localTaskEntities = entity;
        Flowable.fromIterable(localTaskEntities)
                .subscribe(new Consumer<XJTaskEntity>() {
                    @Override
                    public void accept(XJTaskEntity xjLocalTaskEntity) throws Exception {
                        if (xjLocalTaskEntity.isFinished) {
                            mUploadTasks.add(xjLocalTaskEntity);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if(mOnResultListener!=null){
                            mOnResultListener.onSuccess(mUploadTasks.size());
                        }
                    }
                });

    }

    @Override
    public void getLocalTaskFailed(String errorMsg) {

    }

    @Override
    public void saveLocalTaskSuccess() {

    }

    @Override
    public void saveLocalTaskFailed(String errorMsg) {

    }
}
