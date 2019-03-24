package com.freeplc.simulator.command;

public class DisconnectCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.DISCONNECT;
	}
	
	public void process(byte[] data) {
		System.out.println("Processando comando Disconnect");
		simulator.closeCurrentConnection();
	}

}
