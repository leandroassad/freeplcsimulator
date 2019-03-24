package com.freeplc.simulator.parser;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

public class LadderFile {

	public static final int FILE_TYPE_DIGITAL_INPUT = 0; 
	public static final int FILE_TYPE_DIGITAL_OUTPUT = 1; 
	public static final int FILE_TYPE_ANALOG_INPUT = 2; 
	public static final int FILE_TYPE_ANALOG_OUTPUT = 3; 
	public static final int FILE_TYPE_MEMORY = 4; 
	public static final int FILE_TYPE_NUMERIC = 5; 
	public static final int FILE_TYPE_FLOAT = 6;
	public static final int FILE_TYPE_COUNTER = 7;	// Previsto mas nao implementado
	public static final int FILE_TYPE_TIMER = 8;	// Previsto mas nao implementado
	
	public static Pattern wordVarNamePattern = Pattern.compile("([IONF]):(\\d)\\.?(\\d)?");
	public static Pattern bitVarNamePattern = Pattern.compile("([IOM]):(\\d)\\/(\\d)");
	
	public LadderFile() {
	}

	// Propriedades do arquivo
	@Getter @Setter private String filename;	// I, O, M, N ou F (C e T no futuro)
	@Getter @Setter private int slotIndex;		// Usados em arquivos I ou O pois sao "Slots fisicos"
	@Getter @Setter private int wordIndex;		// Usados em M, N ou F
	@Getter @Setter private int bitIndex;	// Valor que varia de 0 a 15  
	@Getter @Setter private int type;			// Um dos FILE_TYPE_* acima

	// Valores
	@Getter @Setter private BitSet bitSetValue = new BitSet(16);									// Valores usado nas entradas e saidas digitais
	@Getter @Setter private ArrayList<Integer> numericValue = new ArrayList<Integer>();				// Valores numerico, usado nas variaveis N
	@Getter @Setter private ArrayList<Double> floatValue = new ArrayList<Double>(); 				// Valores para leituras e saidas analogicas

	public static LadderFile parseVariable(String varName) {
		LadderFile lf = null;
		
		// Primeiro, verifica se é uma variavel do tipo bit
		Matcher m = bitVarNamePattern.matcher(varName);
		if (m.matches()) {
			lf = new LadderFile();
			String _filename = m.group(1);
			lf.setFilename(_filename);
			lf.setSlotIndex(Integer.parseInt(m.group(2)));
			lf.setBitIndex(Integer.parseInt(m.group(3)));
			switch(_filename) {
			case "I":
				lf.setType(FILE_TYPE_DIGITAL_INPUT);
				break;
			case "O":
				lf.setType(FILE_TYPE_DIGITAL_OUTPUT);
				break;
			case "M":
				lf.setType(FILE_TYPE_MEMORY);
				break;
			}
		}
		else {
			m = wordVarNamePattern.matcher(varName);
			if (m.matches()) {
				lf = new LadderFile();
				String _filename = m.group(1);
				lf.setSlotIndex(Integer.parseInt(m.group(2)));
				if (_filename.equals("I") || _filename.equals("O")) {
					lf.setWordIndex(Integer.parseInt(m.group(3)));
				}
				switch(_filename) {
					case "I":
						lf.setType(FILE_TYPE_ANALOG_INPUT);
						break;
					case "O":
						lf.setType(FILE_TYPE_ANALOG_OUTPUT);
						break;
					case "N":
						lf.setType(FILE_TYPE_NUMERIC);
					case "F":
						lf.setType(FILE_TYPE_FLOAT);
				}
			}
		}
		
		return lf;
	}
	
	public int getBitValue(int _bitIndex) {
		return (bitSetValue.get(_bitIndex) == true) ? 1 : 0;
	}
	
	public void setBitValue(int _bitIndex, int _bitValue) {
		bitSetValue.set(_bitIndex, (_bitValue==1) ? true : false);
	}
		
	public static void main(String[] args) {
		LadderFile lf1 = LadderFile.parseVariable("I:0/0");
		LadderFile lf2 = LadderFile.parseVariable("I:0/1");
		LadderFile lf3 = LadderFile.parseVariable("I:0/5");
		
		lf1.setBitValue(0, 1);
		lf1.setBitValue(1, 1);
		lf1.setBitValue(2, 0);
		lf1.setBitValue(5, 1);
		
		System.out.println("Valor do Bit 0: " + lf1.getBitValue(0));
		System.out.println("Valor do Bit 1: " + lf1.getBitValue(1));
		System.out.println("Valor do Bit 2: " + lf1.getBitValue(2));
		System.out.println("Valor do Bit 5: " + lf1.getBitValue(5));
	}
	
}


