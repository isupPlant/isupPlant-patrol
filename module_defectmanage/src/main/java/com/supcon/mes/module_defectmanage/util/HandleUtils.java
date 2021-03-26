package com.supcon.mes.module_defectmanage.util;

import com.supcon.mes.middleware.model.bean.FileEntity;
import com.supcon.mes.module_defectmanage.model.bean.FileUploadDefectEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:    2021/3/24  15: 54
 * Author： mac
 * Des:
 */
public class HandleUtils {
    private static String deviceIdList;

    public static List<FileUploadDefectEntity> converFileToUploadFile(List<FileEntity> fileEntities) {
        List<FileUploadDefectEntity> resultList = new ArrayList<>();
        if (fileEntities != null && fileEntities.size() > 0) {
            for (FileEntity fileEntity : fileEntities) {
                FileUploadDefectEntity entity = new FileUploadDefectEntity();
                entity.setFileIcon(fileEntity.getFileIcon());
                entity.setFilename(fileEntity.getFilename());
                entity.setPath(fileEntity.getPath());
                resultList.add(entity);
            }
        }
        return resultList;
    }

    public static String getDeviceIdList() {
        return deviceIdList;
    }

    public static void setDeviceIdList(String deviceIdList) {
        HandleUtils.deviceIdList = deviceIdList;
    }
}
