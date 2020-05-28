package com.supcon.mes.module_xj;

import com.supcon.common.com_router.widget.IWidgetWapper;
import com.supcon.common.com_router.widget.WidgetWapperManager;
import com.supcon.mes.module_xj.widget.XJTaskWidget;

/**
 * @API WidgetWapper created by apt
 */
public final class WidgetWapper implements IWidgetWapper {
  static {
    WidgetWapperManager.getInstance().add("XJHZ", XJTaskWidget.class);
  }

  /**
   * @created by apt 
   */
  public static void setup() {
  }
}
