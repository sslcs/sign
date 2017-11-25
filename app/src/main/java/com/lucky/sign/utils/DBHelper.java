package com.lucky.sign.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.lucky.sign.ui.Record;

/**
 * 数据库操作类
 * Created by cs on 17-11-15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static int VERSION = 1;
    private final static String DB_NAME = "name.db";
    private final static String TABLE_NAME = "name";
    private final static String ID = "_id";
    private final static String NUMBER = "number";
    private final static String NAME = "name";
    private final static String FLAG = "flag";
    private final static String PRIZE = "prize";
    private final static String EXCHANGE = "exchange";
    private final static String CREATE_CMD = "create table " + TABLE_NAME
            + "(_id integer primary key autoincrement, "
            + NUMBER + " text, " + NAME + " text, " + PRIZE + " text, " + EXCHANGE + " text, " + FLAG + " text)";
    private SQLiteDatabase db;

    //数据库的构造函数，传递一个参数的， 数据库名字和版本号都写死了
    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    // 回调函数，第一次创建时才会调用此函数，创建一个数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    //回调函数，当你构造DBHelper的传递的Version与之前的Version调用此函数
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("update Database");
    }

    //插入方法
    private void insert(Record record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUMBER, record.number);
        values.put(NAME, record.name);
        values.put(FLAG, record.flag);
        values.put(PRIZE, record.prize);
        values.put(EXCHANGE, record.exchange);
        if (db != null) {
            long id = db.insert(TABLE_NAME, null, values);
            DebugLog.e("id: " + id);
        }
    }

    //查询方法
    public Cursor queryAll() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null, null);
    }

    //查询方法
    public Cursor query(String number) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, null, NUMBER + "=?", new String[]{number}, null, null, null);
    }

    private Cursor queryInput(String number) {
        return db.query(TABLE_NAME, null, NUMBER + "=?", new String[]{number}, null, null, null);
    }

    //更新数据库的内容
    public void update(Record record) {
        if (record == null) return;
        ContentValues values = getValues(record);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME, values, ID + "=" + record.id, null);
        db.close();
    }

    private ContentValues getValues(Record record) {
        ContentValues values = new ContentValues();
        values.put(FLAG, record.flag);
        values.put(PRIZE, record.prize);
        values.put(EXCHANGE, record.exchange);
        return values;
    }

    private void updateImport(Record record) {
        if (record == null) return;
        ContentValues values = getValues(record);
        db.update(TABLE_NAME, values, ID + "=" + record.id, null);
    }

    public Record convert(Cursor cursor) {
        Record record = new Record();
        record.id = cursor.getInt(cursor.getColumnIndex(ID));
        record.number = cursor.getString(cursor.getColumnIndex(NUMBER));
        record.name = cursor.getString(cursor.getColumnIndex(NAME));
        record.flag = cursor.getString(cursor.getColumnIndex(FLAG));
        record.prize = cursor.getString(cursor.getColumnIndex(PRIZE));
        record.exchange = cursor.getString(cursor.getColumnIndex(EXCHANGE));
        return record;
    }

    public void beginTransaction() {
        db = getWritableDatabase();
        db.beginTransaction();
    }

    public void endTransaction() {
        if (db != null) {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            db = null;
        }
    }

    private boolean isEmpty(String str)
    {
        return TextUtils.isEmpty(str) || "null".equals(str);
    }

    public void importRecord(Record record) {
        Cursor cursor = queryInput(record.number);
        if (cursor == null || cursor.isAfterLast()) {
            insert(record);
            return;
        }

        cursor.moveToFirst();
        Record local = convert(cursor);
        boolean update = false;
        if (isEmpty(local.flag) && !isEmpty(record.flag)) {
            update = true;
            local.flag = record.flag;
        }
        if (isEmpty(local.prize) && !isEmpty(record.prize)) {
            update = true;
            local.prize = record.prize;
        }
        if (isEmpty(local.exchange) && !isEmpty(record.exchange)) {
            update = true;
            local.exchange = record.exchange;
        }
        if (update) {
            updateImport(local);
        }
    }
}