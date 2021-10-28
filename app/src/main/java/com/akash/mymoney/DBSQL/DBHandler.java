package com.akash.mymoney.DBSQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "trandb";
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "myTran";

    // below variable is for our column.
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String RATE_COL = "rate";
    private static final String AMOUNT_COL = "amount";
    private static final String TIME_COL = "time";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + RATE_COL + " TEXT,"
                + AMOUNT_COL + " TEXT,"
                + TIME_COL + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addNewUser(String Name, String rate, String Amount, String time){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_COL, Name);
        values.put(RATE_COL, rate);
        values.put(AMOUNT_COL, Amount);
        values.put(TIME_COL, time);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // we have created a new method for reading all the courses.
    public ArrayList<Tran_Model> readCourses() {

        SQLiteDatabase db = this.getReadableDatabase();
        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        // on below line we are creating a new array list.
        ArrayList<Tran_Model> courseModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new Tran_Model(
                        cursorCourses.getString(1),
                        cursorCourses.getString(2),
                        cursorCourses.getString(3),
                        cursorCourses.getString(4)));
            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }

        cursorCourses.close();
        return courseModalArrayList;
    }

}
