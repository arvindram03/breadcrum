package fragments;

import java.util.ArrayList;

import models.MessageLog;

import com.example.android.geofence.R;

import helpers.database.DataStoreHelper;
import adapters.LogAdapter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LogListFragment extends Fragment {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private LogAdapter adapter; 
	
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
		ListView messageLogList = (ListView) rootView.findViewById(R.id.message_list);
		messageLogList.setAdapter(adapter);
	    return rootView;
	}
	
	@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}

	private void listLogs(FragmentActivity context) {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		ArrayList<MessageLog> messageLogs = dataStoreHelper.getAllMessageLogs();
        adapter = new LogAdapter(context, R.layout.log_list, messageLogs);	
	}
}
