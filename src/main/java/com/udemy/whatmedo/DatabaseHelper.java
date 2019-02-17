package com.udemy.whatmedo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TaskDatabase";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TASKTABLE(_id INTEGER, TASK TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public void insertTask(int rowNumder, String taskContent) {
        ContentValues taskValue = new ContentValues();
        try {
            db = getWritableDatabase(); //Наш хелпер получает название базы из конструктора. Оно тут не надо.
            taskValue.put("_id", rowNumder);
            taskValue.put("TASK", taskContent);
            db.insert("TASKTABLE", null, taskValue);
            db.close();
        } catch (SQLException e) {
            Toast.makeText(context, R.string.could_not_write, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTask(int rowNumber) {
        try {
            db = getWritableDatabase();
            String rn = Integer.toString(rowNumber);
            db.delete("TASKTABLE", "_id = ?", new String[] {rn});
            db.close();
        } catch (SQLException e) {
            Toast.makeText(context, R.string.could_not_write, Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> getTaskList() {
        ArrayList<String> allRecords = new ArrayList<>();
        try {
            db = getReadableDatabase();
            Cursor cursor = db.query("TASKTABLE", new String[] {"TASK", "_id"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    allRecords.add(cursor.getString(0));
//                    Log.v("mylist", "_id= " + cursor.getString(1) + " TASK= " + cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(context, R.string.could_not_read, Toast.LENGTH_SHORT).show();
        }
        return allRecords;
    }

    public void writeTaskList(ArrayList<String> taskList) {

        try {
            // Очистить старую базу
            db = getWritableDatabase();
            db.delete("TASKTABLE", null, null);

            // Записать новую из листа
            for (int i = 0; i < taskList.size(); i++) {
                ContentValues taskValue = new ContentValues();
                taskValue.put("_id", i);
                taskValue.put("TASK", taskList.get(i));
                db.insert("TASKTABLE", null, taskValue);
            }
            db.close();
        } catch (SQLException e) {
            Toast.makeText(context, R.string.could_not_write, Toast.LENGTH_SHORT).show();
        }
    }
}
