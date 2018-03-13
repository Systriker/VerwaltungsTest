package com.example.administrator.verwaltungstest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Klasse mit Hilfsmitteln zu erstellung und nutzung der Datenbank
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    // DB-Basics
    private static final String DB_NAME = "verwaltung.db";
    private static final int DB_VERSION = 1;

    // Tabellen der DB
    static final String TABLE_KUNDE = "kunde";
    static final String TABLE_LAGER = "lager";
    static final String TABLE_BESTELLUNGEN = "bestellungen";
    static final String TABLE_LAGER_ZU_BESTELLUNGEN = "lager_zu_bestellungen";
    static final String TABLE_ADRESSE = "adresse";

    // Fleder der "kunde"-Tabelle
    static final String COLUMN_KUNDE_ID = "_id";
    static final String COLUMN_KUNDE_NAME = "name";
    static final String COLUMN_KUNDE_ADRESSE = "adresse";
    static final String COLUMN_KUNDE_KUNDETYP = "kundetyp";

    // Fleder der "lager"-Tabelle
    static final String COLUMN_PRODUCT_ID = "_id";
    static final String COLUMN_PRODUCT_NAME = "name";
    static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    static final String COLUMN_PRODUCT_PREIS = "preis";

    // Fleder der "bestellungen"-Tabelle
    static final String COLUMN_BESTELLUNG_ID = "_id";
    static final String COLUMN_BESTELLUNG_KUNDE = "kunde";
    static final String COLUMN_BESTELLUNG_BOOKED = "booked";

    // Fleder der "lager_zu_bestellungen"-Tabelle
    static final String COLUMN_LAGER_ZU_BESTELLUNG_ID = "_id";
    static final String COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG = "bestellung";
    static final String COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT = "product";
    static final String COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY = "quantity";

    // Felder der "adresse"-Tabelle
    static final String COLUMN_ADRESSE_ID = "_id";
    static final String COLUMN_ADRESSE_STRASSE = "strasse";
    static final String COLUMN_ADRESSE_HAUSNUMMER = "hausnummer";
    static final String COLUMN_ADRESSE_ZUSATZ = "zusatz";
    static final String COLUMN_ADRESSE_ORT = "ort";
    static final String COLUMN_ADRESSE_PLZ = "plz";

    // SQL-Befehle zum erstellen der Tabellen
    private static final String SQL_CREATE_KUNDE = "CREATE TABLE " + TABLE_KUNDE +
            "(" + COLUMN_KUNDE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_KUNDE_NAME + " TEXT NOT NULL, " +
            COLUMN_KUNDE_ADRESSE + " INTEGER NOT NULL, " +
            COLUMN_KUNDE_KUNDETYP + " TEXT NOT NULL," +
            "FOREIGN KEY(" + COLUMN_KUNDE_ADRESSE + ") " +
            "REFERENCES " +TABLE_ADRESSE+"("+ COLUMN_ADRESSE_ID +"));";

    private static final String SQL_CREATE_LAGER = "CREATE TABLE " + TABLE_LAGER +
            "(" + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
            COLUMN_PRODUCT_PREIS + " DOUBLE NOT NULL);";

    private static final String SQL_CREATE_BESTELLUNGEN = "CREATE TABLE " + TABLE_BESTELLUNGEN +
            "(" + COLUMN_BESTELLUNG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BESTELLUNG_KUNDE + " INTEGER NOT NULL, " +
            COLUMN_BESTELLUNG_BOOKED + " BOOLEAN NOT NULL," +
            "FOREIGN KEY(" + COLUMN_BESTELLUNG_KUNDE + ") REFERENCES " +TABLE_KUNDE+"("+ COLUMN_KUNDE_ID +"));";

    private static final String SQL_CREATE_LAGER_ZU_BESTELLUNGEN = "CREATE TABLE " + TABLE_LAGER_ZU_BESTELLUNGEN +
            "(" + COLUMN_LAGER_ZU_BESTELLUNG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG + " INTEGER NOT NULL, " +
            COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT + " INTEGER NOT NULL, " +
            COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG + ") " +
            "REFERENCES " +TABLE_BESTELLUNGEN+"("+ COLUMN_BESTELLUNG_ID +")," +
            "FOREIGN KEY(" + COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT + ") " +
            "REFERENCES " +TABLE_LAGER+"("+ COLUMN_PRODUCT_ID +"));";

    private static final String SQL_CREATE_ADRESSE = "CREATE TABLE " + TABLE_ADRESSE +
            "(" + COLUMN_ADRESSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ADRESSE_STRASSE + " TEXT NOT NULL, " +
            COLUMN_ADRESSE_HAUSNUMMER + " INTEGER NOT NULL, " +
            COLUMN_ADRESSE_ZUSATZ + " TEXT NOT NULL, " +
            COLUMN_ADRESSE_ORT + " TEXT NOT NULL, " +
            COLUMN_ADRESSE_PLZ + " TEXT NOT NULL);";

    // SQL-Befehle zum löschen der Tabellen
    private static final String SQL_DROP_KUNDE = "DROP TABLE IF EXISTS " + TABLE_KUNDE;
    private static final String SQL_DROP_LAGER = "DROP TABLE IF EXISTS " + TABLE_LAGER;
    private static final String SQL_DROP_LAGER_ZU_BESTELLUNGEN = "DROP TABLE IF EXISTS " + TABLE_LAGER_ZU_BESTELLUNGEN;
    private static final String SQL_DROP_BESTELLUNGEN = "DROP TABLE IF EXISTS " + TABLE_BESTELLUNGEN;
    private static final String SQL_DROP_ADRESSE = "DROP TABLE IF EXISTS " + TABLE_ADRESSE;

    public DbHelper(Context context){
        super(context,DB_NAME, null,DB_VERSION);
        Log.d(TAG, "DbHelper hat die Datenbank " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ADRESSE);
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
        db.execSQL(SQL_DROP_ADRESSE);
        onCreate(db);
    }

}
