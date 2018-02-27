package com.example.administrator.verwaltungstest;

public class Bestellung {
    private long id;
    private Kunde kunde;

    public Bestellung(long id, Kunde kunde) {
        this.id = id;
        this.kunde = kunde;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    @Override
    public String toString() {
        return id + " " + kunde.getName();
    }
}
