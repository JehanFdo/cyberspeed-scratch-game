package ae.cyberspeed.interview.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GameService {


    String[][] generateMatrixWithStandardSymbol();

    void addBonusSymbol(String[][] matrix, StringBuilder assignedBonus);

    Map<String, Set<String>> applyWinningCombinations(String[][] matrix);

    List<String> findSymbolsInCoveredAreas(String[][] matrix, List<List<String>> coveredAreas);


    double calculateReward(double betAmount, Map<String, Set<String>> appliedWinningCombinations, String appliedBonusSymbol);
}
