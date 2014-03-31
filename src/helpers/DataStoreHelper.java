package helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import models.Contact;
import models.LocationTag;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.text.Editable;
import android.util.Log;

 
public class DataStoreHelper extends SQLiteOpenHelper {
 
    private static final int DATABASE_VERSION = 1;
 
    private static final String DATABASE_NAME = "contactsManager";
 
    private static final String TABLE_CONTACTS = "contacts";

    private static final String TABLE_LOCATION_TAGS = "location_tags";
 
    private static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phone_number";
 
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    
    public DataStoreHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TAGS);
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";
        
        String CREATE_LOCATION_TAGS_TABLE = "CREATE TABLE " + TABLE_LOCATION_TAGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DESCRIPTION + " TEXT,"
                + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT" + ")";
        
        
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_LOCATION_TAGS_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TAGS);
        onCreate(db);
    }
 
    public boolean addContact(Contact contact) {
        if(getContact(contact.getPhoneNumber())!=null)
        	return false;
    	SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); 
        values.put(KEY_PH_NO, contact.getPhoneNumber());
 
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
        return true;
    }
 
    public Contact getContact(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_NAME, KEY_PH_NO }, KEY_PH_NO + "=?",
                new String[] { phoneNumber }, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
            		cursor.getString(1), cursor.getString(2));
            return contact;
        }
        else
        	return null;
    }
     
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 
        return contactList;
    }
 
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());
 
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }
 
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
 
 
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        return cursor.getCount();
    }

	public Cursor getAllContactsAdapter() {
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public boolean addLocationTag(LocationTag locationTag) {
		if(getLocationTag(locationTag.getDescription())!=null)
        	return false;
    	SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, locationTag.getDescription()); 
        values.put(KEY_LATITUDE, locationTag.getLatitude());
        values.put(KEY_LONGITUDE, locationTag.getLongitude()); 
        db.insert(TABLE_LOCATION_TAGS, null, values);
        db.close();
        return true;
		
	}

	private Object getLocationTag(String description) {
		SQLiteDatabase db = this.getReadableDatabase();
		description = description.toLowerCase(Locale.getDefault()); 
        Cursor cursor = db.query(TABLE_LOCATION_TAGS, new String[] { KEY_ID,
                KEY_DESCRIPTION, KEY_LATITUDE, KEY_LONGITUDE }, KEY_DESCRIPTION + "=?",
                new String[] { description }, null, null, null, null);
        
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
            		cursor.getString(1), cursor.getString(2));
            return contact;
        }
        else
        	return null;
	}

	public Cursor getAllLocationTagsAdapter() {
		String selectQuery = "SELECT  * FROM " + TABLE_LOCATION_TAGS;
		 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;

	}
 
}
