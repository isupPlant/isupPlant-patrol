package com.supcon.mes.module_defectmanage.presenter;

import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.FunctionEx;
import com.supcon.mes.middleware.util.FileUtil;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.contract.AddDefectContract;
import com.supcon.mes.module_defectmanage.model.network.DefectManagerHttpClient;

import java.util.List;

import io.reactivex.functions.Consumer;

public class AddDefectPresenter extends AddDefectContract.Presenter {



    @Override
    public void defectEntry(DefectModelEntity info) {

        mCompositeSubscription.add(DefectManagerHttpClient.defectEntry(info)
                .onErrorReturn(new FunctionEx<Throwable, Object>() {
                    @Override
                    public BAP5CommonEntity apply(Throwable throwable)  {
                        //写入到本地接口日志文件中；
//                        String fileName = FileUtil.getFilePathAndName(FileUtil.NETWORK_REQUEST_LOG);
//                        FileUtil.write2FileAppend(fileName, (Throwable) throwable);
                        return super.apply(throwable);
                    }
                }).subscribe(new Consumer<BAP5CommonEntity<Object>>() {
                    @Override
                    public void accept(BAP5CommonEntity<Object> incidentTmpEntityCommonEntity) throws Exception {
                        if (incidentTmpEntityCommonEntity.success) {
                            getView().defectEntrySuccess(incidentTmpEntityCommonEntity);
                        } else {
                            //写入到本地接口日志文件中；
                            String fileName = FileUtil.getFilePathAndName(FileUtil.NETWORK_REQUEST_LOG);
                            FileUtil.write2FileAppend(fileName, GsonUtil.gsonString(info));

                            getView().defectEntryFailed(incidentTmpEntityCommonEntity.msg);
                        }
                    }
                }));
    }

    @Override
    public void defectEntryBatch(List<DefectModelEntity> infoList) {
        mCompositeSubscription.add(DefectManagerHttpClient.defectEntryBatch(infoList)
                .onErrorReturn(new FunctionEx<Throwable, Object>() {
                    @Override
                    public BAP5CommonEntity apply(Throwable throwable)  {
                        //写入到本地接口日志文件中；
                        String fileName = FileUtil.getFilePathAndName(FileUtil.NETWORK_REQUEST_LOG);
                        FileUtil.write2FileAppend(fileName, (Throwable) throwable);

                        return super.apply(throwable);
                    }
                }).subscribe(new Consumer<BAP5CommonEntity<Object>>() {
                    @Override
                    public void accept(BAP5CommonEntity<Object> incidentTmpEntityCommonEntity) throws Exception {
                        if (incidentTmpEntityCommonEntity.success) {
                            getView().defectEntryBatchSuccess(incidentTmpEntityCommonEntity);
                        } else {
                            //写入到本地接口日志文件中；
                            String fileName = FileUtil.getFilePathAndName(FileUtil.NETWORK_REQUEST_LOG);
                            FileUtil.write2FileAppend(fileName, GsonUtil.gsonString(infoList));

                            getView().defectEntryBatchFailed(incidentTmpEntityCommonEntity.msg);
                        }
                    }
                }));
    }
}
