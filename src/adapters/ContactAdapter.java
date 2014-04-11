package adapters;

import java.util.List;

import com.example.android.geofence.R;

import models.Contact;
import android.app.Activity;
import android.content.Context;
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
		TextView messageCount;
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
            holder.messageCount = (TextView) convertView.findViewById(R.id.message_count);
            holder.contactImage = (ImageView) convertView.findViewById(R.id.contact_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
 
        holder.contactName.setText(contact.getName());
        holder.phoneNumber.setText(contact.getPhoneNumber());
        holder.messageCount.setText("10");
        holder.contactImage.setImageResource(R.drawable.ic_action_person);
 
        return convertView;
    }
	

}
