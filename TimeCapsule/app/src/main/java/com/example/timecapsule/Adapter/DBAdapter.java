package com.example.timecapsule.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.timecapsule.Bean.Diary;


public class DBAdapter {

	private static final String DB_NAME = "diary.db";
	private static final String DB_TABLE = "diaryinfo";
	private static final int DB_VERSION = 1;
	 
	public static final String KEY_ID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_DATE = "date";
	public static final String KEY_IMAGE = "image";
	public static final String KEY_LOCK = "lock";
	
	private SQLiteDatabase db;
	private final Context context;
	private DBOpenHelper dbOpenHelper;
	
	public DBAdapter(Context _context) {
	    context = _context;
	  }

	public void close() {
		if (db != null){
			db.close();
			db = null;
		}
	}

	public void open() throws SQLiteException {
		dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
		try {
			db = dbOpenHelper.getWritableDatabase();
		}
		catch (SQLiteException ex) {
			db = dbOpenHelper.getReadableDatabase();
		}
	}
	  
	
	public long insert(Diary diary) {
	    ContentValues newValues = new ContentValues();
	    newValues.put(KEY_TITLE, diary.getTitle());
	    newValues.put(KEY_CONTENT, diary.getContent());
	    newValues.put(KEY_DATE, diary.getDate());
	    newValues.put(KEY_IMAGE, diary.getImage());
	    newValues.put(KEY_LOCK, diary.getLock());
	    return db.insert(DB_TABLE, null, newValues);
	}

	public Diary[] queryAllData() {
		Cursor results =  db.query(DB_TABLE, new String[] { KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_IMAGE, KEY_LOCK},
				  null, null, null, null, null);
		return ConvertToDiaries(results);
	}
	  
	public Diary[] queryOneData(long id) {
		Cursor results =  db.query(DB_TABLE, new String[] { KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_IMAGE, KEY_LOCK},
				  KEY_ID + "=" + id, null, null, null, null);
		return ConvertToDiaries(results);
	}
	  
	private Diary[] ConvertToDiaries(Cursor cursor){
		int resultCounts = cursor.getCount();
		if (resultCounts == 0 || !cursor.moveToFirst())
			return null;

		Diary[] diaries = new Diary[resultCounts];
		for (int i = 0 ; i < resultCounts; i++){
			diaries[i] = new Diary();
			diaries[i].setID(cursor.getInt(0));
			int titleIndex = cursor.getColumnIndex(KEY_TITLE);
			int contentIndex = cursor.getColumnIndex(KEY_CONTENT);
			int dateIndex = cursor.getColumnIndex(KEY_DATE);
			int imageIndex = cursor.getColumnIndex(KEY_IMAGE);
			int lockIndex = cursor.getColumnIndex(KEY_LOCK);
			assert titleIndex >= 0 && contentIndex >= 0 && dateIndex >= 0 && imageIndex >= 0 && lockIndex >=0;
			diaries[i].setTitle(cursor.getString(titleIndex));
			diaries[i].setContent(cursor.getString(contentIndex));
			diaries[i].setDate(cursor.getString(dateIndex));
			diaries[i].setImage(cursor.getString(imageIndex));
			diaries[i].setLock(cursor.getString(lockIndex));
			cursor.moveToNext();
		}
		return diaries;
	}
	  
	public long deleteAllData() {
		return db.delete(DB_TABLE, null, null);
	}

	public long deleteOneData(long id) {
		return db.delete(DB_TABLE,  KEY_ID + "=" + id, null);
	}

	public long updateOneData(long id, Diary diary){
		ContentValues updateValues = new ContentValues();
		updateValues.put(KEY_TITLE, diary.getTitle());
		updateValues.put(KEY_CONTENT, diary.getContent());
		updateValues.put(KEY_DATE, diary.getDate());
		updateValues.put(KEY_IMAGE, diary.getImage());
		updateValues.put(KEY_LOCK, diary.getLock());
		return db.update(DB_TABLE, updateValues,  KEY_ID + "=" + id, null);
	}
	  

	private static class DBOpenHelper extends SQLiteOpenHelper {

		public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		private static final String DB_CREATE = "create table " +
				DB_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
				KEY_TITLE + " text not null, " + KEY_CONTENT + " text not null, " +
				KEY_DATE + " text not null, " + KEY_IMAGE + " text not null, " +
				KEY_LOCK + " text not null);";

		@Override
		public void onCreate(SQLiteDatabase _db) {
		    _db.execSQL(DB_CREATE);
		  }

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			_db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		    onCreate(_db);
		}
	}
}