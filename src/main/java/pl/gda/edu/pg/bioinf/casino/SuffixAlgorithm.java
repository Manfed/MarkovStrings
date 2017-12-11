package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.math.State;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class SuffixAlgorithm {

    private Dice fairDice;
    private Dice loadedDice;
    private List<Integer> observedSequence;

    private Map<State, List<Double>> probabilityOfSuffix;

    public SuffixAlgorithm(Dice fairDice, Dice loadedDice, List<Integer> observedSequence) {
        this.fairDice = fairDice;
        this.loadedDice = loadedDice;
        this.observedSequence = observedSequence;
        this.probabilityOfSuffix = initProbabilities(fairDice, loadedDice);
    }

    private Map<State, List<Double>> initProbabilities(Dice fairDice, Dice loadedDice) {
        return Arrays.stream(State.values())
                .collect(toMap(Function.identity(), state -> Arrays.asList(1.0)));
    }

    public Dice getFairDice() {
        return fairDice;
    }

    public Dice getLoadedDice() {
        return loadedDice;
    }

    public List<Integer> getObservedSequence() {
        return observedSequence;
    }
}
