package cn.kiway.message.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import cn.kiway.message.db.DBHelper;

public class VideoProvider extends ContentProvider {
	/**
	 * 数据库
	 * */
	private DBHelper dbHelper;
	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final int VIDEOS = 1;
	private static final int VIDEO = 2;
	private static final String AUTHOR = "cn.kiway.Yjpty.video";
	public static String VIDEO_URL = "content://" + AUTHOR + "/video";
	public static String VIDEOS_URL = "content://" + AUTHOR + "/video/#";
	static {
		MATCHER.addURI(AUTHOR, "video", VIDEOS);
		MATCHER.addURI(AUTHOR, "video/#", VIDEO);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {
		case VIDEOS:
			count = db.delete(DBHelper.VIDEOLIST_TABLE, selection,
					selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		case VIDEO:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = db.delete(DBHelper.VIDEOLIST_TABLE, where, selectionArgs);
			return count;
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case VIDEOS:
			return "vnd.android.cursor.dir/video";
		case VIDEO:
			return "vnd.android.cursor.item/video";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (MATCHER.match(uri)) {
		case VIDEOS:
			long rowid = db.insert(DBHelper.VIDEOLIST_TABLE, null, values);
			Uri insertUri = ContentUris.withAppendedId(uri, rowid);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return insertUri;
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		switch (MATCHER.match(uri)) {
		case VIDEOS:
			return db.query(DBHelper.VIDEOLIST_TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);
		case VIDEO:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			return db.query(DBHelper.VIDEOLIST_TABLE, projection, where,
					selectionArgs, null, null, sortOrder);
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {
		case VIDEOS:
			count = db.update(DBHelper.VIDEOLIST_TABLE, values, selection,
					selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		case VIDEO:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = db.update(DBHelper.VIDEOLIST_TABLE, values, where,
					selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
		return 0;
	}

}
