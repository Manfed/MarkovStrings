package pl.gda.edu.pg.bioinf.casino;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class Dice {
    private static final int MIN_DICE_RESULT = 1;
    private static final int MAX_DICE_RESULT = 6;

    private final Map<Integer, Double> probabilityOfResults;
    private final double probabilityOfDiceSwitch;

    public Dice(List<Double> probabilities, double probabilityOfDiceSwitch) {
        double sumOfProbabilities = probabilities.stream().mapToDouble(Double::doubleValue).sum();

        if (probabilities == null || probabilities.size() != 6 || sumOfProbabilities != 1) {
            throw new IllegalArgumentException("Not valid probabilities");
        }

        this.probabilityOfResults = IntStream.rangeClosed(MIN_DICE_RESULT, MAX_DICE_RESULT)
                .boxed()
                .collect(
                        toMap(identity(),
                        val -> probabilities.stream().mapToDouble(Double::doubleValue).limit(val).sum()
                ));

        this.probabilityOfDiceSwitch = probabilityOfDiceSwitch;
    }

    public static Dice createFairDice(double probabilityOfSwitch) {
        List<Double> probabilities = Collections.nCopies(MAX_DICE_RESULT, 1/6.0);

        return new Dice(probabilities, probabilityOfSwitch);
    }

    /**
     * Roll the dice
     * @return  The roll result
     */
    public Integer roll() {
        for (Map.Entry<Integer, Double> entry: probabilityOfResults.entrySet()) {
            if (entry.getValue() > Math.random()) {
                return entry.getKey();
            }
        }
        throw new NoSuchElementException("Roll ");
    }

    /**
     * Should croupier switch the dices?
     * @return The croupier decision
     */
    public boolean shouldSwitchDice() {
        return Math.random() < probabilityOfDiceSwitch;
    }
}
