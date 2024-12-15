package ae.cyberspeed.interview.util;

import ae.cyberspeed.interview.model.RewardResult;

import java.util.Arrays;

public class OutputUtil {
    public static String generateOutput(RewardResult result) {
        StringBuilder output = new StringBuilder("{\n");
        output.append("  \"matrix\": [\n");
        for (String[] row : result.getMatrix()) {
            output.append("    ").append(Arrays.toString(row)).append(",\n");
        }
        output.deleteCharAt(output.length() - 2); // Remove the last comma
        output.append("  ],\n");
        output.append("  \"reward\": ").append(result.getReward()).append(",\n");
        output.append("  \"applied_winning_combinations\": ").append(result.getAppliedWinningCombinations()).append(",\n");
        output.append("  \"applied_bonus_symbol\": \"").append(result.getAppliedBonusSymbol()).append("\"\n");
        output.append("}");
        return output.toString();
    }
}
