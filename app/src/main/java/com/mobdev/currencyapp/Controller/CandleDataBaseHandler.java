package com.mobdev.currencyapp.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobdev.currencyapp.Model.Coin;

public class CandleDataBaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "coinsManager";
    private static final String TABLE_COINS = "candles";
    private static final String KEY_ID = "id";
    private static final String KEY_COIN_ID = "coinId";
    private static final String KEY_DATE = "date";
    private static final String KEY_OPEN = "open";
    private static final String KEY_CLOSE = "close";
    private static final String KEY_HIGH = "high";
    private static final String KEY_LOW = "low";


    public CandleDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COINS_TABLE = "CREATE TABLE " + TABLE_COINS + "("+ KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATE+ " DATE," + KEY_OPEN + " REAL,"
                + KEY_CLOSE + " REAL," + KEY_HIGH + " REAL," + KEY_LOW + " REAL," +KEY_COIN_ID+" TEXT" + ")";
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
    public void addCandle(Coin coin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, coin.getName());
        values.put(KEY_OPEN, coin.getSymbol());
        values.put(KEY_CLOSE, coin.getRank());
        values.put(KEY_HIGH, coin.getCurrentPriceUSD());
        values.put(KEY_LOW, coin.getPercentChange1H());
        values.put(KEY_COIN_ID, coin.getId());

        // Inserting Row
        db.insert(TABLE_COINS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public Coin getCoin(int rank) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COINS, new String[] {
                        KEY_DATE,KEY_OPEN, KEY_CLOSE, KEY_HIGH, KEY_LOW}, KEY_DATE + "=?",
                new String[] { String.valueOf(rank) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

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
    public int updateCoin(Coin coin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, coin.getName());
        values.put(KEY_OPEN, coin.getSymbol());
        values.put(KEY_CLOSE, coin.getRank());
        values.put(KEY_HIGH, coin.getCurrentPriceUSD());
        values.put(KEY_LOW, coin.getPercentChange1H());

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
