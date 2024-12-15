package ae.cyberspeed.interview.service;

import ae.cyberspeed.interview.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameServiceImpl implements GameService {

    private final int rows;
    private final int columns;
    private final Map<String, Symbol> symbols;
    private final Probabilities probabilities;
    private final Map<String, WinCombination> winCombinations;

    public GameServiceImpl(Config config) {
        this.rows = config.getRows();
        this.columns = config.getColumns();
        this.symbols = config.getSymbols();
        this.probabilities = config.getProbabilities();
        this.winCombinations = config.getWinCombinations();
    }



    @Override
    public String[][] generateMatrixWithStandardSymbol() {
        String[][] matrix = new String[this.rows][this.columns];
        Random random = new Random();

        for (SymbolProbability probability : this.probabilities.getStandardSymbols()) {
            int row = probability.getRow();
            int column = probability.getColumn();
            int totalProbability = probability.getSymbols().values().stream().mapToInt(Integer::intValue).sum();
            int randomNumber = random.nextInt(totalProbability) + 1;

            double cumulativeProbability = 0;

            for (Map.Entry<String, Integer> entry : probability.getSymbols().entrySet()) {
                cumulativeProbability += entry.getValue();
                if (randomNumber <= cumulativeProbability) {
                    matrix[row][column] = entry.getKey();
                    break;
                }
            }
        }

        return matrix;
    }

    @Override
    public void addBonusSymbol(String[][] matrix, StringBuilder bonus) {
        Random random = new Random();
        int totalProbability = probabilities.getBonusSymbol().getSymbols().values().stream().mapToInt(Integer::intValue).sum();
        int randomNumber = random.nextInt(totalProbability) + 1;
        double cumulativeProbability = 0;

        for (Map.Entry<String, Integer> entry : probabilities.getBonusSymbol().getSymbols().entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomNumber <= cumulativeProbability) {
                while (true) {
                    int row = random.nextInt(this.rows);
                    int column = random.nextInt(this.columns);
                    if (matrix[row][column] != null) {
                        matrix[row][column] = entry.getKey();
                        bonus.append(entry.getKey());
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    public Map<String, Set<String>> applyWinningCombinations(String[][] matrix) {
        Map<String, Set<String>> appliedWinningCombinations = new HashMap<>();


        Map<String, Integer> symbolCount = Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(s -> s, Collectors.summingInt(s -> 1)));

        this.winCombinations.entrySet().forEach(entry -> {
            String winCombinationName = entry.getKey();
            WinCombination winCombination = entry.getValue();

            if ("same_symbols".equals(winCombination.getWhen())) {
                int maxCount = this.winCombinations.get(winCombinationName).getCount();

                symbolCount.entrySet().stream()
                        .filter(symbolEntry -> symbolEntry.getValue() == maxCount)
                        .forEach(symbolEntry -> appliedWinningCombinations.compute(symbolEntry.getKey(), (key, oldValue) ->
                                (oldValue == null || oldValue.size() < maxCount) ?
                                        new HashSet<>(Collections.singleton(winCombinationName)) :
                                        oldValue
                        ));
            } else if ("linear_symbols".equals(winCombination.getWhen()) && !appliedWinningCombinations.containsKey(winCombinationName)) {
                List<String> matchingSymbols = findSymbolsInCoveredAreas(matrix, winCombination.getCoveredAreas());
                matchingSymbols.forEach(symbol -> appliedWinningCombinations
                        .computeIfAbsent(symbol, k -> new HashSet<>())
                        .add(winCombinationName));
            }
        });

        return appliedWinningCombinations;
    }

    @Override
    public List<String> findSymbolsInCoveredAreas(String[][] matrix, List<List<String>> coveredAreas) {
        List<String> matchingSymbols = new ArrayList<>();

        for (List<String> area : coveredAreas) {
            String firstSymbol = null;
            boolean match = true;
            for (String crd : area) {
                int row = Integer.parseInt(crd.split(":")[0]);
                int col = Integer.parseInt(crd.split(":")[1]);
                String symbol = matrix[row][col];

                if (symbol == null || (firstSymbol != null && !symbol.equals(firstSymbol))) {
                    match = false;
                    break;
                }
                firstSymbol = symbol;
            }
            if (match) {
                matchingSymbols.add(firstSymbol);
            }
        }

        return matchingSymbols;
    }

    @Override
    public double calculateReward(double betAmount, Map<String, Set<String>> appliedWinningCombinations, String appliedBonusSymbol) {
        double totalReward = 0.0;

        for (Map.Entry<String, Set<String>> entry : appliedWinningCombinations.entrySet()) {
            String symbol = entry.getKey();
            Set<String> combinations = entry.getValue();

            double symbolReward = this.symbols.containsKey(symbol) ? this.symbols.get(symbol).getRewardMultiplier() : 0.0;
            double symbolTotalReward = symbolReward;


            for (String combination : combinations) {
                WinCombination winCombination = this.winCombinations.get(combination);
                if (winCombination != null) {
                    double combinationReward = winCombination.getRewardMultiplier();
                    symbolTotalReward *= combinationReward;
                }
            }

            totalReward += symbolTotalReward;
        }
        totalReward = totalReward * betAmount;


        if (appliedBonusSymbol != null) {
            if (appliedBonusSymbol.contains("x")) {
                int multiplier = Integer.parseInt(appliedBonusSymbol.replace("x", ""));
                totalReward *= multiplier;
            } else if (appliedBonusSymbol.contains("+")) {
                int bonusAmount = Integer.parseInt(appliedBonusSymbol.replace("+", ""));
                totalReward += bonusAmount;
            }
        }

        return totalReward;
    }
}
