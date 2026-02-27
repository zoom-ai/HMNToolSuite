package dpnm.mobiledevice.policy.ruleobjects;

public class Event {
	public static final int VOICECALL = 0;
	public static final int STREAMING = 1;
	public static final int FTP = 2;
	public static final int VIDEOCALL = 3;
	public static final int WEBBROWSER = 4;
	public static final int SMS = 5;

	public static final String EVENT_STR[] = {
		"VoiceCall", "Streaming", "FTP", "VideoCall", "WebBrowser", "SMS"
	};
	
	private int type;

	public Event(int type) {		
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}	

	public static Event createEvent(String name) {
		for (int i = 0; i < EVENT_STR.length; i++) {
			if (EVENT_STR[i].intern() == name.intern()) {
				return new Event(i);
			}
		}
		return null;
	}
	
	public static int getEvent(String name) {
		for (int i = 0; i < EVENT_STR.length; i++) {
			if (EVENT_STR[i].intern() == name.intern()) {
				return i;
			}
		}
		return 0;
	}
}
