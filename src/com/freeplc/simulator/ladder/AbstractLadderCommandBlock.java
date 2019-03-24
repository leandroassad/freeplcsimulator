package com.freeplc.simulator.ladder;

import com.freeplc.simulator.parser.LadderParser;

public abstract class AbstractLadderCommandBlock implements LadderCommandBlock {
	int command;
	String varName;
	LadderParser parser;
	LadderExpression expression;
	
	public AbstractLadderCommandBlock(LadderParser parser, LadderExpression expression) {
		this.parser = parser;
		this.expression = expression;
	}
	
	public void setCommand(int command) {
		this.command = command;
	}
	
	public int getCommand() {
		return command;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public void setVarName(String varName) {
		this.varName = varName;
	}
	
	public void set(int command, String varName) {
		setCommand(command);
		setVarName(varName);
	}
		
}
