
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
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_OWNER = "owner";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TREE = "tree";
    private static final String COLUMN_HASH = "hash";
    private static final String COLUMN_NOTE = "note";

    public static final long FAIL = -1;

    public BookmarkSQliteOpenHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        final String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARK + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_OWNER + " TEXT NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_TREE + " TEXT NOT NULL, " + COLUMN_HASH
                + " TEXT NOT NULL, " + COLUMN_NOTE + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
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
    public long insert(final String owner, final String name, final String tree, final String hash,
            final String note) {
        long id = FAIL;
        final ContentValues contentValues = createContentValues(owner, name, tree, hash, note);
        final SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            id = db.insert(TABLE_BOOKMARK, null, contentValues);
            db.setTransactionSuccessful();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public int update(final long id, final String owner, final String name, final String tree,
            final String hash, final String note) {
        final ContentValues contentValues = createContentValues(owner, name, tree, hash, note);
        final SQLiteDatabase db = getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            result = db.update(TABLE_BOOKMARK, contentValues, COLUMN_ID + " = " + id, null);
            db.setTransactionSuccessful();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public Bookmark[] select() {
        final ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
        final String[] selects = {
                COLUMN_ID, COLUMN_OWNER, COLUMN_NAME, COLUMN_TREE, COLUMN_HASH,
                COLUMN_NOTE
        };
        final SQLiteDatabase db = getReadableDatabase();
        try {
            final Cursor cursor = db.query(TABLE_BOOKMARK, selects, null, null, null, null, null);
            while (cursor.moveToNext()) {
                final Bookmark bookmark = new Bookmark(cursor.getLong(0), cursor.getString(1),
                        cursor
                                .getString(2), cursor.getString(3), cursor.getString(4), cursor
                                .getString(5));
                bookmarks.add(bookmark);
            }
        } finally {
            db.close();
        }

        return bookmarks.toArray(new Bookmark[0]);
    }

    public int delete(final long id) {
        int result = 0;
        final SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            result = db.delete(TABLE_BOOKMARK, COLUMN_ID + " = " + id, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    private ContentValues createContentValues(final String owner, final String name,
            final String tree, final String hash,
            String note) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_OWNER, owner);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_TREE, tree);
        contentValues.put(COLUMN_HASH, hash);
        contentValues.put(COLUMN_NOTE, note);

        return contentValues;
    }
}
