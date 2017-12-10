package pl.gda.edu.pg.bioinf;

import pl.gda.edu.pg.bioinf.casino.Casino;
import pl.gda.edu.pg.bioinf.casino.Croupier;
import pl.gda.edu.pg.bioinf.casino.Dice;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Application {
    private final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Dice fairDice, unfairDice;
        int numberOfRounds;
        boolean startWithFairDice;

        fairDice = getArgumentsAndCreateFairDice(sc);
        unfairDice = getArgumentsAndCreateUnfairDice(sc);

        //number of rounds
        System.out.println("Podaj liczbę losowań: ");
        numberOfRounds = sc.nextInt();

        //start with fair Dice?
        System.out.println("Czy chcesz rozpocząć od uczciwej kostki? T/N");
        Pattern pattern = Pattern.compile("[tTnN]");
        startWithFairDice = "t".equalsIgnoreCase(sc.next(pattern));

        //creating cassino and play rounds
        Croupier croupier = new Croupier(fairDice, unfairDice, startWithFairDice);
        Casino casino = new Casino(croupier);
        casino.playNRounds(numberOfRounds);
    }

    public static Dice getArgumentsAndCreateFairDice(Scanner sc) {
        System.out.println("Podaj prawdopodobieństwo zmiany kostki z uczciwej na fałszywą: ");
        double probabilityOfFairDiceSwitch = sc.nextDouble();

        Dice fairDice = Dice.createFairDice(probabilityOfFairDiceSwitch);

        return fairDice;
    }

    public static Dice getArgumentsAndCreateUnfairDice(Scanner sc) {
        List<Double> unfairDiceProbabilityList = new ArrayList<>();

        System.out.println("Podaj prawdopodobieństwo zmiany kostki z nieuczciwej na uczciwą: ");
        double probabilityOfUnfairDiceSwitch = sc.nextDouble();
        System.out.println("Podaj prawdopodobieństwo wyrzucenia każdej wartości na kostce, zaczynając od 1: ");
        for (int i = 0; i < 6; i++) {
            unfairDiceProbabilityList.add(sc.nextDouble());
        }
        Dice unfairDice = new Dice (unfairDiceProbabilityList, probabilityOfUnfairDiceSwitch);

        return unfairDice;
    }
}
