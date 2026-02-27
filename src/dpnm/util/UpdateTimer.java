package dpnm.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import dpnm.Conf;

public class UpdateTimer {
	private static UpdateTimer _instance = null;
	
	private Vector<UpdateListener> listeners = null;
	
	private Timer timer = null;
	
	private TimerTask task = null;
	
	public static UpdateTimer getInstance() {
		if (_instance == null) {
			_instance = new UpdateTimer();
		}
		return _instance;
	}
	
	public UpdateTimer() {
		timer = new Timer();
	}
	
	public synchronized void addUpdateListener(UpdateListener listener) {
		if (listeners == null) {
    		listeners = new Vector<UpdateListener>();
		}
		listeners.addElement(listener);
	}
	
	public synchronized void removeUpdateListener(UpdateListener listener) {
		if (listeners != null) {
			listeners.removeElement(listener);
			if (listeners.size() == 0) {
				listeners = null;
			}
		}
	}
	
	
	public void start() {
		task = new UpdateTask();
		try {
			timer.schedule(task, 0, Conf.UPDATE_TIMEOUT);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void stop() {
		if (timer == null || task == null) {
			return;
		}
		try {
			task.cancel();
			task = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void fireUpdateEvent() {
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			listeners.elementAt(i).updateInfo();
		}
	}
	

	public class UpdateTask extends TimerTask {
		@Override
    	public synchronized void run() {
    		//	fire event
    		fireUpdateEvent();
    	}
	}
	
	public Timer getTimer() {
		return timer;
	}
}
