package pl.gda.edu.pg.bioinf.casino;

import java.util.ArrayList;

public class PrefixAlgorithm {

    public double calculateProbability(double emissionProbability, int state) {
        int index;
        int lastIndex = 0;
        int l = 0;
        double probability = 0.0;
        double sum = 0.0;
        ArrayList<Double> stateChangeProbability = new ArrayList<>();
        ArrayList<Double> function = new ArrayList<>();

        for (index = 1; index < lastIndex; index++) {
            for (int k = 1; k < state; k++) {
                sum += function.get(index-1) * stateChangeProbability.get(k);
            }
            function.add(index, emissionProbability*sum);
        }

        return probability;
    }
}
