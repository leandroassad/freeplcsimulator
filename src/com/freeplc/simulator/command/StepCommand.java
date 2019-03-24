package com.freeplc.simulator.command;

public class StepCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.STEP;
	}
	
	public void process(byte[] data) {
		System.out.println("Processando comando Step");
		
		simulator.executeStepLadder();
	}

}
