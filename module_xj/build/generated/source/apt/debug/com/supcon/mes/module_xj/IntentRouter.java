package com.supcon.mes.module_xj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.supcon.common.com_router.api.IRouter;
import com.supcon.common.com_router.util.RouterManager;
import com.supcon.mes.module_xj.ui.XJTaskDetailActivity;
import com.supcon.mes.module_xj.ui.XJTaskGetActivity;
import com.supcon.mes.module_xj.ui.XJTaskListActivity;
import com.supcon.mes.module_xj.ui.XJTaskUploadActivity;
import com.supcon.mes.module_xj.ui.XJTempTaskActivity;
import com.supcon.mes.module_xj.ui.XJWorkActivity;
import com.supcon.mes.module_xj.ui.XJWorkViewActivity;
import java.lang.String;

/**
 * @API intent router created by apt
 * 支持组件化多模块
 * add by wangshizhan
 */
public final class IntentRouter implements IRouter {
  static {
    RouterManager.getInstance().register("XJ_TASK_DETAIL", XJTaskDetailActivity.class);
    RouterManager.getInstance().register("XJ_TASK_GET", XJTaskGetActivity.class);
    RouterManager.getInstance().register("XJ_TASK_LIST", XJTaskListActivity.class);
    RouterManager.getInstance().register("XJGL", XJTaskListActivity.class);
    RouterManager.getInstance().register("XJ_TASK_UPLOAD", XJTaskUploadActivity.class);
    RouterManager.getInstance().register("XJ_TEMP_TASK", XJTempTaskActivity.class);
    RouterManager.getInstance().register("XJ_WORK_ITEM", XJWorkActivity.class);
    RouterManager.getInstance().register("XJ_WORK_ITEM_VIEW", XJWorkViewActivity.class);
  }

  /**
   * @created by apt 
   */
  public static void go(Context context, String name, Bundle extra) {
    Intent intent =new Intent();
    if(extra != null)
    	intent.putExtras(extra);
     switch (name) {
      	case "XJ_TASK_DETAIL": 
      		intent.setClass(context, XJTaskDetailActivity.class);
      		break;
      	case "XJ_TASK_GET": 
      		intent.setClass(context, XJTaskGetActivity.class);
      		break;
      	case "XJ_TASK_LIST": 
      		intent.setClass(context, XJTaskListActivity.class);
      		break;
      	case "XJGL": 
      		intent.setClass(context, XJTaskListActivity.class);
      		break;
      	case "XJ_TASK_UPLOAD": 
      		intent.setClass(context, XJTaskUploadActivity.class);
      		break;
      	case "XJ_TEMP_TASK": 
      		intent.setClass(context, XJTempTaskActivity.class);
      		break;
      	case "XJ_WORK_ITEM": 
      		intent.setClass(context, XJWorkActivity.class);
      		break;
      	case "XJ_WORK_ITEM_VIEW": 
      		intent.setClass(context, XJWorkViewActivity.class);
      		break;
      default: 
      		RouterManager routerManager = RouterManager.getInstance();
      		Class destinationClass = routerManager.getDestination(name);
      		if(destinationClass == null) return;
      		intent.setClass(context, destinationClass);
      		break;
    }
    context.startActivity(intent);
  }

  /**
   * @created by apt */
  public static void go(Context context, String name) {
    go(context, name, null);
  }

  /**
   * @created by apt */
  public static void setup() {
  }
}
