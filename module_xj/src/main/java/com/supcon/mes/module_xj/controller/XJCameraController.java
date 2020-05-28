package com.supcon.mes.module_xj.controller;

import android.text.TextUtils;
import android.view.View;

import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.mbap.beans.GalleryBean;
import com.supcon.mes.mbap.view.CustomGalleryView;
import com.supcon.mes.middleware.controller.BaseCameraController;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.event.ImageDeleteEvent;
import com.supcon.mes.module_xj.util.FaultPicHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangshizhan on 2019/3/8
 * Email:wangshizhan@supcom.com
 */
public class XJCameraController extends BaseCameraController {

    BaseListDataRecyclerViewAdapter<XJWorkEntity> mAdapter;

    public XJCameraController(View rootView) {
        super(rootView);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteImage(ImageDeleteEvent imageDeleteEvent) {
        CustomGalleryView customGalleryView = actionGalleryView;
        List<String> picStrs = FaultPicHelper.getImagePathList(customGalleryView.getGalleryAdapter().getList());
        int position = -1;
        boolean isMatch = false;
        for (int i = 0; !isMatch && picStrs.size() > 0; i++) {
            String name = picStrs.get(i);
            if (name.equals(imageDeleteEvent.getPicName())) {
                position = picStrs.indexOf(name);
                isMatch = true;
            }
        }

        if (position != -1) {
            customGalleryView.deletePic(position);
            deleteFile(imageDeleteEvent.getPicName());

            XJWorkEntity xjWorkItemEntity = mAdapter.getItem(actionPosition);
            String xjImgUrl = xjWorkItemEntity.xjImgName;
            if (xjImgUrl.startsWith(imageDeleteEvent.getPicName())) {
                if (xjImgUrl.equals(imageDeleteEvent.getPicName())) {
                    xjWorkItemEntity.xjImgName = xjImgUrl.replace(imageDeleteEvent.getPicName(), "");
                } else {
                    xjWorkItemEntity.xjImgName = xjImgUrl.replace(imageDeleteEvent.getPicName() + ",", "");
                }
            } else {
                xjWorkItemEntity.xjImgName = xjImgUrl.replace("," + imageDeleteEvent.getPicName(), "");
            }
            if (TextUtils.isEmpty(xjWorkItemEntity.xjImgName)) {  //实际拍照字段还原
                xjWorkItemEntity.isRealPhoto = false;
            }

//            mAdapter.notifyItemChanged(actionPosition);
            mAdapter.notifyDataSetChanged();

        }
    }

    public void addListener(CustomGalleryView customGalleryView, int position, BaseListDataRecyclerViewAdapter xjWorkItemListNewAdapter) {
        super.addListener(position, customGalleryView);
        mAdapter = xjWorkItemListNewAdapter;
    }

    @Override
    protected void onFileReceived(File file) {

        if(actionPosition == -1){
            LogUtil.e("actionPosition == -1");
            return;
        }
        XJWorkEntity xjWorkItemEntity = mAdapter.getItem(actionPosition);

        if(xjWorkItemEntity == null){


            return;
        }
        if (TextUtils.isEmpty(xjWorkItemEntity.xjImgName)) {
            xjWorkItemEntity.xjImgName = file.getAbsolutePath();
        } else {
            xjWorkItemEntity.xjImgName += ","+file.getAbsolutePath();
        }
        if (!TextUtils.isEmpty(xjWorkItemEntity.xjImgName)) {  //拍照时才可以修改(拍照但是又X掉，该字段为空)
            xjWorkItemEntity.isRealPhoto = true;
        }
        mAdapter.notifyItemChanged(actionPosition);
    }

    @Override
    protected void onFileDelete(GalleryBean galleryBean, int position) {

        if(actionPosition == -1){
            LogUtil.e("actionPosition == -1");
            return;
        }

        XJWorkEntity xjWorkItemEntity = mAdapter.getItem(actionPosition);
        if(xjWorkItemEntity == null){
            return;
        }
        actionGalleryView.deletePic(position);
        List<String> imgNamesList = Arrays.asList(xjWorkItemEntity.xjImgName.split(","));
        String xjImgUrl = xjWorkItemEntity.xjImgName;
        String imgName = imgNamesList.get(position);
        deleteFile(imgName);

        if (xjImgUrl.startsWith(imgName)) {
            if (xjImgUrl.equals(imgName)) {
                xjWorkItemEntity.xjImgName = xjImgUrl.replace(imgName, "");
            } else {
                xjWorkItemEntity.xjImgName = xjImgUrl.replace(imgName + ",", "");
            }
        } else {
            xjWorkItemEntity.xjImgName = xjImgUrl.replace("," + imgName, "");
        }
        if (TextUtils.isEmpty(xjWorkItemEntity.xjImgName)) {  //实际拍照字段还原
            xjWorkItemEntity.isRealPhoto = false;
        }

        mAdapter.notifyItemChanged(actionPosition);
    }

}
