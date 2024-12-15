package ae.cyberspeed.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Probabilities {
    @JsonProperty("standard_symbols")
    private List<SymbolProbability> standardSymbols;
    @JsonProperty("bonus_symbols")
    private SymbolProbability bonusSymbol;

    public List<SymbolProbability> getStandardSymbols() {
        return standardSymbols;
    }

    public void setStandardSymbols(List<SymbolProbability> standardSymbols) {
        this.standardSymbols = standardSymbols;
    }

    public SymbolProbability getBonusSymbol() {
        return bonusSymbol;
    }

    public void setBonusSymbol(SymbolProbability bonusSymbol) {
        this.bonusSymbol = bonusSymbol;
    }
}
