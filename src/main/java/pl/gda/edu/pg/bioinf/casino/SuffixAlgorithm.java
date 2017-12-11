package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.math.State;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SuffixAlgorithm {
    private List<Integer> observedValues;
    private Dice fairDice;
    private Dice loadedDice;
    private Map<State, List<Double>> probabilities;

    public SuffixAlgorithm(List<Integer> observedValues, Dice fairDice, Dice loadedDice) {
        this.observedValues = observedValues;
        this.fairDice = fairDice;
        this.loadedDice = loadedDice;
    }

    private Map<State, List<Double>> initProbabilities() {
        Map<State, List<Double>> probs = null;
        return probs;
    }

    public List<Integer> getObservedValues() {
        return observedValues;
    }

    public Dice getFairDice() {
        return fairDice;
    }

    public Dice getLoadedDice() {
        return loadedDice;
    }
}
