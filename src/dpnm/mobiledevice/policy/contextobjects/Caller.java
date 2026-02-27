package dpnm.mobiledevice.policy.contextobjects;

public class Caller {
	private String name;
	private String phoneNumber;
	private String group;
	private String address;
	
	public Caller() {
		name = null;
		phoneNumber = null;
		group = null;
		address = null;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}	
}
