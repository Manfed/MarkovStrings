package pl.gda.edu.pg.bioinf.math;

public enum State {
    FAIR_DICE(0),
    UNFAIR_DICE(1);

    private int number;

    State(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
