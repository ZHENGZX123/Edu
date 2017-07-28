package cn.kiway.message.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	/**
	 * 数据库的名称
	 * */
	public static final String DB_NAME = "Yiptj";
	/**
	 * 数据库版本
	 * */
	public static final int DB_VERSION = 1;

	/**
	 * 消息数据表
	 * */
	public static final String MESSAGE_TABLE = "message_table";
	/**
	 * 消息列表数据
	 * */
	public static final String MESSAGELIST_TABLE = "messagelist_table";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ MESSAGE_TABLE
				+ " (_id integer primary key autoincrement,_uid integer,_msgid integer,_name text,_content text,_time integer,_url text,_touid integer,_userid integer,_msgtype integer,_msghome integer,_msnotify integer,_msgctype integer,_msgpic text,_statue integer,_username text,_mid integer,_imge blob)");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ MESSAGELIST_TABLE
				+ " (_id integer primary key autoincrement,_uid integer,_msgid integer,_name text,_content text,_time integer,_url text,_unread integer,_touid integer,_userid integer,_msgtype integer,_msghome integer,_msnotify integer,_msgctype integer,_statue integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + MESSAGELIST_TABLE);
			onCreate(db);
		}
	}

}
