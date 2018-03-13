package com.example.administrator.verwaltungstest;


//Code-Seitige-Darstellung einer Bestellung in der Datenbank
public class Bestellung {
    private long id;
    private Kunde kunde;
    private boolean booked;

    Bestellung(long id, Kunde kunde, boolean booked) {
        this.id = id;
        this.kunde = kunde;
        this.booked = booked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    Kunde getKunde() {
        return kunde;
    }

    boolean isBooked() {
        return booked;
    }

    @Override
    public String toString() {
        return id + " " + kunde.getName() +
                "\nStatus: " + (booked?"gebucht":"offen");
    }
}
