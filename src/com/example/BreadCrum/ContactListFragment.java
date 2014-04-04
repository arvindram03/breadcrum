package com.example.BreadCrum;

import helpers.DataStoreHelper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class ContactListFragment extends ListFragment{
	
private SimpleCursorAdapter adapter; 
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			listAddedContacts(getActivity());
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contacts_home, container, false);
        listAddedContacts(getActivity());
		setListAdapter(adapter);
        return rootView;
    }
   
    @Override
    	public void onActivityCreated(Bundle savedInstanceState) {
    		super.onActivityCreated(savedInstanceState);
    		setListAdapter(adapter);
    	}
    
    private void listAddedContacts(FragmentActivity context) {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		Cursor cursor = dataStoreHelper.getAllContactsAdapter();
		String[] columns = new String[] { DataStoreHelper.KEY_NAME, DataStoreHelper.KEY_PH_NO };
        int[] to = new int[] { R.id.contact_name, R.id.phone_number };
        adapter = new SimpleCursorAdapter(context, R.layout.contact_list, cursor, columns, to);
	}
    
}
