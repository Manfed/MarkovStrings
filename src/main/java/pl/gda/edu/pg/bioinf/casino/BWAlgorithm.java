package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.math.State;

import java.util.List;

public class BWAlgorithm {
    private List<Integer> observableSequence;
    private int observableSequenceLength;
    private static final int NUMBER_OF_STATES = State.values().length; //fair dice, unfair dice
    private static final int NUMBER_OF_SYMBOLS = 6;
    private static final double MAX_PROBABILITY = 1.0;

    private double[] startProbabilityInState;
    private double[][] changingHMMStateMatrix;
    private double[][] stateEmissionMatrix;
    private double[][] alphaMatrix;
    private double[][] betaMatrix;

    public BWAlgorithm(int numberOfRounds, Dice fairDice, Dice unfairDice, List<Integer> observableSequence, double fairStartProbability) {
        this.observableSequence = observableSequence;
        this.observableSequenceLength = numberOfRounds;
        createChangingHMMStateMatrix(fairDice.getProbabilityOfDiceSwitch(), unfairDice.getProbabilityOfDiceSwitch());
        createStateEmissionMatrix(fairDice, unfairDice);
        createStartProbabilityInStateMatrix(fairStartProbability);
    }

    private void createChangingHMMStateMatrix(double changingStateProbabilityFromFair, double changingStateProbabilityFromUnfair) {
        changingHMMStateMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_STATES];

        for (State s1 : State.values()) {
            for (State s2 : State.values()) {
                int idx1 = s1.getNumber();
                int idx2 = s2.getNumber();
                if (s1.equals(s2) && s1.equals(State.FAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = MAX_PROBABILITY - changingStateProbabilityFromFair;
                } else if (s1.equals(s2) && s1.equals(State.UNFAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = MAX_PROBABILITY - changingStateProbabilityFromUnfair;
                } else if (s1.equals(State.FAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = changingStateProbabilityFromFair;
                } else {
                    changingHMMStateMatrix[idx1][idx2] = changingStateProbabilityFromUnfair;
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

    public void runAlgorithm() {
        alphaPass();
        betaPass();
    }

    private void betaPass() {
        betaMatrix = new double[observableSequenceLength][NUMBER_OF_STATES];

        for (int i = 0; i < NUMBER_OF_STATES; i++) {
            betaMatrix[observableSequenceLength - 1][i] = 1.0;
        }

        for (int t = observableSequenceLength - 2; t >= 0; t--) {
            for (int i = 0; i < NUMBER_OF_STATES; i++) {
                double sum = 0.0;
                for (int j = 0; j < NUMBER_OF_STATES; j++) {
                    sum += changingHMMStateMatrix[i][j] * stateEmissionMatrix[j][observableSequence.get(j)]* betaMatrix[t+1][j];
                }
                betaMatrix[t][i] = sum;
            }
        }
    }

    private void alphaPass() {
        alphaMatrix = new double[observableSequenceLength][NUMBER_OF_STATES];

        for (int i = 0; i < NUMBER_OF_STATES; i++) {
            alphaMatrix[0][i] = startProbabilityInState[i] * stateEmissionMatrix[i][observableSequence.get(0) - 1];
        }

        for (int t = 1; t < observableSequenceLength; t++) {
            for (int i = 0; i < NUMBER_OF_STATES; i++) {
                alphaMatrix[t][i] = sumAlpha(i, t) * stateEmissionMatrix[i][observableSequence.get(t) - 1];
            }
        }
    }

    private double sumAlpha(int i, int t) {
        double sum = 0.0;
        for (int j = 0; j < NUMBER_OF_STATES; j++) {
            sum += alphaMatrix[t-1][j] * changingHMMStateMatrix[j][i];
        }

        return sum;
    }
}
