package com.freeplc.simulator.parser;

import java.util.ArrayList;

import lombok.Getter;

public class LadderFileManager {
	@Getter ArrayList<LadderFile> inputLadderFileList = new ArrayList<LadderFile>();
	@Getter ArrayList<LadderFile> outputLadderFileList = new ArrayList<LadderFile>();
	@Getter ArrayList<LadderFile> memoryLadderFileList = new ArrayList<LadderFile>();
	@Getter ArrayList<LadderFile> numericLadderFileList = new ArrayList<LadderFile>();
	@Getter ArrayList<LadderFile> floatLadderFileList = new ArrayList<LadderFile>();

	private static LadderFileManager lfManager = null;
	private LadderFileManager() {
		
	}
	
	public static LadderFileManager getInstance() {
		if (lfManager == null) lfManager = new LadderFileManager();
		
		return lfManager;
	}
	
	public boolean findBySlot(ArrayList<LadderFile> list, LadderFile lf) {
		boolean ret = false;
		
		for (LadderFile ladderFile : list) {
			if ((lf.getType() == ladderFile.getType()) && (lf.getSlotIndex() == ladderFile.getSlotIndex())) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public boolean findByWord(ArrayList<LadderFile> list, LadderFile lf) {
		boolean ret = false;
		
		for (LadderFile ladderFile : list) {
			if ((lf.getType() == ladderFile.getType()) && (lf.getWordIndex() == ladderFile.getWordIndex())) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public LadderFile getLadderFileBySlot(ArrayList<LadderFile> list, LadderFile lf) {
		LadderFile file = null;
		
		for (LadderFile ladderFile : list) {
			if ((lf.getType() == ladderFile.getType()) && (lf.getSlotIndex() == ladderFile.getSlotIndex())) {
				file= ladderFile;
				break;
			}
		}
		return file;
	}

	
	public void insertLadderFile(LadderFile ladderFile) {
		switch (ladderFile.getType()) {
		case LadderFile.FILE_TYPE_DIGITAL_INPUT:
		case LadderFile.FILE_TYPE_ANALOG_INPUT:
			if (findBySlot(inputLadderFileList, ladderFile) == false) {		// Arquivo ainda nao existe na lista de arquivos
				inputLadderFileList.add(ladderFile);
			}
			else {
				if (ladderFile.getType() == LadderFile.FILE_TYPE_DIGITAL_INPUT) {
					LadderFile theFile = getLadderFileBySlot(inputLadderFileList, ladderFile);
					if (theFile != null) {
 						theFile.setBitValue(ladderFile.getBitIndex(), ladderFile.getBitValue(ladderFile.getBitIndex()));
					}
				}
			}
			break;
		case LadderFile.FILE_TYPE_DIGITAL_OUTPUT:
		case LadderFile.FILE_TYPE_ANALOG_OUTPUT:
			if (findBySlot(outputLadderFileList, ladderFile) == false) {		// Arquivo ainda nao existe na lista de arquivos
				outputLadderFileList.add(ladderFile);
			}
			else {
				if (ladderFile.getType() == LadderFile.FILE_TYPE_DIGITAL_OUTPUT) {
					LadderFile theFile = getLadderFileBySlot(outputLadderFileList, ladderFile);
					if (theFile != null) {
						theFile.setBitValue(ladderFile.getBitIndex(), ladderFile.getBitValue(ladderFile.getBitIndex()));
					}
				}
			}
			break;
		case LadderFile.FILE_TYPE_MEMORY:
			if (findBySlot(memoryLadderFileList, ladderFile) == false) {		// Arquivo ainda nao existe na lista de arquivos
				memoryLadderFileList.add(ladderFile);
			}
			else {
				LadderFile theFile = getLadderFileBySlot(memoryLadderFileList, ladderFile);
				if (theFile != null) {
					theFile.setBitValue(ladderFile.getBitIndex(), ladderFile.getBitValue(ladderFile.getBitIndex()));
				}
			}
			break;
		case LadderFile.FILE_TYPE_NUMERIC:
			if (findByWord(numericLadderFileList, ladderFile) == false) {		// Arquivo ainda nao existe na lista de arquivos
				numericLadderFileList.add(ladderFile);
			}
			break;
		case LadderFile.FILE_TYPE_FLOAT:
			if (findByWord(floatLadderFileList, ladderFile) == false) {		// Arquivo ainda nao existe na lista de arquivos
				floatLadderFileList.add(ladderFile);
			}
			break;
		}
	}
}
