package dpnm.tool.data;

public class LogTable {
	int	location;
	int x, y;
	int speed;
	long duration;
	String ap = null;
	String speedAp = null;
	String slaAp = null;
	String apav[] = null;
	String apsv = null;
	String bestAp = null;
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public String getAp() {
		return ap;
	}
	public void setAp(String ap) {
		this.ap = ap;
	}
	public String getSpeedAp() {
		return speedAp;
	}
	public void setSpeedAp(String speedAp) {
		this.speedAp = speedAp;
	}
	public String getSlaAp() {
		return slaAp;
	}
	public void setSlaAp(String slaAp) {
		this.slaAp = slaAp;
	}
	public String[] getApav() {
		return apav;
	}
	public void setApav(String[] apav) {
		this.apav = apav;
	}
	public String getApsv() {
		return apsv;
	}
	public void setApsv(String apsv) {
		this.apsv = apsv;
	}
	public String getBestAp() {
		return bestAp;
	}
	public void setBestAp(String bestAp) {
		this.bestAp = bestAp;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	
}
