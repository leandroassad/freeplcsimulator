package com.freeplc.simulator.command;

import com.freeplc.simulator.util.Util;

public class RunCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.RUN;
	}
	
	
	public void process(byte[] data) {
		System.out.println("Processando comando Run");
		
		int interval = Util.byteToInt(data, Util.MSB_LSB_ORDER);
		System.out.println("Intervalo de execucao do ladder: " + interval + " ms");
		
		simulator.setLadderRunning(true);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (simulator.isLadderRunning()) {
					simulator.executeStepLadder();
					Util._wait(interval);
				}
			}
		}).start();
	}
}
