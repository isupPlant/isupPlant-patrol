package com.supcon.mes.module_xj.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.mbap.network.Api;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.AttachmentEntity;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.DataUtil;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.network.MiddlewareHttpClient;
import com.supcon.mes.middleware.util.FileUtil;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.middleware.util.XJTaskCacheUtil;
import com.supcon.mes.middleware.util.ZipUtils;
import com.supcon.mes.module_xj.model.bean.XJTaskUploadEntity;
import com.supcon.mes.module_xj.model.bean.XJUploadEntity;
import com.supcon.mes.module_xj.model.contract.XJTaskSubmitContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;
import com.supcon.mes.patrol.R;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.supcon.mes.middleware.util.Util.createZipFileForm;


/**
 * Created by wangshizhan on 2020/4/15
 * Email:wangshizhan@supcom.com
 */
public class XJTaskSubmitPresenter extends XJTaskSubmitContract.Presenter {

    public static final String XJ_UPLOAD_JSON_FILE_NAME = "xj_upload.json";
    public static final String XJ_UPLOAD_ZIP_FILE_NAME = "xj_upload.zip";


    @SuppressLint("CheckResult")
    @Override
    public void uploadFile(List<XJTaskEntity> xjTaskEntities, boolean isArea) {
        Flowable.just(xjTaskEntities)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<List<XJTaskEntity>, Publisher<File>>() {
                    @Override
                    public Publisher<File> apply(List<XJTaskEntity> xjTaskEntities) throws Exception {
                        File zipFile = createXJZipFile(xjTaskEntities, null, isArea, null, 0, 0);
                        return Flowable.just(zipFile);
                    }
                })
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        uploadXJZipFile(file);
                    }
                });

    }

    /**
     * @param xjTaskEntities
     * @param isArea
     * @param workName
     * @param actualEndTime   实际结束时间
     * @param actualStartTime 实际开始时间
     */
    @SuppressLint("CheckResult")
    @Override
    public void uploadXJWorkFile(List<XJWorkEntity> xjTaskEntities, boolean isArea, String workName, long actualEndTime, long actualStartTime) {
        Flowable.just(xjTaskEntities)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<List<XJWorkEntity>, Publisher<File>>() {
                    @Override
                    public Publisher<File> apply(List<XJWorkEntity> xjTaskEntities) throws Exception {

                        File zipFile = createXJZipFile(null, xjTaskEntities, isArea, workName, actualEndTime, actualStartTime);
                        return Flowable.just(zipFile);
                    }
                })
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        uploadXJZipFile(file);
                    }
                });
    }


    private File createXJZipFile(@Nullable List<XJTaskEntity> xjTaskEntities, @Nullable List<XJWorkEntity> xjWorkEntities, boolean isArea, @Nullable String taskName, long actualStartTime,long actualEndTime) {
        FileUtil.createDir(Constant.FILE_PATH + "xj");
        XJUploadEntity xjUploadEntity = new XJUploadEntity();
        List<XJTaskUploadEntity> xjTaskUploadEntities = new ArrayList<>();
        List<String> includeFiles = new ArrayList<>();
        XJTaskUploadEntity xjTaskUploadEntity = null;
        includeFiles.add(XJ_UPLOAD_JSON_FILE_NAME);
        if (xjWorkEntities != null && xjWorkEntities.size() > 0) {

            List<XJTaskAreaEntity> areaEntities = new ArrayList<>();
            for (XJWorkEntity workEntity : xjWorkEntities) {
                xjTaskUploadEntity = new XJTaskUploadEntity(workEntity, "PATROL_taskState/completed", taskName);
                xjTaskUploadEntity.actualStartTime = actualStartTime;
                xjTaskUploadEntity.actualEndTime = actualEndTime;
                XJTaskAreaEntity areaEntity = new XJTaskAreaEntity();
                areaEntity.id = workEntity.eamId.id;
                areaEntity.code = workEntity.eamId.code;
                areaEntity.name = workEntity.eamId.name;
                areaEntities.add(areaEntity);//填充巡检区域数据
                if (workEntity.xjImgName != null) {
                    workEntity.xjImgName = workEntity.xjImgName.replaceAll("/storage/emulated/0/isupPlant/xj/pics/", "");
                    String imgUrl = workEntity.xjImgName;
                    if (!TextUtils.isEmpty(imgUrl)) {
                        if (imgUrl.contains(",")) {
                            includeFiles.addAll(Arrays.asList(imgUrl.split(",")));
                        } else {
                            includeFiles.add(imgUrl);
                        }
                    }
                }

            }
            if (xjTaskUploadEntity != null) {
                if (areaEntities.size() != 0) {
                    xjTaskUploadEntity.setWorkAreas(areaEntities, true);
                }
                xjTaskUploadEntity.setXJDeviceWorkItems(xjWorkEntities);
                xjTaskUploadEntities.add(xjTaskUploadEntity);
            }
        }

        if (xjTaskEntities != null && xjTaskEntities.size() > 0) {
            for (XJTaskEntity xjTaskEntity : xjTaskEntities) {
                List<XJTaskAreaEntity> xjTaskAreaEntityList = XJTaskCacheUtil.getTaskArea(xjTaskEntity);
                if (xjTaskAreaEntityList != null) {
                    xjTaskEntity.areas = new ArrayList<>();
                    xjTaskEntity.areas.addAll(xjTaskAreaEntityList);
                }
                xjTaskUploadEntity = new XJTaskUploadEntity(xjTaskEntity, isArea ? "PATROL_taskState/running" : "PATROL_taskState/completed");
                xjTaskUploadEntity.actualStartTime = xjTaskEntity.realStartTime;
                xjTaskUploadEntity.actualEndTime = xjTaskEntity.realStartTime;

                if (xjTaskEntity.areas == null || xjTaskEntity.areas.size() == 0) {
                    continue;
                }

                List<XJTaskAreaEntity> areaEntities = new ArrayList<>();
                List<XJTaskWorkEntity> workEntities = new ArrayList<>();

                for (XJTaskAreaEntity xjAreaEntity : xjTaskEntity.areas) {
                    if (xjAreaEntity.works != null && xjAreaEntity.works.size() != 0) {
                        if (xjAreaEntity.completeTime == 0) {
                            xjAreaEntity.completeTime =  System.currentTimeMillis();
                        }
                        areaEntities.add(xjAreaEntity);
                        workEntities.addAll(xjAreaEntity.works);
                    }
                }

                //TODO...处理图片、json文件的压缩
                for (XJTaskWorkEntity xjWorkEntity : workEntities) {
                    if (xjWorkEntity.xjImgName == null) {
                        continue;
                    }
                    xjWorkEntity.xjImgName = xjWorkEntity.xjImgName.replaceAll("/storage/emulated/0/isupPlant/xj/pics/", "");
                    String imgUrl = xjWorkEntity.xjImgName;

                    if (!TextUtils.isEmpty(imgUrl)) {
                        if (imgUrl.contains(",")) {
                            includeFiles.addAll(Arrays.asList(imgUrl.split(",")));
                        } else {
                            includeFiles.add(imgUrl);
                        }
                    }
                }


                if (areaEntities.size() != 0) {
                    xjTaskUploadEntity.setWorkAreas(areaEntities, false);
                }

                if (workEntities.size() != 0) {
                    xjTaskUploadEntity.setWorkItems(workEntities);
                }

                xjTaskUploadEntities.add(xjTaskUploadEntity);
            }
        }

        xjUploadEntity.uploadTaskResultDTOs = xjTaskUploadEntities;
        File xjJsonFile = new File(Constant.XJ_PATH, XJ_UPLOAD_JSON_FILE_NAME);
        String result =  GsonUtil.gsonString(xjUploadEntity);
        FileUtil.write2File(xjJsonFile.getAbsolutePath(), result);
        LogUtil.i("xj upload:" + result);
        try {
            //对文件进行压缩,且仅仅压缩includeFiles中的文件名称所对应的文件
            ZipUtils.zipFolderFilesEx(Constant.XJ_PATH, Constant.FILE_PATH + XJ_UPLOAD_ZIP_FILE_NAME, includeFiles);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //判断上传巡检包文件大小
        File file = new File(Constant.FILE_PATH + XJ_UPLOAD_ZIP_FILE_NAME);
        if (file.exists() && file.isFile()){
            if (file.length()/(1024*1024 ) > 100){
                if(isViewNonNull(getView())){
                    getView().uploadFileFailed(SupPlantApplication.getAppContext().getString(R.string.xj_patrol_upload_size_warning));
                }
                return file;
            }
        }
        return file;
    }

    private boolean isViewNonNull(Object o){

        return o != null;

    }


    private void uploadXJZipFile(File uploadZip) {
        Api.getInstance().setTimeOut(300);
        mCompositeSubscription.add(
                MiddlewareHttpClient.bapUploadFile(createZipFileForm(uploadZip))
                        .compose(RxSchedulers.io_main())
                        .onErrorReturn(throwable -> {
                            Api.getInstance().setTimeOut(30);
                            BAP5CommonEntity commonEntity = new BAP5CommonEntity();
                            commonEntity.success = false;
                            commonEntity.msg = throwable.toString();
                            return commonEntity;
                        })
                        .subscribe(new Consumer<BAP5CommonEntity<AttachmentEntity>>() {
                            @Override
                            public void accept(BAP5CommonEntity<AttachmentEntity> commonEntity) throws Exception {
                                Api.getInstance().setTimeOut(30);
                                if (commonEntity.success) {
//                            if (null == commonEntity.message && 0 == commonEntity.code) {

                                    AttachmentEntity attachmentEntity = commonEntity.data;

                                    if(isViewNonNull(getView())){
                                        getView().uploadFileSuccess(attachmentEntity.path);
                                    }
                                    deleteXJZipFile();
                                } else {
                                    if(isViewNonNull(getView())){
                                        getView().uploadFileFailed(commonEntity.msg);
                                    }
                                }
                            }
                        }));

    }

    private void deleteXJZipFile() {
        String json = Util.getFileFromSD(Constant.XJ_PATH + XJ_UPLOAD_JSON_FILE_NAME);

        if (TextUtils.isEmpty(json)) {
            return;
        }

        FileUtil.deleteFile(Constant.FILE_PATH + XJ_UPLOAD_JSON_FILE_NAME);
        FileUtil.deleteFile(Constant.FILE_PATH + XJ_UPLOAD_ZIP_FILE_NAME);
    }


    @Override
    public void uploadXJData(boolean isOnLine, Map<String, Object> pageMap) {
        mCompositeSubscription.add(XJHttpClient.uploadTaskResult(isOnLine, pageMap)
                .onErrorReturn(new Function<Throwable, BAP5CommonEntity<Long>>() {
                    @Override
                    public BAP5CommonEntity<Long> apply(Throwable throwable) throws Exception {
                        BAP5CommonEntity bap5CommonEntity = new BAP5CommonEntity();
                        bap5CommonEntity.success = false;
                        bap5CommonEntity.msg = throwable.toString();
                        return bap5CommonEntity;
                    }
                })
                .subscribe(new Consumer<BAP5CommonEntity<Long>>() {
                    @Override
                    public void accept(BAP5CommonEntity<Long> commonEntity) throws Exception {
                        if(commonEntity.success){
                            getView().uploadXJDataSuccess(commonEntity.data);
                        }
                        else{
                            getView().uploadXJDataFailed(commonEntity.msg);
                        }
                    }
                }));
    }
}
