package com.freeplc.simulator.command;

public class DownloadApplicationCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.DOWNLOAD_APPLICATION;
	}
	
	public void process(byte[] data) {
		int index = 0;
		int startIndex = 0;
		
		StringBuffer ladder = new StringBuffer();
		System.out.println("Processando comando DownloadAplication");
		while (index < data.length) {
			while (data[index] != 0x0A) {
				index++;
			}
			String rungLine = new String(data, startIndex, index-startIndex);
			ladder.append(rungLine).append("\n");
			System.out.println("Linha do Ladder: " + rungLine);
			index++;
			startIndex = index;
		}
		
		simulator.addLadderToParser(ladder.toString());
		
	}
}
