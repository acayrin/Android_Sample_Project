package me.acayrin.sampleproject.database.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import me.acayrin.sampleproject.database.model.Rental

class DAORental(context: Context) : DAOBase<Rental>("Rental", context) {
	fun get(value: Int): Rental? {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.toString())
		).ifEmpty { null }?.get(0)
	}

	fun get(value: Rental): Rental? {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.id.toString())
		).ifEmpty { null }?.get(0)
	}

	override fun get(value: Any?): Rental {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.toString())
		)[0]
	}

	override val all: ArrayList<Rental>
		get() = query("select * from %table%", null)

	fun all(maUser: Int): ArrayList<Rental> {
		return query("select * from %table% where id_user = ?", arrayOf(maUser.toString()))
	}

	override fun insert(value: Rental): Boolean {
		return if (query(
				"select * from %table% where id = ?",
				arrayOf(value.id.toString())
			).size > 0
		) false else doWork(value, Work.INSERT)
	}

	override fun update(value: Rental): Boolean {
		return doWork(value, Work.UPDATE)
	}

	override fun delete(value: Rental): Boolean {
		return doWork(value, Work.DELETE)
	}

	fun delete(value: Int): Boolean {
		return doWork(Rental(value, -1, -1, -1, "", 0, ""), Work.DELETE)
	}

	override fun query(sql: String?, params: Array<String>?): ArrayList<Rental> {
		val arrayList = ArrayList<Rental>()
		val sqLiteDatabase = appDatabase.readableDatabase
		val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
		try {
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast) {
					arrayList.add(
						Rental(
							cursor.getInt(0), // id pm
							cursor.getInt(1), // id user
							cursor.getInt(2), // id tv
							cursor.getInt(3), // id sach
							cursor.getString(4), // ngay thue
							cursor.getInt(5), // trang thai
							cursor.getString(6) // ngay tra
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

	override fun doWork(value: Rental, work: Work?): Boolean {
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
			e.message.let { Log.e("DAORental", it!!) }
			false
		}
	}

	private fun getContentValues(value: Rental): ContentValues {
		val contentValues = ContentValues()
		contentValues.put("id", value.id)
		contentValues.put("id_librarian", value.id_librarian)
		contentValues.put("id_member", value.id_member)
		contentValues.put("id_book", value.id_book)
		contentValues.put("date_start", value.date_start)
		contentValues.put("status", value.status)
		contentValues.put("date_end", value.date_end)
		return contentValues
	}
}
