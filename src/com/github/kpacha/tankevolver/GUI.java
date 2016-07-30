package com.github.kpacha.tankevolver;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import com.github.kpacha.tankbuilder.Individual;

import java.awt.Color;

import javax.swing.JSplitPane;
import java.awt.GridLayout;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;

public class GUI {

	private JFrame frmTankEvolver;
	private Runner runner;
	private int generations;
	private int population;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			GUI window = new GUI(100, 50);
			window.frmTankEvolver.setVisible(true);
			window.runner.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public GUI(int generations, int population) {
		runner = new Runner(generations, population);
		this.generations = generations;
		this.population = population;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTankEvolver = new JFrame();
		frmTankEvolver.setTitle("Tank evolver");
		frmTankEvolver.setBounds(50, 50, 900, 800);
		frmTankEvolver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel statusPanel = new StatusPanel(runner);
		frmTankEvolver.getContentPane().add(statusPanel, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.1);
		frmTankEvolver.getContentPane().add(splitPane, BorderLayout.CENTER);

		JSplitPane innerSplitPane = new JSplitPane();
		innerSplitPane.setResizeWeight(0.7);
		splitPane.setRightComponent(innerSplitPane);

		JPanel graphContainer = new JPanel();
		innerSplitPane.setLeftComponent(graphContainer);

		graphContainer.setLayout(new GridLayout(2, 0, 0, 0));

		graphContainer.add(new GraphPanel(generations, "Generations", Color.RED, runner) {

			@Override
			public void onGenerationCompleted(Individual fittest) {
				trace.addPoint(fittest.generation(), fittest.fitness());
			}

			@Override
			public void onIndividualCompleted(Individual individual) {
			}

		});

		graphContainer.add(new GraphPanel(population, "Individuals", Color.BLUE, runner) {

			@Override
			public void onGenerationCompleted(Individual fittest) {
				trace.removeAllPoints();
			}

			@Override
			public void onIndividualCompleted(Individual individual) {
				trace.addPoint(individual.id(), individual.fitness());
			}

		});

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		innerSplitPane.setRightComponent(tabbedPane);
		
		JTextPane logsPane = new LoggerPanel(runner);
		tabbedPane.addTab("Log", null, new JScrollPane(logsPane), null);

		JTextPane codePane = new JTextPane();
		codePane.setEditable(false);
		tabbedPane.addTab("Robot", null, new JScrollPane(codePane), null);

		JPanel listContainer = new FittestPanel(runner, codePane);
		splitPane.setLeftComponent(listContainer);
	}

}
