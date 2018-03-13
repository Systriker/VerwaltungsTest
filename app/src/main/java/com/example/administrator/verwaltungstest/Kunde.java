package com.example.administrator.verwaltungstest;

//Code-Seitige-Darstellung eines Kunden in der Datenbank
public class Kunde {
    private long id;
    private String name;
    private long adresse;
    private String kundenType;

    Kunde(long id, String name, long adresse, String kundenTyle) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.kundenType = kundenTyle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    long getAdresse() {
        return adresse;
    }

    String getKundenType() {
        return kundenType;
    }

    @Override
    public String toString() {
        return id + " " + name;
    }
}
