package com.supcon.mes.module_xj.model.bean;

import android.text.TextUtils;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.ObjectEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskRouteEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;

import java.util.List;

import javax.annotation.Nullable;

/**
 * 巡检本地缓存实体类，只存必要数据，为上传做准备
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
 public class XJTaskUploadEntity extends BaseEntity {

   /**
    * {
    * 		"potrolTask": {
    * 			"id":1024,
    * 			"taskState": {
    * 				"id":"PATROL_taskState/notIssued"
    *                        }* 		},
    * 		"workItems": [
    *            {
    * 				"id" : 1012,
    * 				"concluse": "是",
    * 				"realValue":{
    * 					"id" : "PATROL_realValue/normal"
    *                },
    * 				"isRealPass":false,
    * 				"isRealPhoto":false,
    * 				"completeDate":1585640716459,
    * 				"xjImgName":"xjRecord20190924_140906.jpg,xjRecord20190924_140913.jpg"
    *            },
    *            {
    * 				"id" : 1013,
    * 				"concluse": "温度异常",
    * 				"realValue":{
    * 					"id" : "PATROL_realValue/abnormal"
    *                },
    * 				"isRealPass":true,
    * 				"passReason":{
    * 					"id" : "PATROL_passReason/notEnabled"
    *                },
    * 				"isRealPhoto":false,
    * 				"completeDate":1585640716459,
    * 				"xjImgName":null
    *            },
    *            {
    * 				"id" : 1014,
    * 				"concluse": "是",
    * 				"realValue":{
    * 					"id" : "PATROL_realValue/normal"
    *                },
    * 				"isRealPass":true,
    * 				"passReason":{
    * 					"id" : "PATROL_passReason/notEnabled"
    *                },
    * 				"isRealPhoto":false,
    * 				"completeDate":1585640716459,
    * 				"xjImgName":null
    *            }
    * 		],
    * 		"workAreas":[
    *            {
    * 				"id" : 1002,
    * 				"payCardType": {
    * 					"id": "PATROL_payCardType/card"
    *                },
    * 				"cardTime": 1585640716459,
    * 				"signInReason": {
    * 					"id":null
    *                }
    *            },
    *            {
    * 				"id" : 1003,
    * 				"payCardType": {
    * 					"id": "PATROL_payCardType/signIn"
    *                },
    * 				"cardTime": 1585640716459,
    * 				"signInReason": {
    * 					"id":"PATROL_signInType/notPosted"
    *                }
    *            },
    *            {
    * 				"id" : 1004,
    * 				"payCardType": {
    * 					"id": "PATROL_payCardType/signIn"
    *                },
    * 				"cardTime": 1585640716459,
    * 				"signInReason": {
    * 					"id":"PATROL_signInType/break"
    *                }
    *            }
    * 		],
    * 		"actualStartTime": 1585640716459,
    * 		"actualEndTime": 1585640874312
    *    },

    */

   public XJTaskUploadEntity(XJWorkEntity xjTaskEntity, String taskStateId, String taskName) {

       potrolTask = new PotrolTask();
       potrolTask.taskName = taskName;
       potrolTask.taskType = Constant.SPOTCHECK;
       potrolTask.taskState = new StringIdEntity(taskStateId);
       potrolTask.completeStaff = new ObjectEntity(SharedPreferencesUtils.getParam(SupPlantApplication.getAppContext(), Constant.BAPQuery.STAFF_ID, 0L));

//       potrolTask.startTime= xjTaskEntity.startTime;
//       potrolTask.endTime= xjTaskEntity.endTime;
//       if (!potrolTask.isTemp){
//           potrolTask.tableInfoId = xjTaskEntity.tableInfoId;
//       }else{
//           potrolTask.source=new StringIdEntity("PATROL_tempTaskSource/PDA");
//       }
//        if (xjTaskEntity.id!=null){
//            potrolTask.id = xjTaskEntity.id;
//        }
   }


    public XJTaskUploadEntity(XJTaskEntity xjTaskEntity, String taskStateId) {
        potrolTask = new PotrolTask();

        potrolTask.taskState = new StringIdEntity(taskStateId);
        potrolTask.completeStaff = new ObjectEntity(SupPlantApplication.getAccountInfo().staffId);
        potrolTask.isTemp = xjTaskEntity.isTemp;
        if (xjTaskEntity.workRoute != null) {
            potrolTask.workRoute = xjTaskEntity.workRoute;
        }
        potrolTask.startTime = xjTaskEntity.startTime;
        potrolTask.endTime = xjTaskEntity.endTime;
        if (!potrolTask.isTemp) {
            potrolTask.tableInfoId = xjTaskEntity.tableInfoId;
        } else {
            potrolTask.source = new StringIdEntity("PATROL_tempTaskSource/PDA");
        }
        if (xjTaskEntity.id!=null){
            potrolTask.id = xjTaskEntity.id;
        }

    }


    public void setWorkItems(List<XJTaskWorkEntity> works) {

       if(works == null){
           return;
       }

        this.workItems = GsonUtil.jsonToList(GsonUtil.gsonString(works), XJWorkUploadEntity.class);

        if (workItems.size() != 0)
            for (int i = workItems.size() - 1; i >= 0; i--) {
                XJWorkUploadEntity xjWorkUploadEntity = workItems.get(i);
                if (TextUtils.isEmpty(xjWorkUploadEntity.concluse) && !xjWorkUploadEntity.isRealPass || xjWorkUploadEntity.completeDate == 0) {
                    workItems.remove(i);
                }
            }

    }


    public void setXJDeviceWorkItems(List<XJWorkEntity> works) {

        if (works == null) {
            return;
        }

        this.workItems = GsonUtil.jsonToList(GsonUtil.gsonString(works), XJWorkUploadEntity.class);

//        if (workItems.size() != 0)
//            for (int i = workItems.size() - 1; i >= 0; i--) {
//                XJWorkUploadEntity xjWorkUploadEntity = workItems.get(i);
//                if (TextUtils.isEmpty(xjWorkUploadEntity.concluse) && !xjWorkUploadEntity.isRealPass || xjWorkUploadEntity.completeDate == 0) {
//                    workItems.remove(i);
//                }
//            }

    }


    public void setWorkAreas(List<XJTaskAreaEntity> areas, @Nullable boolean spotCheck) {

        if(areas == null){
            return;
        }
        this.workAreas = GsonUtil.jsonToList(GsonUtil.gsonString(areas), XJAreaUploadEntity.class);

        if (!spotCheck) {
            if (workAreas.size() != 0)
                for (int i = workAreas.size() - 1; i >= 0; i--) {
                    XJAreaUploadEntity xjAreaUploadEntity = workAreas.get(i);
                    if (xjAreaUploadEntity.cardTime == 0) {
                        workAreas.remove(i);
                    }
                }
        }
    }


    public PotrolTask potrolTask;

    public long actualStartTime;
    public long actualEndTime;
    List<XJAreaUploadEntity> workAreas;
    List<XJWorkUploadEntity> workItems;




    class PotrolTask extends BaseEntity{
        public String taskType;
        public String taskName;
       public Long id;
       public StringIdEntity taskState;
       public long tableInfoId;
       public ObjectEntity completeStaff;
       public boolean isTemp;
       public XJTaskRouteEntity workRoute;
       public Long startTime;
       public Long endTime;
       public StringIdEntity source;
    }



}
