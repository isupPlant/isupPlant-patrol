package com.supcon.mes.module_defectmanage.controller;

import android.content.Context;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.mes.module_defectmanage.presenter.AddDefectPresenter;

/**
 * Time:    2021/3/25  17: 23
 * Author： mac
 * Des:
 */
@Presenter(value = AddDefectPresenter.class)
public class BathUploadDefectController extends BaseDataController {
    public BathUploadDefectController(Context context) {
        super(context);
    }


}
