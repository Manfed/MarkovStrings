package pl.gda.edu.pg.bioinf.casino;

import java.util.ArrayList;
import java.util.List;

public class Croupier {
    private final Dice fairDice;
    private final Dice loadedDice;
    private boolean fairDiceInGame;
    private List<String> dices = new ArrayList<>();

    public Croupier(Dice fairDice, Dice loadedDice, boolean fairDiceInGame) {
        this.fairDice = fairDice;
        this.loadedDice = loadedDice;
        this.fairDiceInGame = fairDiceInGame;
    }

    public Integer performRound() {
        Integer result = rollDice();
        swapDices();

        return result;
    }

    public List<String> getDices() {
        return dices;
    }

    private void swapDices() {
        boolean shouldSwapDices;
        if (fairDiceInGame) {
            dices.add("Fair");
            shouldSwapDices = fairDice.shouldSwitchDice();
        } else {
            dices.add("Unfair");
            shouldSwapDices = loadedDice.shouldSwitchDice();
        }

        if (shouldSwapDices) {
            fairDiceInGame = !fairDiceInGame;
        }
    }

    private Integer rollDice() {
        if (fairDiceInGame) {
            return fairDice.roll();
        }
        return loadedDice.roll();
    }
}
