package com.freeplc.simulator.ladder;

import com.freeplc.simulator.parser.LadderParser;

public class OneShotRisingCommandBlock extends AbstractLadderCommandBlock {

	public OneShotRisingCommandBlock(LadderParser parser, LadderExpression expression) {
		super(parser, expression);
	}

	public boolean evaluate(boolean firstExecution) {
		
		int value = parser.getValueOfVariable(varName);
		
		return (value==1) ? true : false;
	}
	
}
