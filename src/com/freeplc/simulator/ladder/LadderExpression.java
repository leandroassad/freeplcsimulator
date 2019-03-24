package com.freeplc.simulator.ladder;

import java.util.ArrayList;
import java.util.List;

import com.freeplc.simulator.parser.LadderParser;

public class LadderExpression {
	
	public static final int XIC = 0;	// Examina se 1
	public static final int XIO = 1;	// Examina se 0
	public static final int OTE = 2;	// Output Energize
	public static final int BST = 3;	// OR, Inicio
	public static final int NXB = 4;	// OR, Meio
	public static final int BND = 5;	// OR, Fim
	public static final int OSR = 6;	// One Shot Rising
	public static final int OTL = 7;	// Output Latch
	public static final int OTU = 8;	// Output Unlatch

	List<LadderCommandBlock> commandBlockList = new ArrayList<LadderCommandBlock>();
	LadderParser parser = null;
	boolean expressionValue = true;
	
	public LadderExpression(LadderParser parser) {
		this.parser = parser;
	}
	
	public void addCommandBlock(int commandBlock, String varName) {
		LadderCommandBlock block = LadderCommandBlockBuilder.buildLadderCommandBlock(commandBlock, parser, this);
		block.setVarName(varName);
		commandBlockList.add(block);		
	}
	
	
	public void addCommandBlock(LadderCommandBlock block) {
		commandBlockList.add(block);
	}
	
	public void evaluateExpression(boolean firstExecution) {
		expressionValue = true;
		for (LadderCommandBlock ladderCommandBlock : commandBlockList) {
			expressionValue &= ladderCommandBlock.evaluate(firstExecution);
		}
	}
	
	public boolean getExpressionValue() {
		return expressionValue;
	}
	
}
