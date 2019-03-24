package com.freeplc.simulator.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import com.freeplc.simulator.util.Util;

public class Test {

	static String ladder = "XIC I:0.0/0 XIO B3:0/0 OTE B3:0/1\n" + 
			"XIC I:0.0/0 OTE B3:0/0\n" + 
			"XIC B3:0/1 OTE B3:0/2\n" + 
			"BST XIC B3:0/2 XIO B3:0/3 NXB XIC B3:0/3 XIO B3:0/2 BND OTE B3:0/3\n" + 
			"XIC B3:0/3 OTE O:0.0/0\n";
	
	public static void main(String[] args) throws Exception {
		Socket sock = new Socket("localhost", 5000);
		
		DataOutputStream out = new DataOutputStream(sock.getOutputStream());
		DataInputStream in = new DataInputStream(sock.getInputStream());
		int ladderLen = ladder.length();
		int dataLen = ladderLen + 1;
		int frameLen = dataLen + 5;
		byte data[] = new byte[frameLen];
		byte dataLenBytes[] = new byte[2];
		data[0] = 0x02;
		Util.intToByte(dataLen, dataLenBytes, Util.MSB_LSB_ORDER);
		data[1] = dataLenBytes[0];
		data[2] = dataLenBytes[1];
		
		data[3] = 0x01;		// DOWNLOAD_COMMAND;
		System.arraycopy(ladder.getBytes(), 0, data, 4, ladderLen);
		int count = 0;
		byte crc = 0;
		for (int i=0; i<dataLen; i++) {			
			crc ^= data[i+3];
		}
		
		data[frameLen - 2] = crc;
		data[frameLen - 1] = 0x03;
		
		out.write(data);

//		count = 0;
//		while (count < 10) {
//			out.write(data);
//			Util._wait(100);
//			count++;
//		}

		byte [] newData = new byte[8];
		newData[0] = 0x2;
		Util.intToByte(3, dataLenBytes, Util.MSB_LSB_ORDER);
		newData[1] = dataLenBytes[0];
		newData[2] = dataLenBytes[1];
		
		newData[3] = 0x02;		// Run
		int interval = 1000;	// A cada 2 segundos
		Util.intToByte(interval, dataLenBytes, Util.MSB_LSB_ORDER);
		newData[4] = dataLenBytes[0];
		newData[5] = dataLenBytes[1];
		
		crc = 0;
		for (int i=0; i<3; i++) {			
			crc ^= newData[i+3];
		}		
		
		newData[6] = crc;
		newData[7] = 0x03;
		
		System.out.println("Mandando executar o Ladder\n");
		
		out.write(newData);
		
		Util._wait(10000);
		
		System.out.println("Mandando parar o Ladder\n");
		
		byte[] stopData = new byte[6];

		stopData[0] = 0x02;
		Util.intToByte(1, dataLenBytes, Util.MSB_LSB_ORDER);
		stopData[1] = dataLenBytes[0];
		stopData[2] = dataLenBytes[1];
		
		stopData[3] = 0x03;		// Stop
		stopData[4] = 0x03;		// CRC
		stopData[5] = 0x03;		// ETX
		
		out.write(stopData);
		
		stopData[3] = 0x06;		// Disconnect
		stopData[4] = 0x06;		// Disconnect
		out.write(stopData);

//		newData[3] = 0x04;	// Step
//		newData[4] = 0x04;
//		out.write(newData);
//
//		for (int i=0; i<10; i++) {
//			Util._wait(1000);
//			out.write(newData);
//		}

		in.close();
		out.close();
		sock.close();
	}

}
