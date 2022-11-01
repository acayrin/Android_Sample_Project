package me.acayrin.sampleproject.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteDatabase(context: Context) : SQLiteOpenHelper(context, "SQL", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(
            "create table Rental(" +
                "id integer primary key autoincrement not null," +
                "id_librarian integer not null," +
                "id_member integer not null," +
                "id_book integer not null," +
                "date_start text not null," +
                "status integer not null," +
                "date_end text)"
        )

        db.execSQL(
            "create table Book(" +
                "id integer primary key autoincrement not null," +
                "name text not null," +
                "id_author integer not null," +
                "id_publisher integer not null," +
                "id_genre integer not null," +
                "price real not null)"
        )

        db.execSQL(
            "create table Genre(" +
                "id integer primary key autoincrement not null," +
                "name text not null)"
        )

        db.execSQL(
            "create table Author(" +
                "id integer primary key autoincrement not null," +
                "name text not null)"
        )

        db.execSQL(
            "create table Publisher(" +
                "id integer primary key autoincrement not null," +
                "name text not null)"
        )

        db.execSQL(
            "create table Member(" +
                "id integer primary key autoincrement not null," +
                "full_name text not null," +
                "birthdate text not null," +
                "address text not null)"
        )

        db.execSQL(
            "create table Librarian(" +
                "id integer primary key autoincrement not null," +
                "full_name text," +
                "email text," +
                "username text not null," +
                "password text not null)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, v1: Int, v2: Int) {
        db!!.execSQL("drop table Rental")
        db.execSQL("drop table Book")
        db.execSQL("drop table Genre")
        db.execSQL("drop table Author")
        db.execSQL("drop table Publisher")
        db.execSQL("drop table Member")
        db.execSQL("drop table Librarian")
        onCreate(db)
    }
}
