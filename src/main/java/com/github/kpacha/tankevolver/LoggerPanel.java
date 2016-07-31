package com.github.kpacha.tankevolver;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.github.kpacha.tankbuilder.Individual;

public class LoggerPanel extends JTextPane implements EvolutionLogger, EvolutionObserver {

	private JScrollPane logsScrollPane;

	public LoggerPanel(Runner runner){
		setEditable(false);
		runner.addLogger(this);
		runner.addObserver(this);
		logsScrollPane = new JScrollPane(this);
	}

	public JScrollPane getScrollPane(){
		return logsScrollPane;
	}

	@Override
	public void log(String msg) {
		setText(getText() + "\n" + msg);
		JScrollBar sb = logsScrollPane.getVerticalScrollBar();
		sb.setValue(sb.getMaximum());
	}

	@Override
	public void onGenerationCompleted(Individual fittest) {
		setText("");
	}

	@Override
	public void onIndividualCompleted(Individual individual) {
	}
}
