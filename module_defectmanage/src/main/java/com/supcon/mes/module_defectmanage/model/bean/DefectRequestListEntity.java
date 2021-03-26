package com.supcon.mes.module_defectmanage.model.bean;

public class DefectRequestListEntity {

    int pageNo;
    int pageSize;
    String defectSource = "OSI";
    long cid;
    String areaCode;
    String tableNo;

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }
}
