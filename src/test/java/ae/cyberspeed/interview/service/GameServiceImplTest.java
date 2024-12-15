package ae.cyberspeed.interview.service;

import ae.cyberspeed.interview.model.*;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceImplTest {

    private GameServiceImpl gameService;
    private Config config;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper objectMapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
        config = objectMapper.readValue(getClass().getClassLoader().getResource("config.json"), Config.class);
        this.gameService = new GameServiceImpl(config);
    }

    @Test
    public void testGenerateMatrixWithStandardSymbol() {
        String[][] matrix = gameService.generateMatrixWithStandardSymbol();

        assertNotNull(matrix, "Matrix should not be null");
        assertEquals(3, matrix.length, "Matrix should have 3 rows");
        assertEquals(3, matrix[0].length, "Matrix should have 3 columns");


        List<String> allowedLetters = List.of("A", "B", "C", "D", "E", "F");

        boolean containsValidSymbols = Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .allMatch(symbol -> symbol == null || allowedLetters.contains(symbol));

        assertTrue(containsValidSymbols, "Matrix should only contain valid symbols (A, B, C, D, E, F or null)");
    }

    @Test
    public void testAddBonusSymbol() {
        String[][] matrix = gameService.generateMatrixWithStandardSymbol();
        StringBuilder bonus = new StringBuilder();

        gameService.addBonusSymbol(matrix, bonus);

        List<String> allowedBonusSymbols = List.of("10x", "5x", "+1000", "+500", "MISS");



        assertNotNull(bonus.toString(), "Bonus symbol should not be null");
        assertTrue(allowedBonusSymbols.contains(bonus.toString()),
                "Bonus symbol should be '10x' | '5x' | '1000' | '+500' | 'MISS'");
    }

    @Test
    public void testApplyWinningCombinations() {
        String[][] matrix = {
                {"A", "A", "A"},
                {"B", "+1000", "B"},
                {"A", "B", "A"}
        };

        Map<String, Set<String>> winningCombinations = gameService.applyWinningCombinations(matrix);
        assertNotNull(winningCombinations, "Winning combinations should not be null");
        assertTrue(winningCombinations.containsKey("A"), "Winning combinations should include symbol 'A'");
        assertTrue(winningCombinations.get("A").contains("same_symbol_5_times") && winningCombinations.get("A").contains("same_symbols_horizontally") ,
                "Winning combinations for 'A' should include 'same_symbol_5_times' && 'same_symbols_horizontally'");

    }

    @Test
    public void testCalculateReward() {

        String[][] matrix = {
                {"F", "E", "C"},
                {"+500", "F", "E"},
                {"C", "D", "F"}
        };

        Map<String, Set<String>> winningCombinations = gameService.applyWinningCombinations(matrix);

        double reward = gameService.calculateReward(100, winningCombinations, "+500");

        assertEquals(1000, reward, 0.01, "Reward should match expected value (1000)");
    }
}