package com.freeplc.simulator.ladder;

import com.freeplc.simulator.parser.LadderParser;

public class ExamineIfOpenCommandBlock extends AbstractLadderCommandBlock {
	public ExamineIfOpenCommandBlock(LadderParser parser, LadderExpression expression) {
		super(parser, expression);
	}

	public boolean evaluate(boolean firstExecution) {
		int value = parser.getValueOfVariable(varName);
		
		return (value==0) ? true : false;
	}

}
