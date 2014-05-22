package adapters;

import java.util.List;

import models.Contact;
import utils.ContactUtility;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spot.R;

public class ContactAdapter extends ArrayAdapter<Contact> {
	Context context;

	public ContactAdapter(Context context, int resource, List<Contact> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	private class ViewHolder {
		ImageView contactImage;
		ImageView shutdownIcon;
		TextView contactName;
		TextView phoneNumber;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Contact contact = getItem(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.contact_list, null);
			holder = new ViewHolder();
			holder.contactName = (TextView) convertView
					.findViewById(R.id.contact_name);
			holder.phoneNumber = (TextView) convertView
					.findViewById(R.id.phone_number);
			holder.contactImage = (ImageView) convertView
					.findViewById(R.id.contact_image);
			convertView.setTag(holder);
			holder.shutdownIcon = (ImageView) convertView
					.findViewById(R.id.shutdown_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.contactName.setText(contact.getName());
		holder.phoneNumber.setText(contact.getPhoneNumber());
		Uri contactImageUri = ContactUtility.getContactImage(
				contact.getPhoneNumber(), context);
		if (contactImageUri != null) {
			holder.contactImage.setImageURI(contactImageUri);
			holder.contactImage.setBackgroundResource(R.color.white);
		} else {
			holder.contactImage
					.setImageResource(R.drawable.ic_action_person_light);
			holder.contactImage.setBackgroundResource(R.color.icon_bg);
		}
		if(contact.isShutdownNotificationEnabled()) {
			holder.shutdownIcon.setColorFilter(Color.parseColor("#02798b"), Mode.MULTIPLY);
		}
		else {
			holder.shutdownIcon.setColorFilter(Color.parseColor("#bbbbbb"), Mode.MULTIPLY);
		}
		
		return convertView;
	}

}
