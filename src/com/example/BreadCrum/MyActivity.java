package com.example.BreadCrum;

import helpers.DataStoreHelper;
import models.Contact;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MyActivity extends FragmentActivity implements ActionBar.TabListener {

    
    private static final int RQS_PICK_CONTACT = 1;
    private static final int LOCATION = 0;
    private static final int LOG = 1;
    private static final int CONTACT = 2;

	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;
    
    
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
       
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        for (int position = 0; position < mAppSectionsPagerAdapter.getCount(); position++) {
        	Drawable tabIcon = getTabIcon(position);
            actionBar.addTab(actionBar.newTab().setIcon(tabIcon).setTabListener(this));
        }
        mViewPager.setCurrentItem(LOG);
    }

    private Drawable getTabIcon(int position) {
    	Bitmap bitmap;
    	Drawable tabIcon=null;
    	switch(position){
    	case LOCATION:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_place); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	case LOG:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_email); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	case CONTACT:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	}
		return tabIcon;
	}

	@Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment fragment=null;
        	Bundle args;
            switch (position) {
            case LOCATION:
	            	fragment = new LocationListFragment();
	                args = new Bundle();
	                args.putInt(LocationListFragment.ARG_SECTION_NUMBER, position + 1);
	                fragment.setArguments(args);
	                break;
            case LOG:
	            	fragment = new LogListFragment();
	                args = new Bundle();
	                args.putInt(LogListFragment.ARG_SECTION_NUMBER, position + 1);
	                fragment.setArguments(args);
	                break;
            case CONTACT:
		        	 fragment = new ContactListFragment();
	                 args = new Bundle();
	                 args.putInt(ContactListFragment.ARG_SECTION_NUMBER, position + 1);
	                 fragment.setArguments(args);
	                 break;
	        	 	
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    public void addLocationTag(View view){
    	LocationListFragment locationListFragment = new LocationListFragment();
    	EditText locationTagText = (EditText)findViewById(R.id.location_tag);
    	if(locationListFragment.addLocationTag(locationTagText.getText().toString(), this)){
    		locationTagText.setText("");
    	}
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.contacts_action_list, menu);
      return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
      switch (item.getItemId()) {
      
      case R.id.add_contact:
    	intent = new Intent(Intent.ACTION_GET_CONTENT);
      	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
      	startActivityForResult(intent, 1);
        break;
        }

      return true;
    }


@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	
	super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == RQS_PICK_CONTACT) {
			if(resultCode == RESULT_OK) {
		   
				Uri contactData = data.getData();
				Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
				cursor.moveToFirst();
		
				String normalizedPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
				String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		      
				if(normalizedPhoneNumber != null) {
					Contact newContact = new Contact(name,normalizedPhoneNumber);
					DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
					if(!dataStoreHelper.addContact(newContact)){
						Toast.makeText(this,"Contact Already Added", Toast.LENGTH_SHORT).show();
					}
					else{
						
					} 
		      }
		      else{
		    	  Toast.makeText(this,"Not a valid Phone Number", Toast.LENGTH_SHORT).show();
		      }
		     }
   		}
}
}
