package com.example.bookstore;
public class Book {
    private String isbn, title;
    private double price;
    private int quantity; // available stock
    private int selectedQuantity = 1; // quantity user wants to buy

    public Book(String isbn, String title, double price, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.selectedQuantity = 1;
    }

    // Getters & setters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public int getSelectedQuantity() { return selectedQuantity; }
    public void setSelectedQuantity(int selectedQuantity) { this.selectedQuantity = selectedQuantity; }
}
