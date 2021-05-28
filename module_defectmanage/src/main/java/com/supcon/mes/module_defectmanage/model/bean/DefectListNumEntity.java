package com.supcon.mes.module_defectmanage.model.bean;

import com.supcon.common.com_http.BaseEntity;

/**
 * Time:    2021/5/11  13: 15
 * Author： mac
 * Des:
 */
public class DefectListNumEntity extends BaseEntity {


    /**
     * attrMap : null
     * cid : 1000
     * createStaff : null
     * createTime : null
     * id : 102133547767040
     * listedName : 挂牌二
     * listedNumber : xyf002
     * remarks : null
     * sort : null
     * status : null
     * tableInfoId : null
     * tableNo : null
     * valid : true
     * version : 0
     */

    private Object attrMap;
    private int cid;
    private Object createStaff;
    private Object createTime;
    private long id;
    private String listedName;
    private String listedNumber;
    private Object remarks;
    private Object sort;
    private Object status;
    private Object tableInfoId;
    private Object tableNo;
    private boolean valid;
    private int version;

    public Object getAttrMap() {
        return attrMap;
    }

    public void setAttrMap(Object attrMap) {
        this.attrMap = attrMap;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public Object getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Object createStaff) {
        this.createStaff = createStaff;
    }

    public Object getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Object createTime) {
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getListedName() {
        return listedName;
    }

    public void setListedName(String listedName) {
        this.listedName = listedName;
    }

    public String getListedNumber() {
        return listedNumber;
    }

    public void setListedNumber(String listedNumber) {
        this.listedNumber = listedNumber;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public Object getSort() {
        return sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getTableInfoId() {
        return tableInfoId;
    }

    public void setTableInfoId(Object tableInfoId) {
        this.tableInfoId = tableInfoId;
    }

    public Object getTableNo() {
        return tableNo;
    }

    public void setTableNo(Object tableNo) {
        this.tableNo = tableNo;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return listedNumber;
    }
}
