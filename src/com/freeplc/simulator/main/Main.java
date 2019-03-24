package com.freeplc.simulator.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.freeplc.simulator.global.Constants;
import com.freeplc.simulator.global.GlobalProperties;
import com.freeplc.simulator.server.DiscoveryUDPServer;
import com.freeplc.simulator.server.FreePLCSimulator;

public class Main {

	public static void main(String[] args) {
        Properties props = new Properties();
        try {
        	System.out.println("FreePLCSimulator - " + Constants.FREE_PLC_SIMULATOR_VERSION + "." + Constants.FREE_PLC_SIMULATOR_RELEASE);
        	System.out.println("Starting...");
        	
			FileInputStream in = new FileInputStream(args[0]);
			props.load(in);
			in.close();
			
			GlobalProperties.setProperties(props);
			
			FreePLCSimulator simulator = new FreePLCSimulator();
			simulator.start();
			DiscoveryUDPServer discoveryUDPServer = new DiscoveryUDPServer();
			discoveryUDPServer.start();
        }
        catch(IOException e) {
        	System.out.println("Falha iniciando FreePLCEditor: "  + e.getMessage());
        }

	}

}
