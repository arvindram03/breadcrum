package helpers.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.Geofence;

import models.Contact;
import models.SimpleGeofence;
import models.MessageLog;
import models.UserPosition;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

 
public class DataStoreHelper extends SQLiteOpenHelper {
 
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
 
    private static final String TABLE_CONTACTS = "contacts";
 
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phone_number";
    
    private static final String TABLE_SIMPLE_GEOFENCES = "simple_geofences";
    
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_EXPIRATION_DURATION = "expiration_duration";
    public static final String KEY_TRANSITION_TYPE = "transition_type";

	private static final String TABLE_MESSAGE_LOGS = "message_logs";

	public static final String KEY_RECEIVER_NAME = "receiver_name";
	public static final String KEY_RECEIVER_PHONE = "receiver_phone_number";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_TIMESTAMP = "timestamp";

	private static final String TABLE_CURRENT_GEOFENCE_STATE = "current_geofence_state";
	private static final String KEY_GEOFENCE_NAME = "geofence_name";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
    public DataStoreHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";
        
        String CREATE_LOCATION_TAGS_TABLE = "CREATE TABLE " + TABLE_SIMPLE_GEOFENCES + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL,"
                + KEY_RADIUS + " REAL," + KEY_EXPIRATION_DURATION + " INTEGER,"
                + KEY_TRANSITION_TYPE + " INTEGER" + ")";
        
        String CREATE_MESSAGE_LOGS_TABLE = "CREATE TABLE " + TABLE_MESSAGE_LOGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_RECEIVER_NAME + " TEXT,"
                + KEY_RECEIVER_PHONE + " TEXT," + KEY_CONTENT + " TEXT,"
                + KEY_TIMESTAMP + " DATETIME"+ ")";
        
        String CREATE_CURRENT_GEOFENCE_STATE_TABLE = "CREATE TABLE " + TABLE_CURRENT_GEOFENCE_STATE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GEOFENCE_NAME + " TEXT,"
                + KEY_TRANSITION_TYPE + " TEXT, "
                + KEY_TIMESTAMP + " DATETIME"+ ")";
        
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_LOCATION_TAGS_TABLE);
        db.execSQL(CREATE_MESSAGE_LOGS_TABLE);
        db.execSQL(CREATE_CURRENT_GEOFENCE_STATE_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TAGS);
        //onCreate(db);
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
    	String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY "+KEY_NAME+" ASC";
    	 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
        	do{
            Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
            		cursor.getString(1), cursor.getString(2));
            if(phoneNumber.contains(contact.getPhoneNumber()) || contact.getPhoneNumber().contains(phoneNumber))
            	return contact;
        	}while(cursor.moveToNext());
        }
        return null;
    }
     
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY "+KEY_NAME+" ASC";
 
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
 
        int result = db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        return result;
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

    public boolean addSimpleGeofence(SimpleGeofence geofence) {
		if(getSimpleGeofence(geofence.getId())!=null)
        	return false;
    	SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_ID, geofence.getId()); 
        values.put(KEY_LATITUDE, geofence.getLatitude());
        values.put(KEY_LONGITUDE, geofence.getLongitude());
        values.put(KEY_RADIUS, geofence.getRadius());
        values.put(KEY_EXPIRATION_DURATION, geofence.getExpirationDuration());
        values.put(KEY_TRANSITION_TYPE, geofence.getTransitionType());
        db.insert(TABLE_SIMPLE_GEOFENCES, null, values);
        return true;
		
	}

	private SimpleGeofence getSimpleGeofence(String description) {
		SQLiteDatabase db = this.getReadableDatabase();
		description = description.toLowerCase(Locale.getDefault()); 
        Cursor cursor = db.query(TABLE_SIMPLE_GEOFENCES, new String[] {
                KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_RADIUS, KEY_EXPIRATION_DURATION, KEY_TRANSITION_TYPE }, KEY_ID + "=?",
                new String[] { description }, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            SimpleGeofence simpleGeofence = new SimpleGeofence(cursor.getString(0),cursor.getDouble(1),cursor.getDouble(2),cursor.getFloat(3), cursor.getLong(4),cursor.getInt(5));
            return simpleGeofence;
        }
        else
        	return null;
	}

	public void addMessageLog(MessageLog messageLog) {
		SQLiteDatabase db = this.getWritableDatabase();
	    
	    ContentValues values = new ContentValues();
	    values.put(KEY_RECEIVER_NAME, messageLog.getReceiverName()); 
	    values.put(KEY_RECEIVER_PHONE, messageLog.getReceiverPhoneNumber());
	    values.put(KEY_CONTENT, messageLog.getContent());
	    
	    values.put(KEY_TIMESTAMP, dateFormat.format(messageLog.getTimestamp()));
	    db.insert(TABLE_MESSAGE_LOGS, null, values);
	}
	
	public List<Geofence> getAllGeofences(){
	List<Geofence> geofenceList = new ArrayList<Geofence>();
    String selectQuery = "SELECT  * FROM " + TABLE_SIMPLE_GEOFENCES;

    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
        do {
        	SimpleGeofence simpleGeofence = new SimpleGeofence(cursor.getString(0),cursor.getDouble(1),cursor.getDouble(2),cursor.getFloat(3), cursor.getLong(4),cursor.getInt(5));
            geofenceList.add(simpleGeofence.toGeofence());
        } while (cursor.moveToNext());
    }
    return geofenceList;
	}

	public void updateCurrentGeofenceState(String ids, String transitionType) {
		SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	java.util.Date now = new java.util.Date();
    	
    	values.put(KEY_GEOFENCE_NAME,ids);
    	values.put(KEY_TRANSITION_TYPE, transitionType);
    	values.put(KEY_ID,1);
    	values.put(KEY_TIMESTAMP, dateFormat.format(new Timestamp(now.getTime())));
    	
	        if(getUserPosition()!=null){
	        	db.update(TABLE_CURRENT_GEOFENCE_STATE, values, KEY_ID + " =?", new String[] { String.valueOf(1) });
	        }
	        else{
	        	db.insert(TABLE_CURRENT_GEOFENCE_STATE, null, values);
	        }
	    }

	public UserPosition getUserPosition() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CURRENT_GEOFENCE_STATE, new String[] {
                KEY_ID, KEY_GEOFENCE_NAME, KEY_TRANSITION_TYPE, KEY_TIMESTAMP }, KEY_ID + "=?",
                new String[] { String.valueOf(1) }, null, null, null, null);
		if (cursor.moveToFirst()) {
	        do {
	        
	        	UserPosition userPosition = new UserPosition(cursor.getInt(0),cursor.getString(1), cursor.getString(2), Timestamp.valueOf(cursor.getString(3))); 
	        	return userPosition;
	        }while(cursor.moveToNext());
		}
		return null;
	}

	public void deleteAllContacts() {
		
		SQLiteDatabase db = this.getWritableDatabase();		 
        db.delete(TABLE_CONTACTS, null, null);
        
	}
	
	public void deleteAllMessageLogs() {
		SQLiteDatabase db = this.getWritableDatabase();		 
		db.delete(TABLE_MESSAGE_LOGS, null, null);
	}
	
	public void deleteAllGeofences() {
		SQLiteDatabase db = this.getWritableDatabase();		 
		db.delete(TABLE_SIMPLE_GEOFENCES, null, null);
	}
	
	public void deleteAllGeofenceStates() {
		SQLiteDatabase db = this.getWritableDatabase();		 
		db.delete(TABLE_CURRENT_GEOFENCE_STATE, null, null);
	}

	public ArrayList<String> getAllSimpleGeofences() {
		ArrayList<String> geofenceList = new ArrayList<String>();
	    String selectQuery = "SELECT  * FROM " + TABLE_SIMPLE_GEOFENCES + " ORDER BY "+KEY_ID+" ASC";

	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	geofenceList.add(cursor.getString(0));
	        } while (cursor.moveToNext());
	    }
	    return geofenceList;
	}

	public ArrayList<MessageLog> getAllMessageLogs() {
		ArrayList<MessageLog> messageLogs = new ArrayList<MessageLog>();
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE_LOGS + " ORDER BY "+KEY_TIMESTAMP+" DESC";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
                MessageLog messageLog = new MessageLog();
                messageLog.set_id(cursor.getInt(0));
                messageLog.setReceiverName(cursor.getString(1));
                messageLog.setReceiverPhoneNumber(cursor.getString(2));
                messageLog.setContent(cursor.getString(3));
                messageLog.setTimestamp(Timestamp.valueOf(cursor.getString(4)));
                
                messageLogs.add(messageLog);
            } while (cursor.moveToNext());
        }
        return messageLogs;
	}
	
	
	
}