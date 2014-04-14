package utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ContactUtility {
public static Uri getContactImage(String phoneNumber, Context context) {
		ContentResolver mResolver = context.getContentResolver();

	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

	    Cursor cursor = mResolver.query(uri, new String[] {
	            PhoneLookup.PHOTO_THUMBNAIL_URI, PhoneLookup.PHOTO_URI }, null, null, null);

	    Uri contactImageUri = null;

	    if (cursor.moveToFirst()) {
	    		String contactImageUriString = cursor.getString(cursor.getColumnIndex(PhoneLookup.PHOTO_URI));
	    		if(contactImageUriString!=null)
	    			contactImageUri = Uri.parse(contactImageUriString);
	    		else
	    		{
	    			contactImageUriString = cursor.getString(cursor.getColumnIndex(PhoneLookup.PHOTO_THUMBNAIL_URI));
	    			if(contactImageUriString!=null)
		    			contactImageUri = Uri.parse(contactImageUriString);
	    		}
	    }

	    cursor.close();
	    cursor = null;
		return contactImageUri;
	}
}
