package dpnm.comm;

public class Packet {
	protected CommunicationDevice src = null;
	protected CommunicationDevice dst = null;
	
	public Packet(CommunicationDevice src, CommunicationDevice dst) {
		this.src = src;
		this.dst = dst;
	}

	public CommunicationDevice getSrc() {
		return src;
	}

	public void setSrc(CommunicationDevice src) {
		this.src = src;
	}

	public CommunicationDevice getDst() {
		return dst;
	}

	public void setDst(CommunicationDevice dst) {
		this.dst = dst;
	}
}
