package com.example.administrator.verwaltungstest;

/**
 * Created by Administrator on 06.03.2018.
 */

public class Adresse {
    private long id;
    private String straße;
    private String ort;
    private long plz;
    private int hausnummer;
    private String zusatz;

    public Adresse(long id, String straße, String ort, long PLZ, int hausnummer, String zusatz) {
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

    public String getStraße() {
        return straße;
    }

    public void setStraße(String straße) {
        this.straße = straße;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public long getPlz() {
        return plz;
    }

    public void setPlz(long plz) {
        this.plz = plz;
    }

    public int getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(int hausnummer) {
        this.hausnummer = hausnummer;
    }

    public String getZusatz() {
        return zusatz;
    }

    public void setZusatz(String zusatz) {
        this.zusatz = zusatz;
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
