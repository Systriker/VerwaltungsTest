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

    private String[] columns_Lager = {
            DbHelper.COLUMN_PRODUCT_ID,
            DbHelper.COLUMN_PRODUCT_NAME,
            DbHelper.COLUMN_PRODUCT_QUANTITY,
    };

    private String[] columns_Bestellung = {
            DbHelper.COLUMN_BESTELLUNG_ID,
            DbHelper.COLUMN_BESTELLUNG_KUNDE,
    };

    private String[] columns_Lager_Zu_Bestellung = {
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID,
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG,
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT,
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY,
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

    public Product createProduct(long id, String Name, int Quantity){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PRODUCT_NAME,Name);
        values.put(DbHelper.COLUMN_PRODUCT_QUANTITY,Quantity);

        long insertId = database.insert(DbHelper.TABLE_LAGER,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_LAGER,columns_Lager,
                DbHelper.COLUMN_PRODUCT_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        Product product = cursorToProduct(cursor);
        cursor.close();
        return product;
    }

    public Product getProduct(long id){
        Cursor cursor = database.query(DbHelper.TABLE_LAGER,columns_Lager,
                DbHelper.COLUMN_PRODUCT_ID + " = " + id,null,null,null,null);
        Product product;
        cursor.moveToFirst();
        product = cursorToProduct(cursor);
        Log.d(TAG, "ID: " + product.getId() + " Inhalt " + product.toString());
        cursor.close();
        return  product;
    }

    public List<Product> getAllProducts(){
        List<Product> productList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_LAGER,columns_Lager,
                null,null,null,null,null);
        cursor.moveToFirst();
        Product product;
        while (!cursor.isAfterLast()){
            product = cursorToProduct(cursor);
            productList.add(product);
            Log.d(TAG, "ID: " + product.getId() + " Inhalt " + product.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return  productList;
    }

    private Product cursorToProduct(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_PRODUCT_ID);
        int idName = cursor.getColumnIndex(DbHelper.COLUMN_PRODUCT_NAME);
        int idQuantity = cursor.getColumnIndex(DbHelper.COLUMN_PRODUCT_QUANTITY);


        String name = cursor.getString(idName);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);

        Product product = new Product(id,name,quantity);
        return product;
    }

    public Product updateProduct(long id, String newName, int newQuantity){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PRODUCT_NAME,newName);
        values.put(DbHelper.COLUMN_PRODUCT_QUANTITY,newQuantity);


        database.update(DbHelper.TABLE_LAGER,values,
                DbHelper.COLUMN_PRODUCT_ID + "=" + id,null);
        Cursor cursor = database.query(DbHelper.TABLE_LAGER,
                columns_Lager,DbHelper.COLUMN_PRODUCT_ID + "=" + id,
                null,null,null,null);
        cursor.moveToFirst();
        Product product = cursorToProduct(cursor);
        cursor.close();
        return product;
    }

    public void deleteProduct(Product product){
        long id = product.getId();

        database.delete(DbHelper.TABLE_LAGER,
                DbHelper.COLUMN_PRODUCT_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + product.toString());
    }

    public Bestellung createBestellung(long id, int kunde_id){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_BESTELLUNG_KUNDE,kunde_id);

        long insertId = database.insert(DbHelper.TABLE_BESTELLUNGEN,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,columns_Bestellung,
                DbHelper.COLUMN_BESTELLUNG_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        Bestellung bestellung = cursorToBestellung(cursor);
        cursor.close();
        return bestellung;
    }

    public Bestellung getBestellung(long id){
        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,columns_Bestellung,
                DbHelper.COLUMN_BESTELLUNG_ID + " = " + id,null,null,null,null);
        Bestellung bestellung;
        cursor.moveToFirst();
        bestellung = cursorToBestellung(cursor);
        Log.d(TAG, "ID: " + bestellung.getId() + " Inhalt " + bestellung.toString());
        cursor.close();
        return  bestellung;
    }

    public List<Bestellung> getAllBestellungen(){
        List<Bestellung> bestellungList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,columns_Bestellung,
                null,null,null,null,null);
        cursor.moveToFirst();
        Bestellung bestellung;
        while (!cursor.isAfterLast()){
            bestellung = cursorToBestellung(cursor);
            bestellungList.add(bestellung);
            Log.d(TAG, "ID: " + bestellung.getId() + " Inhalt " + bestellung.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return  bestellungList;
    }

    public List<Bestellung> getAllBestellungen(long kunde_id){
        List<Bestellung> bestellungList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,columns_Bestellung,
                DbHelper.COLUMN_BESTELLUNG_KUNDE + "=" + kunde_id,null,null,null,null);
        cursor.moveToFirst();
        Bestellung bestellung;
        while (!cursor.isAfterLast()){
            bestellung = cursorToBestellung(cursor);
            bestellungList.add(bestellung);
            Log.d(TAG, "ID: " + bestellung.getId() + " Inhalt " + bestellung.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return  bestellungList;
    }

    private Bestellung cursorToBestellung(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_BESTELLUNG_ID);
        int idKunde = cursor.getColumnIndex(DbHelper.COLUMN_BESTELLUNG_KUNDE);


        int kunde_id = cursor.getInt(idKunde);
        Kunde kunde = getKunde(kunde_id);
        long id = cursor.getLong(idIndex);

        Bestellung bestellung = new Bestellung(id,kunde);
        return bestellung;
    }

    public Bestellung updateBestellung(long id, int newKunde_id){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_BESTELLUNG_KUNDE,newKunde_id);


        database.update(DbHelper.TABLE_BESTELLUNGEN,values,
                DbHelper.COLUMN_BESTELLUNG_ID + "=" + id,null);
        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,
                columns_Bestellung,DbHelper.COLUMN_BESTELLUNG_ID + "=" + id,
                null,null,null,null);
        cursor.moveToFirst();
        Bestellung bestellung = cursorToBestellung(cursor);
        cursor.close();
        return bestellung;
    }

    public void deleteBestellung(Bestellung bestellung){
        long id = bestellung.getId();

        database.delete(DbHelper.TABLE_BESTELLUNGEN,
                DbHelper.COLUMN_BESTELLUNG_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + bestellung.toString());
    }

    public LagerZuBestellung createLager_zu_Bestellung(long id, long bestellung_id, long product_id, int quantity){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG,bestellung_id);
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT,product_id);
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY,quantity);

        long insertId = database.insert(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,columns_Lager_Zu_Bestellung,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        LagerZuBestellung lagerZuBestellung = cursorToLager_zu_Bestellung(cursor);
        cursor.close();
        return lagerZuBestellung;
    }

    public LagerZuBestellung getLager_zu_Bestellung(long id){
        Cursor cursor = database.query(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,columns_Lager_Zu_Bestellung,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID + " = " + id,null,null,null,null);
        LagerZuBestellung lagerZuBestellung;
        cursor.moveToFirst();
        lagerZuBestellung = cursorToLager_zu_Bestellung(cursor);
        Log.d(TAG, "ID: " + lagerZuBestellung.getId() + " Inhalt " + lagerZuBestellung.toString());
        cursor.close();
        return  lagerZuBestellung;
    }

    public List<LagerZuBestellung> getAllLager_zu_Bestellungen(long bestellung_id){
        List<LagerZuBestellung> lagerZuBestellungList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,columns_Lager_Zu_Bestellung,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG + "=" + bestellung_id,
                null,null,null,null);
        cursor.moveToFirst();
        LagerZuBestellung lagerZuBestellung;
        while (!cursor.isAfterLast()){
            lagerZuBestellung = cursorToLager_zu_Bestellung(cursor);
            lagerZuBestellungList.add(lagerZuBestellung);
            Log.d(TAG, "ID: " + lagerZuBestellung.getId() + " Inhalt " + lagerZuBestellung.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return  lagerZuBestellungList;
    }

    public List<LagerZuBestellung> getAllLager_zu_Bestellungen_Product(long product_id){
        List<LagerZuBestellung> lagerZuBestellungList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,columns_Lager_Zu_Bestellung,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT + "=" + product_id,
                null,null,null,null);
        cursor.moveToFirst();
        LagerZuBestellung lagerZuBestellung;
        while (!cursor.isAfterLast()){
            lagerZuBestellung = cursorToLager_zu_Bestellung(cursor);
            lagerZuBestellungList.add(lagerZuBestellung);
            Log.d(TAG, "ID: " + lagerZuBestellung.getId() + " Inhalt " + lagerZuBestellung.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return  lagerZuBestellungList;
    }

    private LagerZuBestellung cursorToLager_zu_Bestellung(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID);
        int idBestellung = cursor.getColumnIndex(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG);
        int idProduct = cursor.getColumnIndex(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT);
        int idQuantity = cursor.getColumnIndex(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY);


        long bestellung_id = cursor.getLong(idBestellung);
        Bestellung bestellung = getBestellung(bestellung_id);
        long product_id = cursor.getLong(idProduct);
        Product product = getProduct(product_id);
        long id = cursor.getLong(idIndex);
        int quantity = cursor.getInt(idQuantity);

        LagerZuBestellung lagerZuBestellung = new LagerZuBestellung(id,bestellung,product,quantity);
        return lagerZuBestellung;
    }

    public LagerZuBestellung updateLager_zu_Bestellung(long id, long newBestellung_id, long newProduct_id, int newQuantity){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG,newBestellung_id);
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT,newProduct_id);
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY,newQuantity);


        database.update(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,values,
                DbHelper.COLUMN_BESTELLUNG_ID + "=" + id,null);
        Cursor cursor = database.query(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,
                columns_Lager_Zu_Bestellung,DbHelper.COLUMN_BESTELLUNG_ID + "=" + id,
                null,null,null,null);
        cursor.moveToFirst();
        LagerZuBestellung lagerZuBestellung = cursorToLager_zu_Bestellung(cursor);
        cursor.close();
        return lagerZuBestellung;
    }

    public void deleteLager_zu_Bestellung(LagerZuBestellung lagerZuBestellung){
        long id = lagerZuBestellung.getId();

        database.delete(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + lagerZuBestellung.toString());
    }

}
