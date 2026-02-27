package dpnm.server;

/**
 * This class is for location server
 * 
 * @author Administrator
 *
 */
public class LocationServer extends Server {
	public LocationServer(String id) {
		super(id);
		setType(LOCATION);
	}
}
