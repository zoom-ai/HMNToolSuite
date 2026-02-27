package dpnm.mobiledevice.policy.contextobjects;

public class Schedule {
		private String status;

	public Schedule() {
		this("idle");
	}
	public Schedule(String status) {		
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}	
}
