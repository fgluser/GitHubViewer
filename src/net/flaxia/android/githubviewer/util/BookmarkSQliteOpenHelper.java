package net.flaxia.android.githubviewer.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarkSQliteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "gitHubViewer.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_BOOKMARK = "bookmark";
    private static final String COLUMN_OWNER = "owner";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TREE = "tree";
    private static final String COLUMN_HASH = "hash";
    private static final String COLUMN_MEMO = "memo";
    
    public static final long FAIL = -1;

    public BookmarkSQliteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS" + TABLE_BOOKMARK
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_OWNER + " TEXT NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_TREE + " TEXT NOT NULL, " + COLUMN_HASH
                + " TEXT NOT NULL, " + COLUMN_MEMO + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * ブックマークを追加する
     * @param owner
     * @param name
     * @param tree
     * @param hash
     * @param memo
     * @return
     */
    public long add(String owner, String name, String tree, String hash, String memo) {
        long id = FAIL;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_OWNER, owner);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_TREE, tree);
        contentValues.put(COLUMN_HASH, hash);
        contentValues.put(COLUMN_MEMO, memo);

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            id = db.insert(TABLE_BOOKMARK, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return id;
    }
}
