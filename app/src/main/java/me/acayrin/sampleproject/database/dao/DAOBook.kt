package me.acayrin.sampleproject.database.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import me.acayrin.sampleproject.database.model.Book

class DAOBook(context: Context) : DAOBase<Book>("Book", context) {
    fun get(value: Int): Book? {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.toString())
        ).ifEmpty { null }?.get(0)
    }

    fun get(value: Book): Book? {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.id.toString())
        ).ifEmpty { null }?.get(0)
    }

    override fun get(value: Any?): Book {
        return query(
            "select * from %table% where id = ?",
            arrayOf(value.toString())
        )[0]
    }

    override val all: ArrayList<Book>
        get() = query("select * from %table%", null)

    override fun insert(value: Book): Boolean {
        return if (query(
                "select * from %table% where id = ?",
                arrayOf(value.id.toString())
            ).size > 0
        ) false else doWork(value, Work.INSERT)
    }

    override fun update(value: Book): Boolean {
        return doWork(value, Work.UPDATE)
    }

    override fun delete(value: Book): Boolean {
        return doWork(value, Work.DELETE)
    }

    fun delete(value: Int): Boolean {
        return doWork(Book(value, "", -1, -1, -1, -1.0), Work.DELETE)
    }

    override fun query(sql: String?, params: Array<String>?): ArrayList<Book> {
        val arrayList = ArrayList<Book>()
        val sqLiteDatabase = appDatabase.readableDatabase
        val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    arrayList.add(
                        Book(
                            cursor.getInt(0), // ma sach
                            cursor.getString(1), // ten sach
                            cursor.getInt(2), // ma tac gia
                            cursor.getInt(3), // ma nha xuat ban
                            cursor.getInt(4), // ma loai
                            cursor.getDouble(5) // gia thue
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

    override fun doWork(value: Book, work: Work?): Boolean {
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

    private fun getContentValues(value: Book): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("id", value.id)
        contentValues.put("name", value.name)
        contentValues.put("id_author", value.id_author)
        contentValues.put("id_publisher", value.id_publisher)
        contentValues.put("id_genre", value.id_genre)
        contentValues.put("price", value.price)
        return contentValues
    }
}
