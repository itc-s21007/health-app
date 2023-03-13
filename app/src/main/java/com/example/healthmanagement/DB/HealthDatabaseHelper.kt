package com.example.healthmanagement.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class HealthDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DBNAME, null, VERSION) {

    companion object{
        private const val DBNAME = "HealthApp"
        private const val VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.let {
            it.execSQL("CREATE TABLE DATE(" + "IdDate INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT)")
            it.execSQL("CREATE TABLE TIME(" + "IdTime INTEGER PRIMARY KEY AUTOINCREMENT, Time TEXT, FkDate INTEGER , FkFood INTEGER, FOREIGN KEY(FkDate) REFERENCES DATE(IdDate), FOREIGN KEY(FkFood) REFERENCES FOOD(IdFood))")
            it.execSQL("CREATE TABLE FOOD(" + "IdFood INTEGER PRIMARY KEY AUTOINCREMENT, Food TEXT, Calorie TEXT)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){
        db?.let {
            it.execSQL("DROP TABLE IF EXISTS DATE")
            it.execSQL("DROP TABLE IF EXISTS TIME")
            it.execSQL("DROP TABLE IF EXISTS FOOD")
            onCreate(it)
        }
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
    }
}