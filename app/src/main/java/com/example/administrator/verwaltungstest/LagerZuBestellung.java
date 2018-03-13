package com.example.administrator.verwaltungstest;

//Code-Seitige-Darstellung eines Produktes auf einer Bestellung in der Datenbank
public class LagerZuBestellung {
    private long id;
    private Product product;
    private int quantity;

    LagerZuBestellung(long id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    Product getProduct() {
        return product;
    }

    int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Produkt:" + product.getName() + "\nAnzahl:" + quantity + " Preis:" + (product.getPreis() * quantity) + "€";
    }
}
