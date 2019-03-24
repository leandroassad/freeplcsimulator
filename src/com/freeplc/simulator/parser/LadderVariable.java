package com.freeplc.simulator.parser;

public class LadderVariable {
	
	String varName;
	int previousValue;
	int currentValue;
	
	public LadderVariable(String name) {
		varName = name;
		previousValue = 0;
		currentValue = 0;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public int getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(int previousValue) {
		this.previousValue = previousValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		previousValue = this.currentValue;
		this.currentValue = currentValue;
	}
	
	public void toggle() {
		previousValue = currentValue;
		currentValue = (currentValue == 1) ? 0 : 1;
		
	}
}
