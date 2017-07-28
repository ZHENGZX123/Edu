package cn.kiway.message.model;

import cn.kiway.message.db.DBHelper;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.UriMatcher;
/**
 * 消息列表内容提供者
 * */
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MessageChatProvider extends ContentProvider {
	/**
	 * 数据库
	 * */
	private DBHelper dbHelper;
	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final int MESSAGECHATS = 1;
	private static final int MESSAGECHAT = 2;
	private static final String AUTHOR = "cn.kiway.Yjptj.messagechat";
	public static String MESSAGECHATS_URL = "content://" + AUTHOR
			+ "/messagechat";
	public static String MESSAGECHAT_URL = "content://" + AUTHOR
			+ "/messagechat/#";
	static {
		MATCHER.addURI(AUTHOR, "messagechat", MESSAGECHATS);
		MATCHER.addURI(AUTHOR, "messagechat/#", MESSAGECHAT);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {
		case MESSAGECHATS:
			count = db.delete(DBHelper.MESSAGELIST_TABLE, selection,
					selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		case MESSAGECHAT:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = db.delete(DBHelper.MESSAGELIST_TABLE, where, selectionArgs);
			return count;
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case MESSAGECHATS:
			return "vnd.android.cursor.dir/messagechat";
		case MESSAGECHAT:
			return "vnd.android.cursor.item/messagechat";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (MATCHER.match(uri)) {
		case MESSAGECHATS:
			long rowid = db.insert(DBHelper.MESSAGELIST_TABLE, null, values);
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
		case MESSAGECHATS:
			return db.query(DBHelper.MESSAGELIST_TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);
		case MESSAGECHAT:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			return db.query(DBHelper.MESSAGELIST_TABLE, projection, where,
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
		case MESSAGECHATS:
			count = db.update(DBHelper.MESSAGELIST_TABLE, values, selection,
					selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		case MESSAGECHAT:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = db.update(DBHelper.MESSAGELIST_TABLE, values, where, selectionArgs);
			this.getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
		return 0;
	}

}
