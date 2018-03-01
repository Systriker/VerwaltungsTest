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
    public static final String TABLE_LAGER = "lager";
    public static final String TABLE_BESTELLUNGEN = "bestellungen";
    public static final String TABLE_LAGER_ZU_BESTELLUNGEN = "lager_zu_bestellungen";

    public static final String COLUMN_KUNDE_ID = "_id";
    public static final String COLUMN_KUNDE_NAME = "name";
    public static final String COLUMN_KUNDE_ADRESSE = "adresse";
    public static final String COLUMN_KUNDE_KUNDETYP = "kundetyp";

    public static final String COLUMN_PRODUCT_ID = "_id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_PREIS = "preis";

    public static final String COLUMN_BESTELLUNG_ID = "_id";
    public static final String COLUMN_BESTELLUNG_KUNDE = "kunde";

    public static final String COLUMN_LAGER_ZU_BESTELLUNG_ID = "_id";
    public static final String COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG = "bestellung";
    public static final String COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT = "product";
    public static final String COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY = "quantity";

    public static final String SQL_CREATE_KUNDE = "CREATE TABLE " + TABLE_KUNDE +
            "(" + COLUMN_KUNDE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_KUNDE_NAME + " TEXT NOT NULL, " +
            COLUMN_KUNDE_ADRESSE + " TEXT NOT NULL, " +
            COLUMN_KUNDE_KUNDETYP + " TEXT NOT NULL);";

    public static final String SQL_CREATE_LAGER = "CREATE TABLE " + TABLE_LAGER +
            "(" + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
            COLUMN_PRODUCT_PREIS + " DOUBLE NOT NULL);";

    public static final String SQL_CREATE_BESTELLUNGEN = "CREATE TABLE " + TABLE_BESTELLUNGEN +
            "(" + COLUMN_BESTELLUNG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BESTELLUNG_KUNDE + " INTEGER NOT NULL," +
            "FOREIGN KEY(" + COLUMN_BESTELLUNG_KUNDE + ") REFERENCES " +TABLE_KUNDE+"("+ COLUMN_KUNDE_ID +"));";

    public static final String SQL_CREATE_LAGER_ZU_BESTELLUNGEN = "CREATE TABLE " + TABLE_LAGER_ZU_BESTELLUNGEN +
            "(" + COLUMN_LAGER_ZU_BESTELLUNG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG + " INTEGER NOT NULL, " +
            COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT + " INTEGER NOT NULL, " +
            COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG + ") " +
            "REFERENCES " +TABLE_BESTELLUNGEN+"("+ COLUMN_BESTELLUNG_ID +")," +
            "FOREIGN KEY(" + COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT + ") " +
            "REFERENCES " +TABLE_LAGER+"("+ COLUMN_PRODUCT_ID +"));";

    public static final String SQL_DROP_KUNDE = "DROP TABLE IF EXISTS " + TABLE_KUNDE;
    public static final String SQL_DROP_LAGER = "DROP TABLE IF EXISTS " + TABLE_LAGER;
    public static final String SQL_DROP_LAGER_ZU_BESTELLUNGEN = "DROP TABLE IF EXISTS " + TABLE_LAGER_ZU_BESTELLUNGEN;
    public static final String SQL_DROP_BESTELLUNGEN = "DROP TABLE IF EXISTS " + TABLE_BESTELLUNGEN;

    public DbHelper(Context context){
        super(context,DB_NAME, null,DB_VERSION);
        Log.d(TAG, "DbHelper hat die Datenbank " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_KUNDE);
            db.execSQL(SQL_CREATE_LAGER);
            db.execSQL(SQL_CREATE_BESTELLUNGEN);
            db.execSQL(SQL_CREATE_LAGER_ZU_BESTELLUNGEN);
            Log.d(TAG, "Die Tabellen wurden angelegt");
        }catch (Exception e){
            Log.d(TAG, "Fehler beim Anlegen der Tabellen " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP_LAGER_ZU_BESTELLUNGEN);
        db.execSQL(SQL_DROP_BESTELLUNGEN);
        db.execSQL(SQL_DROP_LAGER);
        db.execSQL(SQL_DROP_KUNDE);
        onCreate(db);
    }

}
