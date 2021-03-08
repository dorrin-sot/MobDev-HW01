package com.mobdev.currencyapp.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobdev.currencyapp.Model.Coin;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "coinsManager";
    private static final String TABLE_COINS = "coins";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SYM = "symbol";
    private static final String KEY_RANK = "rank";
    private static final String KEY_PRICE = "price";
    private static final String KEY_P_C_H = "percent_hour";
    private static final String KEY_P_C_D = "percent_day";
    private static final String KEY_P_C_W = "percent_week";
    private static final String KEY_URL = "icon";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COINS_TABLE = "CREATE TABLE " + TABLE_COINS + "("+ KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT," + KEY_SYM + " TEXT,"
                + KEY_RANK + " INTEGER," + KEY_PRICE + " REAL," + KEY_P_C_H + " REAL," + KEY_P_C_D + " REAL,"+KEY_P_C_W+" REAL,"+KEY_URL+" TEXT" + ")";
        db.execSQL(CREATE_COINS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COINS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addCoin(Coin coin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, coin.getName());
        values.put(KEY_SYM, coin.getSymbol());
        values.put(KEY_RANK, coin.getRank());
        values.put(KEY_PRICE, coin.getCurrentPriceUSD());
        values.put(KEY_P_C_H, coin.getPercentChange1H());
        values.put(KEY_P_C_D, coin.getPercentChange1D());
        values.put(KEY_P_C_W, coin.getPercentChange1W());
        values.put(KEY_URL, coin.getLogoURL());

        // Inserting Row
        db.insert(TABLE_COINS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public Coin getCoin(int rank) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COINS, new String[] {
                        KEY_NAME,KEY_SYM, KEY_RANK, KEY_PRICE, KEY_P_C_H, KEY_P_C_D,KEY_P_C_W,KEY_URL }, KEY_ID + "=?",
                new String[] { String.valueOf(rank) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
cursor.
        Coin coin = new Coin(cursor.getString(0),cursor.getString(1),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(2)),cursor.getString(7),Double.parseDouble(cursor.getString(3)),Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)),Double.parseDouble(cursor.getString(6)));
        // return contact
        return coin;
    }

    // code to get all contacts in a list view
  /*  public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }*/

    // code to update the single contact
    public int updateContact(Coin coin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, coin.getName());
        values.put(KEY_SYM, coin.getSymbol());
        values.put(KEY_RANK, coin.getRank());
        values.put(KEY_PRICE, coin.getCurrentPriceUSD());
        values.put(KEY_P_C_H, coin.getPercentChange1H());
        values.put(KEY_P_C_D, coin.getPercentChange1D());
        values.put(KEY_P_C_W, coin.getPercentChange1W());
        values.put(KEY_URL, coin.getLogoURL());

        // updating row
        return db.update(TABLE_COINS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(coin.getId()) });
    }

    // Deleting single contact
    public void deleteCoin(Coin coin) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COINS, KEY_ID + " = ?",
                new String[] { String.valueOf(coin.getId()) });
        db.close();
    }

    // Getting contacts Count
    public int getCoinCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COINS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
