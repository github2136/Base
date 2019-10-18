package com.github2136.basemvvm.download.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by YB on 2019/6/6
 */
class DownLoadDBHelper private constructor(context: Context) : SQLiteOpenHelper(context, "DownloadDB.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.apply {
            execSQL("CREATE TABLE IF NOT EXISTS " +
                        "${DownloadFileDao.TAB_NAME} (" +
                        "${DownloadFileDao.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "${DownloadFileDao.COL_FILE_URL} VARCHAR," +
                        "${DownloadFileDao.COL_FILE_PATH} VARCHAR," +
                        "${DownloadFileDao.COL_FILE_SIZE} LONG," +
                        "${DownloadFileDao.COL_FILE_TOTAL} LONG," +
                        "${DownloadFileDao.COL_COMPLETE} INTEGER)")
            execSQL("CREATE TABLE IF NOT EXISTS " +
                        "${DownloadBlockDao.TAB_NAME} (" +
                        "${DownloadBlockDao.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "${DownloadBlockDao.COL_FILE_ID} LONG," +
                        "${DownloadBlockDao.COL_START} LONG," +
                        "${DownloadBlockDao.COL_END} LONG," +
                        "${DownloadBlockDao.COL_FILE_URL} VARCHAR," +
                        "${DownloadBlockDao.COL_FILE_PATH} VARCHAR," +
                        "${DownloadBlockDao.COL_FILE_SIZE} LONG," +
                        "${DownloadBlockDao.COL_COMPLETE} INTEGER)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.apply {
            execSQL("DROP TABLE ${DownloadFileDao.TAB_NAME}")
            execSQL("DROP TABLE ${DownloadBlockDao.TAB_NAME}")
            onCreate(this)
        }
    }

    companion object {
        @Volatile
        private var instance: DownLoadDBHelper? = null

        fun getInstance(context: Context): DownLoadDBHelper {
            if (instance == null) {
                synchronized(DownLoadDBHelper::class) {
                    if (instance == null) {
                        instance = DownLoadDBHelper(context)
                    }
                }
            }
            return instance!!
        }
    }
}