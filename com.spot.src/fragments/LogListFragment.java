package fragments;

import helpers.database.DataStoreHelper;

import java.util.ArrayList;

import models.MessageLog;
import adapters.LogAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.spot.R;

public class LogListFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	private static LogAdapter adapter;
	View rootView;

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.log_home, container, false);
		final FragmentActivity context = getActivity();
		listLogs(context);
		ListView messageLogView = (ListView) rootView
				.findViewById(R.id.message_list);
		TextView emptyMessageText = (TextView) rootView
				.findViewById(R.id.empty_message_list);
		messageLogView.setEmptyView(emptyMessageText);
		messageLogView.setAdapter(adapter);
		messageLogView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int position, long arg3) {
						removeMessageLogFromList(position, context);
						return true;
					}

				});
		return rootView;
	}

	private void removeMessageLogFromList(final int position,
			final FragmentActivity context) {
		if (adapter.getCount() <= position) {
			listLogs(context);
			if (rootView != null) {

				ListView messagesListView = (ListView) rootView
						.findViewById(R.id.message_list);
				messagesListView.setAdapter(adapter);

			}
		}
		final MessageLog messageLog = adapter.getItem(position);
		new AlertDialog.Builder(context)
				.setMessage("Remove this message ?")
				.setPositiveButton(R.string.remove,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								DataStoreHelper dataStoreHelper = new DataStoreHelper(
										context);
								dataStoreHelper.deleteMessageLog(messageLog);
								adapter.remove(adapter.getItem(position));
								adapter.notifyDataSetChanged();
							}
						}).setNegativeButton(android.R.string.no, null).show();

	}

	public void listLogs(final FragmentActivity context) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
				ArrayList<MessageLog> messageLogs = dataStoreHelper
						.getAllMessageLogs();
				adapter = new LogAdapter(context, R.layout.log_list,
						messageLogs);

			}
		});

	}

	public LogAdapter getAdapter() {
		return adapter;
	}
}
