package dpnm.network.event;

import java.util.EventObject;

public class NetworkEvent extends EventObject {
	public String type = null;
	
	public NetworkEvent(Object arg0) {
		super(arg0);
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
