package utils_cedric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateAIBattleClusterScripts
{
    public static void main(final String[] args)
    {
        final int numPlayout = 100;
        final String clusterLogin = "epiette"; // Replace with your actual login
        final String mainScriptName = "GenAIBattle0.sh";
        final String mainRepoName = "AIBattle0";
        final String playoutweight = "0.0";
        final String outputDirPath = "/Users/cedricantoine/Documents/GitHub/Ludii/Mining/src/utils_cedric"; // Replace with your desired directory path
        
        // Create the output directory if it doesn't exist
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        final List<String> games = Arrays.asList(
            "Spline+.lud",
            "Splice.lud",
            "Spree.lud",
            "Real_Spava.lud",
            "Splade.lud",
            "Sparro.lud",
            "Spaniel.lud",       
            
            "Span.lud",
            "Sponnect.lud",
            "Spight.lud",
            "Spice.lud",
            "Spaji.lud",
           
            "Spyramid.lud",
        
            "Spire.lud",
            "Spinimax.lud",
            "Splastwo.lud",
           
            "Sprite.lud",
            "Spirit.lud"
        );

        final List<String> aiAgents = Arrays.asList(
            "UCT",
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/centreProximity.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/lineCompletionHeuristic.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/cornerProximity.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/influence.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/influenceAdvanced.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/sidesProximity.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/nullHeuristic.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/topLayerProximity.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/topANDline.txt;playout_value_weight=" + playoutweight,
            "algorithm=UCT;heuristics=/home/ucl/ingi/epiette/" + mainRepoName + "/topANDcentre.txt;playout_value_weight=" + playoutweight,
            "Monte Carlo (flat)",
            "MC-GRAVE",
            "MC-BRAVE",
            "UCB1Tuned",
            "Score Bounded MCTS",
            "Progressive History",
            "Progressive Bias",
            "MAST",
            "NST",
            "UCB1-GRAVE",
            //"Biased MCTS",
            //"Biased MCTS (Uniform Playouts)",
            //"MCTS (Hybrid Selection)",
            "Bandit Tree Search",
            "EPT",
            "EPT-QB",
            "AlphaBeta"
        );

        final ArrayList<String> gameAgentPairs = new ArrayList<>();
        for (String game : games) {
            for (String agent1 : aiAgents) {
                for (String agent2 : aiAgents) {
                    if (!agent1.equals(agent2)) {
                        gameAgentPairs.add(game + " | " + agent1 + " | " + agent2);
                        gameAgentPairs.add(game + " | " + agent2 + " | " + agent1);
                    }
                }
            }
        }

        try (final PrintWriter mainWriter = new PrintWriter(new File(outputDir, mainScriptName), "UTF-8"))
        {
            int scriptId = 0;
            for (int i = 0; i < (gameAgentPairs.size() / 42 + 1); i++)
            {
                final String scriptName = "GenTrial_" + scriptId + ".sh";
                mainWriter.println("sbatch " + scriptName);

                try (final PrintWriter writer = new PrintWriter(new File(outputDir, scriptName), "UTF-8"))
                {
                    writer.println("#!/bin/bash");
                    writer.println("#SBATCH -J GenTrials" + scriptId);
                    writer.println("#SBATCH -p batch");
                    writer.println("#SBATCH -o /home/ucl/ingi/" + clusterLogin + "/" + mainRepoName + "/Out/Out_%J_" + scriptName + ".out");
                    writer.println("#SBATCH -e /home/ucl/ingi/" + clusterLogin + "/" + mainRepoName + "/Err/Err_%J_" + scriptName + ".err");
                    writer.println("#SBATCH -t 2880");
                    writer.println("#SBATCH -N 1");
                    writer.println("#SBATCH --cpus-per-task=128");
                    writer.println("#SBATCH --mem=400G");
                    writer.println("#SBATCH --exclusive");
                    writer.println("module load Java/11.0.20");

                    for (int j = 0; j < 42; j++)
                    {
                        if ((i * 42 + j) < gameAgentPairs.size())
                        {
                            final String[] parts = gameAgentPairs.get(i * 42 + j).split(" \\| ");
                            final String gameName = parts[0];
                            final String agentName1 = parts[1];
                            final String agentName2 = parts[2];

                            String jobLine = "taskset -c " + (3 * j) + "," + (3 * j + 1) + "," + (3 * j + 2) + " ";
                            jobLine += "java -Xms5120M -Xmx5120M -XX:+HeapDumpOnOutOfMemoryError -da -dsa -XX:+UseStringDeduplication -jar \"/home/ucl/ingi/" + clusterLogin + "/" + mainRepoName + "/AIBattle.jar\" ";
                            jobLine += numPlayout + " \"/home/ucl/ingi/" + clusterLogin + "/" + mainRepoName + "/results\" \"" + gameName + "\" \"" + agentName1 + "\" \"" + agentName2 + "\" &";
                            writer.println(jobLine);
                        }
                    }
                    writer.println("wait");
                }
                catch (final FileNotFoundException | UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                scriptId++;
            }
        }
        catch (final FileNotFoundException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }
}
