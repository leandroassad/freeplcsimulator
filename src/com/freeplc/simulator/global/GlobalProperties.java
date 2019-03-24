package com.freeplc.simulator.global;

import java.util.Properties;

public class GlobalProperties {

	protected Properties props;
	protected GlobalProperties() {
		
	}
	
	public static GlobalProperties global = new GlobalProperties();
	public static void setProperties(Properties newProps) {
		global.props = newProps;
	}

	public static String getPropertyString(String key) {
		if (global.props != null) {
			return global.props.getProperty(key);
		}
		
		return null;
	}

	public static String getPropertyString(String key, String defaultValue) {
		if (global.props != null) {
			return global.props.getProperty(key);
		}
		
		return defaultValue;
	}
	
	public static void setPropertyString(String key, String value) {
		if (global.props != null) {
			global.props.setProperty(key, value);
		}
	}
	
	public static int getPropertyInt(String key) {
		if (global.props != null) {
			if (global.props.getProperty("key") != null)
			return Integer.parseInt(global.props.getProperty(key));
		}
		
		return -1;
	}

	public static int getPropertyInt(String key, int defaultValue) {
		if (global.props != null) {
			if (global.props.getProperty("key") != null)
			return Integer.parseInt(global.props.getProperty(key));
		}
		
		return defaultValue;
	}

	
	public static void setPropertyInt(String key, int value) {
		if (global.props != null) {
			global.props.setProperty(key, Integer.toString(value));
		}
	}
}
