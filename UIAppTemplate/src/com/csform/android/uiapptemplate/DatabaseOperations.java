/*
 * This class contain necessary database operation e.g: update data, remove data etc... needed in application
 */

package com.csform.android.uiapptemplate;

import java.util.ArrayList;

import com.csform.android.uiapptemplate.TableData.TableInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOperations extends SQLiteOpenHelper {

	public static final int database_version = 5;
	public String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TableInfo.TABLE_NAME + "("
			+ TableInfo.ID + " TEXT PRIMARY KEY," + TableInfo.IDENTIFIER
			+ " TEXT," + TableInfo.TITLE + " TEXT ," + TableInfo.DESCRIPTION
			+ " TEXT ," + TableInfo.TYPE_NAME + " TEXT ," + TableInfo.ADDRESS
			+ " TEXT ," + TableInfo.LAT + " REAL ," + TableInfo.LNG + " REAL ,"
			+ TableInfo.IMAGE0 + " TEXT ," + TableInfo.IMAGE1 + " TEXT ,"
			+ TableInfo.IMAGE2 + " TEXT ," + TableInfo.USER_LOGIN + " TEXT ,"
			+ TableInfo.DATE_OCCURED + " TEXT );";

	public String CREATE_QUERY1 = "CREATE TABLE IF NOT EXISTS "
			+ TableInfo.TABLE_NAME_CATEGORY + "(" + TableInfo.IDCATEGORY
			+ " TEXT PRIMARY KEY," + TableInfo.TYPE_NAME_CATEGORY + " TEXT);";

	public DatabaseOperations(Context context) {
		super(context, TableInfo.DATABASE_NAME, null, database_version);
		Log.d("database operation", "database created");
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		db.execSQL(CREATE_QUERY);
		db.execSQL(CREATE_QUERY1);
	}
	@Override
	public void onCreate(SQLiteDatabase sdb) {
		// TODO Auto-generated method stub
		sdb.execSQL(CREATE_QUERY);
		sdb.execSQL(CREATE_QUERY1);
		
		Log.d("database operation", "table created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub


		db.delete(TableInfo.TABLE_NAME, null, null);
		db.delete(TableInfo.TABLE_NAME_CATEGORY, null, null);
		db.execSQL("delete from "+ TableInfo.TABLE_NAME);
		db.execSQL("delete from "+ TableInfo.TABLE_NAME_CATEGORY);
	}

	public long putInformation(DatabaseOperations dop, String id,
			String identifier, String title, String description,
			String type_name, String address, double lat, double lng,
			String image0, String image1, String image2, String user_login,
			String date_occured) {
		try {
			SQLiteDatabase SQ = dop.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(TableInfo.ID, id);
			cv.put(TableInfo.IDENTIFIER, identifier);
			cv.put(TableInfo.TITLE, title);
			cv.put(TableInfo.DESCRIPTION, description);
			cv.put(TableInfo.TYPE_NAME, type_name);
			cv.put(TableInfo.ADDRESS, address);
			cv.put(TableInfo.LAT, lat);
			cv.put(TableInfo.LNG, lng);
			cv.put(TableInfo.IMAGE0, image0);
			cv.put(TableInfo.IMAGE1, image1);
			cv.put(TableInfo.IMAGE2, image2);
			cv.put(TableInfo.USER_LOGIN, user_login);
			cv.put(TableInfo.DATE_OCCURED, date_occured);
			long k = SQ.insert(TableInfo.TABLE_NAME, null, cv);
			Log.d("database operation", "one row inserted");
			return k;
		} catch (Exception ex) {
			return -1;
		}
	}

	public long putInformationCategory(DatabaseOperations dop, String id,String type_name) {
		try {
			SQLiteDatabase SQ = dop.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(TableInfo.IDCATEGORY, id);
		
			cv.put(TableInfo.TYPE_NAME_CATEGORY, type_name);
			long k = SQ.insert(TableInfo.TABLE_NAME_CATEGORY, null, cv);
			Log.d("database operation", "one row inserted");
			return k;
		} catch (Exception ex) {
			return -1;
		}
	}



	public Cursor getInformationCategory(DatabaseOperations dop) {
		
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TableInfo.TABLE_NAME_CATEGORY;
		Cursor c = SQ.rawQuery(selectQuery, null);
		return c;

	}

	public Cursor getInformationByLatLng(DatabaseOperations dop,double lat,double lng)
	{
		String s=String.valueOf(lat)+" "+String.valueOf(lng);
		Log.e("database select",s );
		SQLiteDatabase SQ=dop.getReadableDatabase();
		String selectQuery="SELECT * FROM "+TableInfo.TABLE_NAME+" WHERE "+TableInfo.LAT+ " ="+lat+" AND +"+TableInfo.LNG+"="+lng;
		Cursor c = SQ.rawQuery(selectQuery,null);
		return c;
		
		
	}
	public Cursor getInformation(DatabaseOperations dop, double bottom,
			double top, double left, double right) {
		String s = String.valueOf(bottom) + " " + String.valueOf(top) + " "
				+ String.valueOf(left) + " " + String.valueOf(right);
		Log.e("database select", s);
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TableInfo.TABLE_NAME
				+ " WHERE " + TableInfo.LAT + " BETWEEN " + bottom + " AND "
				+ top + " and " + TableInfo.LNG + " BETWEEN " + left + " AND "
				+ right + " ORDER BY " + TableInfo.TYPE_NAME + " DESC";
		Cursor c = SQ.rawQuery(selectQuery, null);
		return c;

	}

	public Cursor getCrimeType(DatabaseOperations dop, double bottom,
			double top, double left, double right) {
		String query = "SELECT count(*)," + TableInfo.TYPE_NAME + " FROM "
				+ TableInfo.TABLE_NAME + " WHERE " + TableInfo.LAT + " BETWEEN " + bottom + " AND "
						+ top + " and " + TableInfo.LNG + " BETWEEN " + left + " AND "
						+ right +  " GROUP BY " + TableInfo.TYPE_NAME
				+ " ORDER BY " + TableInfo.TYPE_NAME + " DESC";
		SQLiteDatabase SQ = dop.getReadableDatabase();
		Cursor c = SQ.rawQuery(query, null);
		return c;
	}

}
