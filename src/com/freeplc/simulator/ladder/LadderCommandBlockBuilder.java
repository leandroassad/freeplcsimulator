package com.freeplc.simulator.ladder;

import com.freeplc.simulator.parser.LadderParser;

public class LadderCommandBlockBuilder {

	public static LadderCommandBlock buildLadderCommandBlock(int commandBlock, LadderParser parser, LadderExpression expression) {
		LadderCommandBlock block = null;
		
		switch (commandBlock) {
		case LadderExpression.XIC:
			block = new ExamineIFClosedCommandBlock(parser, expression);
			break;
		case LadderExpression.XIO:
			block = new ExamineIfOpenCommandBlock(parser, expression);
			break;
		case LadderExpression.OTE:
			block = new OutputEnergizeCommandBlock(parser, expression);
			break;
		case LadderExpression.BST:
			block = new BranchCommandBlock(parser, expression);
			break;
		case LadderExpression.OSR:
			block = new OneShotRisingCommandBlock(parser, expression);
			break;
		case LadderExpression.OTL:
			break;
		case LadderExpression.OTU:
			break;
		default:
				break;
		}
		
		if (block != null) block.setCommand(commandBlock);
		return block;
	}
}
