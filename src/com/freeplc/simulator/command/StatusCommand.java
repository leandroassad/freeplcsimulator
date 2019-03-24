package com.freeplc.simulator.command;

public class StatusCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.STATUS;
	}
	
	public void process(byte[] data) {
		System.out.println("Processando comando Status");		
	}

}
