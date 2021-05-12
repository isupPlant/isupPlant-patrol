package com.supcon.mes.module_defectmanage.presenter;

import com.google.gson.JsonObject;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.BapPageResultEntity;
import com.supcon.mes.middleware.model.bean.FunctionEx;
import com.supcon.mes.module_defectmanage.model.bean.DefectSourceEntity;
import com.supcon.mes.module_defectmanage.model.contract.GetDefectSourceListContract;
import com.supcon.mes.module_defectmanage.model.network.DefectManagerHttpClient;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class GetDefectSourceListPresenter extends GetDefectSourceListContract.Presenter {

    /**
     *     //{
     *     //	"classifyCodes": "",
     *     //	"customCondition": {},
     *     //	"permissionCode": "DefectManage_6.0.0.1_problemSource_sourceLayoutRef",
     *     //	"pageNo": 1,
     *     //	"paging": true,
     *     //	"pageSize": 20,
     *     //	"crossCompanyFlag": "false"
     *     //}
     * @param pageNo
     */
    @Override
    public void getDefectSourceList(int pageNo) {
        Map<String, Object> pageQueryParam = new HashMap<>();
        pageQueryParam.put("pageSize", 100);
        pageQueryParam.put("paging", true);
        pageQueryParam.put("pageNo", pageNo);
        pageQueryParam.put("crossCompanyFlag", false);

        JsonObject customCondition=new JsonObject();
        pageQueryParam.put("customCondition",customCondition);
        pageQueryParam.put("permissionCode","DefectManage_6.0.0.1_problemSource_sourceLayoutRef");
        pageQueryParam.put("classifyCodes", "");
        mCompositeSubscription.add(DefectManagerHttpClient.sourcePartRefQuery(pageQueryParam)
                .onErrorReturn(new FunctionEx<Throwable, BapPageResultEntity<DefectSourceEntity>>() {
                    @Override
                    public BAP5CommonEntity apply(Throwable throwable)  {
                        return super.apply(throwable);
                    }
                }).subscribe(new Consumer<BAP5CommonEntity<BapPageResultEntity<DefectSourceEntity>>>() {
                    @Override
                    public void accept(BAP5CommonEntity<BapPageResultEntity<DefectSourceEntity>> incidentTmpEntityCommonEntity) throws Exception {
                        if (incidentTmpEntityCommonEntity.success) {
                            getView().getDefectSourceListSuccess(incidentTmpEntityCommonEntity);
                        } else {
                            getView().getDefectSourceListFailed(incidentTmpEntityCommonEntity.msg);
                        }
                    }
                }));
    }
}
