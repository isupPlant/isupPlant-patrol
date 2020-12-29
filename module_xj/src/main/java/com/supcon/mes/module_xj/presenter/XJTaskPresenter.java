package com.supcon.mes.module_xj.presenter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.model.bean.JoinSubcondEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.util.BAPQueryParamsHelper;
import com.supcon.mes.module_xj.model.contract.XJTaskContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJTaskPresenter extends XJTaskContract.Presenter {

    @Override
    public void getTaskList(int pageNo, int pageSize, Map<String, Object> queryMap) {
        FastQueryCondEntity fastQueryCondEntity = BAPQueryParamsHelper.createSingleFastQueryCond(queryMap);
        fastQueryCondEntity.modelAlias = "potrolTask";
        fastQueryCondEntity.viewCode = "PATROL_1.0.0_patrolTask_potrolTaskList";
  //      fastQueryCondEntity.viewCode = "PATROL_1.0.0_patrolTask_mobilePotrolTaskList";
//        fastQueryCondEntity.condName = "fastCond";
//        fastQueryCondEntity.remark = "fastCond";

        JoinSubcondEntity subcondEntity = BAPQueryParamsHelper.crateJoinSubcondEntity(new HashMap<>(), "MP_TASK_STAFFS,PATROL_TASK,MP_POTROL_TASKS,ID");

        Map<String, Object> joinCondMap = new HashMap<>();
        joinCondMap.put(Constant.BAPQuery.XJ_ID, SupPlantApplication.getAccountInfo().staffId);
        joinCondMap.put(Constant.BAPQuery.NAME, SupPlantApplication.getAccountInfo().staffName);
        JoinSubcondEntity joinSubcondEntity = BAPQueryParamsHelper.crateJoinSubcondEntity(joinCondMap, "base_staff,ID,MP_TASK_STAFFS,STAFF_ID");

        subcondEntity.subconds.add(joinSubcondEntity);

        fastQueryCondEntity.subconds.add(subcondEntity);

        Map<String, Object> pageQueryParam = new HashMap<>();
        pageQueryParam.put("pageSize", pageSize);
        pageQueryParam.put("paging", true);
        pageQueryParam.put("pageNo", pageNo);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        pageQueryParam.put("fastQueryCond", gson.toJson(fastQueryCondEntity));
//        pageQueryParam.put("customCondition", new CustomCondition());

        mCompositeSubscription.add(XJHttpClient.getTaskList(pageQueryParam)
                .onErrorReturn(new Function<Throwable, BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>>>() {
                    @Override
                    public BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>> apply(Throwable throwable) throws Exception {

                        BAP5CommonEntity bap5CommonEntity = new BAP5CommonEntity();
                        bap5CommonEntity.success = false;
                        bap5CommonEntity.msg = throwable.toString();
                        return bap5CommonEntity;
                    }
                })
                .subscribe(new Consumer<BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>>>() {
                    @Override
                    public void accept(BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>> commonBAPListEntityBAP5CommonEntity) throws Exception {
                        if(commonBAPListEntityBAP5CommonEntity.success){
                            getView().getTaskListSuccess(commonBAPListEntityBAP5CommonEntity.data);
                        }
                        else{
                            getView().getTaskListFailed(commonBAPListEntityBAP5CommonEntity.msg);
                        }
                    }
                }));

    }
}
