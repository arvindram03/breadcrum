package helpers.messaging;

import helpers.database.DataStoreHelper;
import helpers.location.LocationHelper;
import helpers.notification.NotificationHelper;
import interfaces.LocationChangeNotifier;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import utils.AnalyticsUtil;

import models.Contact;
import models.MessageLog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SmsManager;


public class Messenger implements LocationChangeNotifier{

	private Context context;
	private LocationHelper locationHelper;
	private DataStoreHelper dataStoreHelper;
	Contact contact;
	String baseMessage;

	public Messenger(Context context) {
		this.context = context;
		this.locationHelper = new LocationHelper(context);
		this.dataStoreHelper = new DataStoreHelper(context);
	}

	public void sendMessage(Contact contact, String baseMessage) {
		this.contact = contact;
		this.baseMessage = baseMessage;	
		locationHelper.requestLocationUpdate(this);
	}
	
	private String constructMessage(Location location, String baseMessage) {
		String geofencePosition = locationHelper.getUserPosition() != null ? locationHelper
				.getUserPosition() : "";
		String content = baseMessage;
		
			if (location != null) {
				if (!geofencePosition.contains("Entered")){
					content+="I am near ";
				}
				String address = getAddress(location);
				content +=  address
						+ " http://www.google.co.in/maps/place/"
						+ location.getLatitude() + ","
						+ location.getLongitude();
			}
			
		return geofencePosition + content;
	}

	private String getAddress(Location location) {
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		String address = "";
		if (isConnectingToInternet()) {
			try {
				List<Address> addresses = geocoder.getFromLocation(
						location.getLatitude(), location.getLongitude(), 1);
				if (addresses.size() != 0) {
					Address currentAddress = addresses.get(0);
					int totalAddressLines = currentAddress
							.getMaxAddressLineIndex();
					if (totalAddressLines > 0)
						address += currentAddress.getAddressLine(0);
					if (currentAddress.getLocality() != null)
						address += ", " + currentAddress.getLocality();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return address;
	}

	private boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	@Override
	public void locationChanged(Location newLocation) {
		String phoneNumber = contact.getPhoneNumber();
		SmsManager smsManager = SmsManager.getDefault();
		String content = constructMessage(newLocation, baseMessage);
		String signature = "\n\nSent by Spot";
		ArrayList<String> message = smsManager.divideMessage(content
				+ signature);
		smsManager.sendMultipartTextMessage(phoneNumber, null, message, null,
				null);
		AnalyticsUtil.logEvent(context,baseMessage);
		java.util.Date now = new java.util.Date();
		MessageLog messageLog = new MessageLog(contact.getName(), phoneNumber,
				content, new Timestamp(now.getTime()));
		dataStoreHelper.addMessageLog(messageLog);
		NotificationHelper.sendMessageNotification(context, messageLog);
	}
}
