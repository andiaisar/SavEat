package com.example.saveat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.saveat.model.BahanHariIni;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "saveat.db";
    private static final int DATABASE_VERSION = 3;

    // ... (Definisi nama tabel dan kolom tetap sama)
    private static final String TABEL_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";

    private static final String TABEL_BAHAN = "bahan_hari_ini";
    private static final String COLUMN_BAHAN_ID = "id";
    private static final String COLUMN_NAMA_BAHAN = "nama";
    private static final String COLUMN_JUMLAH = "jumlah";
    private static final String COLUMN_SATUAN = "satuan";
    private static final String COLUMN_KADALUARSA = "kadaluarsa";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_CATEGORY = "category";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABEL_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT , " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT UNIQUE" +
                    ")";

    private static final String CREATE_TABLE_BAHAN =
            "CREATE TABLE " + TABEL_BAHAN + "(" +
                    COLUMN_BAHAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAMA_BAHAN + " TEXT NOT NULL," +
                    COLUMN_JUMLAH + " INTEGER NOT NULL," +
                    COLUMN_SATUAN + " TEXT NOT NULL," +
                    COLUMN_KADALUARSA + " INTEGER NOT NULL," +
                    COLUMN_USER_ID + " INTEGER NOT NULL," +
                    COLUMN_IMAGE_PATH + " TEXT," +
                    COLUMN_CATEGORY + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABEL_USERS + "(" + COLUMN_ID + ")" +
                    ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BAHAN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABEL_BAHAN + " ADD COLUMN " + COLUMN_IMAGE_PATH + " TEXT;");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABEL_BAHAN + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT;");
        }
    }

    public long adduser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);

        long id = db.insert(TABEL_USERS, null, values);
        // HAPUS: db.close();
        return id;
    }

    public boolean cekEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABEL_USERS, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        // HAPUS: db.close();
        return exists;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABEL_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        // HAPUS: db.close();
        return exists;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABEL_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        }
        cursor.close();
        // HAPUS: db.close();
        return name;
    }

    public long tambahBahan(String nama, int jumlah, String satuan, long kadaluarsaTimestamp, long userId, String imagePath, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA_BAHAN, nama);
        values.put(COLUMN_JUMLAH, jumlah);
        values.put(COLUMN_SATUAN, satuan);
        values.put(COLUMN_KADALUARSA, kadaluarsaTimestamp);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_CATEGORY, category);

        long id = db.insert(TABEL_BAHAN, null, values);
        // HAPUS: db.close();
        return id;
    }

    public List<BahanHariIni> getAllBahanHariIni(long userId) {
        List<BahanHariIni> bahanList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABEL_BAHAN +
                " WHERE " + COLUMN_USER_ID + " = ?" +
                " ORDER BY " + COLUMN_KADALUARSA + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BAHAN_ID));
                String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BAHAN));
                int jumlah = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JUMLAH));
                String satuan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SATUAN));
                Date kadaluarsa = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_KADALUARSA)));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));

                BahanHariIni bahan = new BahanHariIni(id, nama, jumlah, satuan, kadaluarsa, imagePath, category);
                bahanList.add(bahan);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // HAPUS: db.close();
        return bahanList;
    }

    public boolean hapusBahan(long bahanId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABEL_BAHAN, COLUMN_BAHAN_ID + " = ?",
                new String[]{String.valueOf(bahanId)});
        // HAPUS: db.close();
        return rowsAffected > 0;
    }

    public boolean updateBahan(long bahanId, String nama, int jumlah, String satuan, long kadaluarsa, String imagePath, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA_BAHAN, nama);
        values.put(COLUMN_JUMLAH, jumlah);
        values.put(COLUMN_SATUAN, satuan);
        values.put(COLUMN_KADALUARSA, kadaluarsa);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_CATEGORY, category);

        int rowsAffected = db.update(TABEL_BAHAN, values,
                COLUMN_BAHAN_ID + " = ?",
                new String[]{String.valueOf(bahanId)});
        // HAPUS: db.close();
        return rowsAffected > 0;
    }

    public BahanHariIni getBahanById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABEL_BAHAN,
                null,
                COLUMN_BAHAN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        BahanHariIni bahan = null;
        if (cursor != null && cursor.moveToFirst()) {
            bahan = new BahanHariIni(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BAHAN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BAHAN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JUMLAH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SATUAN)),
                    new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_KADALUARSA))),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            );
            cursor.close();
        }
        // HAPUS: if (db != null) { db.close(); }
        return bahan;
    }
}