package com.github.kpacha.tankevolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.github.kpacha.tankbuilder.Individual;
import com.github.kpacha.tankbuilder.Population;

import net.sf.robocode.io.Logger;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotResults;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;

public class Runner {

	private static final String OPPONENT = "sample.SpinBot";
	private static final int EXECUTION_TIMEOUT = 5;
	private static final int NUM_ROUNDS = 5;
	private static BattlefieldSpecification fieldSpecs = new BattlefieldSpecification();
	private RobocodeEngine engine;
	private int generations;
	private int population;
	private Population currentGeneration;
	private BattleObserver listener = new BattleObserver();

	private List<EvolutionObserver> observers = new ArrayList<EvolutionObserver>();
	private List<EvolutionLogger> loggers = new ArrayList<EvolutionLogger>();

	public Runner(int generations, int population) {
		this.generations = generations;
		this.population = population;
	}

	public void addObserver(EvolutionObserver observer) {
		observers.add(observer);
	}

	public void addLogger(EvolutionLogger logger) {
		loggers.add(logger);
	}

	protected void log(String msg) {
		for (EvolutionLogger logger : loggers) {
			logger.log(msg);
		}
	}

	public void run() {
		engine = new RobocodeEngine();
		engine.addBattleListener(listener);
		currentGeneration = Population.random(population);
		for (int g = 0; g < generations; g++) {
			runGeneration(g);
			Individual winner = currentGeneration.fittest();
			for (EvolutionObserver o : observers) {
				o.onGenerationCompleted(winner);
			}
			currentGeneration = currentGeneration.nextGeneration();
		}
		engine.removeBattleListener(listener);
		engine.close();
	}

	private void runGeneration(int generationNum) {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		int performance;
		for (Individual subject : currentGeneration.getIndividuals()) {
			performance = -1;
			try {
				InlineCompiler.compile(subject.toString(), subject.filePath(), subject.fqName());
				Future<Boolean> f = ex.submit(new Callable<Boolean>() {
					public Boolean call() {
						try {
							engine.runBattle(new BattleSpecification(NUM_ROUNDS, fieldSpecs,
									engine.getLocalRepository(OPPONENT + "," + subject.robotName())), true);
							return true;
						} catch (Exception e) {
							log("#" + (population * generationNum + subject.id()) + ": " + generationNum + "-"
									+ subject.id() + ": -1");
							return false;
						}
					}
				});
				if (f.get(EXECUTION_TIMEOUT, TimeUnit.SECONDS)) {
					performance = calculateFitness(generationNum, subject.id(), subject.robotName());
				}
			} catch (Exception e) {
				log("#" + (population * generationNum + subject.id()) + ": " + generationNum + "-" + subject.id()
						+ ": -1");
			}
			subject.fitness_$eq(performance);
			for (EvolutionObserver o : observers) {
				o.onIndividualCompleted(subject);
			}
		}
	}

	private int calculateFitness(int generationNum, int individual, String subject) {
		int fitness = 1;
		int subjectPoints = 0;
		int totalPoints = 0;
		for (RobotResults r : listener.lastResults) {
			totalPoints += r.getScore();
			if (r.getRobot().getName().equalsIgnoreCase(subject)) {
				subjectPoints = r.getScore();
			}
			log("#" + (population * generationNum + individual) + ": " + generationNum + "-" + individual + ": "
					+ r.getRobot().getName() + " ended #" + r.getRank() + " with: " + r.getScore());
		}
		fitness += (int) (1000.0 * subjectPoints / totalPoints);
		log("#" + (population * generationNum + individual) + ": " + generationNum + "-" + individual + ": " + fitness);
		return fitness;
	}

	public static void main(String[] args) {
		Runner runner = new Runner(100, 100);
		runner.run();
	}

	class BattleObserver extends BattleAdaptor {
		public RobotResults[] lastResults;

		@Override
		public void onBattleError(final BattleErrorEvent event) {
			Logger.realErr.println(event.getError());
			log(event.getError());
		}

		@Override
		public void onBattleCompleted(final BattleCompletedEvent event) {
			lastResults = RobotResults.convertResults(event.getIndexedResults());
		}
	}
}
