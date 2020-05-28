package com.supcon.mes.module_xj.model.contract;

import com.supcon.common.view.base.presenter.BasePresenter;
import com.supcon.common.view.contract.IBaseView;
import com.supcon.mes.module_xj.model.api.XJLocalTaskAPI;
import java.lang.String;
import java.util.List;

/**
 * @Contract created by apt
 * 注解内实体和方法是一一对应的
 * add by wangshizhan
 */
public interface XJLocalTaskContract {
  /**
   * @View created by apt
   */
  interface View extends IBaseView {
    /**
     * @method create by apt
     */
    void getLocalTaskSuccess(List entity);

    /**
     * @method create by apt
     */
    void getLocalTaskFailed(String errorMsg);

    /**
     * @method create by apt
     */
    void saveLocalTaskSuccess();

    /**
     * @method create by apt
     */
    void saveLocalTaskFailed(String errorMsg);
  }

  /**
   * @Presenter created by apt
   */
  abstract class Presenter extends BasePresenter<XJLocalTaskContract.View> implements XJLocalTaskAPI {
  }
}
