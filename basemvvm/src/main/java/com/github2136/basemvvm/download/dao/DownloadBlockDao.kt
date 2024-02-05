package com.github2136.basemvvm.download.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github2136.basemvvm.download.entity.DownloadBlock

/**
 * Created by YB on 2019/6/11
 * 文件块操作
 */
class DownloadBlockDao(context: Context) {
    private val dbHelper by lazy { DownLoadDBHelper.getInstance(context) }
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun install(file: DownloadBlock): Long {
        db.beginTransaction()
        val id: Long
        try {
            val contentValues = ContentValues()
            contentValues.put(COL_FILE_ID, file.fileId)
            contentValues.put(COL_START, file.start)
            contentValues.put(COL_END, file.end)
            contentValues.put(COL_FILE_URL, file.fileUrl)
            contentValues.put(COL_FILE_PATH, file.filePath)
            contentValues.put(COL_FILE_SIZE, file.fileSize)
            contentValues.put(COL_COMPLETE, if (file.complete) 1 else 0)
            id = db.insert(TAB_NAME, null, contentValues)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return id
    }

    fun install(files: List<DownloadBlock>): List<Long> {
        db.beginTransaction()
        val ids = mutableListOf<Long>()
        try {
            files.forEach { file ->
                val contentValues = ContentValues()
                contentValues.put(COL_FILE_ID, file.fileId)
                contentValues.put(COL_START, file.start)
                contentValues.put(COL_END, file.end)
                contentValues.put(COL_FILE_URL, file.fileUrl)
                contentValues.put(COL_FILE_PATH, file.filePath)
                contentValues.put(COL_FILE_SIZE, file.fileSize)
                contentValues.put(COL_COMPLETE, if (file.complete) 1 else 0)
                ids.add(db.insert(TAB_NAME, null, contentValues))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return ids
    }

    fun get(fileId: Long): List<DownloadBlock> {
        val cursor = db.query(TAB_NAME, COLS, "$COL_FILE_ID =?", arrayOf(fileId.toString()), null, null, null)
        val blocks = mutableListOf<DownloadBlock>()
        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COL_ID)
                val idFileId = cursor.getColumnIndex(COL_FILE_ID)
                val idStart = cursor.getColumnIndex(COL_START)
                val idEnd = cursor.getColumnIndex(COL_END)
                val idFileUrl = cursor.getColumnIndex(COL_FILE_URL)
                val idFilePath = cursor.getColumnIndex(COL_FILE_PATH)
                val idFileSize = cursor.getColumnIndex(COL_FILE_SIZE)
                val idComplete = cursor.getColumnIndex(COL_COMPLETE)
                val block = DownloadBlock(
                    cursor.getLong(idIndex),
                    cursor.getLong(idFileId),
                    cursor.getLong(idStart),
                    cursor.getLong(idEnd),
                    cursor.getString(idFileUrl),
                    cursor.getString(idFilePath),
                    cursor.getLong(idFileSize),
                    cursor.getInt(idComplete) == 1
                )
                blocks.add(block)
            } while (cursor.moveToNext())

        }
        cursor.close()
        return blocks
    }

    fun update(file: DownloadBlock): Boolean {
        db.beginTransaction()
        val id: Int
        try {
            val contentValues = ContentValues()
            contentValues.put(COL_FILE_ID, file.fileId)
            contentValues.put(COL_START, file.start)
            contentValues.put(COL_END, file.end)
            contentValues.put(COL_FILE_URL, file.fileUrl)
            contentValues.put(COL_FILE_PATH, file.filePath)
            contentValues.put(COL_FILE_SIZE, file.fileSize)
            contentValues.put(COL_COMPLETE, if (file.complete) 1 else 0)
            id = db.update(TAB_NAME, contentValues, "$COL_ID = ?", arrayOf(file.id.toString()))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return id > 0
    }

    fun delete(fileUrl: String) {
        db.delete(TAB_NAME, "$COL_FILE_URL = ?", arrayOf(fileUrl))
    }

    fun close() {
        db.close()
    }

    companion object {
        const val TAB_NAME = "DownloadBlock"
        const val COL_ID = "'id'"
        const val COL_FILE_ID = "'fileId'"
        const val COL_START = "'start'"
        const val COL_END = "'end'"
        const val COL_FILE_URL = "'fileUrl'"
        const val COL_FILE_PATH = "'filePath'"
        const val COL_FILE_SIZE = "'fileSize'"
        const val COL_COMPLETE = "'complete'"
        val COLS = arrayOf(COL_ID, COL_FILE_ID, COL_START, COL_END, COL_FILE_URL, COL_FILE_PATH, COL_FILE_SIZE, COL_COMPLETE)
    }
}