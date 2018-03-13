package com.example.administrator.verwaltungstest;

//Code-Seitige-Darstellung eines Produktes in der Datenbank
public class Product {

    private long id;
    private String name;
    private int quantity;
    private double preis;

    Product(long id, String name, int quantity, double preis) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.preis = preis;
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

    int getQuantity() {
        return quantity;
    }

    double getPreis() {
        return preis;
    }

    @Override
    public String toString() {
        return "Nr.:" + id +
                " " + name +
                "\nPreis:" + preis + "€" +
                ", Anzahl:" + quantity;
    }
}
