package supplementary.experiments.concepts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.commons.rng.RandomProviderState;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import org.json.JSONObject;
import org.json.JSONTokener;

import features.feature_sets.network.JITSPatterNetFeatureSet;
import game.Game;
import game.rules.end.End;
import game.rules.end.EndRule;
import game.rules.phase.Phase;
import game.rules.play.moves.Moves;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import main.CommandLineArgParse;
import main.CommandLineArgParse.ArgOption;
import main.CommandLineArgParse.OptionTypes;
import main.DaemonThreadFactory;
import main.collections.ListUtils;
import manager.utils.game_logs.MatchRecord;
import metrics.Metric;
import metrics.MetricsTracker;
import metrics.Utils;
import metrics.multiple.MultiMetricFramework.MultiMetricValue;
import metrics.multiple.metrics.BoardSitesOccupied;
import metrics.multiple.metrics.BranchingFactor;
import metrics.multiple.metrics.DecisionFactor;
import metrics.multiple.metrics.MoveDistance;
import metrics.multiple.metrics.PieceNumber;
import metrics.multiple.metrics.ScoreDifference;
import metrics.single.boardCoverage.BoardCoverageDefault;
import metrics.single.boardCoverage.BoardCoverageFull;
import metrics.single.boardCoverage.BoardCoverageUsed;
import metrics.single.complexity.DecisionMoves;
import metrics.single.complexity.GameTreeComplexity;
import metrics.single.complexity.StateSpaceComplexity;
import metrics.single.duration.DurationActions;
import metrics.single.duration.DurationMoves;
import metrics.single.duration.DurationTurns;
import metrics.single.duration.DurationTurnsNotTimeouts;
import metrics.single.duration.DurationTurnsStdDev;
import metrics.single.outcome.AdvantageP1;
import metrics.single.outcome.Balance;
import metrics.single.outcome.Completion;
import metrics.single.outcome.Drawishness;
import metrics.single.outcome.OutcomeUniformity;
import metrics.single.outcome.Timeouts;
import other.GameLoader;
import other.concept.Concept;
import other.concept.ConceptDataType;
import other.concept.ConceptType;
import other.context.Context;
import other.move.Move;
import other.trial.Trial;

/**
 * Implementation of an experiment that computes concepts for multiple games in parallel.
 * Primarily meant for use on larger compute nodes with many cores available.
 * 
 * @author Dennis Soemers
 */
public class ParallelComputeConceptsMultipleGames 
{
	
	//-------------------------------------------------------------------------
	
	protected int numCoresTotal;
	protected int numThreadsPerJob;
	protected List<String> jsonFiles;

	/** 
	 * Whether to create a small GUI that can be used to manually interrupt training run. 
	 * False by default. 
	 */
	protected boolean useGUI;

	/** Max wall time in minutes (or -1 for no limit) */
	protected int maxWallTime;

	//-------------------------------------------------------------------------
	
	/**
	 * Constructor. No GUI for interrupting experiment, no wall time limit.
	 */
	public ParallelComputeConceptsMultipleGames()
	{
		// all defaults already set above
	}

	/**
	 * Constructor. No wall time limit.
	 * @param useGUI
	 */
	public ParallelComputeConceptsMultipleGames(final boolean useGUI)
	{
		this.useGUI = useGUI;
	}

	/**
	 * Constructor
	 * @param useGUI
	 * @param maxWallTime Wall time limit in minutes.
	 */
	public ParallelComputeConceptsMultipleGames(final boolean useGUI, final int maxWallTime)
	{
		this.useGUI = useGUI;
		this.maxWallTime = maxWallTime;
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * Starts the experiment
	 */
	public void startExperiment()
	{
		final List<Concept> booleanConcepts = new ArrayList<Concept>();
		final List<Concept> nonBooleanConcepts = new ArrayList<Concept>();
		for (final Concept concept : Concept.values())
		{
			if (concept.dataType().equals(ConceptDataType.BooleanData))
				booleanConcepts.add(concept);
			else
				nonBooleanConcepts.add(concept);
		}
		
		final AtomicInteger numCoresAvailable = new AtomicInteger(numCoresTotal);
		
		@SuppressWarnings("resource")
		final ExecutorService threadPool = Executors.newFixedThreadPool(
				(int) Math.ceil((double)numCoresTotal / numThreadsPerJob), 
				DaemonThreadFactory.INSTANCE);
		
		// Queue of jobs that are waiting to be submitted because they depend on a set of other jobs
		final List<WaitingJob> waitingJobs = new LinkedList<WaitingJob>();
		
		final long startTime = System.currentTimeMillis();
		
		try
		{
			for (final String jsonFile : jsonFiles)
			{
				// First check if we can now submit any jobs that were still waiting around
				final Iterator<WaitingJob> it = waitingJobs.iterator();
				while (it.hasNext() && numCoresAvailable.get() >= numThreadsPerJob)
				{
					final WaitingJob job = it.next();
					if (job.checkDependencies()) 
					{
						// This job can be submitted now
						it.remove();
						numCoresAvailable.addAndGet(-numThreadsPerJob);
						threadPool.submit(job.runnable);
					}
				}
				
				while (numCoresAvailable.get() <= 0)
				{
					// We could just continue creating jobs and submitting them to the pool, but then we would
					// already be loading all those games into memory. We don't want to do that, so we'll wait
					// with submitting more tasks until we actually have cores to run them.
					Thread.sleep(20000L);
				}
				
				// Let's try to start jobs for another game
				final GameRulesetToCompute experiment = GameRulesetToCompute.fromJson(jsonFile);
				
				// Load game for this batch
				final Game game;
				
				if (experiment.treatGameNameAsFilepath)
				{
					if (experiment.ruleset != null && !experiment.ruleset.equals(""))
						game = GameLoader.loadGameFromFile(new File(experiment.gameName), experiment.ruleset);
					else
						game = GameLoader.loadGameFromFile(new File(experiment.gameName), new ArrayList<String>());	// TODO add support for options
				}
				else
				{
					if (experiment.ruleset != null && !experiment.ruleset.equals(""))
						game = GameLoader.loadGameFromName(experiment.gameName, experiment.ruleset);
					else
						game = GameLoader.loadGameFromName(experiment.gameName, new ArrayList<String>());	// TODO add support for options
				}
				
				final List<String> gameOptions = game.getOptions();
				
				game.setMaxTurns(Math.min(5000, game.getMaxTurnLimit()));
				
				// Let's clear some unnecessary memory
				game.description().setParseTree(null);
				game.description().setExpanded(null);
								
				System.out.println("Num cores available: " + numCoresAvailable.get());
				System.out.println("Submitting jobs.");
				System.out.println("Game: " + experiment.gameName);
				System.out.println("Ruleset: " + experiment.ruleset);
				
				// Check if we need to submit jobs for generating trials (if we don't already have them)
				final File trialsDir = new File(experiment.trialsDir);
				int numExistingTrialFiles = 0;
				
				if (trialsDir.exists())
				{
					for (final File trialFile : trialsDir.listFiles())
					{
						if (trialFile.isFile() && trialFile.getAbsolutePath().endsWith(".txt"))
							++numExistingTrialFiles;
					}
				}
				
				final List<Future<?>> trialJobFutures = new LinkedList<Future<?>>();
				
				if (numExistingTrialFiles < experiment.numTrials)
				{
					// We have to submit a job to create more trial files
					final int numTrialsToRun = experiment.numTrials - numExistingTrialFiles;
					final int firstTrialIndex = numExistingTrialFiles;
					
					numCoresAvailable.addAndGet(-numThreadsPerJob);
					trialJobFutures.add
					(
						threadPool.submit
						(
							() -> 
							{
								try
								{								
									generateRandomTrials
									(
										game, numTrialsToRun, firstTrialIndex,
										trialsDir, numThreadsPerJob, experiment.gameName,
										gameOptions
									);
								}
								catch (final Exception e)
								{
									e.printStackTrace();
								}
								finally
								{
									numCoresAvailable.addAndGet(numThreadsPerJob);
								}
							}
						)
					);
				}
				
				// Create jobs for computing concepts
				final Runnable conceptsJob = () -> {
					try
					{								
						computeConcepts
						(
							game, trialsDir, new File(experiment.conceptsDir),
							numThreadsPerJob
						);
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						numCoresAvailable.addAndGet(numThreadsPerJob);
					}
				};
				
				if (!trialJobFutures.isEmpty())
				{
					// We can't submit the job directly, but have to wait until
					// all trials have been generated
					waitingJobs.add(new WaitingJob(conceptsJob, trialJobFutures, numThreadsPerJob));
				}
				else
				{
					// We can directly submit this job, as we already have the trials
					numCoresAvailable.addAndGet(-numThreadsPerJob);
					threadPool.submit(conceptsJob);
				}

				while (numCoresAvailable.get() <= 0)
				{
					// We could just continue creating jobs and submitting them to the pool, but then we would
					// already be loading all those games into memory. We don't want to do that, so we'll wait
					// with submitting more tasks until we actually have cores to run them.
					Thread.sleep(20000L);
				}
			}
			
			// Shut down, but wait until everything that is still running is done
			if (threadPool != null)
			{
				final long maxWallTimeMillis = maxWallTime * 60 * 1000L;
				final long alreadyElapsedTime = System.currentTimeMillis() - startTime;
				
				threadPool.shutdown();
				threadPool.awaitTermination(maxWallTimeMillis - alreadyElapsedTime, TimeUnit.MILLISECONDS);
			}
		}
		catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * Helper method for parallelised generation of random trials
	 * 
	 * @param game
	 * @param numTrialsToRun
	 * @param firstTrialIndex
	 * @param trialsDir
	 * @param numThreads
	 * @param gameName
	 * @param gameOptions
	 */
	protected static void generateRandomTrials
	(
		final Game game, final int numTrialsToRun, final int firstTrialIndex,
		final File trialsDir, final int numThreads, final String gameName,
		final List<String> gameOptions
	)
	{
		@SuppressWarnings("resource")
		final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads, DaemonThreadFactory.INSTANCE);
		
		final TIntArrayList trialIndicesToGenerate = ListUtils.range(firstTrialIndex, firstTrialIndex + numTrialsToRun);
		final TIntArrayList[] trialIndicesPerJob = ListUtils.split(trialIndicesToGenerate, numThreads);
		
		try
		{
			for (final TIntArrayList trialIndices : trialIndicesPerJob)
			{
				// Submit a job for this sublist of trial indices
				threadPool.submit
				(
					() -> 
					{
						for (int i = 0; i < trialIndices.size(); ++i)
						{
							final int trialIdx = trialIndices.getQuick(i);
							
							try
							{
								final Trial trial = new Trial(game);
								final Context context = new Context(game, trial);
								final RandomProviderDefaultState gameStartRngState = (RandomProviderDefaultState) context.rng().saveState();
								game.start(context);
								game.playout(context, null, 1.0, null, 0, -1, ThreadLocalRandom.current());
								
								String trialFilepath = trialsDir.getAbsolutePath();
								trialFilepath = trialFilepath.replaceAll(Pattern.quote("\\"), "/");
								if (!trialFilepath.endsWith("/"))
									trialFilepath += "/";
								trialFilepath += "Trial_" + trialIdx + ".txt";
								
								trial.saveTrialToTextFile(new File(trialFilepath), gameName, gameOptions, gameStartRngState);
							}
							catch (final Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			threadPool.shutdown();
			try 
			{
				threadPool.awaitTermination(24, TimeUnit.HOURS);
			} 
			catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Helper method for parallelised computation of concepts for a game.
	 * 
	 * @param game
	 * @param trialsDir Where are our trials stored?
	 * @param conceptsDir Where do we want to write our concepts?
	 * @param numThreads
	 */
	protected static void computeConcepts
	(
		final Game game, final File trialsDir, 
		final File conceptsDir, final int numThreads
	)
	{
		// Load all our trials
		final List<Trial> allTrials = new ArrayList<Trial>();
		final List<RandomProviderState> trialStartRNGs = new ArrayList<RandomProviderState>();
		
		for (final File trialFile : trialsDir.listFiles())
		{
			if (trialFile.getName().endsWith(".txt"))
			{
				MatchRecord loadedRecord;
				try
				{
					loadedRecord = MatchRecord.loadMatchRecordFromTextFile(trialFile, game);
					final Trial loadedTrial = loadedRecord.trial();
					allTrials.add(loadedTrial);
					trialStartRNGs.add(loadedRecord.rngState());
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		// Split trials into batches, each to be processed by a different thread
		final List<List<Trial>> trialsPerJob = ListUtils.split(allTrials, numThreads);
		final List<List<RandomProviderState>> rngStartStatesPerJob = ListUtils.split(trialStartRNGs, numThreads);
		
		@SuppressWarnings("resource")
		final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads, DaemonThreadFactory.INSTANCE);
		
		try
		{
			final List<Future<TDoubleArrayList>> frequenciesPerJob = new ArrayList<Future<TDoubleArrayList>>();
			
			for (int jobIdx = 0; jobIdx < numThreads; ++jobIdx)
			{
				final List<Trial> trials = trialsPerJob.get(jobIdx);
				final List<RandomProviderState> rngStartStates = rngStartStatesPerJob.get(jobIdx);
				
				// Submit a job for this sublist of trials
				frequenciesPerJob.add(threadPool.submit
				(
					() -> 
					{
						// We need a separate metrics tracker per job. Separate list of Metric
						// objects too, as we use them in a stateful manner
						final List<Metric> conceptMetrics = new ArrayList<Metric>();
						// Duration
						conceptMetrics.add(new DurationActions());
						conceptMetrics.add(new DurationMoves());
						conceptMetrics.add(new DurationTurns());
						conceptMetrics.add(new DurationTurnsStdDev());
						conceptMetrics.add(new DurationTurnsNotTimeouts());
						// Complexity
						conceptMetrics.add(new DecisionMoves());
						conceptMetrics.add(new GameTreeComplexity());
						conceptMetrics.add(new StateSpaceComplexity());
						// Board Coverage
						conceptMetrics.add(new BoardCoverageDefault());
						conceptMetrics.add(new BoardCoverageFull());
						conceptMetrics.add(new BoardCoverageUsed());
						// Outcome
						conceptMetrics.add(new AdvantageP1());
						conceptMetrics.add(new Balance());
						conceptMetrics.add(new Completion());
						conceptMetrics.add(new Drawishness());
						conceptMetrics.add(new Timeouts());
						conceptMetrics.add(new OutcomeUniformity());
						// Board Sites Occupied
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.Average, Concept.BoardSitesOccupiedAverage));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.Median, Concept.BoardSitesOccupiedMedian));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.Max, Concept.BoardSitesOccupiedMaximum));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.Min, Concept.BoardSitesOccupiedMinimum));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.Variance, Concept.BoardSitesOccupiedVariance));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.ChangeAverage, Concept.BoardSitesOccupiedChangeAverage));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.ChangeSign, Concept.BoardSitesOccupiedChangeSign));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.ChangeLineBestFit, Concept.BoardSitesOccupiedChangeLineBestFit));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.ChangeNumTimes, Concept.BoardSitesOccupiedChangeNumTimes));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.MaxIncrease, Concept.BoardSitesOccupiedMaxIncrease));
						conceptMetrics.add(new BoardSitesOccupied(MultiMetricValue.MaxDecrease, Concept.BoardSitesOccupiedMaxDecrease));
						// Branching Factor
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.Average, Concept.BranchingFactorAverage));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.Median, Concept.BranchingFactorMedian));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.Max, Concept.BranchingFactorMaximum));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.Min, Concept.BranchingFactorMinimum));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.Variance, Concept.BranchingFactorVariance));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.ChangeAverage, Concept.BranchingFactorChangeAverage));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.ChangeSign, Concept.BranchingFactorChangeSign));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.ChangeLineBestFit, Concept.BranchingFactorChangeLineBestFit));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.ChangeNumTimes, Concept.BranchingFactorChangeNumTimesn));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.MaxIncrease, Concept.BranchingFactorChangeMaxIncrease));
						conceptMetrics.add(new BranchingFactor(MultiMetricValue.MaxDecrease, Concept.BranchingFactorChangeMaxDecrease));
						// Decision Factor
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.Average, Concept.DecisionFactorAverage));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.Median, Concept.DecisionFactorMedian));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.Max, Concept.DecisionFactorMaximum));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.Min, Concept.DecisionFactorMinimum));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.Variance, Concept.DecisionFactorVariance));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.ChangeAverage, Concept.DecisionFactorChangeAverage));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.ChangeSign, Concept.DecisionFactorChangeSign));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.ChangeLineBestFit, Concept.DecisionFactorChangeLineBestFit));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.ChangeNumTimes, Concept.DecisionFactorChangeNumTimes));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.MaxIncrease, Concept.DecisionFactorMaxIncrease));
						conceptMetrics.add(new DecisionFactor(MultiMetricValue.MaxDecrease, Concept.DecisionFactorMaxDecrease));
						// Move Distance
						conceptMetrics.add(new MoveDistance(MultiMetricValue.Average, Concept.MoveDistanceAverage));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.Median, Concept.MoveDistanceMedian));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.Max, Concept.MoveDistanceMaximum));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.Min, Concept.MoveDistanceMinimum));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.Variance, Concept.MoveDistanceVariance));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.ChangeAverage, Concept.MoveDistanceChangeAverage));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.ChangeSign, Concept.MoveDistanceChangeSign));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.ChangeLineBestFit, Concept.MoveDistanceChangeLineBestFit));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.ChangeNumTimes, Concept.MoveDistanceChangeNumTimes));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.MaxIncrease, Concept.MoveDistanceMaxIncrease));
						conceptMetrics.add(new MoveDistance(MultiMetricValue.MaxDecrease, Concept.MoveDistanceMaxDecrease));
						// Piece Number
						conceptMetrics.add(new PieceNumber(MultiMetricValue.Average, Concept.PieceNumberAverage));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.Median, Concept.PieceNumberMedian));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.Max, Concept.PieceNumberMaximum));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.Min, Concept.PieceNumberMinimum));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.Variance, Concept.PieceNumberVariance));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.ChangeAverage, Concept.PieceNumberChangeAverage));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.ChangeSign, Concept.PieceNumberChangeSign));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.ChangeLineBestFit, Concept.PieceNumberChangeLineBestFit));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.ChangeNumTimes, Concept.PieceNumberChangeNumTimes));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.MaxIncrease, Concept.PieceNumberMaxIncrease));
						conceptMetrics.add(new PieceNumber(MultiMetricValue.MaxDecrease, Concept.PieceNumberMaxDecrease));
						// Score Difference
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.Average, Concept.ScoreDifferenceAverage));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.Median, Concept.ScoreDifferenceMedian));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.Max, Concept.ScoreDifferenceMaximum));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.Min, Concept.ScoreDifferenceMinimum));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.Variance, Concept.ScoreDifferenceVariance));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.ChangeAverage, Concept.ScoreDifferenceChangeAverage));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.ChangeSign, Concept.ScoreDifferenceChangeSign));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.ChangeLineBestFit, Concept.ScoreDifferenceChangeLineBestFit));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.ChangeNumTimes, Concept.ScoreDifferenceChangeNumTimes));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.MaxIncrease, Concept.ScoreDifferenceMaxIncrease));
						conceptMetrics.add(new ScoreDifference(MultiMetricValue.MaxDecrease, Concept.ScoreDifferenceMaxDecrease));
						
						final MetricsTracker metricsTracker = new MetricsTracker(conceptMetrics);
						
						// Frequencies returned by all the playouts.
						final double[] frequencyPlayouts = new double[Concept.values().length];
						
						for (int trialIndex = 0; trialIndex < trials.size(); trialIndex++)
						{
							final Trial trial = trials.get(trialIndex);
							final RandomProviderState rngState = rngStartStates.get(trialIndex);

							// Setup a new instance of the game
							final Context context = Utils.setupNewContext(game, rngState);
							metricsTracker.startNewTrial(context, trial);

							// Frequencies returned by that playout.
							final double[] frequencyPlayout = new double[Concept.values().length];

							// Run the playout.
							int turnsWithMoves = 0;
							Context prevContext = null;
							for (int i = trial.numInitialPlacementMoves(); i < trial.numMoves(); i++)
							{
								final Moves legalMoves = context.game().moves(context);

								final int numLegalMoves = legalMoves.moves().size();
								if (numLegalMoves > 0)
									turnsWithMoves++;

								for (final Move legalMove : legalMoves.moves())
								{
									final BitSet moveConcepts = legalMove.moveConcepts(context);
									for (int indexConcept = 0; indexConcept < Concept.values().length; indexConcept++)
									{
										final Concept concept = Concept.values()[indexConcept];
										if (moveConcepts.get(concept.id()))
											frequencyPlayout[indexConcept] += 1.0 / numLegalMoves;
									}
								}

								// We keep the context before the ending state for the frequencies of the end
								// conditions.
								if (i == trial.numMoves() - 1)
									prevContext = new Context(context);

								// We go to the next move.
								context.game().apply(context, trial.getMove(i));
								metricsTracker.observeNextState(context);
							}
							
							metricsTracker.observeFinalState(context);
							
							// Compute avg for all the playouts.
							for (int j = 0; j < frequencyPlayout.length; j++)
								frequencyPlayouts[j] += frequencyPlayout[j] / turnsWithMoves;

							context.trial().lastMove().apply(prevContext, true);

							boolean noEndFound = true;

							if (context.rules().phases() != null)
							{
								final int mover = context.state().mover();
								final Phase endPhase = context.rules().phases()[context.state().currentPhase(mover)];
								final End endPhaseRule = endPhase.end();

								// Only check if action not part of setup
								if (context.active() && endPhaseRule != null)
								{
									final EndRule[] endRules = endPhaseRule.endRules();
									for (final EndRule endingRule : endRules)
									{
										final EndRule endRuleResult = endingRule.eval(prevContext);
										if (endRuleResult == null)
											continue;

										final BitSet endConcepts = endingRule.stateConcepts(prevContext);

										noEndFound = false;
										for (int indexConcept = 0; indexConcept < Concept.values().length; indexConcept++)
										{
											final Concept concept = Concept.values()[indexConcept];
											if (concept.type().equals(ConceptType.End) && endConcepts.get(concept.id()))
											{
												frequencyPlayouts[indexConcept]++; 
											}
										}
										break;
									}
								}
							}

							final End endRule = context.rules().end();
							if (noEndFound && endRule != null)
							{
								final EndRule[] endRules = endRule.endRules();
								for (final EndRule endingRule : endRules)
								{
									final EndRule endRuleResult = endingRule.eval(prevContext);
									if (endRuleResult == null)
										continue;

									final BitSet endConcepts = endingRule.stateConcepts(prevContext);

									noEndFound = false;
									for (int indexConcept = 0; indexConcept < Concept.values().length; indexConcept++)
									{
										final Concept concept = Concept.values()[indexConcept];
										if (concept.type().equals(ConceptType.End) && endConcepts.get(concept.id()))
										{
											frequencyPlayouts[indexConcept]++; 
										}
									}
									break;
								}
							}

							if (noEndFound)
							{
								frequencyPlayouts[Concept.Draw.ordinal()]++; 
							}
						}

						final TDoubleArrayList frequenciesThisJob = TDoubleArrayList.wrap(new double[Concept.values().length]);
						for (int indexConcept = 0; indexConcept < Concept.values().length; indexConcept++)
						{
							frequenciesThisJob.setQuick(indexConcept, frequencyPlayouts[indexConcept] / trials.size());
						}
						
						// TODO extract metrics from metricsTracker
						
						return frequenciesThisJob;
					}
				));
			}
			
			// TODO: collect all the results from the futures and do something with them
			// TODO do something with frequencies (take them first: they have 0.0 also for non-frequency concepts)
			// TODO do something with metrics
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			threadPool.shutdown();
			try 
			{
				threadPool.awaitTermination(24, TimeUnit.HOURS);
			} 
			catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//-------------------------------------------------------------------------
	
	/**
	 * A single game (+ ruleset) for which we wish to compute concepts.
	 * 
	 * @author Dennis Soemers
	 */
	public static class GameRulesetToCompute
	{
		
		protected final String gameName;
		protected final String ruleset;
		protected final int numTrials;
		protected final String trialsDir;
		protected final String conceptsDir;
		protected final boolean treatGameNameAsFilepath;
		
		/**
		 * Constructor
		 * 
		 * @param gameName
		 * @param ruleset
		 * @param numTrials
		 * @param trialsDir
		 * @param conceptsDir
		 * @param treatGameNameAsFilepath
		 */
		public GameRulesetToCompute
		(
			final String gameName, final String ruleset, final int numTrials,
			final String trialsDir, final String conceptsDir, 
			final boolean treatGameNameAsFilepath
		) 
		{
			this.gameName = gameName;
			this.ruleset = ruleset;
			this.numTrials = numTrials;
			this.trialsDir = trialsDir;
			this.conceptsDir = conceptsDir;
			this.treatGameNameAsFilepath = treatGameNameAsFilepath;
		}
		
		public void toJson(final String jsonFilepath)
		{
			BufferedWriter bw = null;
			try
			{
				final File file = new File(jsonFilepath);
				file.getParentFile().mkdirs();
				if (!file.exists())
					file.createNewFile();

				final JSONObject json = new JSONObject();
				
				json.put("gameName", gameName);
				json.put("ruleset", ruleset);
				json.put("numTrials", numTrials);
				json.put("trialsDir", trialsDir);
				json.put("conceptsDir", conceptsDir);
				json.put("treatGameNameAsFilepath", treatGameNameAsFilepath);

				final FileWriter fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				bw.write(json.toString(4));
				
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (bw != null)
						bw.close();
				}
				catch (final Exception ex)
				{
					System.out.println("Error in closing the BufferedWriter" + ex);
				}
			}
		}
		
		public static GameRulesetToCompute fromJson(final String filepath)
		{
			try (final InputStream inputStream = new FileInputStream(new File(filepath)))
			{
				final JSONObject json = new JSONObject(new JSONTokener(inputStream));
				
				final String gameName = json.getString("gameName");
				final String ruleset = json.getString("ruleset");
				final int numTrials = json.getInt("numTrials");
				final String trialsDir = json.getString("trialsDir");
				final String conceptsDir = json.getString("conceptsDir");
				final boolean treatGameNameAsFilepath = json.optBoolean("treatGameNameAsFilepath", false);
				
				return new GameRulesetToCompute(gameName, ruleset, numTrials, 
						trialsDir, conceptsDir, treatGameNameAsFilepath);

			}
			catch (final Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * A job that is waiting for other jobs that need to be finished first before
	 * it can be submitted to an ExecutorService
	 * 
	 * @author Dennis Soemers
	 */
	public static class WaitingJob
	{
		
		/** The actual job */
		protected final Runnable runnable;
		
		/** Jobs which must've completed running before we can be queued */
		protected final List<Future<?>> dependencies;
		
		/** Num threads this job expects to be using */
		protected final int numThreads;
		
		/**
		 * Constructor
		 * 
		 * @param runnable
		 * @param dependencies
		 * @param numThreads
		 */
		public WaitingJob(final Runnable runnable, final List<Future<?>> dependencies, final int numThreads)
		{
			this.runnable = runnable;
			this.dependencies = dependencies;
			this.numThreads = numThreads;
		}
		
		/**
		 * @return True if and only if all dependencies have finished running.
		 */
		public boolean checkDependencies()
		{
			final Iterator<Future<?>> it = dependencies.iterator();
			while (it.hasNext())
			{
				final Future<?> future = it.next();
				
				if (future.isDone())
					it.remove();
				else
					return false;
			}
			
			return true;
		}
		
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * Can be used for quick testing without command-line args, or proper
	 * testing with elaborate setup through command-line args
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(final String[] args)
	{
		// Feature Set caching is safe in this main method
		JITSPatterNetFeatureSet.ALLOW_FEATURE_SET_CACHE = true;
		
		// Define options for arg parser
		final CommandLineArgParse argParse = 
				new CommandLineArgParse
				(
					true,
					"Compute concepts for multiple games in parallel. Configuration of all experiments to be run should be in JSON files."
				);
		
		argParse.addOption(new ArgOption()
				.withNames("--num-cores-total")
				.help("Total number of cores we expect to be able to use for all jobs together.")
				.withNumVals(1)
				.withType(OptionTypes.Int)
				.setRequired());
		argParse.addOption(new ArgOption()
				.withNames("--num-threads-per-job")
				.help("Number of threads to be used per job. Jobs may either generate random trials, or compute concepts")
				.withNumVals(1)
				.withType(OptionTypes.Int)
				.setRequired());
		
		argParse.addOption(new ArgOption()
				.withNames("--json-files")
				.help("JSON files, each describing one game for which we should compute trials/concepts in this job.")
				.withNumVals("+")
				.withType(OptionTypes.String)
				.setRequired());
		
		argParse.addOption(new ArgOption()
				.withNames("--useGUI")
				.help("Whether to create a small GUI that can be used to "
						+ "manually interrupt training run. False by default."));
		argParse.addOption(new ArgOption()
				.withNames("--max-wall-time")
				.help("Max wall time in minutes (or -1 for no limit).")
				.withDefault(Integer.valueOf(-1))
				.withNumVals(1)
				.withType(OptionTypes.Int));
		
		// Parse the args
		if (!argParse.parseArguments(args))
			return;

		// Use the parsed args
		final ParallelComputeConceptsMultipleGames experiment = 
				new ParallelComputeConceptsMultipleGames
				(
					argParse.getValueBool("--useGUI"), 
					argParse.getValueInt("--max-wall-time")
				);
		
		experiment.numCoresTotal = argParse.getValueInt("--num-cores-total");
		experiment.numThreadsPerJob = argParse.getValueInt("--num-threads-per-job");
		experiment.jsonFiles = (List<String>) argParse.getValue("--json-files");
		
		experiment.startExperiment();
	}

}