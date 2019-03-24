package com.freeplc.simulator.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Armazena todos as variaveis e valores das expressões Ladder
public class LadderData {

	protected Map<String, LadderVariable> varMap = new HashMap<String, LadderVariable>();
	
	public LadderData() {
		
	}
	
	public void clear() {
		varMap.clear();
	}
	
	public void addVariable(String varName) {
		LadderVariable var = new LadderVariable(varName);
		varMap.put(varName, var);
	}
	
	public void removeVariable(String varName) {
		varMap.remove(varName);
	}
	
	public LadderVariable getVariable(String varName) {
		return varMap.get(varName);
	}
	
	public void setVariableValue(String varName, int value) {
		LadderVariable var = getVariable(varName);
		
		if (var != null) {
			var.setCurrentValue(value);
			
			varMap.replace(varName, var);
		}
	}

	// Toggle sets: 1->0 or 0->1
	public void toggleVariableValue(String varName) {
		LadderVariable var = getVariable(varName);
		
		if (var != null) {
			// Faz o valor anterior ser o atual
			var.toggle();
			
			varMap.replace(varName, var);
		}
		
	}
	
	public int getVariableValue(String varName) {
		LadderVariable var = getVariable(varName);
		
		if (var != null)
			return var.getCurrentValue();
			
		return -1;
	}
	
	public int getVariablePreviousValue(String varName) {
		LadderVariable var = getVariable(varName);
		
		if (var != null)
			return var.getPreviousValue();
			
		return -1;
		
	}
	
	public List<LadderVariable> getLadderVariableList() {
		List<LadderVariable> list = new ArrayList<LadderVariable>();
		
		list.addAll(varMap.values());
		
		return list;
	}
}
