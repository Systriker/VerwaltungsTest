package com.example.administrator.verwaltungstest;

//Code-Seitige-Darstellung einer Adresse in der Datenbank
public class Adresse {
    private long id;
    private String straße;
    private String ort;
    private long plz;
    private int hausnummer;
    private String zusatz;

    Adresse(long id, String straße, String ort, long PLZ, int hausnummer, String zusatz) {
        this.id = id;
        this.straße = straße;
        this.ort = ort;
        this.plz = PLZ;
        this.hausnummer = hausnummer;
        this.zusatz = zusatz;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    String getStraße() {
        return straße;
    }

    String getOrt() {
        return ort;
    }

    long getPlz() {
        return plz;
    }

    int getHausnummer() {
        return hausnummer;
    }

    String getZusatz() {
        return zusatz;
    }

    @Override
    public String toString() {
        if (zusatz != null && zusatz.length()>0) {
            return straße + " " + hausnummer + " " + zusatz + ", " + plz + " " + ort;
        }else {
            return straße + " " + hausnummer + ", " + plz + " " + ort;
        }
    }
}
