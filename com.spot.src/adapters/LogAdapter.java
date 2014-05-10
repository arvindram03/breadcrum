package adapters;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import models.MessageLog;
import utils.ContactUtility;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spot.R;

public class LogAdapter extends ArrayAdapter<MessageLog> {
	Context context;

	public LogAdapter(Context context, int resource, List<MessageLog> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	private class ViewHolder {
		ImageView contactImage;
		TextView receiverName;
		TextView content;
		TextView timestamp;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		MessageLog messageLog = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.log_list, null);
			holder = new ViewHolder();
			holder.receiverName = (TextView) convertView
					.findViewById(R.id.receiver_name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.timestamp = (TextView) convertView
					.findViewById(R.id.timestamp);
			holder.contactImage = (ImageView) convertView
					.findViewById(R.id.contact_image);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		holder.content.setText(messageLog.getContent());
		String formattedDate = formatDate(messageLog.getTimestamp());
		holder.timestamp.setText(formattedDate);
		holder.receiverName.setText(messageLog.getReceiverName());
		Uri contactImageUri = ContactUtility.getContactImage(
				messageLog.getReceiverPhoneNumber(), context);
		if (contactImageUri != null) {
			holder.contactImage.setImageURI(contactImageUri);
			holder.contactImage.setBackgroundResource(R.color.white);
		} else{
			holder.contactImage
					.setImageResource(R.drawable.ic_action_person_light);
			holder.contactImage.setBackgroundResource(R.color.icon_bg);
		}
		return convertView;
	}

	private String formatDate(Timestamp timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"hh:mm a, dd MMM yy");
		String dateFormat = simpleDateFormat.format(timestamp);
		return dateFormat;
	}

}
