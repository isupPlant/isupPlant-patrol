package com.supcon.mes.module_defectmanage.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.CompanyEntity;
import com.supcon.mes.middleware.model.bean.DepartmentEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * Time:    2020-05-30  14: 09
 * Author： nina
 * Des:
 * <p>
 * 缺陷录入接口：	defectEntry
 * 传入参数	字段	字段名称	字段类型	必填	字段描述	备注
 * name	缺陷名称	String	是
 * eamCode	设备编码	String	否		二选一选填
 * eamId	设备Id	Long	否
 * finderCode	发现人编码	String	逻辑必填		二选一必填
 * finderId	发现人Id	Long	逻辑必填
 * findTime	发现时间	String	否	格式为：yyyy/MM/dd HH:mm:ss	默认当前时间
 * eamDeptCode	受检装置/部门编码	String	逻辑必填		二选一必填
 * eamDeptId	受检装置/部门Id	Long	逻辑必填
 * areaCode	受检区域编码	String	逻辑必填		二选一必填
 * areaId	受检区域Id	Long	逻辑必填
 * isClosed	是否完成	Boolean	否		默认false
 * defectType	缺陷类型	String	是	工艺：DefectManage_problemClass/Tec;        设备：DefectManage_problemClass/equipment;   生产：DefectManage_problemClass/produce;    故障：DefectManage_problemClass/Fault;      泄露：DefectManage_problemClass/Leak;
 * problemLevel	缺陷等级	String	否	一般：DefectManage_problemLevel/common;     重要：DefectManage_problemLevel/important;	默认一般
 * defectSource	缺陷来源	String	是	MES报警：MESA;                              巡检：OSI;  	该字段对应缺陷来源页面配置数据编码
 * hiddenApperance	现象描述	String	否
 * listed	是否挂牌	Boolean	逻辑必填
 * listedNumber	挂牌号	String	逻辑必填
 * leakName	漏点名称	String	逻辑必填
 * listedTime	挂牌时间	String	逻辑必填	格式为：yyyy/MM/dd HH:mm:ss
 * assessorCode	评估人编码	String	逻辑必填		未完成缺陷，二选一必填
 * assessorId	评估人Id	Long	逻辑必填
 * eliminateTime	消缺时间	String	逻辑必填	格式为：yyyy/MM/dd HH:mm:ss	已完成缺陷，必填
 * dealStaffCode	处理人编码	String	逻辑必填		已完成缺陷，二选一必填
 * dealStaffId	处理人Id	Long	逻辑必填
 * classification	隐患分类	String	否
 * defectFile	缺陷附件	String	否
 * cid	公司Id	Long	是
 * <p>
 * 传出参数	code	编码	int	是
 * success	成功标识	Boolean	是	true：成功！    false：失败！
 * data	缺陷Id	Long	是
 * msg	错误信息	String	否
 * <p>
 * 详细信息参照接口文档
 */
@Entity
public class DefectModelEntity extends BaseEntity {

    @Id(autoincrement = false)
    public Long dbId;
    
    public String tableNo;//巡检中的区域的表？一个区域一个表吗//外键

    public boolean isValid;//合法的，默认不合法，就是这条数据有没有完成，保存的时候我就做了校验，提交的时候可以直接使用

    public String name;
    public String eamName;
    public String eamCode;//选填 设备编码
    public Long eamId;//设备Id	Long	否		二选一选填

    public String finderCode;
    public Long finderId;
    public String finderName;
    public String findTime;
    public String eamDeptCode;
    public String eamDeptName;
    public Long eamDeptId;
    public String areaCode;
    public String areaName;
    public Long areaId;
    public Boolean isClosed;
    public String defectType;
    public String problemLevel;
    public String defectSource;
    public String hiddenApperance;
    public Boolean listed;
    public String listedNumber;
    public String leakName;
    public String listedTime;
    public String assessorCode;
    public String assessorName;
    public Long assessorId;
    public String eliminateTime;
    public String dealStaffCode;
    public Long dealStaffId;
    public String classification;
    @Transient
    public List<FileUploadDefectEntity> defectFile;
    public Long cid;

    public String fileJson;//存放 file数组的json的的字段

    public String getFileJson() {
        return fileJson;
    }

    public void setFileJson(String fileJson) {
        this.fileJson = fileJson;
    }


    @Generated(hash = 1719998226)
    public DefectModelEntity() {
    }

    @Generated(hash = 496418111)
    public DefectModelEntity(Long dbId, String tableNo, boolean isValid, String name, String eamName, String eamCode, Long eamId, String finderCode, Long finderId, String finderName, String findTime, String eamDeptCode,
            String eamDeptName, Long eamDeptId, String areaCode, String areaName, Long areaId, Boolean isClosed, String defectType, String problemLevel, String defectSource, String hiddenApperance, Boolean listed, String listedNumber,
            String leakName, String listedTime, String assessorCode, String assessorName, Long assessorId, String eliminateTime, String dealStaffCode, Long dealStaffId, String classification, Long cid, String fileJson) {
        this.dbId = dbId;
        this.tableNo = tableNo;
        this.isValid = isValid;
        this.name = name;
        this.eamName = eamName;
        this.eamCode = eamCode;
        this.eamId = eamId;
        this.finderCode = finderCode;
        this.finderId = finderId;
        this.finderName = finderName;
        this.findTime = findTime;
        this.eamDeptCode = eamDeptCode;
        this.eamDeptName = eamDeptName;
        this.eamDeptId = eamDeptId;
        this.areaCode = areaCode;
        this.areaName = areaName;
        this.areaId = areaId;
        this.isClosed = isClosed;
        this.defectType = defectType;
        this.problemLevel = problemLevel;
        this.defectSource = defectSource;
        this.hiddenApperance = hiddenApperance;
        this.listed = listed;
        this.listedNumber = listedNumber;
        this.leakName = leakName;
        this.listedTime = listedTime;
        this.assessorCode = assessorCode;
        this.assessorName = assessorName;
        this.assessorId = assessorId;
        this.eliminateTime = eliminateTime;
        this.dealStaffCode = dealStaffCode;
        this.dealStaffId = dealStaffId;
        this.classification = classification;
        this.cid = cid;
        this.fileJson = fileJson;
    }

    public boolean getIsValid() {
        return this.isValid;
    }
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEamCode() {
        return this.eamCode;
    }
    public void setEamCode(String eamCode) {
        this.eamCode = eamCode;
    }
    public Long getEamId() {
        return this.eamId;
    }
    public void setEamId(Long eamId) {
        this.eamId = eamId;
    }
    public String getFinderCode() {
        return this.finderCode;
    }
    public void setFinderCode(String finderCode) {
        this.finderCode = finderCode;
    }
    public Long getFinderId() {
        return this.finderId;
    }
    public void setFinderId(Long finderId) {
        this.finderId = finderId;
    }
    public String getFindTime() {
        return this.findTime;
    }
    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }
    public String getEamDeptCode() {
        return this.eamDeptCode;
    }
    public void setEamDeptCode(String eamDeptCode) {
        this.eamDeptCode = eamDeptCode;
    }
    public Long getEamDeptId() {
        return this.eamDeptId;
    }
    public void setEamDeptId(Long eamDeptId) {
        this.eamDeptId = eamDeptId;
    }
    public String getAreaCode() {
        return this.areaCode;
    }
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public Long getAreaId() {
        return this.areaId;
    }
    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
    public Boolean getIsClosed() {
        return this.isClosed;
    }
    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }
    public String getDefectType() {
        return this.defectType;
    }
    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }
    public String getProblemLevel() {
        return this.problemLevel;
    }
    public void setProblemLevel(String problemLevel) {
        this.problemLevel = problemLevel;
    }
    public String getDefectSource() {
        return this.defectSource;
    }
    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }
    public String getHiddenApperance() {
        return this.hiddenApperance;
    }
    public void setHiddenApperance(String hiddenApperance) {
        this.hiddenApperance = hiddenApperance;
    }
    public Boolean getListed() {
        return this.listed;
    }
    public void setListed(Boolean listed) {
        this.listed = listed;
    }
    public String getListedNumber() {
        return this.listedNumber;
    }
    public void setListedNumber(String listedNumber) {
        this.listedNumber = listedNumber;
    }
    public String getLeakName() {
        return this.leakName;
    }
    public void setLeakName(String leakName) {
        this.leakName = leakName;
    }
    public String getListedTime() {
        return this.listedTime;
    }
    public void setListedTime(String listedTime) {
        this.listedTime = listedTime;
    }
    public String getAssessorCode() {
        return this.assessorCode;
    }
    public void setAssessorCode(String assessorCode) {
        this.assessorCode = assessorCode;
    }
    public Long getAssessorId() {
        return this.assessorId;
    }
    public void setAssessorId(Long assessorId) {
        this.assessorId = assessorId;
    }
    public String getEliminateTime() {
        return this.eliminateTime;
    }
    public void setEliminateTime(String eliminateTime) {
        this.eliminateTime = eliminateTime;
    }
    public String getDealStaffCode() {
        return this.dealStaffCode;
    }
    public void setDealStaffCode(String dealStaffCode) {
        this.dealStaffCode = dealStaffCode;
    }
    public Long getDealStaffId() {
        return this.dealStaffId;
    }
    public void setDealStaffId(Long dealStaffId) {
        this.dealStaffId = dealStaffId;
    }
    public String getClassification() {
        return this.classification;
    }
    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Long getCid() {
        return this.cid;
    }
    public void setCid(Long cid) {
        this.cid = cid;
    }
    public String getEamName() {
        return this.eamName;
    }
    public void setEamName(String eamName) {
        this.eamName = eamName;
    }
    public String getFinderName() {
        return this.finderName;
    }
    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }
    public String getAreaName() {
        return this.areaName;
    }
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    

    public Long getDbId() {
        return this.dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getEamDeptName() {
        return this.eamDeptName;
    }

    public void setEamDeptName(String eamDeptName) {
        this.eamDeptName = eamDeptName;
    }

    public String getAssessorName() {
        return this.assessorName;
    }

    public void setAssessorName(String assessorName) {
        this.assessorName = assessorName;
    }

    public List<FileUploadDefectEntity> getDefectFile() {
        return defectFile;
    }

    public void setDefectFile(List<FileUploadDefectEntity> defectFile) {
        this.defectFile = defectFile;
    }

    public String getTableNo() {
        return this.tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }
}
