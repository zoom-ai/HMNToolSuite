package dpnm.util;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Logger {
	private static Logger _instance;
	
	public synchronized static Logger getInstance() {
		if (_instance == null) {
			_instance = new Logger();
		}
		return _instance;
	}

	File deviceLog;
	File emulatorLog;
	File cliLog;
    SimpleDateFormat sdf;
	
	public Logger() {
		deviceLog = new File("device.log");
		emulatorLog = new File("emulator.log");
		cliLog = new File("cli.log");
//        sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
        sdf = new SimpleDateFormat("yyMMdd HHmmss.SSS");
		try {

    		String log = sdf.format(new Date()) + " [DEVICE LOG STARTED]";
			FileWriter writer = new FileWriter(deviceLog, false);
			writer.write(log+"\r\n");
			writer.close();
			
			log = sdf.format(new Date()) + " [EMULATOR LOG STARTED]";
			writer = new FileWriter(emulatorLog, false);
			writer.write(log+"\r\n");
			writer.close();
			
			log = sdf.format(new Date()) + " [CLI LOG STARTED]";
			writer = new FileWriter(cliLog, false);
			writer.write(log+"\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
	
	}
	
	public void logDevice(String id, String str) {
		String log = sdf.format(new Date()) + " [" + id + "] : " + str;
		try {

			FileWriter writer = new FileWriter(deviceLog, true);
			writer.write(log+ "\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
		Log.message(log);
		
	}
	
	public void logEmulator(String id, String str) {
		String log = sdf.format(new Date()) + " [" + id + "] : " + str;
		try {

			FileWriter writer = new FileWriter(emulatorLog, true);
			writer.write(log+ "\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
		Log.message(log);
	}
	
	public void logCli(String id, String str) {
		String log = sdf.format(new Date()) + " [" + id + "] : " + str;
		try {

			FileWriter writer = new FileWriter(cliLog, true);
			writer.write(log+ "\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
		Log.message(log);
	}}
