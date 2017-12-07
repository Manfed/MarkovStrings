package pl.gda.edu.pg.bioinf.casino;

public class Croupier {
    private final Dice fairDice;
    private final Dice loadedDice;
    private boolean fairDiceInGame;

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

    private void swapDices() {
        boolean shouldSwapDices;
        if (fairDiceInGame) {
            shouldSwapDices = fairDice.shouldSwitchDice();
        } else {
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
