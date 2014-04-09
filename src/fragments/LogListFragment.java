package fragments;

import com.example.android.geofence.R;

import helpers.database.DataStoreHelper;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class LogListFragment extends ListFragment {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private SimpleCursorAdapter adapter; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listLogs(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View rootView = inflater.inflate(R.layout.log_home, container, false);
	    listLogs(getActivity());
		setListAdapter(adapter);
	    return rootView;
	}
	
	@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setListAdapter(adapter);
		}

	private void listLogs(FragmentActivity context) {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		Cursor cursor = dataStoreHelper.getAllMessageLogsAdapter();
		String[] columns = new String[] { DataStoreHelper.KEY_RECEIVER_NAME, DataStoreHelper.KEY_RECEIVER_PHONE, DataStoreHelper.KEY_CONTENT, DataStoreHelper.KEY_TIMESTAMP  };
        int[] to = new int[] { R.id.receiver_name, R.id.receiver_phone_number, R.id.content, R.id.timestamp  };
        adapter = new SimpleCursorAdapter(context, R.layout.log_list, cursor, columns, to);	
	}
}
