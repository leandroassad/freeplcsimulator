package com.freeplc.simulator.command;

import com.freeplc.simulator.server.FreePLCSimulator;

public interface PLCCommand extends Cloneable {
	public static final int DOWNLOAD_APPLICATION = 0x01;
	public static final int RUN = 0x02;
	public static final int STOP = 0x03;
	public static final int STEP = 0x04;
	public static final int STATUS = 0x05;
	public static final int DISCONNECT = 0x06;
	
	public int getCommandId();
	public void process(byte[] data);
	public PLCCommand clone();
	public void initialize(FreePLCSimulator simulator);
	public byte[] getBytes();
	public void prepareCommand(byte[] data);

}
