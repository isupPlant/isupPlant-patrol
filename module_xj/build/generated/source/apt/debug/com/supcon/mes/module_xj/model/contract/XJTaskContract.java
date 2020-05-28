package com.supcon.mes.module_xj.model.contract;

import com.supcon.common.view.base.presenter.BasePresenter;
import com.supcon.common.view.contract.IBaseView;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.module_xj.model.api.XJTaskAPI;
import java.lang.String;

/**
 * @Contract created by apt
 * 注解内实体和方法是一一对应的
 * add by wangshizhan
 */
public interface XJTaskContract {
  /**
   * @View created by apt
   */
  interface View extends IBaseView {
    /**
     * @method create by apt
     */
    void getTaskListSuccess(CommonBAPListEntity entity);

    /**
     * @method create by apt
     */
    void getTaskListFailed(String errorMsg);
  }

  /**
   * @Presenter created by apt
   */
  abstract class Presenter extends BasePresenter<XJTaskContract.View> implements XJTaskAPI {
  }
}
