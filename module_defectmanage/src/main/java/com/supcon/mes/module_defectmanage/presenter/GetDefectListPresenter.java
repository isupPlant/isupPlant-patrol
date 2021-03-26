package com.supcon.mes.module_defectmanage.presenter;

import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.FunctionEx;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectOnlineEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectRequestListEntity;
import com.supcon.mes.module_defectmanage.model.contract.GetDefectListContract;
import com.supcon.mes.module_defectmanage.model.network.DefectManagerHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class GetDefectListPresenter extends GetDefectListContract.Presenter {


    /**
     *      * {
     *      * "pageSize":10,
     *      * "pageNum":1,
     *      * "cid":1000,
     *      * "defectSource":"OSI",
     *      * "eamDeptId":null,
     *      * "areaCode":null,
     *      * "tableNo":null,
     *      * "eamId":null
     *      * }
     * @param tableNo
     * @param areaCode
     */
    @Override
    public void getDefectList(String tableNo, String areaCode, int pageNo) {

        DefectRequestListEntity entity = new DefectRequestListEntity();
//        entity.setAreaCode(areaCode);
//        entity.setTableNo(tableNo);
        entity.setCid(SupPlantApplication.getAccountInfo().companyId);
        entity.setDefectSource("OSI");
        entity.setPageNo(pageNo);
        entity.setPageSize(20);
        mCompositeSubscription.add(DefectManagerHttpClient.getDefectList(entity)
                .onErrorReturn(new FunctionEx<Throwable, List<DefectOnlineEntity>>() {
                    @Override
                    public BAP5CommonEntity apply(Throwable throwable)  {
                        return super.apply(throwable);
                    }
                }).subscribe(new Consumer<BAP5CommonEntity<List<DefectOnlineEntity>>>() {
                    @Override
                    public void accept(BAP5CommonEntity<List<DefectOnlineEntity>> incidentTmpEntityCommonEntity) throws Exception {
                        if (incidentTmpEntityCommonEntity.success) {
                            getView().getDefectListSuccess(incidentTmpEntityCommonEntity);
                        } else {
                            getView().getDefectListFailed(incidentTmpEntityCommonEntity.msg);
                        }
                    }
                }));
    }
}
