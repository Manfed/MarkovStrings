package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.Application;
import pl.gda.edu.pg.bioinf.math.State;

import java.util.List;

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

    private double maxProbability = 1.0;

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
                    changingHMMStateMatrix[idx1][idx2] = 0.95;
                } else if (s1.equals(s2) && s1.equals(State.UNFAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = 0.95;
                } else if (s1.equals(State.FAIR_DICE)) {
                    changingHMMStateMatrix[idx1][idx2] = 0.05;
                } else {
                    changingHMMStateMatrix[idx1][idx2] = 0.05;
                }
            }
        }
    }

    private void createStateEmissionMatrix() {
        stateEmissionMatrix = new double[NUMBER_OF_STATES][NUMBER_OF_SYMBOLS];

//        for (State s : State.values()) {
//            stateEmissionMatrix[s.getNumber()][0] = 0.15;
//            stateEmissionMatrix[s.getNumber()][1] = 0.2;
//            stateEmissionMatrix[s.getNumber()][2] = 0.25;
//            stateEmissionMatrix[s.getNumber()][3] = 0.05;
//            stateEmissionMatrix[s.getNumber()][4] = 0.1;
//            stateEmissionMatrix[s.getNumber()][5] = 0.25;
//        }
        stateEmissionMatrix[0][0] = 0.15;
        stateEmissionMatrix[0][1] = 0.2;
        stateEmissionMatrix[0][2] = 0.25;
        stateEmissionMatrix[0][3] = 0.05;
        stateEmissionMatrix[0][4] = 0.1;
        stateEmissionMatrix[0][5] = 0.25;

        stateEmissionMatrix[1][0] = 0.07;
        stateEmissionMatrix[1][1] = 0.03;
        stateEmissionMatrix[1][2] = 0.05;
        stateEmissionMatrix[1][3] = 0.05;
        stateEmissionMatrix[1][4] = 0.1;
        stateEmissionMatrix[1][5] = 0.7;
    }

    private void createStartProbabilityInStateMatrix() {
        startProbabilityInState = new double[NUMBER_OF_STATES];

        startProbabilityInState[State.FAIR_DICE.getNumber()] = 0.9;
        startProbabilityInState[State.UNFAIR_DICE.getNumber()] = 0.1;
    }

    public void runAlgorithm() {
        System.out.println("Old state emission matrix: ");
        Application.printMatrix(stateEmissionMatrix);
        System.out.println("Old state changing matrix: ");
        Application.printMatrix(changingHMMStateMatrix);
        System.out.println("Sequence: " + observableSequence);
//        System.out.println("Old state emission matrix: ");
//        Application.printMatrix(stateEmissionMatrix);
//        System.out.println("Old state changing matrix: ");
//        Application.printMatrix(changingHMMStateMatrix);
//        System.out.println("Sequence: " + observableSequence);
        for (int i = 0; i < 1000; i++) {
            alphaPass();
            betaPass();
            double probability = calculateProbability();
            if (Double.isNaN(maxProbability - probability) || maxProbability - probability < 1e-20) {
                break;
            }
            maxProbability = probability;
            gammaPass();
            reestimatation();
        }
        System.out.println("New state emission matrix: ");
        Application.printMatrix(stateEmissionMatrix);
        System.out.println("New state changing matrix: ");
        Application.printMatrix(changingHMMStateMatrix);
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
            for (int i = 0; i < NUMBER_OF_STATES; i++) {
                for (int j = 0; j < NUMBER_OF_STATES; j++) {
                    gamma3DMatrix[t][i][j] = alphaMatrix[t][i] * changingHMMStateMatrix[i][j] * stateEmissionMatrix[j][observableSequence.get(t + 1) - 1] * betaMatrix[t+1][j] / maxProbability;
                    gammaMatrix[t][i] += gamma3DMatrix[t][i][j];
                }
            }
        }
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
