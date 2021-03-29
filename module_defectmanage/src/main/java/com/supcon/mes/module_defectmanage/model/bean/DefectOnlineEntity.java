package com.supcon.mes.module_defectmanage.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.BaseCodeIdNameEntity;
import com.supcon.mes.middleware.model.bean.FileEntity;

import java.util.List;

/**
 * Time:    2021/3/26  18: 24
 * Author： mac
 * Des:
 */
public class DefectOnlineEntity extends BaseEntity {

    /**
     * id : 76982459675904
     * name : vfwfefwfw
     * code : PM_2021_03_16_0001
     * hiddenApperance : null
     * tableNo : null
     * defectType : {"name":"DefectManage.custom.random1615361348116","code":"Leak"}
     * problemLevel : {"name":"DefectManage.custom.random1615195084764","code":"common"}
     * defectSource : {"id":76595634910464,"name":"巡检","code":"OSI"}
     * eam : {"id":70290724115712,"name":"总公司设备002","code":"ZGSSB002"}
     * area : {"id":1001,"name":"储运一车间","code":"AREA_202008001"}
     * finder : {"id":1145397801806176,"name":"向云飞","code":"xyf008"}
     * assessor : {"id":1145397801806176,"name":"向云飞","code":"xyf008"}
     * eamDept : {"id":1008966765832544,"name":"MES开发三部","code":"001"}
     * findTime :
     * defectState : {"name":"DefectManage.custom.random1615195188503","code":"unAssess"}
     * defectFile : null
     */

    private long id;
    private String name;
    private String code;
    private Object hiddenApperance;
    private Object tableNo;
    private BaseCodeIdNameEntity defectType;
    private BaseCodeIdNameEntity problemLevel;
    private BaseCodeIdNameEntity defectSource;
    private BaseCodeIdNameEntity eam;
    private BaseCodeIdNameEntity area;
    private BaseCodeIdNameEntity finder;
    private BaseCodeIdNameEntity assessor;
    private BaseCodeIdNameEntity eamDept;
    private String findTime;
    private BaseCodeIdNameEntity defectState;
    private List<FileEntity> defectFile;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getHiddenApperance() {
        return hiddenApperance;
    }

    public void setHiddenApperance(Object hiddenApperance) {
        this.hiddenApperance = hiddenApperance;
    }

    public Object getTableNo() {
        return tableNo;
    }

    public void setTableNo(Object tableNo) {
        this.tableNo = tableNo;
    }

    public BaseCodeIdNameEntity getDefectType() {
        return defectType;
    }

    public void setDefectType(BaseCodeIdNameEntity defectType) {
        this.defectType = defectType;
    }

    public BaseCodeIdNameEntity getProblemLevel() {
        return problemLevel;
    }

    public void setProblemLevel(BaseCodeIdNameEntity problemLevel) {
        this.problemLevel = problemLevel;
    }

    public BaseCodeIdNameEntity getDefectSource() {
        return defectSource;
    }

    public void setDefectSource(BaseCodeIdNameEntity defectSource) {
        this.defectSource = defectSource;
    }

    public BaseCodeIdNameEntity getEam() {
        return eam;
    }

    public void setEam(BaseCodeIdNameEntity eam) {
        this.eam = eam;
    }

    public BaseCodeIdNameEntity getArea() {
        return area;
    }

    public void setArea(BaseCodeIdNameEntity area) {
        this.area = area;
    }

    public BaseCodeIdNameEntity getFinder() {
        return finder;
    }

    public void setFinder(BaseCodeIdNameEntity finder) {
        this.finder = finder;
    }

    public BaseCodeIdNameEntity getAssessor() {
        return assessor;
    }

    public void setAssessor(BaseCodeIdNameEntity assessor) {
        this.assessor = assessor;
    }

    public BaseCodeIdNameEntity getEamDept() {
        return eamDept;
    }

    public void setEamDept(BaseCodeIdNameEntity eamDept) {
        this.eamDept = eamDept;
    }

    public BaseCodeIdNameEntity getDefectState() {
        return defectState;
    }

    public void setDefectState(BaseCodeIdNameEntity defectState) {
        this.defectState = defectState;
    }

    public List<FileEntity> getDefectFile() {
        return defectFile;
    }

    public void setDefectFile(List<FileEntity> defectFile) {
        this.defectFile = defectFile;
    }

    public String getFindTime() {
        return findTime;
    }

    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }



}
