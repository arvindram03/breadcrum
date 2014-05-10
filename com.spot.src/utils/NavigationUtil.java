package utils;

import helpers.database.DataStoreHelper;
import android.content.Context;

public class NavigationUtil {
	public static final int LOCATION = 0;
	public static final int LOG = 1;
	public static final int CONTACT = 2;

	public static int getCurrentItem(Context context, String action) {
		if(action.equals("open-logs")){
			return LOCATION;
		}
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		if (dataStoreHelper.getAllContacts().size() == 0)
			return CONTACT;
		else
			return LOCATION;
	}

}
