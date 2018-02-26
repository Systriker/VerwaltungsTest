package com.example.administrator.verwaltungstest;

public class Lager_Zu_Bestellung {
    private long id;
    private long bestellung_id;
    private long product_id;

    public Lager_Zu_Bestellung(long id, long bestellung_id, long product_id) {
        this.id = id;
        this.bestellung_id = bestellung_id;
        this.product_id = product_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBestellung_id() {
        return bestellung_id;
    }

    public void setBestellung_id(long bestellung_id) {
        this.bestellung_id = bestellung_id;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    @Override
    public String toString() {
        return "Lager_Zu_Bestellung{" +
                "id=" + id +
                ", bestellung_id=" + bestellung_id +
                ", product_id=" + product_id +
                '}';
    }
}
