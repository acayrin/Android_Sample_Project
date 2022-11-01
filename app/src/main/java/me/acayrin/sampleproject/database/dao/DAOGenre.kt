package me.acayrin.sampleproject.database.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import me.acayrin.sampleproject.database.model.Genre

class DAOGenre(context: Context) : DAOBase<Genre>("Genre", context) {
    fun get(value: Int): Genre? {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.toString())
        ).ifEmpty { null }?.get(0)
    }

    fun get(value: Genre): Genre? {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.id.toString())
        ).ifEmpty { null }?.get(0)
    }

    override fun get(value: Any?): Genre {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.toString())
        )[0]
    }

    override val all: ArrayList<Genre>
        get() = query("select * from %table%", null)

    override fun insert(value: Genre): Boolean {
        return if (query(
                "select * from %table% where id = ?",
                arrayOf(value.id.toString())
            ).size > 0
        ) false else doWork(value, Work.INSERT)
    }

    override fun update(value: Genre): Boolean {
        return doWork(value, Work.UPDATE)
    }

    override fun delete(value: Genre): Boolean {
        return doWork(value, Work.DELETE)
    }

    fun delete(value: Int): Boolean {
        return doWork(Genre(value, ""), Work.DELETE)
    }

    override fun query(sql: String?, params: Array<String>?): ArrayList<Genre> {
        val arrayList = ArrayList<Genre>()
        val sqLiteDatabase = appDatabase.readableDatabase
        val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    arrayList.add(
                        Genre(
                            cursor.getInt(0), // ma loai sach
                            cursor.getString(1) // ten loai sach
                        )
                    )
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e(table, e.message!!)
        } finally {
            if (!cursor.isClosed) cursor.close()
            if (sqLiteDatabase.isOpen) sqLiteDatabase.close()
        }
        return arrayList
    }

    override fun doWork(value: Genre, work: Work?): Boolean {
        return try {
            val sqLiteDatabase = appDatabase.writableDatabase
            when (work) {
                Work.INSERT -> {
                    sqLiteDatabase.insert(table, null, getContentValues(value))
                }
                Work.DELETE -> {
                    sqLiteDatabase.delete(
                        table,
                        "id = ?",
                        arrayOf(value.id.toString())
                    )
                }
                Work.UPDATE -> {
                    sqLiteDatabase.update(
                        table,
                        getContentValues(value),
                        "id = ?",
                        arrayOf(value.id.toString())
                    )
                }
                else -> {
                    sqLiteDatabase.insert(table, null, getContentValues(value))
                }
            }
            if (sqLiteDatabase.isOpen) sqLiteDatabase.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getContentValues(value: Genre): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("id", value.id)
        contentValues.put("name", value.name)
        return contentValues
    }
}