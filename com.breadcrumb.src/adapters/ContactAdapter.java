package adapters;

import java.util.List;

import utils.ContactUtility;

import com.breadcrumb.R;


import models.Contact;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<Contact>{
	Context context;
	public ContactAdapter(Context context, int resource, List<Contact> objects) {
		super(context, resource, objects);
		this.context = context;
	}
	
	private class ViewHolder{
		ImageView contactImage;
		TextView contactName;
		TextView phoneNumber;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Contact contact = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.contact_list, null);
            holder = new ViewHolder();
            holder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            holder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
            holder.contactImage = (ImageView) convertView.findViewById(R.id.contact_image);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.contactName.setText(contact.getName());
        holder.phoneNumber.setText(contact.getPhoneNumber());
        Uri contactImageUri = ContactUtility.getContactImage(contact.getPhoneNumber(),context);
        Log.d(contact.getName()+"uri1",contactImageUri+"");
        if(contactImageUri!=null){
        	holder.contactImage.setImageURI(contactImageUri);
        	holder.contactImage.setBackgroundResource(R.color.white);
        }
        else{
        	holder.contactImage.setImageResource(R.drawable.ic_action_person_light);
        	holder.contactImage.setBackgroundResource(R.color.icon_bg);
        }
        return convertView;
    }

	
	

}
