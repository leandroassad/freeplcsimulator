package com.freeplc.simulator.ladder;

public interface LadderCommandBlock {
	public void setCommand(int command);
	public int getCommand();
	public String getVarName();
	public void setVarName(String varName);
	public void set(int command, String varName);
	public boolean evaluate(boolean firstExecution);
}
