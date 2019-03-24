package com.freeplc.simulator.parser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultEditorKit;

import com.freeplc.simulator.command.PLCCommand;
import com.freeplc.simulator.command.PLCCommandMap;
import com.freeplc.simulator.ladder.BranchCommandBlock;
import com.freeplc.simulator.ladder.LadderCommandBlockBuilder;
import com.freeplc.simulator.ladder.LadderExpression;
import com.freeplc.simulator.server.FreePLCSimulator;

public class LadderParser extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	public static final String SIMULATOR_TITLE = "PLC Simulator";
	
	private static final String LOAD_LADDER = "LOAD_LADDER";
	private static final String SAVE_LADDER = "SAVE_LADDER";
	private static final String EXIT = "EXIT";
	
	ArrayList<LadderExpression> ladderExpressionList = new ArrayList<LadderExpression>();
	
	JButton runButton;			// Botao que executa o ladder em ladderText
	JButton clearOutputButton;	// Botao que limpa a saida da execucao do ladder
	JButton compileButton;		// Botao que compila o ladder e carrega as variaveis
	JTextArea ladderText;		// Campo que armazena o ladder
	JTextArea outputText;		// Campo de saida do resultado do ladder
	JTextField iterationsText;	// Numero de iteracoes para cada execucao do ladder
	JPanel executionPanel;		// Painel com os textos do ladder e saida
	JPanel buttonsPanel;		// Painel com os botoes
	JScrollPane ladderScrollPane;
	JScrollPane outScrollPane;
	JScrollPane varScrollPane;
	JTable varTable;
	DefaultTableModel varTableModel;
	JPopupMenu popup = new JPopupMenu();
	boolean firstExecution = true;
	LadderData ladderData = new LadderData();
	
	public class MyTableModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
	
	FreePLCSimulator simulator = null;;
	public LadderParser(FreePLCSimulator simulator) {
		this.simulator = simulator;
		
		setLocation(100, 100);
		setPreferredSize(new Dimension(700, 600));
		setResizable(true);
		setTitle(SIMULATOR_TITLE);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});  
		
		setLayout(new BorderLayout(5,5));	
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Arquivo");
		JMenuItem load = new JMenuItem("Carregar Ladder");
		JMenuItem save = new JMenuItem("Salvar Ladder");
		JMenuItem exit = new JMenuItem("Fechar");
		
		menu.add(load);
		menu.add(save);
		menu.add(exit);
		
		menuBar.add(menu);
		
		setJMenuBar(menuBar);
		
		load.putClientProperty("MENUITEMID", LOAD_LADDER);
		save.putClientProperty("MENUITEMID", SAVE_LADDER);
		exit.putClientProperty("MENUITEMID", EXIT);
		
		load.addActionListener(this);
		save.addActionListener(this);
		exit.addActionListener(this);
		

		createExecutionPanel();
		createVariablesPanel();
		createButtonsPanel();
		
		setResizable(true);
		
		pack();
		setVisible(true);
	}
	
	private final Action action = new DefaultEditorKit.CopyAction();
	private final String actionText = "Copiar";
	
	void createExecutionPanel() {
		executionPanel = new JPanel();
		executionPanel.setLayout(new BoxLayout(executionPanel, BoxLayout.Y_AXIS));
		
		ladderText = new JTextArea();
		outputText = new JTextArea();
		
		ladderScrollPane = new JScrollPane(ladderText);
		ladderScrollPane.setBorder(BorderFactory.createTitledBorder("Expressão LADDER"));

		outScrollPane = new JScrollPane(outputText);
		outScrollPane.setBorder(BorderFactory.createTitledBorder("Saída/Resultados"));
		JMenuItem item = new JMenuItem(action);
		item.setText(actionText);
        popup.add(item);

        outputText.addMouseListener(new PopupListener());
        ladderText.addMouseListener(new PopupListener());
		
		ladderScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ladderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		outScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		outScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ladderScrollPane.setPreferredSize(new Dimension(500, 200));
		outScrollPane.setPreferredSize(new Dimension(500,350));
		outputText.setEditable(false);
				
		executionPanel.add(ladderScrollPane);
		executionPanel.add(outScrollPane);
				
		add(executionPanel, BorderLayout.LINE_START);
		
	}
	
	void createVariablesPanel() {
		varTableModel = new MyTableModel();
		varTableModel.addColumn("Label");
		varTableModel.addColumn("Valor");
		varTable = new JTable(varTableModel);
		
		varTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int row = varTable.rowAtPoint(event.getPoint());
//				int col = varTable.columnAtPoint(event.getPoint());
				
				String varName = (String)varTableModel.getValueAt(row, 0);
//				int value = Integer.parseInt((String)varTableModel.getValueAt(row, 1));
//				varTableModel.setValueAt((value==1)?"0":"1", row, 1);
				
				int value = ladderData.getVariableValue(varName);
				ladderData.toggleVariableValue(varName);
				
				varTableModel.setValueAt((value==1)?"0":"1", row, 1);
				
				ladderData.setVariableValue(varName, (value==1)?0:1);
				
			}
		});
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int columnIndex = 0; columnIndex < varTableModel.getColumnCount(); columnIndex++)
        {
            varTable.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
		
		varScrollPane = new JScrollPane(varTable);
		varScrollPane.setBorder(BorderFactory.createTitledBorder("Variáveis"));
		varScrollPane.setPreferredSize(new Dimension(180,600));
		varScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		varScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add(varScrollPane, BorderLayout.LINE_END);

		
	}
	
	void createButtonsPanel() {
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.setPreferredSize(new Dimension(600, 50));
		runButton = new JButton("Executar");
		runButton.setToolTipText("Executa o ladder usando as variaveis e destaque");
		clearOutputButton = new JButton("Limpar Saída");
		compileButton = new JButton("Compilar/Reiniciar");
		compileButton.setToolTipText("Compila e reinicia as variaveis");
		
		buttonsPanel.add(compileButton);
		buttonsPanel.add(runButton);
		buttonsPanel.add(clearOutputButton);
		
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				executeLadder();
			}
		});

		clearOutputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				outputText.setText("");
				firstExecution = true;
			}
		});

		compileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					compileLadder();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		runButton.setEnabled(false);
		
		add(buttonsPanel, BorderLayout.PAGE_END);


	}
	
	int rowNumber = 0;
	BranchCommandBlock branch = null;
	int branchNode = -1;
		
	public void addVariable(LadderExpression expression, int expressionType, String varName) {

		if (varName != null && !tableContainsVar(varName)) {		
			Vector<Object> data = new Vector<Object>();
			
			data.add(varName);
			data.addElement("0");
			
			varTableModel.addRow(data);			
		}

		ladderData.addVariable(varName);
		
		if (branchNode != -1) {
			switch(branchNode) {
			case LadderExpression.BST:
				branch.addCommandBlockToFirstExpression(expressionType, varName);
				break;
			case LadderExpression.NXB:
				branch.addCommandBlockToSecondExpression(expressionType, varName);
				break;
			case LadderExpression.BND:
				expression.addCommandBlock(branch);
			}
		}
		else {
			expression.addCommandBlock(expressionType, varName);
		}
		
	}
	
	public int ladderEvaluateExpression(LadderExpression expression, String[] token, int index) {
		int newIndex = index;
		String varName;
		
		switch(token[newIndex]) {
		case " ":
		case "":
			newIndex++;
			break;
		case "XIC":
			newIndex++;
			varName = token[newIndex++];
			addVariable(expression, LadderExpression.XIC, varName);
			break;
		case "XIO":
			newIndex++;
			varName = token[newIndex++];
			addVariable(expression, LadderExpression.XIO, varName);
			break;
		case "BST":
			newIndex++;
			branch = (BranchCommandBlock)LadderCommandBlockBuilder.buildLadderCommandBlock(LadderExpression.BST, this, expression);
			branchNode = LadderExpression.BST; 
			newIndex = ladderEvaluateExpression(expression, token, newIndex);
			break;
		case "NXB":
			newIndex++;
			branchNode = LadderExpression.NXB;
			newIndex = ladderEvaluateExpression(expression, token, newIndex);
			break;
		case "BND":
			newIndex++;
			branchNode = LadderExpression.BND;
			addVariable(expression, LadderExpression.BND, null);
			branchNode = -1;
			break;
		case "OTE":
			newIndex++;
			varName = token[newIndex++];
			addVariable(expression, LadderExpression.OTE, varName);
			break;
		case "OSR":
			newIndex++;
			varName = token[newIndex++];
			addVariable(expression, LadderExpression.OSR, varName);
			break;
		case "OTL":
			newIndex++;
			varName = token[newIndex++];
			break;
		case "OTU":
			newIndex++;
			varName = token[newIndex++];
			break;
		default:
			outputText.append("Token: [" + token[newIndex] + "] invalido\n");
			newIndex++;
			break;
		}
		return newIndex;
	}
	
	public boolean ladderParseLine(String line) {
		String [] token = line.split(" ");
		int index = 0;
		
		LadderExpression expression = new LadderExpression(this);
		while (index < token.length) {
			index = ladderEvaluateExpression(expression, token, index);
		}
		
		ladderExpressionList.add(expression);
		
		return true;
	}
	
	public void compileLadder() throws IOException {		
		String ladder = ladderText.getText();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(ladder.getBytes())));
		String line;
		
		// Limpa mapa de variaveis
		ladderData.clear();
		
		if (ladder.trim().length() > 0) {
			int nRows = varTableModel.getRowCount();
			for (int i=0; i<nRows; i++) {
				varTableModel.removeRow(0);
			}
			rowNumber = 0;
			
			if (ladderExpressionList != null) {
				ladderExpressionList.clear();
			}
			
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0)
					ladderParseLine(line);
			}
			firstExecution = true;
			runButton.setEnabled(true);			
		}
		else {
			JOptionPane.showMessageDialog(this,  "Por favor, entre com a expressão LADDER");
		}
	}
	
	
	public void executeLadder() {
		
		int rowCount = varTableModel.getRowCount();
		if (firstExecution) {
			StringBuffer header = new StringBuffer();
			for (int i=0; i<rowCount; i++) {
				String name = (String)varTableModel.getValueAt(i, 0);
				header.append(name).append("  ");
			}
			header.append("\n");
			outputText.append(header.toString());
			
		}
		for (LadderExpression ladderExpression : ladderExpressionList) {
			ladderExpression.evaluateExpression(firstExecution);
		}
		
		StringBuffer line = new StringBuffer();
		for (int i=0; i<rowCount; i++) {
			String name = (String)varTableModel.getValueAt(i, 0);
			String value = (String)varTableModel.getValueAt(i,  1);
			int len;
			if (i == 0) {
				len = (name.length() + 2)/2 + 1;
			}
			else len = name.length() + 5;
			
			for (int j=0; j<len; j++) line.append(" ");
			line.append(value);	
		}
		line.append("\n");
		outputText.append(line.toString());
		
		firstExecution = false;

		// Manda os dados para o FreePLCEditor
		sendUpdateStatus();
		
	}
	
	// Arrumar esta merda!!!!!!!!!!!!!
	public void sendUpdateStatus() {
		if (simulator.isConnected()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			List<LadderVariable> list = ladderData.getLadderVariableList();
			for (LadderVariable ladderVariable : list) {
				if (ladderVariable.getVarName() != null) {
					LadderFile lf = LadderFile.parseVariable(ladderVariable.getVarName());
					byte fileType=0x00;
					byte slot = (byte)lf.getSlotIndex();
					byte bit = (byte)lf.getBitIndex();
					byte value = (byte)ladderVariable.getCurrentValue();
					switch(lf.getFilename()) {
					case "I":
						fileType = 0x01;
						break;
					case "O":
						fileType = 0x02;
						break;
					case "M":
						fileType = 0x03;
						break;
					}
					baos.write(fileType);
					baos.write(slot);
					baos.write(bit);
					baos.write(value);
					System.out.println("Variavel: " + ladderVariable.getVarName() + " = " + ladderVariable.getCurrentValue());
					System.out.println("File: " + fileType + " Slot: " + slot + " Bit: " + bit + " Valor: " + value);
				}
			}			
			System.out.println("---------------------------------");			
			PLCCommand command = PLCCommandMap.getPLCCommandMap().getPLCCommand((byte)PLCCommand.STATUS, simulator);
			command.prepareCommand(baos.toByteArray());
			simulator.sendDataToEditor(command.getBytes());				
		}		
	}
	
	protected void printLadderFile(List<LadderFile> list) {
		for (LadderFile ladderFile : list) {
			System.out.println("File: " + ladderFile.getFilename() + " Slot: " + ladderFile.getSlotIndex());
			BitSet bitSet = ladderFile.getBitSetValue();
			byte [] bytes = bitSet.toByteArray();
			for (int i=0; i<bytes.length; i++) {
				System.out.println("--------->  " + Integer.toHexString(bytes[i]&0xFF));
			}
			
		}
		
		System.out.println("---------------------------------");
		
	}
	
	public int getValueOfVariable(String varName) {
		int value = 0;
		
		int numberOfRows = varTableModel.getRowCount();
		for (int i=0; i<numberOfRows; i++) {
			String name = (String)varTableModel.getValueAt(i, 0);
			if (name.equals(varName)) {
				value = Integer.parseInt((String)varTableModel.getValueAt(i, 1));
				break;
			}			
		}
		
		return value;
	}
	
	public void setValueOfVariable(String varName, int value) {
		int numberOfRows = varTableModel.getRowCount();
		for (int i=0; i<numberOfRows; i++) {
			String name = (String)varTableModel.getValueAt(i, 0);
			if (name.equals(varName)) {
				varTableModel.setValueAt((value==1)?"1":"0", i, 1);
				break;
			}			
		}
		
		ladderData.setVariableValue(varName, value);
		
	}
	
	public boolean tableContainsVar(String varName) {
		int numberOfRows = varTableModel.getRowCount();
		for (int i=0; i<numberOfRows; i++) {
			String name = (String)varTableModel.getValueAt(i, 0);
			if (name.equals(varName)) return true;	
		}
		return false;
		
	}
/**
 * Ladder de testes
 * XIC I:1/0 XIC I:1/1 BST XIC A XIC B NXB XIC C XIC D BND XIC E OTE O:2/0
 *  
 BST XIC I:0.0/0 NXB XIC B3:0/0 BND XIO B3:0/2 OTE B3:0/0 
 BST XIO I:0.0/0 NXB XIC B3:0/1 BND XIC B3:0/0 OTE B3:0/1 
 BST XIC I:0.0/0 XIC B3:0/0 XIC B3:0/1 NXB XIC B3:0/2 XIC I:0.0/0 BND OTE B3:0/2 
 XIC B3:0/0 OTE O:0.0/0
  
 * @param args
 */
	public static void main(String[] args) throws Exception {
		LadderParser parser = new LadderParser(null);
	}

	
	private class PopupListener extends MouseAdapter {
	      public void mousePressed(MouseEvent e) {
	         maybeShowPopup(e);
	     }

	     public void mouseReleased(MouseEvent e) {
	         maybeShowPopup(e);
	     }

	     private void maybeShowPopup(MouseEvent e) {
	         if (e.isPopupTrigger()) {
	             popup.show(e.getComponent(),
	                        e.getX(), e.getY());
	         }
	     }
	   }

	String lastPath = null;
	public void loadLadderFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Ladder Extensions (.ldr)", "ldr"));
		fileChooser.setDialogTitle("Selecione um arquivo ladder para carga");
		if (lastPath == null) {
			try {
			lastPath = new File(".").getCanonicalPath();
			} catch (IOException e){}
		}
		fileChooser.setCurrentDirectory(new File(lastPath));
		
		int ret = fileChooser.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		
		File ladderFile = fileChooser.getSelectedFile();
		lastPath = ladderFile.getPath();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(ladderFile)));
			String line;
			ladderText.setText("");
			while ((line = reader.readLine()) != null) {
				ladderText.append(line);
				ladderText.append("\n");
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Falha abrindo arquivo: " + fileChooser.getSelectedFile(), "LadderParser", JOptionPane.WARNING_MESSAGE);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Falha lendo arquivo: " + fileChooser.getSelectedFile(), "LadderParser", JOptionPane.WARNING_MESSAGE);			
		}
		finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}

	}
	
	public void saveLadderFile() {
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setDialogTitle("Selecione o arquivo ladder");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Ladder Extensions (.ldr)", "ldr"));
		if (lastPath == null) {
			try {
			lastPath = new File(".").getCanonicalPath();
			} catch (IOException e){}
		}
		fileChooser.setCurrentDirectory(new File(lastPath));

		
		int ret = fileChooser.showSaveDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		
		File ladderFile = fileChooser.getSelectedFile();
		PrintStream out = null;
		try {
			out = new PrintStream(ladderFile);
			out.println(ladderText.getText());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "Falha salvando arquivo ladder");
			return;
		}
		finally {
			out.close();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String item = (String)((JMenuItem)e.getSource()).getClientProperty("MENUITEMID");
		
		switch (item) {
		case LOAD_LADDER:
			loadLadderFile();
			break;
		case SAVE_LADDER:
			saveLadderFile();
			break;
		case EXIT:
			System.exit(0);;
			break;
		}		
	}
	
	public void setLadder(String ladder) {
		ladderText.setText("");
		ladderText.setText(ladder);
		try {
			compileLadder();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Programa Ladder com problemas, favor verificar no FreePLCEditor.");
		}
	}
	
	public void ladderStep() {
		executeLadder();
	}
	
	public void ladderStop() {
		
	}
	
	public void ladderRun() {
		
	}
}
