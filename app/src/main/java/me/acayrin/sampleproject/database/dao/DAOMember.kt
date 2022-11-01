package me.acayrin.sampleproject.database.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import me.acayrin.sampleproject.database.model.Member

class DAOMember(context: Context) : DAOBase<Member>("Member", context) {
	fun get(value: Int): Member? {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.toString())
		).ifEmpty { null }?.get(0)
	}

	fun get(value: Member): Member? {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.id.toString())
		).ifEmpty { null }?.get(0)
	}

	override fun get(value: Any?): Member {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.toString())
		)[0]
	}

	override val all: ArrayList<Member>
		get() = query("select * from %table%", null)

	override fun insert(value: Member): Boolean {
		return if (query(
				"select * from %table% where id = ?",
				arrayOf(value.id.toString())
			).size > 0
		) false else doWork(value, Work.INSERT)
	}

	override fun update(value: Member): Boolean {
		return doWork(value, Work.UPDATE)
	}

	override fun delete(value: Member): Boolean {
		return doWork(value, Work.DELETE)
	}

	fun delete(value: Int): Boolean {
		return doWork(Member(value, "", "", ""), Work.DELETE)
	}

	override fun query(sql: String?, params: Array<String>?): ArrayList<Member> {
		val arrayList = ArrayList<Member>()
		val sqLiteDatabase = appDatabase.readableDatabase
		val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
		try {
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast) {
					arrayList.add(
						Member(
							cursor.getInt(0), // ma thanh vien
							cursor.getString(1), // ten thanh vien
							cursor.getString(2), // ngay sinh
							cursor.getString(3) // dia chi
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

	override fun doWork(value: Member, work: Work?): Boolean {
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

	private fun getContentValues(value: Member): ContentValues {
		val contentValues = ContentValues()
		contentValues.put("id", value.id)
		contentValues.put("full_name", value.full_name)
		contentValues.put("birthdate", value.birthdate)
		contentValues.put("address", value.address)
		return contentValues
	}
}
