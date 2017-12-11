package pl.gda.edu.pg.bioinf.casino;

import pl.gda.edu.pg.bioinf.math.State;

import java.util.ArrayList;

public class PrefixAlgorithm {

    public double calculateProbability(double emissionProbability, State state) {
        int index;
        int lastIndex = 0;
        int l = 0;
        double probability = 0.0;
        double sum = 0.0;
        ArrayList<Double> stateChangeProbability = new ArrayList<>();
        ArrayList<Double> function = new ArrayList<>();

        for (index = 1; index < lastIndex; index++) {
            for (int k = 1; k < state.getNumber(); k++) {
                sum += function.get(index-1) * stateChangeProbability.get(k);
            }
            function.add(index, emissionProbability*sum);
        }

        return probability;
    }
}
