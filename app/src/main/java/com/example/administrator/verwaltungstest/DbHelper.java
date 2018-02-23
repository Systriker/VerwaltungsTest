package com.example.administrator.verwaltungstest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public static final String DB_NAME = "verwaltung.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_KUNDE = "kunde";

    public static final String COLUMN_KUNDE_ID = "_id";
    public static final String COLUMN_KUNDE_NAME = "name";
    public static final String COLUMN_KUNDE_ADRESSE = "adresse";
    public static final String COLUMN_KUNDE_KUNDETYP = "kundetyp";

    public static final String SQL_CREATE_KUNDE = "CREATE TABLE " + TABLE_KUNDE +
            "(" + COLUMN_KUNDE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_KUNDE_NAME + " TEXT NOT NULL, " +
            COLUMN_KUNDE_ADRESSE + " TEXT NOT NULL, " +
            COLUMN_KUNDE_KUNDETYP + " TEXT NOT NULL);";

    public static final String SQL_DROP_KUNDE = "DROP TABLE IF EXISTS " + TABLE_KUNDE;

    public DbHelper(Context context){
        super(context,DB_NAME, null,DB_VERSION);
        Log.d(TAG, "DbHelper hat die Datenbank " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_KUNDE);
            Log.d(TAG, "Die Tabelle wurde mit der Anweisung " + SQL_CREATE_KUNDE + " angelegt");
        }catch (Exception e){
            Log.d(TAG, "Fehler beim Anlegen der Tabelle " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP_KUNDE);
        onCreate(db);
    }

}
