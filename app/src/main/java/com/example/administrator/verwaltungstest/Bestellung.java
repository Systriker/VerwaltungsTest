package com.example.administrator.verwaltungstest;

public class Bestellung {
    private long id;
    private long kunde_id;

    public Bestellung(long id, long kunde_id) {
        this.id = id;
        this.kunde_id = kunde_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getKunde_id() {
        return kunde_id;
    }

    public void setKunde_id(long kunde_id) {
        this.kunde_id = kunde_id;
    }

    @Override
    public String toString() {
        return "Bestellung{" +
                "id=" + id +
                ", kunde_id=" + kunde_id +
                '}';
    }
}
