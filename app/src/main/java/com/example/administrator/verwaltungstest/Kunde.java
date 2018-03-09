package com.example.administrator.verwaltungstest;

//Code-Seitige-Darstellung eines Kunden in der Datenbank
public class Kunde {
    private long id;
    private String name;
    private long adresse;
    private String kundenType;

    public Kunde(long id, String name, long adresse, String kundenTyle) {
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

    public long getAdresse() {
        return adresse;
    }

    public void setAdresse(long adresse) {
        this.adresse = adresse;
    }

    public String getKundenType() {
        return kundenType;
    }

    public void setKundenType(String kundenType) {
        this.kundenType = kundenType;
    }

    @Override
    public String toString() {
        return id + " " + name;
    }
}
