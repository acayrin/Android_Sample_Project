package me.acayrin.sampleproject.database.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import me.acayrin.sampleproject.database.model.Publisher

class DAOPublisher(context: Context) : DAOBase<Publisher>("Publisher", context) {
    fun get(value: Int): Publisher? {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.toString())
        ).ifEmpty { null }?.get(0)
    }

    fun get(value: Publisher): Publisher? {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.id.toString())
        ).ifEmpty { null }?.get(0)
    }

    override fun get(value: Any?): Publisher {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.toString())
        )[0]
    }

    override val all: ArrayList<Publisher>
        get() = query("select * from %table%", null)

    override fun insert(value: Publisher): Boolean {
        return if (query(
                "select * from %table% where id = ?",
                arrayOf(value.id.toString())
            ).size > 0
        ) false else doWork(value, Work.INSERT)
    }

    override fun update(value: Publisher): Boolean {
        return doWork(value, Work.UPDATE)
    }

    override fun delete(value: Publisher): Boolean {
        return doWork(value, Work.DELETE)
    }

    fun delete(value: Int):Boolean {
        return doWork(Publisher(value, ""), Work.DELETE)
    }

    override fun query(sql: String?, params: Array<String>?): ArrayList<Publisher> {
        val arrayList = ArrayList<Publisher>()
        val sqLiteDatabase = appDatabase.readableDatabase
        val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    arrayList.add(
                        Publisher(
                            cursor.getInt(0), // ma tac guia
                            cursor.getString(1) // ten tac gia
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

    override fun doWork(value: Publisher, work: Work?): Boolean {
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

    private fun getContentValues(value: Publisher): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("id", value.id)
        contentValues.put("name", value.name)
        return contentValues
    }
}