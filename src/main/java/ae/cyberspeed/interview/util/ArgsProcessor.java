package ae.cyberspeed.interview.util;

import java.io.File;

public class ArgsProcessor {
    private String configurationFilePath;
    private int betAmount;


    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public int getBetAmount() {
        return betAmount;
    }


    public boolean initializeArguments(String[] arguments) {
        if (arguments == null || arguments.length == 0) {
            displayHelp();
            return false;
        }

        try {
            for (int i = 0; i < arguments.length; i++) {
                switch (arguments[i]) {
                    case "-c":
                    case "--config":
                        configurationFilePath = extractArgumentValue(arguments, ++i, "config");
                        break;
                    case "-b":
                    case "--betting-amount":
                        betAmount = parseBetAmount(extractArgumentValue(arguments, ++i, "betting amount"));
                        break;
                    case "-h":
                    case "--help":
                        return displayHelp();
                    default:
                        throw new IllegalArgumentException("Unknown argument: " + arguments[i]);
                }
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: " + e.getMessage());
            return displayHelp();
        }

        return validateArguments();
    }


    private boolean validateArguments() {
        if (configurationFilePath == null || configurationFilePath.isBlank()) {
            System.err.println("Error: Configuration file path is not specified.");
            return false;
        }

        if (!new File(configurationFilePath).exists()) {
            System.err.println("Error: Configuration file does not exist at: " + configurationFilePath);
            return false;
        }

        if (betAmount <= 0) {
            System.err.println("Error: Betting amount must be a positive number.");
            return false;
        }

        return true;
    }

    private String extractArgumentValue(String[] arguments, int index, String argumentName) {
        if (index >= arguments.length) {
            throw new IllegalArgumentException("Missing value for " + argumentName + ".");
        }
        return arguments[index];
    }


    private int parseBetAmount(String amountString) {
        try {
            return Integer.parseInt(amountString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid betting amount: " + amountString);
        }
    }


    private boolean displayHelp() {
        System.out.println("Usage: java -jar cyberspeed-scratch-game.jar [options]");
        System.out.println("Options:");
        System.out.println("  -c, --config <path>  Path to the configuration file");
        System.out.println("  -b, --betting-amount <amount>   Betting amount");
        System.out.println("  -h, --help           Display this help message");
        return false;
    }
}
