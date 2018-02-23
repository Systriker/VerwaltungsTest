package com.example.administrator.verwaltungstest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Datasource {

    private static final String TAG = Datasource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    private String[] columns = {
            DbHelper.COLUMN_KUNDE_ID,
            DbHelper.COLUMN_KUNDE_NAME,
            DbHelper.COLUMN_KUNDE_ADRESSE,
            DbHelper.COLUMN_KUNDE_KUNDETYP,
    };

    public Datasource(Context context){
        Log.d(TAG, "Datasource erzeugt den dbHelper");
        dbHelper = new DbHelper(context);
    }

    public void open(){
        Log.d(TAG, "open: Eine Referenz auf die Datenbank wird angefragt");
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "open: Referenz erhalten, Pfad zur DB: " + database.getPath());
    }

    public void close(){
        dbHelper.close();
        Log.d(TAG, "close: Datenbank wurde geschlossen");
    }

    public Kunde createKunde(long id, String Name, String Adresse, String KundeTyp){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_KUNDE_NAME,Name);
        values.put(DbHelper.COLUMN_KUNDE_ADRESSE,Adresse);
        values.put(DbHelper.COLUMN_KUNDE_KUNDETYP,KundeTyp);

        long insertId = database.insert(DbHelper.TABLE_KUNDE,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_KUNDE,columns,
                DbHelper.COLUMN_KUNDE_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        Kunde kunde = cursorToKunde(cursor);
        cursor.close();
        return kunde;
    }

    public Kunde getKunde(long id){
        Cursor cursor = database.query(DbHelper.TABLE_KUNDE,columns,
                DbHelper.COLUMN_KUNDE_ID + " = " + id,null,null,null,null);
        Kunde kunde;
        cursor.moveToFirst();
        kunde = cursorToKunde(cursor);
        Log.d(TAG, "ID: " + kunde.getId() + " Inhalt " + kunde.toString());
        cursor.close();
        return  kunde;
    }

    public List<Kunde> getAllKunde(){
        List<Kunde> kundeList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_KUNDE,columns,
                null,null,null,null,null);
        cursor.moveToFirst();
        Kunde kunde;
        while (!cursor.isAfterLast()){
            kunde = cursorToKunde(cursor);
            kundeList.add(kunde);
            Log.d(TAG, "ID: " + kunde.getId() + " Inhalt " + kunde.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return  kundeList;
    }

    private Kunde cursorToKunde(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_ID);
        int idName = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_NAME);
        int idAdresse = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_ADRESSE);
        int idKundeTyp = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_KUNDETYP);

        String name = cursor.getString(idName);
        String adresse = cursor.getString(idAdresse);
        String kundeTyp = cursor.getString(idKundeTyp);
        long id = cursor.getLong(idIndex);

        Kunde kunde = new Kunde(id,name,adresse,kundeTyp);
        return kunde;
    }

    public Kunde updateKunde(long id, String newName, String newAdresse, String newKundeTyp){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_KUNDE_NAME,newName);
        values.put(DbHelper.COLUMN_KUNDE_ADRESSE,newAdresse);
        values.put(DbHelper.COLUMN_KUNDE_KUNDETYP,newKundeTyp);

        database.update(DbHelper.TABLE_KUNDE,values,
                DbHelper.COLUMN_KUNDE_ID+ "=" + id,null);
        Cursor cursor = database.query(DbHelper.TABLE_KUNDE,
                columns,DbHelper.COLUMN_KUNDE_ID + "=" + id,
                null,null,null,null);
        cursor.moveToFirst();
        Kunde kunde = cursorToKunde(cursor);
        cursor.close();
        return kunde;
    }

    public void deleteKunde(Kunde kunde){
        long id = kunde.getId();

        database.delete(DbHelper.TABLE_KUNDE,
                DbHelper.COLUMN_KUNDE_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + kunde.toString());
    }

}
