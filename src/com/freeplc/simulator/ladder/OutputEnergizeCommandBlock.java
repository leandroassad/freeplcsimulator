package com.freeplc.simulator.ladder;

import com.freeplc.simulator.parser.LadderParser;

public class OutputEnergizeCommandBlock extends AbstractLadderCommandBlock {
	public OutputEnergizeCommandBlock(LadderParser parser, LadderExpression expression) {
		super(parser, expression);
	}

	public boolean evaluate(boolean firstExecution) {
		parser.setValueOfVariable(varName, (expression.getExpressionValue()==true) ? 1 : 0);
		
		return true;
	}

}
