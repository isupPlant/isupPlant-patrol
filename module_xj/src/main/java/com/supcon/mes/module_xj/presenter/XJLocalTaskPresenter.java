package com.supcon.mes.module_xj.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.util.XJTaskCacheUtil;
import com.supcon.mes.module_xj.model.contract.XJLocalTaskContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/4/8
 * Email:wangshizhan@supcom.com
 */
public class XJLocalTaskPresenter extends XJLocalTaskContract.Presenter {

    @SuppressLint("CheckResult")
    @Override
    public void getLocalTask(Map<String, Object> queryMap) {
        List<XJTaskEntity> xjTaskEntities= XJTaskCacheUtil.getTasks();
        getView().getLocalTaskSuccess(xjTaskEntities);
    }
    @Override
    public void saveLocalTask() {

    }
}
