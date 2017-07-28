package cn.kiway.message.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import cn.kiway.message.db.DBHelper;

/**
 * 消息内容提供者
 * */
public class MessageProvider extends ContentProvider {
	/**
	 * 数据库
	 * */
	private DBHelper dbHelper;
	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final int MESSAGES = 1;
	private static final int MESSAGE = 2;
	private static final String AUTHOR = "cn.kiway.Yjpty.message";
	public static String MESSAGES_URL = "content://" + AUTHOR + "/message";
	public static String MESSAGE_URL = "content://" + AUTHOR + "/message/#";
	static {
		MATCHER.addURI(AUTHOR, "message", MESSAGES);
		MATCHER.addURI(AUTHOR, "message/#", MESSAGE);
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
		case MESSAGES:
			return db.query(DBHelper.MESSAGE_TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);
		case MESSAGE:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			return db.query(DBHelper.MESSAGE_TABLE, projection, where,
					selectionArgs, null, null, sortOrder);

		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {
		case MESSAGES:
			count = db.delete(DBHelper.MESSAGE_TABLE, selection, selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		case MESSAGE:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = db.delete(DBHelper.MESSAGE_TABLE, where, selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case MESSAGES:
			return "vnd.android.cursor.dir/message";
		case MESSAGE:
			return "vnd.android.cursor.item/message";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (MATCHER.match(uri)) {
		case MESSAGES:
			long rowid = db.insert(DBHelper.MESSAGE_TABLE, null, values);
			Uri intsertUri = ContentUris.withAppendedId(uri, rowid);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return intsertUri;
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {
		case MESSAGES:
			count = db.update(DBHelper.MESSAGE_TABLE, values, selection,
					selectionArgs);
			return count;
		case MESSAGE:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = db.update(DBHelper.MESSAGE_TABLE, values, where,
					selectionArgs);
			return count;
		}
		return 0;
	}

}
