package net.flaxia.android.githubviewer.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarkSQliteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "gitHubViewer.db";
    private static final int DB_VERSION = 1;

    public BookmarkSQliteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmark (_id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, name TEXT NOT NULL, tree TEXT NOT NULL, hash TEXT NOT NULL, memo TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
