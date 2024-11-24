package com.example.digitaldetox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ActivityDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "activities.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ACTIVITIES = "activities";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COMPLETED = "completed";

    private static ActivityDatabase instance;

    public static synchronized ActivityDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new ActivityDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private ActivityDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_COMPLETED + " INTEGER" +
                ")";
        db.execSQL(CREATE_ACTIVITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        onCreate(db);
    }

    public void addActivity(Activity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, activity.getName());
        values.put(COLUMN_DESCRIPTION, activity.getDescription());
        values.put(COLUMN_COMPLETED, activity.isCompleted() ? 1 : 0);
        db.insert(TABLE_ACTIVITIES, null, values);
        db.close();
    }

    public List<Activity> getActivities() {
        List<Activity> activities = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Activity activity = new Activity();
                activity.setId(cursor.getInt(0));
                activity.setName(cursor.getString(1));
                activity.setDescription(cursor.getString(2));
                activity.setCompleted(cursor.getInt(3) == 1);
                activities.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activities;
    }

    public void updateActivity(Activity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, activity.getName());
        values.put(COLUMN_DESCRIPTION, activity.getDescription());
        values.put(COLUMN_COMPLETED, activity.isCompleted() ? 1 : 0);
        db.update(TABLE_ACTIVITIES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(activity.getId())});
        db.close();
    }
}