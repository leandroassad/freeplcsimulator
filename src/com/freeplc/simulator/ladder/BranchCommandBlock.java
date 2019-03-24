package com.freeplc.simulator.ladder;

import com.freeplc.simulator.parser.LadderParser;

public class BranchCommandBlock extends AbstractLadderCommandBlock {

	LadderExpression firstExpression;
	LadderExpression secondExpression;
	
	public BranchCommandBlock(LadderParser parser, LadderExpression expression) {
		super(parser, expression);
		firstExpression = new LadderExpression(parser);
		secondExpression = new LadderExpression(parser);
	}
	
	public boolean evaluate(boolean firstExecution) {
		
		firstExpression.evaluateExpression(firstExecution);
		secondExpression.evaluateExpression(firstExecution);
		
		return (firstExpression.getExpressionValue() | secondExpression.getExpressionValue());
	}
	
	public void addCommandBlockToFirstExpression(int commandBlock, String varName) {
		firstExpression.addCommandBlock(commandBlock, varName);
	}

	public void addCommandBlockToSecondExpression(int commandBlock, String varName) {
		secondExpression.addCommandBlock(commandBlock, varName);
	}

}
