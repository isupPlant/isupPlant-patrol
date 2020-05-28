package com.supcon.mes.module_xj.model.network;

import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.mes.mbap.network.Api;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import io.reactivex.Flowable;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

/**
 * @API factory created by apt
 */
public final class XJHttpClient {
  /**
   * @created by apt
   */
  public static Flowable<BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>>> getTaskList(
      Map<String, Object> pageMap) {
    return Api.getInstance().retrofit.create(NetworkAPI.class).getTaskList(pageMap).compose(RxSchedulers.io_main());
  }

  /**
   * @created by apt
   */
  public static Flowable<BAP5CommonEntity<String>> taskStateUpdate(Map<String, Object> queryMap) {
    return Api.getInstance().retrofit.create(NetworkAPI.class).taskStateUpdate(queryMap).compose(RxSchedulers.io_main());
  }

  /**
   * @created by apt
   */
  public static Flowable<BAP5CommonEntity<String>> uploadTaskResult(boolean isOnLine,
      Map<String, Object> pageMap) {
    return Api.getInstance().retrofit.create(NetworkAPI.class).uploadTaskResult(isOnLine,pageMap).compose(RxSchedulers.io_main());
  }
}
