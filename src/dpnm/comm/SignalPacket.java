package dpnm.comm;

public class SignalPacket extends Packet {
	private double strength = 0.0;
	
	public SignalPacket(CommunicationDevice src, CommunicationDevice dst) {
		super(src, dst);
	}
	
	public void setSingnalStrength(double strength) {
		this.strength = strength;
	}
	
	public double getSingalStrength() {
		return this.strength;
	}
}
