package adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import fragments.ContactListFragment;
import fragments.LocationListFragment;
import fragments.LogListFragment;

public class TabAdapter extends FragmentPagerAdapter {

	public TabAdapter(FragmentManager fm) {
		super(fm);
	}

	private static final int LOCATION = 0;
	private static final int LOG = 1;
	private static final int CONTACT = 2;

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
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