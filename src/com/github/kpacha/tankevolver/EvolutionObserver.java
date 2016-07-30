package com.github.kpacha.tankevolver;

import com.github.kpacha.tankbuilder.Individual;

public interface EvolutionObserver {
	public void onGenerationCompleted(Individual fittest);

	public void onIndividualCompleted(Individual individual);
}
