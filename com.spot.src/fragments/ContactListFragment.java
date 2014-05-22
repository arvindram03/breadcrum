package fragments;

import helpers.database.DataStoreHelper;

import java.util.ArrayList;

import utils.NavigationUtil;

import models.Contact;
import activities.MainActivity;
import adapters.ContactAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.spot.R;

public class ContactListFragment extends Fragment {

	private ContactAdapter adapter;

	public static final String ARG_SECTION_NUMBER = "section_number";
	View rootView;

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.contacts_home, container, false);
		final FragmentActivity context = getActivity();
		listAddedContacts(context);
		ListView contactListView = (ListView) rootView
				.findViewById(R.id.contacts_list);
		Button addContactText = (Button) rootView
				.findViewById(R.id.empty_contact_list);
		contactListView.setAdapter(adapter);

		contactListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int position, long arg3) {
						removeContactFromList(position, context);
						return true;
					}
				});
		
		contactListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,final int position,
					long arg3) {
				
				final Contact contact = adapter.getItem(position);
				final DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
				new AlertDialog.Builder(context)
				.setMessage("Send SMS to "+contact.getName()+" when my phone shuts down due to low battery?")
				.setPositiveButton(R.string.enable,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								contact.setShutdownNotification(true);
								dataStoreHelper.updateContact(contact);
								adapter.notifyDataSetChanged();
							}
						}).setNegativeButton(R.string.disable, 
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										contact.setShutdownNotification(false);
										dataStoreHelper.updateContact(contact);
										adapter.notifyDataSetChanged();
									}
						
						})
				.show();
				
			}
			
		});

		addContactText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
				startActivityForResult(intent, MainActivity.RQS_PICK_CONTACT);
			}
		});
		contactListView.setEmptyView(addContactText);

		return rootView;
	}

	private void removeContactFromList(final int position,
			final FragmentActivity context) {

		if (adapter.getCount() <= position) {
			listAddedContacts(context);
			if (rootView != null) {
				ListView contactListView = (ListView) rootView
						.findViewById(R.id.contacts_list);
				contactListView.setAdapter(adapter);

			}
		}
		final Contact contact = adapter.getItem(position);
		new AlertDialog.Builder(context)
				.setMessage("Remove contact " + contact.getName() + " ?")
				.setPositiveButton(R.string.remove,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								DataStoreHelper dataStoreHelper = new DataStoreHelper(
										context);
								dataStoreHelper.deleteContact(contact);
								adapter.remove(adapter.getItem(position));
								adapter.notifyDataSetChanged();
							}
						}).setNegativeButton(android.R.string.no, null).show();
	}

	public void listAddedContacts(final FragmentActivity context) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
				ArrayList<Contact> contactList = dataStoreHelper
						.getAllContacts();
				adapter = new ContactAdapter(context, R.layout.contact_list,
						contactList);
			}
		});
	}

	public ContactAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(ContactAdapter adapter) {
		this.adapter = adapter;
	}

}
