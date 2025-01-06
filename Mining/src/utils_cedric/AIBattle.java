package utils_cedric;

import game.Game;
import main.Constants;
import other.context.Context;
import other.model.Model;
import other.trial.Trial;
import utils.AIFactory;
import other.AI;
import other.GameLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to make AI agents play against each other and obtain results.
 */
public class AIBattle {

    private static final int moveLimit = Constants.DEFAULT_MOVES_LIMIT; // Default move limit
    private static final double[] maxTurnTimes = {0.5}; // Different max turn times

    /**
     * Main method to set up AI matches and obtain results.
     */
    public static void main(final String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 5) {
            System.out.println("Usage: java AIBattle <number_of_trials> <output_directory> <game_name> <agent1_name> <agent2_name>");
            System.exit(1);
        }

        // Parse command-line arguments
        int numberOfTrials = Integer.parseInt(args[0]);
        String outputDir = args[1];
        String gameName = args[2];
        String agentName1 = args[3];
        String agentName2 = args[4];

        // Ensure the output directory exists
        new File(outputDir).mkdirs();

        // Load the game
        Game game = GameLoader.loadGameFromName(gameName);
        if (game == null) {
            System.err.println("Game not found or failed to load: " + gameName);
            System.exit(1);
        }

        // Run battles for different max turn times and save 
        for (double maxTurnTime : maxTurnTimes) {
            int[] results = runAIBattle(game, agentName1, agentName2, numberOfTrials, maxTurnTime);
            saveResultsToCSV(results, outputDir, gameName, agentName1, agentName2, maxTurnTime);
        }
    }

    /**
     * Runs the AI battle for the given game.
     */
    private static int[] runAIBattle(final Game game, final String agentName1, final String agentName2, final int numMatches, final double maxTurnTime) {
        // Store results
        int winsAgent1 = 0;
        int winsAgent2 = 0;
        int draws = 0;

        for (int i = 0; i < numMatches; i++) {
            final Trial trial = new Trial(game);
            final Context context = new Context(game, trial);

            final List<AI> ais = new ArrayList<>();
            ais.add(null); // Placeholder for player 0 (not used)
            ais.add(AIFactory.createAI(agentName1));
            ais.add(AIFactory.createAI(agentName2));

            // Initialize AIs
            for (int p = 1; p < ais.size(); p++) {
                ais.get(p).initAI(game, p);
                ais.get(p).setMaxSecondsPerMove(maxTurnTime);
            }

            // Start game and run playout
            game.start(context);
            final Model model = context.model();

            while (!trial.over() && trial.numMoves() < moveLimit) {
                model.startNewStep(context, ais, maxTurnTime);
            }
            
//            System.out.println();
//            System.out.println("----------------------NEW TRIAL----------------------");

            // Determine result
            final int winner = trial.status().winner();
            if (winner == 1) {
                winsAgent1++;
            } else if (winner == 2) {
                winsAgent2++;
            } else {
                draws++;
            }

            // Close AIs
            for (int p = 1; p < ais.size(); p++) {
                ais.get(p).closeAI();
            }
        }

        return new int[]{winsAgent1, winsAgent2, draws};
    }

    /**
     * Saves the results to a CSV file.
     */
    private static void saveResultsToCSV(final int[] results, final String outputDir, final String gameName, final String agentName1, final String agentName2, final double maxTurnTime) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputDir + "/AIBattleResults0.csv", true))) {
            
            String sanitizedAgentName1 = formatAgentName(agentName1);
            String sanitizedAgentName2 = formatAgentName(agentName2);
            
            writer.println(gameName + "," + "\"" + sanitizedAgentName1 + "\"" + "," + "\"" + sanitizedAgentName2 + "\"" + "," + maxTurnTime + "," + results[2] + "," + results[0] + "," + results[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatAgentName(String agentName) {
        StringBuilder formattedName = new StringBuilder();

        if (agentName.contains("algorithm=") && agentName.contains("heuristics=")) {
            // Extraire la partie après "algorithm=" et avant le premier point-virgule
            String algorithm = agentName.split("algorithm=")[1].split(";")[0];
            // Extraire la partie après "heuristics=" et avant le point-virgule suivant ou la fin de la chaîne
            String heuristicsPath = agentName.split("heuristics=")[1].split(";")[0];
            // Extraire le nom de fichier sans le chemin complet
            String heuristics = heuristicsPath.substring(heuristicsPath.lastIndexOf('/') + 1, heuristicsPath.lastIndexOf('.'));
            // Ajouter algorithm et heuristics au nom formaté
            formattedName.append(algorithm).append("-").append(heuristics);
        } else {
            // Retourner le nom tel quel si aucun format avancé n'est détecté
            return agentName;
        }

        // Ajouter "backprop" si présent
        if (agentName.contains("backprop=")) {
            String backprop = agentName.split("backprop=")[1].split(";")[0];
            formattedName.append("-").append(backprop);
        }

        return formattedName.toString();
    }



}
