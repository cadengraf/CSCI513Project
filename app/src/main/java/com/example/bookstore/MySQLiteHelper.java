package com.example.bookstore;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME  = "Bookstore.db";
    private static final int DATABASE_VERSION  = 1;
    private static final String TABLE_CUSTOMER = "customer";
    private static final String TABLE_BOOK = "book";
    private static final String TABLE_PURCHASES = "purchases";
    private static final String TABLE_ADMIN = "admin";
    private static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "(" +
            "customerID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "customerName VARCHAR(255) NOT NULL," +
            "customerPassword VARCHAR(255) NOT NULL," +
            "totalAmountSpent REAL DEFAULT 0);";

    private static final String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_BOOK + "(" +
            "bookISBN CHAR(10) PRIMARY KEY," +
            "title VARCHAR(255) NOT NULL," +
            "price REAL DEFAULT 0," +
            "quantity INTEGER DEFAULT 0);";

    private static final String CREATE_PURCHASES_TABLE = "CREATE TABLE " + TABLE_PURCHASES + "(" +
            "purchaseID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "customerID INTEGER NOT NULL, " +
            "bookISBN TEXT NOT NULL, " +
            "quantity INTEGER DEFAULT 1 NOT NULL, " +
            "UNIQUE (customerID, bookISBN), " +
            "FOREIGN KEY (bookISBN) REFERENCES book(bookISBN), " +
            "FOREIGN KEY (customerID) REFERENCES customer(customerID));";

    private static final String CREATE_ADMIN_TABLE = "CREATE TABLE " + TABLE_ADMIN + "(" +
            "adminID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "adminName VARCHAR(255) NOT NULL," +
            "adminPassword VARCHAR(255) NOT NULL);";

    private static final String DATA_ADMIN_TABLE = "INSERT INTO admin (adminName, adminPassword) VALUES ('admin', '12345');";

    public MySQLiteHelper( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( SQLiteDatabase database ) {
        database.execSQL( CREATE_CUSTOMER_TABLE );
        database.execSQL( CREATE_BOOK_TABLE );
        database.execSQL( CREATE_PURCHASES_TABLE );
        database.execSQL( CREATE_ADMIN_TABLE );
        database.execSQL( DATA_ADMIN_TABLE );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        Log.w( MySQLiteHelper.class.getName( ), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data" );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_CUSTOMER );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_BOOK );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_PURCHASES );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_ADMIN );
        onCreate( db );
    }

    public int getCustomerID(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT customerID FROM " + TABLE_CUSTOMER +
                " WHERE customerName = ? AND customerPassword = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);   // return the actual ID
        }

        cursor.close();
        db.close();
        return id;   // -1 means: user does NOT exist
    }

    public boolean checkSignup(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE customerName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {  // true if at least one row exists
            cursor.close();
            db.close();
            return true;
        }
            // Add the user to the database
        ContentValues values = new ContentValues();
        values.put("customerName", username);
        values.put("customerPassword", password);
        long result = db.insert(TABLE_CUSTOMER, null, values);

        cursor.close();
        db.close();

        return result == -1;
    }

    public boolean checkAdmin(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ADMIN + " WHERE adminName = ? AND adminPassword = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean exists = false;
        if (cursor.moveToFirst()) {  // true if at least one row exists
            exists = true;
        }

        cursor.close();
        db.close();

        return exists;
    }
    public void clearSystem(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_CUSTOMER );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_BOOK );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_PURCHASES );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_ADMIN );
        onCreate( db );
    }

    public boolean addBook(String isbn, String title, Double price, Integer quantity){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bookISBN", isbn);
        values.put("title", title);
        values.put("price", price);
        values.put("quantity", quantity);
        long result = db.insert(TABLE_BOOK, null, values);

        db.close();

        return result != -1;

    }

    public Cursor getAllBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOK, null);
    }

    public Cursor getBooksByPrice(double min, double max) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOK + " WHERE price BETWEEN ? AND ?";
        return db.rawQuery(query, new String[]{String.valueOf(min), String.valueOf(max)});
    }

    public String purchaseBooks(ArrayList<Book> selectedBooks, int customerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        StringBuilder message = new StringBuilder();

        try {
            for (Book book : selectedBooks) {

                // Check if already purchased
                Cursor already = db.rawQuery(
                        "SELECT 1 FROM " + TABLE_PURCHASES +
                                " WHERE customerID = ? AND bookISBN = ?",
                        new String[]{String.valueOf(customerID), book.getIsbn()}
                );

                if (already.moveToFirst()) {
                    message.append(book.getTitle())
                            .append(" skipped (already purchased).\n");
                    already.close();
                    continue;
                }
                already.close();

                // Book info
                Cursor c = db.rawQuery(
                        "SELECT quantity, price FROM " + TABLE_BOOK +
                                " WHERE bookISBN = ?", new String[]{book.getIsbn()}
                );

                if (!c.moveToFirst()) {
                    c.close();
                    continue;
                }

                int currentQty = c.getInt(0);
                double price = c.getDouble(1);
                c.close();

                int buyQty = book.getSelectedQuantity();
                if (buyQty > currentQty) buyQty = currentQty;
                if (buyQty <= 0) continue;

                // Deduct stock
                ContentValues updateQty = new ContentValues();
                updateQty.put("quantity", currentQty - buyQty);
                db.update(TABLE_BOOK, updateQty, "bookISBN = ?", new String[]{book.getIsbn()});

                // Insert purchase
                ContentValues purchaseValues = new ContentValues();
                purchaseValues.put("customerID", customerID);
                purchaseValues.put("bookISBN", book.getIsbn());
                purchaseValues.put("quantity", buyQty);
                db.insert(TABLE_PURCHASES, null, purchaseValues);

                // Update total spent
                db.execSQL(
                        "UPDATE " + TABLE_CUSTOMER +
                                " SET totalAmountSpent = totalAmountSpent + ?" +
                                " WHERE customerID = ?",
                        new Object[]{price * buyQty, customerID}
                );

                message.append("Purchased ")
                        .append(buyQty)
                        .append(" of ")
                        .append(book.getTitle())
                        .append(".\n");
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            db.close();
        }

        if (message.length() == 0) {
            return "No books purchased.";
        }
        return message.toString();
    }


}


