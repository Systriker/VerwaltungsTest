package com.example.administrator.verwaltungstest;

public class LagerZuBestellung {
    private long id;
    private Bestellung bestellung;
    private Product product;
    private int quantity;

    public LagerZuBestellung(long id, Bestellung bestellung, Product product, int quantity) {
        this.id = id;
        this.bestellung = bestellung;
        this.product = product;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bestellung getBestellung() {
        return bestellung;
    }

    public void setBestellung(Bestellung bestellung) {
        this.bestellung = bestellung;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return product.getName() + " " + quantity + " Preis:" + (product.getPreis() * quantity) + "€";
    }
}
