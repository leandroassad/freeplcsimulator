package com.freeplc.simulator.command;

import java.util.HashMap;

import com.freeplc.simulator.server.FreePLCSimulator;

public class PLCCommandMap {

	protected HashMap<Integer, PLCCommand> commandMap = new HashMap<Integer, PLCCommand>();
	protected PLCCommandMap() {
		initMap();
	}
	
	protected void initMap() {
		commandMap.put(PLCCommand.DOWNLOAD_APPLICATION, new DownloadApplicationCommand());
		commandMap.put(PLCCommand.RUN, new RunCommand());
		commandMap.put(PLCCommand.STOP, new StopCommand());
		commandMap.put(PLCCommand.STEP, new StepCommand());
		commandMap.put(PLCCommand.STATUS, new StatusCommand());
		commandMap.put(PLCCommand.DISCONNECT, new DisconnectCommand());
	}
	
	protected static PLCCommandMap plcCommandMap = null; 
	public static PLCCommandMap getPLCCommandMap() {
		if (plcCommandMap == null) {
			plcCommandMap = new PLCCommandMap();
		}
		
		return plcCommandMap;
	}
	
	
	public PLCCommand getPLCCommand(byte commandId, FreePLCSimulator simulator) {
		PLCCommand command = commandMap.get(new Integer(commandId));
		PLCCommand plcCommand = null;
		
		if (command != null) {
			plcCommand = command.clone();
			plcCommand.initialize(simulator);
		}
		
		return plcCommand;
	}


}
