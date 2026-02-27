package dpnm.mobiledevice.policy.contextobjects;

public class ContextFacts {
	private Schedule schedule;
	private Location location;
	private Caller caller;
	
	public ContextFacts() {
		schedule = new Schedule();
		location = new Location();
		caller = new Caller();
	}
	
	public Schedule getSchedule() {
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Caller getCaller() {
		return caller;
	}
	public void setCaller(Caller caller) {
		this.caller = caller;
	}
}
