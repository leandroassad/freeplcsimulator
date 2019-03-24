package com.freeplc.simulator.global;

public class Constants {
	public static final String FREE_PLC_SIMULATOR_VERSION = "01";
	public static final String FREE_PLC_SIMULATOR_RELEASE = "00-Alpha";
	
	// Porta default para descoberta de PLCs
	public static final int UDP_PORT_DEFAULT = 8888;
	
	// Propriedade usada para carregar a porta para discovery
	public static final String UDP_PORT_PROPERTY = "freeplc.udpPort";
	
	public static final String SIMULATOR_PORT_PROPERTY = "freeplc.simuPort";
	public static final int SIMULATOR_PORT_DEFAULT = 5000;
	
	public static final String SIMULATOR_DIGITAL_INPUT_PORTS = "freeplc.digitalInputPorts";
	public static final String SIMULATOR_DIGITAL_OUTPUT_PORTS = "freeplc.digitalOutputPorts";
	public static final String SIMULATOR_ANALOG_INPUT_PORTS = "freeplc.analogInputPorts";
	public static final String SIMULATOR_ANALOG_OUTPUT_PORTS = "freeplc.analogOutputPorts";
	
	public static final byte STX = 0x02;
	public static final byte ETX = 0x03;
}
