package interfaces;

import android.location.Location;

public interface LocationChangeNotifier {
	void locationChanged(Location newLocation);
}
