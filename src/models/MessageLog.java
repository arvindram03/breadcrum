package models;

import java.sql.Date;
import java.sql.Timestamp;

public class MessageLog {

	private int _id;
	private String receiverName;
	private String receiverPhoneNumber;
	private String content;
	private Timestamp timestamp;
	
	public MessageLog() {
	
	}
	public MessageLog(String receiverName, String receiverPhoneNumber, String content, Timestamp timestamp) {
		this.receiverName = receiverName;
		this.receiverPhoneNumber = receiverPhoneNumber;
		this.content = content;
		this.timestamp = timestamp;
		
	}
	public int get_id() {
		return _id;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public String getReceiverPhoneNumber() {
		return receiverPhoneNumber;
	}
	public String getContent() {
		return content;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public void setReceiverPhoneNumber(String receiverPhoneNumber) {
		this.receiverPhoneNumber = receiverPhoneNumber;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
