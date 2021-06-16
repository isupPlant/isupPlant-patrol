package com.supcon.mes.module_defectmanage.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.middleware.model.bean.DaoMaster;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntityDao;

import org.greenrobot.greendao.database.Database;

/**
 * Time:    2021/6/16  11: 11
 * Author： mac
 * Des:
 */
public class DMMySQLiteOpenHelper extends  DaoMaster.OpenHelper {
    public DMMySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        LogUtil.e("onUpgrade" + oldVersion + ":" + newVersion);

        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener(){
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
                LogUtil.e("onCreateAllTables");
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, DefectModelEntityDao.class);
     }
}
