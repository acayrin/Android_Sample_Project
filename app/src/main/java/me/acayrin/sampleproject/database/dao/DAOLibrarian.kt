package me.acayrin.sampleproject.database.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import me.acayrin.sampleproject.database.model.Librarian

class DAOLibrarian(context: Context) : DAOBase<Librarian>("Librarian", context) {
	fun validate(username: String, password: String): Librarian? {
		val matchingUsers = query("select * from %table% where username = ?", arrayOf(username))
		return if (matchingUsers.size == 0) null
		else matchingUsers.takeWhile { it.password == password }.ifEmpty { null }?.get(0)
	}

	fun get(value: Int): Librarian? {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.toString())
		).ifEmpty { null }?.get(0)
	}

	fun get(value: Librarian): Librarian? {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.id.toString())
		).ifEmpty { null }?.get(0)
	}

	override fun get(value: Any?): Librarian {
		return query(
			"select * from %table% where id = ?",
			arrayOf(value.toString())
		)[0]
	}

	override val all: ArrayList<Librarian>
		get() = query("select * from %table%", null)

	override fun insert(value: Librarian): Boolean {
		return if (query(
				"select * from %table% where username = ?",
				arrayOf(value.username)
			).size > 0
		) false else doWork(value, Work.INSERT)
	}

	override fun update(value: Librarian): Boolean {
		return doWork(value, Work.UPDATE)
	}

	override fun delete(value: Librarian): Boolean {
		return doWork(value, Work.DELETE)
	}

	fun delete(value: Int): Boolean {
		return doWork(Librarian(value, null, null, "", ""), Work.DELETE)
	}

	override fun query(sql: String?, params: Array<String>?): ArrayList<Librarian> {
		val arrayList = ArrayList<Librarian>()
		val sqLiteDatabase = appDatabase.readableDatabase
		val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
		try {
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast) {
					arrayList.add(
						Librarian(
							cursor.getInt(0), // ma user
							cursor.getString(1), // ten user
							cursor.getString(2), // email user
							cursor.getString(3), // username
							cursor.getString(4) // password
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

	override fun doWork(value: Librarian, work: Work?): Boolean {
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

	private fun getContentValues(value: Librarian): ContentValues {
		val contentValues = ContentValues()
		contentValues.put("id", value.id)
		contentValues.put("full_name", value.full_name)
		contentValues.put("email", value.email)
		contentValues.put("username", value.username)
		contentValues.put("password", value.password)
		return contentValues
	}
}
