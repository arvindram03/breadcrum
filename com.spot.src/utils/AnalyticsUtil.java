package utils;

import ly.count.android.api.Countly;
import android.content.Context;
import com.spot.R;

public final class AnalyticsUtil {
	private static final String MISSED_CALL_EVENT = "Missed Calls";
	private static final String SHUTDOWN_EVENT = "Shutdowns";
	public static final String SPOT_INSTALL = "Spot Install";
	
	public static void logEvent(Context context, String baseMessage){
		
		Countly.sharedInstance().init(context, "https://cloud.count.ly", context.getString(R.string.countly_app_id));
		Countly.sharedInstance().onStart();
		
		if(baseMessage.equals(MessageUtil.MISSED_CALL_MESSAGE)) {
			Countly.sharedInstance().recordEvent(MISSED_CALL_EVENT, 1);
		}
		else if(baseMessage.equals(MessageUtil.SHUTDOWN_MESSAGE)) {
			Countly.sharedInstance().recordEvent(SHUTDOWN_EVENT, 1);
		}
		else if(baseMessage.equals(SPOT_INSTALL)) {
			Countly.sharedInstance().recordEvent(SPOT_INSTALL, 1);
		}
		Countly.sharedInstance().recordEvent(baseMessage, 1);
		
		Countly.sharedInstance().onStop();
	}

}
