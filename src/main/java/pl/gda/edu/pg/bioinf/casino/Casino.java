package pl.gda.edu.pg.bioinf.casino;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Casino {

    private Croupier croupier;

    public Casino(Croupier croupier) {
        this.croupier = croupier;
    }

    /**
     * Croupier rolls the dice
     * @return the result from the dice
     */
    public Integer playRound() {
        return croupier.performRound();
    }

    /**
     *  Croupier performs multiple rounds
     * @param n number of rounds
     * @return the n results
     */
    public List<Integer> playNRounds(int n) {
        if (n > 0) {
            return IntStream.range(0, n).boxed().map(i -> croupier.performRound()).collect(Collectors.toList());
        }

        throw new IllegalArgumentException("Bad argument");
    }
}
