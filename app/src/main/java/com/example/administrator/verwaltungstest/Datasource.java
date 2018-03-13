package com.example.administrator.verwaltungstest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//Klasse die Funktionen zur verwaltung und nutzung der DB bereitstellt
public class Datasource {

    private static final String TAG = Datasource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    // Fleder der "kunde"-Tabelle
    private String[] columns = {
            DbHelper.COLUMN_KUNDE_ID,
            DbHelper.COLUMN_KUNDE_NAME,
            DbHelper.COLUMN_KUNDE_ADRESSE,
            DbHelper.COLUMN_KUNDE_KUNDETYP,
    };

    // Fleder der "lager"-Tabelle
    private String[] columns_Lager = {
            DbHelper.COLUMN_PRODUCT_ID,
            DbHelper.COLUMN_PRODUCT_NAME,
            DbHelper.COLUMN_PRODUCT_QUANTITY,
            DbHelper.COLUMN_PRODUCT_PREIS,
    };

    // Fleder der "bestellungen"-Tabelle
    private String[] columns_Bestellung = {
            DbHelper.COLUMN_BESTELLUNG_ID,
            DbHelper.COLUMN_BESTELLUNG_KUNDE,
            DbHelper.COLUMN_BESTELLUNG_BOOKED,
    };

    // Fleder der "lager_zu_bestellungen"-Tabelle
    private String[] columns_Lager_Zu_Bestellung = {
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID,
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG,
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT,
            DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY,
    };

    // Fleder der "adresse"-Tabelle
    private String[] columns_Adresse = {
            DbHelper.COLUMN_ADRESSE_ID,
            DbHelper.COLUMN_ADRESSE_STRASSE,
            DbHelper.COLUMN_ADRESSE_HAUSNUMMER,
            DbHelper.COLUMN_ADRESSE_ZUSATZ,
            DbHelper.COLUMN_ADRESSE_ORT,
            DbHelper.COLUMN_ADRESSE_PLZ,
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

    // Funktionen die Kunden betreffen
    // erstellen eines neuen Kunde-Objektes und zugehörigem Datensatz
    void createKunde(String Name, long Adresse, String KundeTyp){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_KUNDE_NAME,Name);
        values.put(DbHelper.COLUMN_KUNDE_ADRESSE,Adresse);
        values.put(DbHelper.COLUMN_KUNDE_KUNDETYP,KundeTyp);

        long insertId = database.insert(DbHelper.TABLE_KUNDE,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_KUNDE,columns,
                DbHelper.COLUMN_KUNDE_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        //Kunde kunde = cursorToKunde(cursor);
        cursor.close();
    }

    // auswahl eines Kunde-Datensatzes anhand der Id und erstellen eines zugehörigen Objektes
    Kunde getKunde(long id){
        Cursor cursor = database.query(DbHelper.TABLE_KUNDE,columns,
                DbHelper.COLUMN_KUNDE_ID + " = " + id,null,null,null,null);
        Kunde kunde;
        cursor.moveToFirst();
        kunde = cursorToKunde(cursor);
        Log.d(TAG, "ID: " + kunde.getId() + " Inhalt " + kunde.toString());
        cursor.close();
        return  kunde;
    }

    // rückgabe einer Liste die alle Kunden-Datensätze der DB als Kunde-Objekte enthält
    List<Kunde> getAllKunde(){
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

    // rückgabe einer Liste die alle Kunden-Datensätze die eine bestimmte Adresse haben
    // als Kunde-Objekte enthält
    List<Kunde> getAllKunden(long adresse_id){
        List<Kunde> kundeList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_KUNDE,columns,
                DbHelper.COLUMN_KUNDE_ADRESSE + "=" + adresse_id,null,
                null,null,null);
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

    // wandelt einen Kunde-Datensatz in ein Kunde-Objekt um
    private Kunde cursorToKunde(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_ID);
        int idName = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_NAME);
        int idAdresse = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_ADRESSE);
        int idKundeTyp = cursor.getColumnIndex(DbHelper.COLUMN_KUNDE_KUNDETYP);

        String name = cursor.getString(idName);
        long adresse = cursor.getLong(idAdresse);
        String kundeTyp = cursor.getString(idKundeTyp);
        long id = cursor.getLong(idIndex);

        return new Kunde(id,name,adresse,kundeTyp);
    }

    // bearbeiten eines Kunde-Datensatzes
    void updateKunde(long id, String newName, long newAdresse, String newKundeTyp){
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
        //Kunde kunde = cursorToKunde(cursor);
        cursor.close();
    }

    // löschen eines Kunde-Datensatzes
    void deleteKunde(Kunde kunde){
        long id = kunde.getId();

        database.delete(DbHelper.TABLE_KUNDE,
                DbHelper.COLUMN_KUNDE_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + kunde.toString());
    }


    //Funktionen die Produkte betreffen
    // erstellen eines neuen Produkt-Objektes und zugehörigem Datensatz
    void createProduct(String Name, int Quantity, double preis){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PRODUCT_NAME,Name);
        values.put(DbHelper.COLUMN_PRODUCT_QUANTITY,Quantity);
        values.put(DbHelper.COLUMN_PRODUCT_PREIS,preis);

        long insertId = database.insert(DbHelper.TABLE_LAGER,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_LAGER,columns_Lager,
                DbHelper.COLUMN_PRODUCT_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        //Product product = cursorToProduct(cursor);
        cursor.close();
    }

    // auswahl eines Produkt-Datensatzes anhand der Id und erstellen eines zugehörigen Objektes
    Product getProduct(long id){
        Cursor cursor = database.query(DbHelper.TABLE_LAGER,columns_Lager,
                DbHelper.COLUMN_PRODUCT_ID + " = " + id,null,null,null,null);
        Product product;
        cursor.moveToFirst();
        product = cursorToProduct(cursor);
        Log.d(TAG, "ID: " + product.getId() + " Inhalt " + product.toString());
        cursor.close();
        return  product;
    }

    // rückgabe einer Liste die alle Produkt-Datensätze der DB als Produkt-Objekte enthält
    List<Product> getAllProducts(){
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

    // wandelt einen Produkt-Datensatz in ein Produkt-Objekt um
    private Product cursorToProduct(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_PRODUCT_ID);
        int idName = cursor.getColumnIndex(DbHelper.COLUMN_PRODUCT_NAME);
        int idQuantity = cursor.getColumnIndex(DbHelper.COLUMN_PRODUCT_QUANTITY);
        int idPreis = cursor.getColumnIndex(DbHelper.COLUMN_PRODUCT_PREIS);


        String name = cursor.getString(idName);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        double preis = cursor.getDouble(idPreis);

        return new Product(id,name,quantity,preis);
    }

    // bearbeiten eines Produkt-Datensatzes
    void updateProduct(long id, String newName, int newQuantity, double newPreis){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PRODUCT_NAME,newName);
        values.put(DbHelper.COLUMN_PRODUCT_QUANTITY,newQuantity);
        values.put(DbHelper.COLUMN_PRODUCT_PREIS,newPreis);

        database.update(DbHelper.TABLE_LAGER,values,
                DbHelper.COLUMN_PRODUCT_ID + "=" + id,null);
        Cursor cursor = database.query(DbHelper.TABLE_LAGER,
                columns_Lager,DbHelper.COLUMN_PRODUCT_ID + "=" + id,
                null,null,null,null);
        cursor.moveToFirst();
        //Product product = cursorToProduct(cursor);
        cursor.close();
    }

    // löschen eines Produkt-Datensatzes
    void deleteProduct(Product product){
        long id = product.getId();

        database.delete(DbHelper.TABLE_LAGER,
                DbHelper.COLUMN_PRODUCT_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + product.toString());
    }


    //Funktionen die Bestellungen betreffen
    // erstellen eines neuen Bestellung-Objektes und zugehörigem Datensatz
    void createBestellung(int kunde_id){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_BESTELLUNG_KUNDE,kunde_id);
        values.put(DbHelper.COLUMN_BESTELLUNG_BOOKED,0);

        long insertId = database.insert(DbHelper.TABLE_BESTELLUNGEN,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,columns_Bestellung,
                DbHelper.COLUMN_BESTELLUNG_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        //Bestellung bestellung = cursorToBestellung(cursor);
        cursor.close();
    }

    // auswahl eines Bestellung-Datensatzes anhand der Id und erstellen eines zugehörigen Objektes
    Bestellung getBestellung(long id){
        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,columns_Bestellung,
                DbHelper.COLUMN_BESTELLUNG_ID + " = " + id,null,null,null,null);
        Bestellung bestellung;
        cursor.moveToFirst();
        bestellung = cursorToBestellung(cursor);
        Log.d(TAG, "ID: " + bestellung.getId() + " Inhalt " + bestellung.toString());
        cursor.close();
        return  bestellung;
    }

    // rückgabe einer Liste die alle Bestellung-Datensätze der DB als Bestellung-Objekte enthält
    List<Bestellung> getAllBestellungen(){
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

    // rückgabe einer Liste die alle Bestellung-Datensätze die einem bestimmten Kunden zugeordnet sind
    // als Bestellung-Objekte enthält
    List<Bestellung> getAllBestellungen(long kunde_id){
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

    // wandelt einen Bestellung-Datensatz in ein Bestellung-Objekt um
    private Bestellung cursorToBestellung(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_BESTELLUNG_ID);
        int idKunde = cursor.getColumnIndex(DbHelper.COLUMN_BESTELLUNG_KUNDE);
        int idBooked = cursor.getColumnIndex(DbHelper.COLUMN_BESTELLUNG_BOOKED);


        int kunde_id = cursor.getInt(idKunde);
        Kunde kunde = getKunde(kunde_id);
        long id = cursor.getLong(idIndex);
        Boolean booked = cursor.getInt(idBooked)==1;

        return new Bestellung(id,kunde,booked);
    }

    // bearbeiten eines Bestellung-Datensatzes
    void updateBestellung(long id, int newKunde_id,int newBooked){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_BESTELLUNG_KUNDE,newKunde_id);
        values.put(DbHelper.COLUMN_BESTELLUNG_BOOKED,newBooked);


        database.update(DbHelper.TABLE_BESTELLUNGEN,values,
                DbHelper.COLUMN_BESTELLUNG_ID + "=" + id,null);
        Cursor cursor = database.query(DbHelper.TABLE_BESTELLUNGEN,
                columns_Bestellung,DbHelper.COLUMN_BESTELLUNG_ID + "=" + id,
                null,null,null,null);
        cursor.moveToFirst();
        //Bestellung bestellung = cursorToBestellung(cursor);
        cursor.close();
    }

    // löschen eines Bestellung-Datensatzes
    void deleteBestellung(Bestellung bestellung){
        long id = bestellung.getId();

        database.delete(DbHelper.TABLE_BESTELLUNGEN,
                DbHelper.COLUMN_BESTELLUNG_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + bestellung.toString());
    }


    //Funktionen die die Zuordnung von Produckten und Bestellungen betreffen
    // erstellen eines neuen LagerZuBestellung-Objektes und zugehörigem Datensatz
    void createLager_zu_Bestellung(long bestellung_id, long product_id, int quantity){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_BESTELLUNG,bestellung_id);
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_PRODUCT,product_id);
        values.put(DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_QUANTITY,quantity);

        long insertId = database.insert(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,columns_Lager_Zu_Bestellung,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        //LagerZuBestellung lagerZuBestellung = cursorToLager_zu_Bestellung(cursor);
        cursor.close();
    }

    // auswahl eines LagerZuBestellung-Datensatzes anhand der Id und erstellen eines zugehörigen Objektes
    LagerZuBestellung getLager_zu_Bestellung(long id){
        Cursor cursor = database.query(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,columns_Lager_Zu_Bestellung,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID + " = " + id,null,null,null,null);
        LagerZuBestellung lagerZuBestellung;
        cursor.moveToFirst();
        lagerZuBestellung = cursorToLager_zu_Bestellung(cursor);
        Log.d(TAG, "ID: " + lagerZuBestellung.getId() + " Inhalt " + lagerZuBestellung.toString());
        cursor.close();
        return  lagerZuBestellung;
    }

    // rückgabe einer Liste die alle LagerZuBestellung-Datensätze die einer bestimmten Bestellung zugeordnet sind
    // als LagerZuBestellung-Objekte enthält
    List<LagerZuBestellung> getAllLager_zu_Bestellungen(long bestellung_id){
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

    // rückgabe einer Liste die alle LagerZuBestellung-Datensätze die ein bestimmtes Produkt enthalten
    // als LagerZuBestellung-Objekte enthält
    List<LagerZuBestellung> getAllLager_zu_Bestellungen_Product(long product_id){
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

    // wandelt einen LagerZuBestellung-Datensatz in ein LagerZuBestellung-Objekt um
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

        return new LagerZuBestellung(id,product,quantity);
    }

    // bearbeiten eines LagerZuBestellung-Datensatzes
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

    // löschen eines LagerZuBestellung-Datensatzes
    void deleteLager_zu_Bestellung(LagerZuBestellung lagerZuBestellung){
        long id = lagerZuBestellung.getId();

        database.delete(DbHelper.TABLE_LAGER_ZU_BESTELLUNGEN,
                DbHelper.COLUMN_LAGER_ZU_BESTELLUNG_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + lagerZuBestellung.toString());
    }


    //Funktionen die Adressen betreffen
    // erstellen eines neuen Adresse-Objektes und zugehörigem Datensatz
    void createAdresse(String strasse,int hausnummer,String zusatz, String ort, long plz){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ADRESSE_STRASSE,strasse);
        values.put(DbHelper.COLUMN_ADRESSE_HAUSNUMMER,hausnummer);
        values.put(DbHelper.COLUMN_ADRESSE_ZUSATZ,zusatz);
        values.put(DbHelper.COLUMN_ADRESSE_ORT,ort);
        values.put(DbHelper.COLUMN_ADRESSE_PLZ,plz);

        long insertId = database.insert(DbHelper.TABLE_ADRESSE,null,values);

        Cursor cursor = database.query(DbHelper.TABLE_ADRESSE,columns_Adresse,
                DbHelper.COLUMN_ADRESSE_ID + "=" + insertId,
                null,null,null,null);
        cursor.moveToFirst();
        //Adresse adresse = cursorToAdresse(cursor);
        cursor.close();
    }

    // auswahl eines Adresse-Datensatzes anhand der Id und erstellen eines zugehörigen Objektes
    Adresse getAdresse(long id){
        Cursor cursor = database.query(DbHelper.TABLE_ADRESSE,columns_Adresse,
                DbHelper.COLUMN_ADRESSE_ID + " = " + id,null,null,null,null);
        Adresse adresse;
        cursor.moveToFirst();
        adresse = cursorToAdresse(cursor);
        Log.d(TAG, "ID: " + adresse.getId() + " Inhalt " + adresse.toString());
        cursor.close();
        return  adresse;
    }

    // rückgabe einer Liste die alle Adresse-Datensätze der DB als Adresse-Objekte enthält
    List<Adresse> getAllArdesse(){
        List<Adresse> adresseList = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_ADRESSE,columns_Adresse,
                null,null,null,null,null);
        cursor.moveToFirst();
        Adresse adresse;
        while (!cursor.isAfterLast()){
            adresse = cursorToAdresse(cursor);
            adresseList.add(adresse);
            Log.d(TAG, "ID: " + adresse.getId() + adresse + adresse.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return  adresseList;
    }

    // wandelt einen Adresse-Datensatz in ein Adresse-Objekt um
    private Adresse cursorToAdresse(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_ADRESSE_ID);
        int idStrasse = cursor.getColumnIndex(DbHelper.COLUMN_ADRESSE_STRASSE);
        int idHausnummer = cursor.getColumnIndex(DbHelper.COLUMN_ADRESSE_HAUSNUMMER);
        int idZusatz = cursor.getColumnIndex(DbHelper.COLUMN_ADRESSE_ZUSATZ);
        int idOrt = cursor.getColumnIndex(DbHelper.COLUMN_ADRESSE_ORT);
        int idPLZ = cursor.getColumnIndex(DbHelper.COLUMN_ADRESSE_PLZ);

        long id = cursor.getLong(idIndex);
        String strasse = cursor.getString(idStrasse);
        int hausnummer = cursor.getInt(idHausnummer);
        String zusatz = cursor.getString(idZusatz);
        String ort = cursor.getString(idOrt);
        long plz = cursor.getLong(idPLZ);

        return new Adresse(id,strasse,ort,plz,hausnummer,zusatz);
    }

    // bearbeiten eines Adresse-Datensatzes
    void updateAdresse(long id, String newStrasse,int newHausnummer,String newZusatz,
                                    String newOrt, long newPlz){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ADRESSE_STRASSE,newStrasse);
        values.put(DbHelper.COLUMN_ADRESSE_HAUSNUMMER,newHausnummer);
        values.put(DbHelper.COLUMN_ADRESSE_ZUSATZ,newZusatz);
        values.put(DbHelper.COLUMN_ADRESSE_ORT,newOrt);
        values.put(DbHelper.COLUMN_ADRESSE_PLZ,newPlz);


        database.update(DbHelper.TABLE_ADRESSE,values,
                DbHelper.COLUMN_ADRESSE_ID + "=" + id,null);
        Cursor cursor = database.query(DbHelper.TABLE_ADRESSE,
                columns_Adresse,DbHelper.COLUMN_ADRESSE_ID+ "=" + id,
                null,null,null,null);
        cursor.moveToFirst();
        //Adresse adresse = cursorToAdresse(cursor);
        cursor.close();
    }

    // löschen eines Adresse-Datensatzes
    void deleteAdresse(Adresse adresse){
        long id = adresse.getId();

        database.delete(DbHelper.TABLE_ADRESSE,
                DbHelper.COLUMN_ADRESSE_ID + "=" + id, null);
        Log.d(TAG, "deleteKunde: Eintrag gelöscht" + id + " " + adresse.toString());
    }

}
