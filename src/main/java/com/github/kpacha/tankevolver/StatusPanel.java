package com.github.kpacha.tankevolver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.github.kpacha.tankbuilder.Individual;

public class StatusPanel extends JPanel implements EvolutionObserver {
	private JLabel evolution;
	private JLabel current;
	private JLabel best;
	private int bestPerformance = -1;

	public StatusPanel(Runner runner) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setLayout(new GridLayout(0, 3, 0, 0));

		evolution = new JLabel("Fittest: Undefined");
		current = new JLabel("Current: Undefined");
		best = new JLabel("Best: Undefined");

		add(best);
		add(evolution);
		add(current);

		runner.addObserver(this);
	}

	@Override
	public void onGenerationCompleted(Individual fittest) {
		evolution.setText("Fittest: " + fittest.name() + " (" + fittest.fitness() + ")");
	}

	@Override
	public void onIndividualCompleted(Individual individual) {
		current.setText("Current: " + individual.name() + " (" + individual.fitness() + ")");
		if (bestPerformance < individual.fitness()) {
			best.setText("Best: " + individual.name() + " (" + individual.fitness() + ")");
			bestPerformance = individual.fitness();
		}
	}

}
