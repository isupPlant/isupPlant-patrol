package com.supcon.mes.module_defectmanage.model.bean;

import com.supcon.common.com_http.BaseEntity;

public class FileUploadDefectEntity extends BaseEntity {

    private String fileIcon;
    private String path;
    private String filename;

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
