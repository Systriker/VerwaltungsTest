package com.example.administrator.verwaltungstest;

public class Bestellung {
    private long id;
    private Kunde kunde;
    private boolean booked;

    public Bestellung(long id, Kunde kunde, boolean booked) {
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

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    @Override
    public String toString() {
        return id + " " + kunde.getName();
    }
}
