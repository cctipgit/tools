package com.rate.quiz.database;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rate.quiz.entity.QuotesItem;


public class QuotesDataBase
{
	private static SQLiteDatabase readableDatabase;
	private static SQLiteDatabase writableDatabase;

	static class QuotesDatebaseHelper extends SQLiteOpenHelper
	{
		private static final String DATABASE_NAME = "QuotesDB";
		private static final int DATABASE_VERSION = 3;

		private static final String TABLE_USER_QUOTES_ALL_CREATE = "create table UserAllQuotes ( _id integer primary key, " +
				"mid text not null, " +
				"cmd text, " +
				"amount text, " +
				"priceTime text, " +
				"tokenFrom text, " +
				"tokenTo text, " +
				"dateUnit text, " +
				"data text, " +
				"priceFrom text, " +
				"childPriceTo text, " +
				"childPriceFrom text, " +
				"childPriceDate text, " +
				"priceTo text);";

		public QuotesDatebaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database)
		{
			database.execSQL(TABLE_USER_QUOTES_ALL_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
		{
			Log.w(QuotesDatebaseHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS UserAllQuotes");
			onCreate(database);
		}
	}

	public static void initDatabase(Context context)
	{
		if (readableDatabase == null || writableDatabase == null)
		{
			synchronized (QuotesDataBase.class)
			{
				if (readableDatabase == null || writableDatabase == null)
				{
					QuotesDatebaseHelper helper = new QuotesDatebaseHelper(context);
					readableDatabase = helper.getReadableDatabase();
					writableDatabase = helper.getWritableDatabase();
				}
			}
		}
	}
	public static void updateByTokenPair(Context context,QuotesItem quotesItem, String tokenFrom, String tokenTo, String dateUtit,String childPriceDate){
		if (writableDatabase == null)
		{
			initDatabase(context);
		}
		writableDatabase.beginTransaction();
		ContentValues contentValues = new ContentValues();
		contentValues.put("mid", quotesItem.mid);
		contentValues.put("cmd", quotesItem.cmd);
		contentValues.put("amount", quotesItem.amount);
		contentValues.put("priceTime", quotesItem.price_time);
		contentValues.put("tokenFrom", quotesItem.tokenFrom);
		contentValues.put("tokenTo", quotesItem.tokenTo);
		contentValues.put("dateUnit",quotesItem.dateUnit);
		contentValues.put("data", quotesItem.data);
		contentValues.put("priceFrom", quotesItem.priceFrom);
		contentValues.put("priceTo", quotesItem.priceTo);
		contentValues.put("childPriceDate", quotesItem.childPriceDate);
		contentValues.put("childPriceTo", quotesItem.childPriceTo);
		contentValues.put("childPriceFrom", quotesItem.childPriceFrom);
		writableDatabase.update("UserAllQuotes",contentValues,"tokenFrom='"+tokenFrom+"' and tokenTo='"+tokenTo+"' and dateUnit='"+dateUtit+"' and childPriceDate='"+childPriceDate+"'",null);
		writableDatabase.setTransactionSuccessful();
		writableDatabase.endTransaction();
	}
	public static void clearByTokenPairPoint(Context context, String tokenFrom, String tokenTo, String dateUtil,String childPriceDate) {
		if (writableDatabase == null)
		{
			initDatabase(context);
		}

		long id =writableDatabase.delete("UserAllQuotes","tokenFrom='"+tokenFrom+"' and tokenTo='"+tokenTo+"' and dateUnit='"+dateUtil+"' and childPriceDate='"+childPriceDate+"'",null);
		if (id == -1) {
			Log.i("-->>", "clearByTokenPairPoint delete failed: ");
		}else {
			Log.i("-->>", "clearByTokenPairPoint : "+id);

		}
	}
	public static void clearByTokenPair(Context context, String tokenFrom, String tokenTo, String dateUtil) {
		if (writableDatabase == null)
		{
			initDatabase(context);
		}

		long id = writableDatabase.delete("UserAllQuotes","tokenFrom='"+tokenFrom+"' and tokenTo='"+tokenTo+"' and dateUnit='"+dateUtil+"'",null);
		if (id == -1) {
			Log.i("-->>", "clearByTokenPairPoint1 delete failed: "+"tokenFrom='"+tokenFrom+"' and tokenTo='"+tokenTo+"' and dateUnit='"+dateUtil+"'");
		}else {
			Log.i("-->>", "clearByTokenPairPoint1 : "+id +"tokenFrom='"+tokenFrom+"' and tokenTo='"+tokenTo+"' and dateUnit='"+dateUtil+"'");

		}
	}
	public static ArrayList<QuotesItem> queryQuotesListByCategoryName(Context context, String tokenFrom, String tokenTo,String dateUnit)
	{
		ArrayList<QuotesItem> productList = new ArrayList<QuotesItem>();
		if (readableDatabase == null)
		{
			initDatabase(context);
		}
		Cursor c = readableDatabase.query("UserAllQuotes", null, "tokenFrom=? and tokenTo=? and dateUnit=?", new String[] {tokenFrom,tokenTo,dateUnit}, null, null, null);
		if (c != null && c.moveToFirst())
		{
			int midIndex = c.getColumnIndex("mid");
			int cmdIndex = c.getColumnIndex("cmd");
			int amountIndex = c.getColumnIndex("amount");
			int tokenFromIndex = c.getColumnIndex("tokenFrom");
			int tokenToIndex = c.getColumnIndex("tokenTo");
			int dateUnitIndex = c.getColumnIndex("dateUnit");
			int dataIndex = c.getColumnIndex("data");
			int priceFromIndex = c.getColumnIndex("priceFrom");
			int priceToIndex = c.getColumnIndex("priceTo");
			int childPriceToIndex = c.getColumnIndex("childPriceTo");
			int childPriceFromIndex = c.getColumnIndex("childPriceFrom");
			int childPriceDateIndex = c.getColumnIndex("childPriceDate");
			int priceTimeIndex = c.getColumnIndex("priceTime");

			do
			{
				QuotesItem product = new QuotesItem();
				product.price_time = c.getString(priceTimeIndex);
				product.mid = c.getString(midIndex);
				product.cmd =  c.getString(cmdIndex);
				product.amount =  c.getString(amountIndex);
				product.tokenFrom = c.getString(tokenFromIndex);
				product.tokenTo=c.getString(tokenToIndex);
				product.dateUnit = c.getString(dateUnitIndex);
				product.data = c.getString(dataIndex);
				product.priceFrom = c.getString(priceFromIndex);
				product.priceTo = c.getString(priceToIndex);
				product.childPriceTo = c.getString(childPriceToIndex);
				product.childPriceFrom = c.getString(childPriceFromIndex);
				product.childPriceDate = c.getString(childPriceDateIndex);
				if (!productList.contains(product))
				{
					productList.add(product);
				}
			} while (c.moveToNext());
			c.close();
		}
		return productList;
	}

	public static void clearAllQuotes(Context context)
	{
		if (writableDatabase == null)
		{
			initDatabase(context);
		}

		writableDatabase.delete("UserAllQuotes",null,null);
	}

	public static void addUserAllQuotes(Context context, ArrayList<QuotesItem> products)
	{
		if (writableDatabase == null)
		{
			initDatabase(context);
		}
		writableDatabase.beginTransaction();
		for (QuotesItem product : products)
		{
			ContentValues contentValues = new ContentValues();

			contentValues.put("mid", product.mid);
			contentValues.put("cmd", product.cmd);
			contentValues.put("amount", product.amount);

			contentValues.put("priceTime", product.price_time);
			contentValues.put("tokenFrom", product.tokenFrom);
			contentValues.put("tokenTo", product.tokenTo);
			contentValues.put("dateUnit", product.dateUnit);
			contentValues.put("data", product.data);
			contentValues.put("priceFrom", product.priceFrom);
			contentValues.put("priceTo", product.priceTo);

			contentValues.put("childPriceTo", product.childPriceTo);
			contentValues.put("childPriceFrom", product.childPriceFrom);
			contentValues.put("childPriceDate", product.childPriceDate);

			long id = writableDatabase.insert("UserAllQuotes", null, contentValues);
			if (id == -1)
			{
				Log.w("-->>", "Failed to insert sell records");
			}
		}
		writableDatabase.setTransactionSuccessful();
		writableDatabase.endTransaction();
		Log.i("-->>", "addUserAllQuotes size:"+products.size());

	}
}

