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
    private double[][] changingHMMStateMatrix;
    private double[][] stateEmissionMatrix;
    private double[][] deltaMatrix;
    private State maximumState = null;

    public ViterbiAlgorithm(int numberOfRounds, Dice fairDice, Dice unfairDice, List<Integer> observableSequence, double fairStartProbability) {
        observableSequenceLength = numberOfRounds;
        this.observableSequence = observableSequence;
        this.observableSequenceLength = observableSequence.size();
        createChangingHMMStateMarix(fairDice.getProbabilityOfDiceSwitch(), unfairDice.getProbabilityOfDiceSwitch());
        createStateEmissionMatrix(fairDice, unfairDice);
        createStartProbabilityInStateMatrix(fairStartProbability);
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

    private void createStartProbabilityInStateMatrix(double fairStartProbability) {
        startProbabilityInState = new double[NUMBER_OF_STATES];

        for (State s : State.values()) {
            if (s.equals(State.FAIR_DICE)) {
                startProbabilityInState[s.getNumber()] = fairStartProbability;
            } else {
                startProbabilityInState[s.getNumber()] = MAX_PROBABILITY  - fairStartProbability;
            }
        }
    }

    public void createSequence() {
        initializeHMMStates();
        forAllSequenceFindBestEdgeAndRemember();
        getTheBestPath();
    }

    private void initializeHMMStates() {
        deltaMatrix = new double[NUMBER_OF_STATES][observableSequenceLength];
        for (int i = 0; i < NUMBER_OF_STATES; i++) {    //for every state
            deltaMatrix[i][0] = stateEmissionMatrix[i][observableSequence.get(0) - 1] * startProbabilityInState[i];
        }
    }

    private void forAllSequenceFindBestEdgeAndRemember() {
        for (int t = 1; t < observableSequenceLength; t++) {
            for (int j = 0; j < NUMBER_OF_STATES; j++) {
                deltaMatrix[j][t] = stateEmissionMatrix[j][observableSequence.get(t) - 1] * maximum(t, j);
            }
        }
    }

    private void getTheBestPath() {
        Stack<State> stateStack = new Stack<>();

        for (int i = observableSequenceLength - 1; i >= 0; i--) {
            int fairDiceNumber = State.FAIR_DICE.getNumber();
            int unfairDiceNumber = State.UNFAIR_DICE.getNumber();
            stateStack.push(deltaMatrix[fairDiceNumber][i] > deltaMatrix[unfairDiceNumber][i]
                    ? State.FAIR_DICE : State.UNFAIR_DICE);
        }

        while(!stateStack.empty()) {
            System.out.println(stateStack.pop());
        }
    }

    private double maximum(int t, int j) {
        double max = 0.0;
        for (State s : State.values()) {
            int i = s.getNumber();
            double var = deltaMatrix[i][t-1] * changingHMMStateMatrix[i][j];
            if (var > max) {
                max = var;
                maximumState = s;
            }
        }
        return max;
    }

    public static void main(String args[]) {
        Dice fair = Dice.createFairDice(0.1);
        Dice unfair = new Dice(Arrays.asList(0.1, 0.1, 0.1, 0.1, 0.1, 0.5), 0.2);
        List<Integer> observableSequence = Arrays.asList(3,6,6,6,4,3,3,3,2,2,1,2,5,6,3);
        ViterbiAlgorithm va = new ViterbiAlgorithm(5, fair, unfair, observableSequence, 0.4);
        va.createSequence();
    }
}
