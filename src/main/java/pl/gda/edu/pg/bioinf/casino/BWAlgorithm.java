package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.Application;
import pl.gda.edu.pg.bioinf.math.State;

import java.util.List;

import static java.lang.Math.abs;

public class BWAlgorithm {
    private List<Integer> observableSequence;
    private int observableSequenceLength;
    private static final int NUMBER_OF_STATES = State.values().length; //fair dice, unfair dice
    private static final int NUMBER_OF_SYMBOLS = 6;

    private double[] startProbabilityInState;
    private double[][] changingHMMStateMatrix;
    private double[][] stateEmissionMatrix;
    private double[][] alphaMatrix;
    private double[][] betaMatrix;
    private double[][][] gamma3DMatrix;
    private double[][] gammaMatrix;

    private double maxProbability = 0.0;

    public BWAlgorithm(int numberOfRounds, List<Integer> observableSequence) {
        this.observableSequence = observableSequence;
        this.observableSequenceLength = numberOfRounds;
        createChangingHMMStateMatrix();
        createStateEmissionMatrix();
        createStartProbabilityInStateMatrix();
    }

    private void createChangingHMMStateMatrix() {
        changingHMMStateMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_STATES];

        for (State s1 : State.values()) {
            for (State s2 : State.values()) {
                int idx1 = s1.getNumber();
                int idx2 = s2.getNumber();
                if (s1.equals(s2) && s1.equals(State.FAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = 0.7;
                } else if (s1.equals(s2) && s1.equals(State.UNFAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = 0.2;
                } else if (s1.equals(State.FAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = 0.3;
                } else {
                    changingHMMStateMatrix[idx1][idx2] = 0.8;
                }
            }
        }
    }

    private void createStateEmissionMatrix() {
        stateEmissionMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_SYMBOLS];

        for (State s : State.values()) {
            for (int i = 0; i < NUMBER_OF_SYMBOLS; i++) {
                stateEmissionMatrix[s.getNumber()][i] = 0.5;
            }
        }
    }

    private void createStartProbabilityInStateMatrix() {
        startProbabilityInState = new double[NUMBER_OF_STATES];

        startProbabilityInState[State.FAIR_DICE.getNumber()] = 0.9;
        startProbabilityInState[State.UNFAIR_DICE.getNumber()] = 0.1;
    }

    public void runAlgorithm() {
        for (int i = 0; i < 100; i++) {
            alphaPass();
            betaPass();
            gammaPass();
            reestimatation();
            double probability = calculateProbability();
            System.out.println(maxProbability + " " + probability);
            if (Double.isNaN(maxProbability - probability)|| abs(maxProbability - probability) <= 1e-10) {
                break;
            }
            maxProbability = probability;
        }
        System.out.println("New state emission matrix: ");
        Application.printMatrix(stateEmissionMatrix);
    }

    private void reestimatation() {
        startProbabilityInStateReestimation();
        changingHMMStateMatrixReestimation();
        stateEmissionMatrixReestimation();
    }

    private void stateEmissionMatrixReestimation() {
        for (int j = 0; j < NUMBER_OF_STATES; j++) {
            for (int k = 0; k < NUMBER_OF_SYMBOLS; k++) {
                double sumGamma = 0.0;
                double sumCondition = 0.0;

                for (int t = 0; t < observableSequenceLength - 1; t++) {
                    sumGamma += gammaMatrix[t][j];
                    if (observableSequence.get(t) - 1 == k) {
                        sumCondition += gammaMatrix[t][j];
                    }
                }
                stateEmissionMatrix[j][k] = sumCondition / sumGamma;
            }
        }
    }

    private void changingHMMStateMatrixReestimation() {
        for (int i = 0; i < NUMBER_OF_STATES; i++) {
            for (int j = 0; j < NUMBER_OF_STATES; j++) {
                double sumGamma = 0.0;
                double sumGamma3D  = 0.0;

                for (int t = 0; t < observableSequenceLength - 1; t++) {
                    sumGamma += gammaMatrix[t][i];
                    sumGamma3D += gamma3DMatrix[t][i][j];
                }
                changingHMMStateMatrix[i][j] = sumGamma3D / sumGamma;
            }
        }
    }

    private void startProbabilityInStateReestimation() {
        System.arraycopy(gammaMatrix[0], 0, startProbabilityInState, 0, NUMBER_OF_STATES);
    }

    private void gammaPass() {
        gamma3DMatrix = new double [observableSequenceLength][NUMBER_OF_STATES][NUMBER_OF_STATES];
        gammaMatrix = new double[observableSequenceLength][NUMBER_OF_STATES];
        for (int t = 0; t < observableSequenceLength-1; t++) {
            double denominator = calculateDenominator(t);
            for (int i = 0; i < NUMBER_OF_STATES; i++) {
                for (int j = 0; j < NUMBER_OF_STATES; j++) {
                    gamma3DMatrix[t][i][j] = alphaMatrix[t][i] * changingHMMStateMatrix[i][j] * stateEmissionMatrix[j][observableSequence.get(t + 1) - 1] * betaMatrix[t+1][j] / maxProbability;
                    gammaMatrix[t][i] += gamma3DMatrix[t][i][j];
                }
            }
        }
    }

    private double calculateDenominator(int t) {
        double result = 0.0;

        for (int i = 0; i < NUMBER_OF_STATES; i++) {
            for (int j = 0; j < NUMBER_OF_STATES; j++) {
                result = alphaMatrix[t][i] * changingHMMStateMatrix[i][j] * stateEmissionMatrix[j][observableSequence.get(t + 1) - 1] * betaMatrix[t+1][j];
            }
        }

        return result;
    }

    private double calculateProbability() {
        double sum = 0.0;
        for (int i = 0; i < NUMBER_OF_STATES; i++) {
            sum += alphaMatrix[observableSequenceLength - 1][i];
        }

        return sum;
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
                    sum += changingHMMStateMatrix[i][j] * stateEmissionMatrix[j][observableSequence.get(j) - 1]* betaMatrix[t+1][j];
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
