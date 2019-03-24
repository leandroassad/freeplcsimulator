package com.freeplc.simulator.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.freeplc.simulator.command.PLCCommand;
import com.freeplc.simulator.command.PLCCommandMap;
import com.freeplc.simulator.global.Constants;
import com.freeplc.simulator.global.GlobalProperties;
import com.freeplc.simulator.parser.LadderParser;
import com.freeplc.simulator.util.Util;

import lombok.Getter;
import lombok.Setter;

public class FreePLCSimulator extends Thread {
	Logger logger = Logger.getLogger("FreePLCSimulator");

	ServerSocket server;
	Socket currentConnection = null;
	DataOutputStream dataOut = null;
	DataInputStream dataIn = null;
	LadderParser ladderParser = null;
	boolean ladderRunning = false;
	@Getter @Setter boolean connected = false;
	public FreePLCSimulator() {
		ladderParser = new LadderParser(this);
		ladderParser.setTitle(LadderParser.SIMULATOR_TITLE + " - Offline");
	}
	
	public void addLadderToParser(String ladder) {
		ladderParser.setLadder(ladder);
	}
	
	public void executeStepLadder() {
		ladderParser.ladderStep();
	}
	
	public boolean isLadderRunning() {
		return ladderRunning;
	}
	
	public void setLadderRunning(boolean running) {
		ladderRunning = running;
	}
	
	public void run()  {
		int simuPort = GlobalProperties.getPropertyInt(Constants.SIMULATOR_PORT_PROPERTY, Constants.SIMULATOR_PORT_DEFAULT);
		try {
			server = new ServerSocket(simuPort);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Falha iniciando simulador", e);
			System.exit(0);
		} 
		
		while (true) {
			try {
				logger.info("Aguardando conexoes do FreePLC");
				ladderParser.setTitle(LadderParser.SIMULATOR_TITLE + " - Offline");
				currentConnection = server.accept();
				handleNewConnection();
			}
			catch (IOException e) {
				logger.log(Level.SEVERE, "Falha aguardando/tratando conexoes", e);
			}
		}
	}
	
	public void handleNewConnection() throws IOException {
		logger.info("Nova conexao de: " + currentConnection.getInetAddress());
		ladderParser.setTitle(LadderParser.SIMULATOR_TITLE + " - Online");
		
		connected = true;
		
		byte[] header = new byte[3];
		byte[] dataLen = new byte[2];
		byte[] data = null;
		int nBytes;
		dataOut = new DataOutputStream(currentConnection.getOutputStream());
		dataIn = new DataInputStream(currentConnection.getInputStream());
		
		while (true) {
			Arrays.fill(header, (byte)0x00);
			logger.info("Aguardando dados do FreePLCEditor");
			
			if (currentConnection == null) return;
			
			// Faz a leitura do header
			nBytes = Util.readBytes(currentConnection, header);
			
			logger.info("Numero de bytes do header = " + nBytes);
			if (nBytes < 0) {
				logger.info("Conexao com FreePLC fechada.");
				break;
			}
			
			if (header[0] == Constants.STX) {
				dataLen[0] = header[1];
				dataLen[1] = header[2];
				int len = Util.byteToInt(dataLen, Util.MSB_LSB_ORDER);
				data = new byte[len];
				
				logger.info("Numero de bytes de dados: " + len);
				nBytes = Util.readBytes(currentConnection, data);
				if (nBytes < len) {
					logger.info("Falha lendo dados do FreePLC");
					break;
				}
				else {
					
					logger.info("Bytes recebidos: " + Util.formatBytesForPrinting(data));

					// Le CRC e ETX
					byte []footer = new byte[2];
					Util.readBytes(currentConnection, footer);

					logger.info("Footer recebido: " + Util.formatBytesForPrinting(footer));

					byte crcData = footer[0];
					byte etx = footer[1];
					byte crc = calcCRC(data, len);					
					if (crcData != crc) {
						logger.info("Falha: Frame com CRC invalido.\nCalculado=" + Integer.toHexString(crc) + "\nFornecido=" + Integer.toHexString(crcData));
						break;
					}
					if (etx != Constants.ETX) {
						logger.info("Falha: Frame sem ETX");
						break;
					}
										
					byte commandId = data[0];
					PLCCommand command = PLCCommandMap.getPLCCommandMap().getPLCCommand(commandId, this);
					if (command != null) {
						byte [] rawData = new byte[len-1];
						System.arraycopy(data, 1, rawData, 0, len-1);
						command.process(rawData);
					}					
				}
			}
			else {
				logger.info("Inicio do frame invalido");
				break;
			}
		}
		
		try {
			dataIn.close();
			dataOut.close();
			if (currentConnection != null) {
				currentConnection.close();
				currentConnection = null;
				connected = false;
			}
		}
		catch (IOException e) {}
	}
	
	public byte calcCRC(byte[] data, int len) {
		byte crc = 0;
		
		for (int i=0; i<len; i++) {
			crc ^= (byte)data[i];
		}
		
		return crc;
	}
	
	public void closeCurrentConnection() {
		if (currentConnection != null) {
			try {
				currentConnection.close();
				if (dataIn != null) dataIn.close();
				if (dataOut != null) dataOut.close();
				connected = false;
			} catch (IOException e) {
			}
			currentConnection = null;
		}
	}
	
	public void sendDataToEditor(byte[] data) {
		if (currentConnection != null) {
			try {
				System.out.println("Enviando dados ao Editor");
				dataOut.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

