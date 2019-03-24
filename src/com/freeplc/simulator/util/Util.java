package com.freeplc.simulator.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Util {

	public static final int DEFAULT_RETRIES = 3;
	public static final int DEFAULT_TIMEOUT = 30000;
	
	public static final String LSB_MSB_ORDER = "LSB-MSB";
	public static final String MSB_LSB_ORDER = "MSB-LSB";
	
	/**
	 * Read from inputstream until data len or timeout
	 * @param in InputStream
	 * @param data Buffer to be filled with bytes
	 * @param length a len to read
	 * @param retries Retries after timeout
	 * @return Number of bytes read or -1 if error
	 * @throws IOException
	 */
	public static int readBytes(InputStream in, byte[] data, int length, int retries, int timeout) throws IOException {
		int totalRead = 0, n;
		int retry = 0;
		long startTime = System.currentTimeMillis()/1000;
		long endTime;
		

//		while ((retry<retries) && totalRead < length) {
		while (totalRead < length) {
			try {
				n = in.read(data, totalRead, length-totalRead);
				if (n < 0) {	// Stream closed
					retry = retries;
					totalRead = -1;
				}
				else if (n == 0) {	// Veio nada. Espera mais um pouco
					_wait(200);
					retry++;
				}
				else {
					totalRead += n;
				}
			}
			catch (SocketTimeoutException e) {
				_wait(200);
				retry=retries;
			}
			catch (IOException e) {
				_wait(200);
				retry++;
			}
			endTime = System.currentTimeMillis()/1000;
			if (endTime-startTime >= timeout) {
				System.out.println("Timeout...");
				totalRead = -1;
				break;
			}
		}
		
		return totalRead;
	}
	
	/**
	 * Read from sock until lenght or timeout
	 * @param sock The client connection socket
	 * @param data Buffer to be filled with bytes
	 * @param length a len to read
	 * @param retries Retries after timeout
	 * @param timeout Timeout in miliseconds
	 * @return Number of bytes read or -1 if error
	 * @throws IOException
	 */
	public static int readBytes(Socket sock, byte[] data, int length, int retries, int timeout) throws IOException {
		sock.setSoTimeout(timeout);
		DataInputStream in = new DataInputStream(sock.getInputStream());
		
		return readBytes(in, data, length, retries,timeout);
	}

	/**
	 * Read data from socket
	 * @param sock The client connection socket
	 * @param data Buffer to be filled with bytes
	 * @return Number of bytes read or -1 if error
	 * @throws IOException
	 */
	public static int readBytes(Socket sock, byte[] data) throws IOException {
		return readBytes(sock, data, data.length, DEFAULT_RETRIES, DEFAULT_TIMEOUT);
	}
	
	/**
	 * Read data from socket
	 * @param sock The client connection socket
	 * @param data Buffer to be filled with bytes
	 * @param timeout Timeout in milisecons
	 * @return Number of bytes read or -1 if error
	 * @throws IOException
	 */
	public static int readBytes(Socket sock, byte[] data, int timeout) throws IOException {
		return readBytes(sock, data, data.length, DEFAULT_RETRIES, timeout);
	}
	
	/**
	 * Converts data bytes to int
	 * @param data the data to be converted (Usually a header size)
	 * @param byteOrder LSB-MSB or MSB-LSB
	 * @return The Integer represented in the data byte
	 */
	public static int byteToInt(byte[] data, String byteOrder) {
		int dataLen;
		if (byteOrder.equals(LSB_MSB_ORDER)) {
			dataLen = (data[1]&0x00FF);
			dataLen <<= 8;
			dataLen |= (data[0]&0x00FF);
		}
		else {
			dataLen = (data[0]&0x00FF);
			dataLen <<= 8;
			dataLen |= (data[1]&0x00FF);				
		}
		return dataLen;
	}
	
	/**
	 * Writes an int into 2 bytes
	 * @param len The integer to be written
	 * @param data The data to receive the bytes
	 * @param byteOrder LS-MSB or MSB-LSB
	 */
	public static void intToByte(int len, byte[] data, String byteOrder) {
		if (byteOrder.equals(LSB_MSB_ORDER)) {
			data[1] = (byte)((len>>8)&0x00FF);
			data[0] = (byte)(len&0x00FF);
		}
		else {
			data[0] = (byte)((len>>8)&0x00FF);
			data[1] = (byte)(len&0x00FF);
		}
	}
	

	/**
	 * Wait for an amount of time
	 * @param milis Miliseconds to wait
	 */
	public static void _wait(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
		}
	}
	
	
	public static String formatBytesForPrinting(byte[] data) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<data.length; i++) {
			buffer.append(String.format("%02x", data[i]).toUpperCase());
			buffer.append(" ");
		}
		return buffer.toString();
	}
	
}
