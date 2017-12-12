package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.math.State;

import java.util.ArrayList;
import java.util.List;

import static pl.gda.edu.pg.bioinf.math.State.FAIR_DICE;

public class SuffixAlgorithm {

    private Dice fairDice;
    private Dice loadedDice;
    private List<Integer> observedSequence;

    //Probability of every roll state
    private PrefixAlgorithm prefixAlgorithm;

    private double[][] probabilityOfSuffix;

    public SuffixAlgorithm(Dice fairDice, Dice loadedDice, List<Integer> observedSequence) {
        this.fairDice = fairDice;
        this.loadedDice = loadedDice;
        this.observedSequence = observedSequence;
        this.probabilityOfSuffix = new double[State.values().length][observedSequence.size()];
        this.prefixAlgorithm = new PrefixAlgorithm(fairDice, loadedDice, observedSequence);
    }

    public double calculateProbability(int startIndex, State startState) {
        if (startIndex > observedSequence.size()) {
            throw new IllegalArgumentException("Start index too big");
        }

        //init last elements
        for (State state : State.values()) {
            probabilityOfSuffix[state.getNumber()][observedSequence.size() - 1] = 1.0;
        }

        for (int i = observedSequence.size() - 2; i >= 0; i--) {
            for(State state: State.values()) {
                probabilityOfSuffix[state.getNumber()][i] = computeNextProbability(i, state);
            }
        }

        double probabilityOfGivenSuffixWhenParticularStateOnIndex = computeProbabilityOfSuffix();

        computeProbableStateOfEveryElement(startState,
                probabilityOfGivenSuffixWhenParticularStateOnIndex);

        System.out.println("Algorytm sufiksowy - prawdopodbieństwo: " + probabilityOfGivenSuffixWhenParticularStateOnIndex);
        return probabilityOfGivenSuffixWhenParticularStateOnIndex;

    }

    private double computeNextProbability(int index, State state) {
        double prob = .0;
        for (State stateX : State.values()) {
            prob += getEmissionProbability(state, observedSequence.get(index + 1) - 1) *
                    probabilityOfSuffix[state.getNumber()][index + 1] *
                    (stateX.equals(state) ? 1 - getDiceSwitchProbability(state) : getDiceSwitchProbability(state));
        }
        return prob;

    }

    private double getDiceSwitchProbability(State state) {
        if (state.equals(FAIR_DICE)) {
            return fairDice.getProbabilityOfDiceSwitch();
        } else {
            return loadedDice.getProbabilityOfDiceSwitch();
        }
    }

    private double getEmissionProbability(State state, int index) {
        if (state.equals(FAIR_DICE)) {
            return fairDice.getProbabilities().get(index);
        } else {
            return loadedDice.getProbabilities().get(index);
        }
    }

    private double computeProbabilityOfSuffix() {
        double probability = .0;
        for (State state : State.values()) {
            probability += getDiceSwitchProbability(state) *
                    getEmissionProbability(state, 0) *
                    probabilityOfSuffix[state.getNumber()][0];
        }

        return probability;
    }

    private void computeProbableStateOfEveryElement(State startState, double probability) {
        prefixAlgorithm.calculateProbability(startState);

        System.out.println("Probability of states when started with " + startState);

        List<Double> probabilitiesOfStates = new ArrayList<>();

        for (int i = 0; i < probabilityOfSuffix[startState.getNumber()].length; i++) {
            probabilitiesOfStates.add(
                    (prefixAlgorithm.getProbabilityMatrix()[startState.getNumber()][i]*
                            probabilityOfSuffix[startState.getNumber()][i]) /
                            probability);

            //System.out.println(i + 1 + ": " + probabilitiesOfStates.get(i));
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
}
