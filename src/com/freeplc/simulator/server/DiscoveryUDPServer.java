package com.freeplc.simulator.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import com.freeplc.simulator.global.Constants;
import com.freeplc.simulator.global.GlobalProperties;

public class DiscoveryUDPServer extends Thread {
		
	public DiscoveryUDPServer() {
		
	}
	
	public void run() {
		
		DatagramSocket udpSock = null;
		// Obtem a porta UDP
		int udpPort = GlobalProperties.getPropertyInt(Constants.UDP_PORT_PROPERTY, Constants.UDP_PORT_DEFAULT);		

		int simuPort = GlobalProperties.getPropertyInt(Constants.SIMULATOR_PORT_PROPERTY, Constants.SIMULATOR_PORT_DEFAULT);
		int digitalInputPorts = GlobalProperties.getPropertyInt(Constants.SIMULATOR_DIGITAL_INPUT_PORTS, 16);
		int digitalOutputPorts = GlobalProperties.getPropertyInt(Constants.SIMULATOR_DIGITAL_OUTPUT_PORTS, 16);
		int analogInputPorts = GlobalProperties.getPropertyInt(Constants.SIMULATOR_ANALOG_INPUT_PORTS, 4);
		int analogOutputPorts = GlobalProperties.getPropertyInt(Constants.SIMULATOR_ANALOG_OUTPUT_PORTS, 2);
		
		
		try {
			udpSock = new DatagramSocket(udpPort, InetAddress.getByName("0.0.0.0"));
			udpSock.setBroadcast(true);

			byte[] recvBuf = new byte[15000];

			while (true) {
				System.out.println("Esperando por conexoes...");

				Arrays.fill(recvBuf, (byte)0);
				
		        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);		        
		        udpSock.receive(packet);
		        
		        //Pacote recebido
		        System.out.println(">>>Conexao recebida de: " + packet.getAddress().getHostAddress());
		        System.out.println(">>>Dados do pacote recebido: " + new String(packet.getData()));
	
		        // O pacote de request tem que ter o seguinte formato:
		        // FREEPLC_DISCOVER
		        // A Resposta tem o seguinte formato: FREEPLC_SIMULATOR,Versao,Release,PortaSimulador
		        String message = new String(packet.getData()).trim();
		        if (message.startsWith("FREEPLC_DISCOVER_REQUEST")) {
		        	
		        	StringBuffer resp = new StringBuffer();
		        	resp.append("FREEPLC_DISCOVER_RESPONSE,FREEPLC_SIMULATOR,")
		        		.append(System.getProperty("os.name"))
		        		.append(",")
		        		.append(Constants.FREE_PLC_SIMULATOR_VERSION)
		        		.append(".")
		        		.append(Constants.FREE_PLC_SIMULATOR_RELEASE)
		        		.append(",")
		        		.append(simuPort)
		        		.append(",")
		        		.append(digitalInputPorts)
		        		.append(",")
		        		.append(digitalOutputPorts)
		        		.append(",")
		        		.append(analogInputPorts)
		        		.append(",")
		        		.append(analogOutputPorts);
		          byte[] sendData = resp.toString().getBytes();
	
		          System.out.println("Enviando pacote de resposta: " + resp.toString());
		          
		          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
		          udpSock.send(sendPacket);
		          udpSock.disconnect();
	
		          System.out.println("Pacote enviado para: " + sendPacket.getAddress().getHostAddress());
		        }
			}
		}
		catch (IOException e) {	
			if (udpSock != null)
				udpSock.close();
			
			e.printStackTrace();
		}

	}
}
