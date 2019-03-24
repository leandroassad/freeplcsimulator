package com.freeplc.simulator.ladder;

import com.freeplc.simulator.parser.LadderParser;

public class ExamineIFClosedCommandBlock extends AbstractLadderCommandBlock {

	public ExamineIFClosedCommandBlock(LadderParser parser, LadderExpression expression) {
		super(parser, expression);
	}

	public boolean evaluate(boolean firstEecution) {
		
		int value = parser.getValueOfVariable(varName);
		
		return (value==1) ? true : false;
	}
	
}
