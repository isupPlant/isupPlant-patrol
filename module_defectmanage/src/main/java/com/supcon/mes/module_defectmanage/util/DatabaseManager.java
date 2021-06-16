package com.supcon.mes.module_defectmanage.util;

import android.database.sqlite.SQLiteDatabase;

import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.module_defectmanage.model.bean.DaoMaster;
import com.supcon.mes.module_defectmanage.model.bean.DaoSession;

/**
 * Time:    2021/3/18  17: 08
 * Author： mac
 * Des:
 */
public class DatabaseManager {
    private static DaoSession daoSession;

    public static DaoSession getDao() {
        if (daoSession == null) {
            setupDatabase();
        }
        return daoSession;
    }

    private  static void setupDatabase() {
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(getWritableDb());
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }


    public static SQLiteDatabase getWritableDb() {
        //创建数据库equipment.db"
        DMMySQLiteOpenHelper helper = new DMMySQLiteOpenHelper(SupPlantApplication.getAppContext(), "DMisupPlant.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        return db;
    }
}
