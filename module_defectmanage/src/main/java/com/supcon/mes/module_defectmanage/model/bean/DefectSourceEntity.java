package com.supcon.mes.module_defectmanage.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.SelectEntity;

/**
 * Time:    2021/5/11  13: 15
 * Author： mac
 * Des:
 */
public class DefectSourceEntity extends BaseEntity {

    /**
     * _code : 1000
     * _parentCode : -1
     * attrMap : null
     * cid : 1000
     * classCode : OSI
     * createStaff : null
     * createTime : null
     * id : 1000
     * isParent : false
     * layNo : 1
     * layRec : 1000
     * name : 巡检
     * parentId : -1
     * sort : null
     * status : null
     * tableInfoId : null
     * tableNo : null
     * valid : true
     * version : 1
     */

    private String _code;
    private String _parentCode;
    private int cid;
    private String classCode;
    private Long id;
    private boolean isParent;
    private int layNo;
    private String layRec;
    private String name;
    private int parentId;
    private boolean valid;
    private int version;

    public String get_code() {
        return _code;
    }

    public String get_parentCode() {
        return _parentCode;
    }

    public int getCid() {
        return cid;
    }

    public String getClassCode() {
        return classCode;
    }

    public Long getId() {
        return id;
    }

    public boolean isParent() {
        return isParent;
    }

    public int getLayNo() {
        return layNo;
    }

    public String getLayRec() {
        return layRec;
    }

    public String getName() {
        return name;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isValid() {
        return valid;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return name;
    }
}
