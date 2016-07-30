package com.github.kpacha.tankevolver;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;

public abstract class GraphPanel extends JPanel implements EvolutionObserver {
	protected ITrace2D trace;

	public GraphPanel(int size, String name, Color color, Runner runner) {
		setLayout(new GridLayout(0, 1, 0, 0));
		setSize(500, 250);
		trace = new Trace2DLtd(size, name);
		trace.setColor(color);
		add(createChart(trace));
		setVisible(true);

		runner.addObserver(this);
	}

	private Chart2D createChart(ITrace2D trace) {
		Chart2D chart = new Chart2D();
	    chart.addTrace(trace);
		return chart;
	}

}