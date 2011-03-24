package net.flaxia.android.githubviewer.util;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.model.Bookmark;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarkSQliteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "gitHubViewer.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_BOOKMARK = "bookmark";
    public static final String COLUMN_ID = "_id";
    private static final String COLUMN_OWNER = "owner";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TREE = "tree";
    private static final String COLUMN_HASH = "hash";
    private static final String COLUMN_NOTE = "note";

    public static final long FAIL = -1;

    public BookmarkSQliteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARK + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_OWNER + " TEXT NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_TREE + " TEXT NOT NULL, " + COLUMN_HASH
                + " TEXT NOT NULL, " + COLUMN_NOTE + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * ブックマークを追加する
     * 
     * @param owner
     * @param name
     * @param tree
     * @param hash
     * @param memo
     * @return
     */
    public long insert(String owner, String name, String tree, String hash, String note) {
        long id = FAIL;
        ContentValues contentValues = createContentValues(owner, name, tree, hash, note);
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

    public int update(long id, String owner, String name, String tree, String hash, String note) {
        ContentValues contentValues = createContentValues(owner, name, tree, hash, note);
        SQLiteDatabase db = getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            result = db.update(TABLE_BOOKMARK, contentValues, COLUMN_ID + " = " + id, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public Bookmark[] select() {
        ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
        String[] selects = { COLUMN_ID, COLUMN_OWNER, COLUMN_NAME, COLUMN_TREE, COLUMN_HASH,
                COLUMN_NOTE };
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_BOOKMARK, selects, null, null, null, null, null);
            while (cursor.moveToNext()) {
                Bookmark bookmark = new Bookmark(cursor.getLong(0), cursor.getString(1), cursor
                        .getString(2), cursor.getString(3), cursor.getString(4));
                bookmarks.add(bookmark);
            }
        } finally {
            db.close();
        }

        return bookmarks.toArray(new Bookmark[0]);
    }

    private ContentValues createContentValues(String owner, String name, String tree, String hash,
            String note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_OWNER, owner);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_TREE, tree);
        contentValues.put(COLUMN_HASH, hash);
        contentValues.put(COLUMN_NOTE, note);

        return contentValues;
    }
}
