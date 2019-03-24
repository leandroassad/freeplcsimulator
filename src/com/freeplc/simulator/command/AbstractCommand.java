package com.freeplc.simulator.command;

import java.util.logging.Logger;

import com.freeplc.simulator.server.FreePLCSimulator;
import com.freeplc.simulator.util.Util;

public abstract class AbstractCommand implements PLCCommand{
	Logger logger = Logger.getLogger("AbstractCommand");
	
	FreePLCSimulator simulator = null;
	byte[] rawData = null;

	
	public void process(byte[] data) {
		
	}
	
	public void initialize(FreePLCSimulator simulator) {
		this.simulator = simulator;
	}
	
	public PLCCommand clone() {
		try {
			return (PLCCommand)super.clone();
		} catch (CloneNotSupportedException e) {
			logger.info("Clone not supported exception");
			return this;
		}
	}
	
	public byte[] getBytes() {
		return rawData;
	}
	
	public void prepareCommand(byte[] data) {
		if (data == null) {	// Nao ha dados, apenas o comando
			rawData = new byte[6];
			rawData[0] = 0x02;
			
			byte[] dataLenBytes = new byte[2];		// STX
			Util.intToByte(1, dataLenBytes, Util.MSB_LSB_ORDER);
			rawData[1] = dataLenBytes[0];
			rawData[2] = dataLenBytes[1];
			
			rawData[3] = (byte)getCommandId();
			
			rawData[4] = rawData[3];	// CRC
			rawData[5] = 0x03;			// ETX
		}
		else {
			int dataLen = data.length + 1;	// o +1 é por conta do comando
			int rawDataLen = dataLen + 5; // DataLen + STX + Len(2 Bytes) + CRC + ETX 
			rawData = new byte[rawDataLen];	
			rawData[0] = 0x02;		// STX
			
			byte[] dataLenBytes = new byte[2];		// LEN
			Util.intToByte(dataLen, dataLenBytes, Util.MSB_LSB_ORDER);
			rawData[1] = dataLenBytes[0];
			rawData[2] = dataLenBytes[1];
			
			rawData[3] = (byte)getCommandId();
			System.arraycopy(data, 0, rawData, 4, data.length);
			
			rawData[rawDataLen-2] = calculateCRC(rawData, 3, dataLen);	// CRC
			rawData[rawDataLen-1] = 0x03;			// ETX
		}
	}
	
	public byte calculateCRC(byte[] data, int offset, int len) {
		byte crc = 0;
		for (int i=offset; i<(offset+len); i++) {
			crc ^= data[i];
		}
		
		return crc;
	}
}
