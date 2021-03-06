package pl.gda.edu.pg.bioinf;

/**
* Dane wejściowe do programu:
* - prawdopodobieństwo zmiany kostki z uczciwej na fałszywą (to unfair dice)
* - prawdopodobieństwo zmiany kostki z nieuczciwej na uczciwą (to fair dice)
* - prawdopodobieństwa wyrzucenia każdej wartości na kostce, zaczynając od 1 (probability X)
* - liczba losowań (rounds count)
* - rozpoczynanie od uczciwej kostki (t/n) (start dice)
 *
 * - prawdopodobieństwo rozpoczęcia gry kostką uczciwą (fair start)
* **/

import pl.gda.edu.pg.bioinf.casino.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
        double fairDiceStartProbability = 0.0;

        // input data
        System.out.println("Czy chcesz pobrać dane z pliku (T), czy podać ręcznie (N)?");
        Pattern inputDataPattern = Pattern.compile("[tTnN]");
        inputFromFile = "t".equalsIgnoreCase(sc.next(inputDataPattern));

        if (inputFromFile) {
            Map<String, String> content = getInputDataFromFile();
            if (content == null) return;

            // getting all input data from file content (in map)
            double fairProb = Double.parseDouble(content.get("to unfair dice"));
            fairDice = Dice.createFairDice(fairProb);

            double unFairProb = Double.parseDouble(content.get("to fair dice"));
            ArrayList<Double> unfairDiceProbabilityList = new ArrayList<>();

            for(int i = 1; i <= 6; i++) {
                unfairDiceProbabilityList.add(Double.parseDouble(content.get("probability "+i)));
            }
            unfairDice = new Dice (unfairDiceProbabilityList, unFairProb);
            numberOfRounds = Integer.parseInt(content.get("rounds count"));
            startWithFairDice = content.get("start dice").trim().equalsIgnoreCase("fair");

            fairDiceStartProbability = Double.parseDouble(content.get("fair start"));

        } else {
            fairDice = getArgumentsAndCreateFairDice(sc);
            if (fairDice == null) return;

            unfairDice = getArgumentsAndCreateUnfairDice(sc);
            if (unfairDice == null) return;

            //number of rounds
            System.out.println("Podaj liczbę losowań: ");
            numberOfRounds = sc.nextInt();

            //start with fair Dice?
//            System.out.println("Czy chcesz rozpocząć od uczciwej kostki? T/N");
//            Pattern dicePattern = Pattern.compile("[tTnN]");
//            startWithFairDice = "t".equalsIgnoreCase(sc.next(dicePattern));
        }

//        System.out.println("Wyniki rzutów kostką: ");
//        for (int i = 1; i <= 6; i++) {
//            printCountOfNumber(i, results);
//        }

        // Viterbi
        if (!inputFromFile) {
            System.out.println("Podaj prawdopodobieństwo rozpoczęcia gry kostką uczciwą: ");
            try {
                fairDiceStartProbability = sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Podano nieprawidłową wartość");
                return;
            }
        }
        BWAlgorithm bw = new BWAlgorithm(numberOfRounds);
        //creating cassino and play rounds
        for (int i = 0; i < 100; i++) {
            Croupier croupier = new Croupier(fairDice, unfairDice, fairDiceStartProbability);
            Casino casino = new Casino(croupier);
            List<Integer> results = casino.playNRounds(numberOfRounds);
//        ViterbiAlgorithm va = new ViterbiAlgorithm(numberOfRounds, fairDice, unfairDice, results, fairDiceStartProbability);
//            List<String> casinoDices = croupier.getDices();
            bw.runAlgorithm(results);
        }
//        List <String> viterbiResults = va.createSequence();
//        System.out.println("Rezultat działania algorytmu Viterbiego: ");
//        System.out.println("liczba oczek na kostce : rzeczywistość : Viterbi");
//        for (int i = 0; i < results.size(); i++) {
//            System.out.println(results.get(i) + " : " +casinoDices.get(i) + " : " + viterbiResults.get(i));
//        }
//
//        //Prefix
//        PrefixAlgorithm prefixAlgorithm = new PrefixAlgorithm(fairDice, unfairDice, results);
//        //Start kostką uczciwą
//        prefixAlgorithm.calculateProbability(State.FAIR_DICE);
//        System.out.println("Macierz prawdopodobieństw dla algorytmu prefiksowego (kostka uczciwa):");
//        System.out.println(Arrays.deepToString(prefixAlgorithm.getProbabilityMatrix()));
//
//        prefixAlgorithm = new PrefixAlgorithm(fairDice, unfairDice, results);
//        //Start kostką nieuczciwą
//        prefixAlgorithm.calculateProbability(State.UNFAIR_DICE);
//        System.out.println("Macierz prawdopodobieństw dla algorytmu prefiksowego (kostka nieuczciwa):");
//        System.out.println(Arrays.deepToString(prefixAlgorithm.getProbabilityMatrix()));
//
//        //Suffix
//        SuffixAlgorithm suffixAlgorithm = new SuffixAlgorithm(fairDice, unfairDice, results);
//
//        System.out.println("Macierz prawdopodobieństw dla algorytmu sufiksowego (kostka uczciwa, start w połowie listy wynikowej):");
//        suffixAlgorithm.calculateProbability(results.size() / 2, State.FAIR_DICE);
    }

    private static void printCountOfNumber(final Number num, final List<Integer> list) {
        int count = 0;
        for(Integer number : list) {
            if (Objects.equals(number, num)) count++;
        }
        System.out.println(num + ": " + count);
    }

    public static Map<String, String> getInputDataFromFile () {
        System.out.println("Podaj ścieżkę do pliku wejściowego: ");
        String rawPath = sc.next();
        Path inputFilePath = Paths.get(rawPath.replaceAll("\"", ""));
        Map<String, String> content = new HashMap<>();

        try {
            Files.lines(inputFilePath)
                    .forEach(s -> content.put(s.split("=")[0].trim(), s.split("=")[1].trim()));
        } catch (NoSuchFileException e) {
            System.out.println("Nieprawidłowa ścieżka: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Wystąpił wyjątek: " + e);
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

    public static void printMatrix(double[][] matrix) {
        for (int  i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
