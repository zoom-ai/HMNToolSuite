package dpnm.server;

/**
 * This class is for Application Server
 * @author Administrator
 *
 */
public class ApplicationServer extends Server {
	public ApplicationServer(String id) {
		super(id);
		setType(APPLICATION);
	}
}
