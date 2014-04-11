package fragments;

import java.util.ArrayList;

import models.Contact;

import com.example.android.geofence.R;

import helpers.database.DataStoreHelper;
import adapters.ContactAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactListFragment extends Fragment{
	
private ContactAdapter adapter; 
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
        ListView contactList = (ListView) rootView.findViewById(R.id.contacts_list);
		contactList.setAdapter(adapter);
        return rootView;
    }
   
    @Override
    	public void onActivityCreated(Bundle savedInstanceState) {
    		super.onActivityCreated(savedInstanceState);
    		
    	}
    
    private void listAddedContacts(FragmentActivity context) {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		ArrayList<Contact> contactList = dataStoreHelper.getAllContacts();
        adapter = new ContactAdapter(context, R.layout.contact_list,contactList);
	}
    
}
