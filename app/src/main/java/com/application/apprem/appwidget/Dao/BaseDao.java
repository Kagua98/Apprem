package com.application.apprem.appwidget.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;


class BaseDao {


    static void insert(@NonNull SQLiteDatabase db, String tableName, ContentValues contentValues) {
        db.insert(tableName, null, contentValues);
    }


    static void insertOrReplace(@NonNull SQLiteDatabase db, String tableName, ContentValues contentValues) {
        db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }


    static void insertOrIgnore(@NonNull SQLiteDatabase db, String tableName, ContentValues contentValues) {
        db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }


    static void delete(@NonNull SQLiteDatabase db, String tableName, String whereClause, String[] whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
    }

    static int update(@NonNull SQLiteDatabase db, String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(tableName, values, whereClause, whereArgs);
    }


    static Cursor query(@NonNull SQLiteDatabase db, String tableName, String selection, String[] selectionArgs) {
        return queryComplex(db, tableName, null, selection, selectionArgs, null, null, null, null);
    }


    static Cursor queryComplex(@NonNull SQLiteDatabase db, String tableName, String[] columns, String selection, String[] selectionArgs, String
            groupBy, String having, String orderBy, String limit) {
        return db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
}
