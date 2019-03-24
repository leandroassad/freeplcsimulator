package com.freeplc.simulator.command;

public class StopCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.STOP;
	}
	
	public void process(byte[] data) {
		System.out.println("Processando comando Stop");
		simulator.setLadderRunning(false);
	}

}
