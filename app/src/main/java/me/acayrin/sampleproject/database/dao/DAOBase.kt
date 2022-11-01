package me.acayrin.sampleproject.database.dao

import android.content.Context
import me.acayrin.sampleproject.database.SQLiteDatabase
import java.util.ArrayList

abstract class DAOBase<T>(protected val table: String, context: Context?) {
    protected val appDatabase: SQLiteDatabase

    init {
        appDatabase = SQLiteDatabase(context!!)
    }

    abstract operator fun get(value: Any?): T

    abstract val all: ArrayList<T>?

    abstract fun insert(value: T): Boolean

    abstract fun update(value: T): Boolean

    abstract fun delete(value: T): Boolean

    abstract fun query(sql: String?, params: Array<String>?): ArrayList<T>

    abstract fun doWork(value: T, work: Work?): Boolean

    enum class Work {
        INSERT, DELETE, UPDATE
    }
}