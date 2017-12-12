package pl.gda.edu.pg.bioinf;

/*
* Dane wejściowe do programu:
* - prawdopodobieństwo zmiany kostki z uczciwej na fałszywą (fair dice)
* - prawdopodobieństwo zmiany kostki z nieuczciwej na uczciwą (unfair dice)
* - prawdopodobieństwa wyrzucenia każdej wartości na kostce, zaczynając od 1 (probability X)
* - liczba losowań (rounds count)
* - rozpoczynanie od uczciwej kostki (t/n) (start dice)
* */

import pl.gda.edu.pg.bioinf.casino.Casino;
import pl.gda.edu.pg.bioinf.casino.Croupier;
import pl.gda.edu.pg.bioinf.casino.Dice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Application {
    private final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Dice fairDice, unfairDice;
        int numberOfRounds;
        boolean startWithFairDice;
        boolean inputFromFile;

        // input data
        System.out.println("Czy chcesz pobrać dane z pliku (T), czy podać ręcznie (N)?");
        Pattern inputDataPattern = Pattern.compile("[tTnN]");
        inputFromFile = "t".equalsIgnoreCase(sc.next(inputDataPattern));

        if (inputFromFile) {
            Map<String, String> content = getInputDataFromFile();
            if (content == null) return;

            // getting all input data from file content (in map)
            double fairProb = Double.parseDouble(content.get("fair dice"));
            fairDice = Dice.createFairDice(fairProb);
            double unFairProb = Double.parseDouble(content.get("unfair dice"));
            ArrayList<Double> unfairDiceProbabilityList = new ArrayList<>();
            for(int i = 1; i <= 6; i++) {
                unfairDiceProbabilityList.add(Double.parseDouble(content.get("probability "+i)));
            }
            unfairDice = new Dice (unfairDiceProbabilityList, unFairProb);
            numberOfRounds = Integer.parseInt(content.get("rounds count"));
            startWithFairDice = content.get("start dice").trim().equalsIgnoreCase("fair");
        } else {
            fairDice = getArgumentsAndCreateFairDice(sc);
            if (fairDice == null) return;
            unfairDice = getArgumentsAndCreateUnfairDice(sc);
            if (unfairDice == null) return;

            //number of rounds
            System.out.println("Podaj liczbę losowań: ");
            numberOfRounds = sc.nextInt();

            //start with fair Dice?
            System.out.println("Czy chcesz rozpocząć od uczciwej kostki? T/N");
            Pattern dicePattern = Pattern.compile("[tTnN]");
            startWithFairDice = "t".equalsIgnoreCase(sc.next(dicePattern));
        }

        //creating cassino and play rounds
        Croupier croupier = new Croupier(fairDice, unfairDice, startWithFairDice);
        Casino casino = new Casino(croupier);
        List <Integer> results = casino.playNRounds(numberOfRounds);
        for (Integer r :
                results) {
            System.out.println(r);
        }
    }

    public static Map<String, String> getInputDataFromFile () {
        System.out.println("Podaj ścieżkę do pliku wejściowego: ");
        String rawPath = sc.next();
        Path inputFilePath = Paths.get(rawPath.replaceAll("\"", ""));
        Map<String, String> content = new HashMap<>();

        try {
            Files.lines(inputFilePath)
                    .forEach(s -> content.put(s.split("=")[0].trim(), s.split("=")[1].trim()));
        } catch (IOException e) {
            System.out.println("Wystąpił wyjątek: " + e.getMessage());
            return null;
        }

        return content;
    }

    public static Dice getArgumentsAndCreateFairDice(Scanner sc) {

        System.out.println("Podaj prawdopodobieństwo zmiany kostki z uczciwej na fałszywą: ");
        Double probabilityOfFairDiceSwitch;
        try {
            probabilityOfFairDiceSwitch = sc.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Podano nieprawidłową wartość");
            return null;
        }

        return Dice.createFairDice(probabilityOfFairDiceSwitch);
    }

    public static Dice getArgumentsAndCreateUnfairDice(Scanner sc) {
        List<Double> unfairDiceProbabilityList = new ArrayList<>();
        double probabilityOfUnfairDiceSwitch;

        System.out.println("Podaj prawdopodobieństwo zmiany kostki z nieuczciwej na uczciwą: ");
        try {
            probabilityOfUnfairDiceSwitch = sc.nextDouble();

            System.out.println("Podaj prawdopodobieństwo wyrzucenia każdej wartości na kostce, zaczynając od 1: ");
            for (int i = 0; i < 6; i++) {
                unfairDiceProbabilityList.add(sc.nextDouble());
            }
        } catch (InputMismatchException e) {
            System.out.println("Podano nieprawidłową wartość");
            return null;
        }

        return new Dice (unfairDiceProbabilityList, probabilityOfUnfairDiceSwitch);
    }
}
