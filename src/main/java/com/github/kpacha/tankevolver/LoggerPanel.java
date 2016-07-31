package com.github.kpacha.tankevolver;

import javax.swing.JTextPane;

import com.github.kpacha.tankbuilder.Individual;

public class LoggerPanel extends JTextPane implements EvolutionLogger, EvolutionObserver {

	public LoggerPanel(Runner runner){
		setEditable(false);
		runner.addLogger(this);
		runner.addObserver(this);
	}

	@Override
	public void log(String msg) {
		setText(getText() + "\n" + msg);
	}

	@Override
	public void onGenerationCompleted(Individual fittest) {
		setText("");
	}

	@Override
	public void onIndividualCompleted(Individual individual) {
	}
}
