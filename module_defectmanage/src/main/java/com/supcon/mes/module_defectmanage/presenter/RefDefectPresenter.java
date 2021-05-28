package com.supcon.mes.module_defectmanage.presenter;

import com.google.gson.JsonObject;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.BapPageResultEntity;
import com.supcon.mes.middleware.model.bean.FunctionEx;
import com.supcon.mes.middleware.util.FileUtil;
import com.supcon.mes.module_defectmanage.model.bean.DefectListNumEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.contract.RefDefectContract;
import com.supcon.mes.module_defectmanage.model.network.DefectManagerHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class RefDefectPresenter extends RefDefectContract.Presenter {


    /**
     * {
     * 	"classifyCodes": "",
     * 	"customCondition": {
     * 		"cid": "1000"
     *        },
     * 	"permissionCode": null,
     * 	"pageNo": 1,
     * 	"paging": true,
     * 	"pageSize": 65535,
     * 	"crossCompanyFlag": "false",
     * 	"includes": "listedNumber"
     * }
     *{
     *   "classifyCodes":"",
     *   "pageNo":1,
     *   "customCondition":{
     *     "cid":"1000"
     *   },
     *   "pageSize":65535,
     *   "paging":true,
     *   "includes":"listedNumber",
     *   "permissionCode":null,
     *   "crossCompanyFlag":"false"
     * }
     */
    @Override
    public void listedRefQuery() {
        Map<String, Object> pageQueryParam = new HashMap<>();
        pageQueryParam.put("pageSize", 65535);
        pageQueryParam.put("paging", true);
        pageQueryParam.put("pageNo", 1);
        pageQueryParam.put("crossCompanyFlag", "false");

        JsonObject customCondition=new JsonObject();
        customCondition.addProperty("cid", SupPlantApplication.getAccountInfo().getCompanyId() +"");
        pageQueryParam.put("customCondition",customCondition);
        pageQueryParam.put("permissionCode",null);
        pageQueryParam.put("includes","listedNumber");
        pageQueryParam.put("classifyCodes", "");

        mCompositeSubscription.add(DefectManagerHttpClient.listedRefQuery(pageQueryParam)
                .onErrorReturn(new FunctionEx<Throwable, BapPageResultEntity<DefectListNumEntity>>() {
                    @Override
                    public BAP5CommonEntity apply(Throwable throwable)  {
                        return super.apply(throwable);
                    }
                }).subscribe(new Consumer<BAP5CommonEntity>() {
                    @Override
                    public void accept(BAP5CommonEntity incidentTmpEntityCommonEntity) throws Exception {
                        if (incidentTmpEntityCommonEntity.success) {
                            getView().listedRefQuerySuccess((BapPageResultEntity) incidentTmpEntityCommonEntity.data);
                        } else {
                            getView().listedRefQueryFailed(incidentTmpEntityCommonEntity.msg);
                        }
                    }
                }));
    }
}
