package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.math.State;

import java.util.*;

public class ViterbiAlgorithm {
    private List<Integer> observableSequence;
    private int observableSequenceLength;
    private static final int NUMBER_OF_STATES = State.values().length; //fair dice, unfair dice
    private static final int NUMBER_OF_SYMBOLS = 6;
    private static final double MAX_PROBABILITY = 1.0;
    private double[] startProbabilityInState;
    double[][] changingHMMStateMatrix;
    double[][] stateEmissionMatrix;
    double[][] wagesMatrix;
    double[][] pathsMatrix;

    ViterbiAlgorithm(int numberOfRounds, State startState, Dice fairDice, Dice unfairDice, List<Integer> observableSequence) {
        observableSequenceLength = numberOfRounds;
        this.observableSequence = observableSequence;
        this.observableSequenceLength = observableSequence.size();
        createChangingHMMStateMarix(fairDice.getProbabilityOfDiceSwitch(), unfairDice.getProbabilityOfDiceSwitch());
        createStateEmissionMatrix(fairDice, unfairDice);
        createStartProbabilityInStateMatrix(startState);
    }

    private void createChangingHMMStateMarix(double changingStateProbabilityFromFair, double changingStateProbabilityFromUnfair) {
        changingHMMStateMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_STATES];

        for (State s1 : State.values()) {
            for (State s2 : State.values()) {
                if (s1.equals(s2) && s1.equals(State.FAIR_DICE)) {
                    changingHMMStateMatrix[s1.getNumber()][s2.getNumber()] = MAX_PROBABILITY - changingStateProbabilityFromFair;
                } else if (s1.equals(s2) && s1.equals(State.UNFAIR_DICE)) {
                    changingHMMStateMatrix[s1.getNumber()][s2.getNumber()] = MAX_PROBABILITY - changingStateProbabilityFromUnfair;
                } else if (s1.equals(State.FAIR_DICE)) {
                    changingHMMStateMatrix[s1.getNumber()][s2.getNumber()] = changingStateProbabilityFromFair;
                } else {
                    changingHMMStateMatrix[s1.getNumber()][s2.getNumber()] = changingStateProbabilityFromUnfair;
                }
            }
        }
    }

    private void createStateEmissionMatrix(Dice fairDice, Dice unfairDice) {
        stateEmissionMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_SYMBOLS];

        for (State s : State.values()) {
            Dice dice;
            if (s.equals(State.FAIR_DICE)) {
                dice = fairDice;
            } else {
                dice = unfairDice;
            }
            for (int i = 0; i < NUMBER_OF_SYMBOLS; i++) {
                stateEmissionMatrix[s.getNumber()][i] = dice.getProbabilities().get(i);
            }
        }
    }

    private void createStartProbabilityInStateMatrix(State startState) {
        startProbabilityInState = new double[NUMBER_OF_STATES];

        for (State s : State.values()) {
            if (s.equals(startState)) {
                startProbabilityInState[s.getNumber()] = 1.0;
            } else {
                startProbabilityInState[s.getNumber()] = 0.0;
            }
        }
    }

    public void createSequence() {
        initializeHMMStates();
    }

    private void initializeHMMStates() {
        wagesMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_STATES];
        pathsMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_SYMBOLS];
        for (int i = 0; i < NUMBER_OF_STATES; i++) {    //for every state
            wagesMatrix[i][0] = stateEmissionMatrix[i][observableSequence.get(0) - 1] * startProbabilityInState[i];
            pathsMatrix[i][0] = 0;
        }
    }

    public static void main(String args[]) {
        Dice fair = Dice.createFairDice(0.1);
        Dice unfair = new Dice(Arrays.asList(0.1, 0.1, 0.1, 0.1, 0.1, 0.5), 0.2);
        List<Integer> observableSequence = Arrays.asList(3,2,4,6,6,6,1);
        ViterbiAlgorithm va = new ViterbiAlgorithm(5, State.FAIR_DICE, fair, unfair, observableSequence);
        va.createSequence();
    }
}
