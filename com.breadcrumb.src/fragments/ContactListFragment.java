package fragments;

import java.util.ArrayList;

import utils.IconUtility;

import com.breadcrumb.R;
import com.google.android.gms.drive.internal.ad;

import models.Contact;


import helpers.database.DataStoreHelper;
import activities.MainActivity;
import adapters.ContactAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactListFragment extends Fragment{
	
	private ContactAdapter adapter;

	public static final String ARG_SECTION_NUMBER = "section_number";
	View rootView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contacts_home, container, false);
        final FragmentActivity context = getActivity();
        listAddedContacts(context);
        ListView contactListView = (ListView) rootView.findViewById(R.id.contacts_list);
        Button addContactText = (Button) rootView.findViewById(R.id.empty_contact_list);
        contactListView.setAdapter(adapter);
        
        contactListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int position, long arg3) {
            	removeContactFromList(position, context);
            	return true;
            }
        });
        
        
        addContactText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK);
		      	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		      	startActivityForResult(intent, 1);
			}
		});
        contactListView.setEmptyView(addContactText);
        
        
        return rootView;
    }
	
	private void removeContactFromList(final int position, final FragmentActivity context) {
		
		
		final Contact contact = adapter.getItem(position);
		new AlertDialog.Builder(context)
    	  .setMessage("Remove contact "+contact.getName()+" ?")
    	  .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
    	      public void onClick(DialogInterface dialog, int whichButton) {
    	    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
    	    	dataStoreHelper.deleteContact(contact);
    	    	adapter.remove(adapter.getItem(position));
    	  		adapter.notifyDataSetChanged();
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
	}
	
    public void listAddedContacts(final FragmentActivity context) {
    	context.runOnUiThread(new Runnable(){
    		public void run(){
				DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
				ArrayList<Contact> contactList = dataStoreHelper.getAllContacts();
		        adapter = new ContactAdapter(context, R.layout.contact_list,contactList);
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
