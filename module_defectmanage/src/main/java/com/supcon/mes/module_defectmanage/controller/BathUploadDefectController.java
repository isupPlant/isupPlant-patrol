package com.supcon.mes.module_defectmanage.controller;

import android.content.Context;

import com.app.annotation.Presenter;
import com.google.gson.Gson;
import com.supcon.common.view.base.activity.BaseActivity;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.model.api.AddFileListAPI;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.FileEntity;
import com.supcon.mes.middleware.model.contract.AddFileListContract;
import com.supcon.mes.middleware.model.event.BaseEvent;
import com.supcon.mes.middleware.presenter.AddFileListPresenter;
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.AddDefectAPI;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntityDao;
import com.supcon.mes.module_defectmanage.model.bean.FileUploadDefectEntity;
import com.supcon.mes.module_defectmanage.model.contract.AddDefectContract;
import com.supcon.mes.module_defectmanage.presenter.AddDefectPresenter;
import com.supcon.mes.module_defectmanage.util.DatabaseManager;
import com.supcon.mes.module_defectmanage.util.HandleUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:    2021/3/25  17: 23
 * Author： mac
 * Des:
 */
@Presenter(value = {AddDefectPresenter.class, AddFileListPresenter.class})
public class BathUploadDefectController extends BaseDataController implements AddDefectContract.View,
        AddFileListContract.View{
    public BathUploadDefectController(Context context) {
        super(context);
    }


    List<DefectModelEntity> defectModelEntityList = new ArrayList<>();
    /**
     * 给巡检提供的接口
     * 返回 对应的巡检任务 对应的areaCode的缺陷单 并上传
     * @param tableNo
     * @return
     */
    public void bathUploadDefectList(String tableNo) {
        defectModelEntityList.clear();

        if (!StringUtil.isBlank(tableNo)) {
            defectModelEntityList = DatabaseManager.getDao().getDefectModelEntityDao().queryBuilder()
                    .where(DefectModelEntityDao.Properties.TableNo.eq(tableNo)).list();

            if (defectModelEntityList != null && defectModelEntityList.size() > 0) {
                //通知页面更新
                submit(defectModelEntityList);
            } else {
                EventBus.getDefault().post(new BaseEvent(true, ""));
                return;
            }
        }

        EventBus.getDefault().post(new BaseEvent(true, ""));
        return;
    }

    public void bathUploadDefectList(List<DefectModelEntity> list) {
        defectModelEntityList.clear();
        defectModelEntityList = list;

        if (defectModelEntityList != null && defectModelEntityList.size() > 0) {
            //通知页面更新
            submit(defectModelEntityList);
        } else {
            EventBus.getDefault().post(new BaseEvent(true, ""));
            return;
        }
    }

    private void submit(List<DefectModelEntity> list) {
        if (!checkIsValid(list)){
            ToastUtils.show(context, R.string.defect_data_is_not_complete);
            EventBus.getDefault().post(new BaseEvent(false, ""));
            return;
        }

        //如果有附件的话 还要添加附件的逻辑
        //如果有附件的话 还要一个个的上传附件
        uploadFileOneByOne(list);
    }

    private boolean checkIsValid(List<DefectModelEntity> list) {
        for (DefectModelEntity value : list) {
            if (!value.isValid) {
                return false;
            }
        }

        return true;
    }


    private void uploadFileOneByOne(List<DefectModelEntity> list) {
        ArrayList<String> pathAllList = new ArrayList<>();
        for (DefectModelEntity entity : list) {
            if (!StringUtil.isBlank(entity.getFileJson())) {
                ArrayList<FileEntity> fileList = (ArrayList<FileEntity>) GsonUtil.jsonToList(entity.fileJson, FileEntity.class);
                ArrayList<String> pathList = new ArrayList<>();
                for (FileEntity item : fileList) {
                    if (!item.isOnline())
                        pathList.add(item.getPath());
                }

                pathAllList.addAll(pathList);
            }
        }

        if (context instanceof BaseActivity) {
            ((BaseActivity)context).onLoading();
        }

        if (pathAllList.size() == 0) {
            presenterRouter.create(AddDefectAPI.class).defectEntryBatch(list);
        } else {
            presenterRouter.create(AddFileListAPI.class).uploadMultiFiles(pathAllList);
        }
    }


    @Override
    public void uploadMultiFilesSuccess(ArrayList entity) {

        if (entity != null) {
            ArrayList<FileEntity> filelist = entity;
            List<FileUploadDefectEntity> uploadFileFormMapArrayList = null;
            if (filelist != null && filelist.size() > 0) {
                uploadFileFormMapArrayList  = HandleUtils.converFileToUploadFile(filelist);
            }

            for (DefectModelEntity value : defectModelEntityList) {
                if (!StringUtil.isBlank(value.fileJson) && uploadFileFormMapArrayList != null) {
                    String json = value.getFileJson();
                    List<FileUploadDefectEntity> defectFileList = new ArrayList<>();
                    for (FileUploadDefectEntity fileUploadDefectEntity : uploadFileFormMapArrayList) {
                        if (StringUtil.contains(json, fileUploadDefectEntity.getFilename())) {
                            //
                            defectFileList.add(fileUploadDefectEntity);
                        }
                    }
                    String fileListJson = GsonUtil.gsonString(defectFileList);
                    value.setDefectFile(fileListJson);
                }
            }

            presenterRouter.create(AddDefectAPI.class).defectEntryBatch(defectModelEntityList);
        }
    }

    @Override
    public void uploadMultiFilesFailed(String errorMsg) {
        if (context instanceof BaseActivity) {
            ((BaseActivity)context).closeLoader();
        }
        ToastUtils.show(context, errorMsg);
        EventBus.getDefault().post(new BaseEvent(false, ""));
    }

    @Override
    public void defectEntrySuccess(BAP5CommonEntity entity) {

    }

    @Override
    public void defectEntryFailed(String errorMsg) {

    }

    @Override
    public void defectEntryBatchSuccess(BAP5CommonEntity entity) {
        //删除上传的数据
        DatabaseManager.getDao().getDefectModelEntityDao().deleteInTx(defectModelEntityList);

        if (context instanceof BaseActivity) {
            ((BaseActivity)context).onLoadSuccess();
        }
        ToastUtils.show(context, context.getString(R.string.submit_success));

        EventBus.getDefault().post(new BaseEvent(true, ""));
    }

    @Override
    public void defectEntryBatchFailed(String errorMsg) {
        if (context instanceof BaseActivity) {
            ((BaseActivity)context).closeLoader();
        }
        //提示用户保存在本地，但是不能重复保存啊,数据库的id是怎么回事
        ToastUtils.show(context, context.getString(R.string.defect_submit_failed_save_to_local));

        EventBus.getDefault().post(new BaseEvent(false, ""));
    }
}
