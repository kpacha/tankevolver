package com.github.kpacha.tankevolver;

import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.kpacha.tankbuilder.Individual;

public class FittestPanel extends JPanel implements ListSelectionListener, EvolutionObserver {
	private JList<String> list;
	private DefaultListModel<String> listModel;
	private List<String> paths = new ArrayList<String>();
	private JTextPane output;

	public FittestPanel(Runner runner, JTextPane output) {
		setLayout(new GridLayout(1, 0, 0, 0));
		setVisible(true);
		this.listModel = new DefaultListModel<String>();
		this.output = output;

		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.setVisible(true);
		add(list);

		runner.addObserver(this);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			if (list.getSelectedIndex() == -1) {
				output.setText("");
			} else {
				try {
					String content = new String(Files.readAllBytes(FileSystems.getDefault().getPath("robots",
							paths.get(listModel.size() - 1 - list.getSelectedIndex()))));
					output.setText(content);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onGenerationCompleted(Individual fittest) {
		listModel.insertElementAt("Gen #" + fittest.generation() + ": " + fittest.fitness(), 0);
		paths.add(fittest.filePath());
	}

	@Override
	public void onIndividualCompleted(Individual individual) {
	}
}