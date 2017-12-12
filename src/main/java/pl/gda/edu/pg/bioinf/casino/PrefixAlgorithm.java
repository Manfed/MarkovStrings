package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.math.State;

import java.util.List;

public class PrefixAlgorithm {

    private Dice fairDice;
    private Dice loadedDice;
    private List<Integer> observedSequence;
    private double[][] probabilityMatrix;

    public PrefixAlgorithm(Dice fairDice, Dice loadedDice, List<Integer> observedSequence) {
        this.fairDice = fairDice;
        this.loadedDice = loadedDice;
        this.observedSequence = observedSequence;
        this.probabilityMatrix = new double[State.values().length][observedSequence.size()];
    }

    public Double calculateProbability(State startState) {
        //init first elements
        for (State state : State.values()) {
            if (startState.equals(state)) {
                probabilityMatrix[state.getNumber()][0] = 1.0;
            } else {
                probabilityMatrix[state.getNumber()][0] = .0;
            }
        }

        for (int i = 1; i < observedSequence.size(); i++) {
            for(State state: State.values()) {
                probabilityMatrix[state.getNumber()][i] = computeNextProbability(i, state);
            }
        }

        final double probability = sumOfColumn(observedSequence.size() - 1);
        System.out.println("Prefix: Obliczone prawdopodobieństwo: " + probability);
        return probability;
    }

    private double computeNextProbability(int index, State state) {
        double prob = .0;
        for (State stateX : State.values()) {
            prob += sumOfColumn(index - 1) *
                    probabilityOfEmission(state, observedSequence.get(index)) *
                    (state.equals(stateX) ? 1 - probabilityOfStateChange(state) : probabilityOfStateChange(state));
        }
        return prob;
    }

    private double sumOfColumn(int columnNumber) {
        double sum = .0;
        for (int i = 0; i < State.values().length; i++) {
            sum += probabilityMatrix[i][columnNumber];
        }
        return sum;
    }

    private double probabilityOfEmission(State state, int result) {
        if (state.equals(State.FAIR_DICE)) {
            return fairDice.getProbabilities().get(result-1);
        } else {
            return loadedDice.getProbabilities().get(result-1);
        }
    }

    private double probabilityOfStateChange(State state) {
        if (state.equals(State.FAIR_DICE)) {
            return fairDice.getProbabilityOfDiceSwitch();
        } else {
            return loadedDice.getProbabilityOfDiceSwitch();
        }
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

    public double[][] getProbabilityMatrix() {
        return probabilityMatrix;
    }
}
