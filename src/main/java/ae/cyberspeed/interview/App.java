package ae.cyberspeed.interview;

import ae.cyberspeed.interview.model.Config;
import ae.cyberspeed.interview.util.ArgsProcessor;
import ae.cyberspeed.interview.model.RewardResult;
import ae.cyberspeed.interview.service.GameService;
import ae.cyberspeed.interview.service.GameServiceImpl;
import ae.cyberspeed.interview.util.ConfigParser;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static ae.cyberspeed.interview.util.OutputUtil.generateOutput;

public class App {
    public static void main(String[] args) throws IOException {
        ArgsProcessor p = new ArgsProcessor();

        if (p.initializeArguments(args)) {

            Config config = ConfigParser.parse(p.getConfigurationFilePath());

            GameService service = new GameServiceImpl(config);
            // generate matrix with standard symbols
            String[][] generatedMatrix = service.generateMatrixWithStandardSymbol();

            StringBuilder bonus = new StringBuilder();
            // Assign Bonus symbol to generated Matrix
            service.addBonusSymbol(generatedMatrix, bonus);

            // check the matrix and apply the winning combination
            Map<String, Set<String>> winningCombinations = service.applyWinningCombinations(generatedMatrix);

            // calculate the final reward
            double reward = service.calculateReward(p.getBetAmount(), winningCombinations, bonus.toString());

            System.out.println(generateOutput(new RewardResult(generatedMatrix, reward, winningCombinations, bonus.toString())));
        }
    }
}
