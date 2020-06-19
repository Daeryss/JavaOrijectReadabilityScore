package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main (String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(System.in);
        Readability readability = new Readability();
        readability.readFile(args[0]);

        System.out.printf("Words: %d\n", readability.getWords());
        System.out.printf("Sentences: %d\n", readability.getSentences());
        System.out.printf("Characters: %d\n", readability.getCharacters());
        System.out.printf("Syllables: %d\n", readability.getSyllables());
        System.out.printf("Polysyllables: %d\n", readability.getPolysyllables());
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String action = s.nextLine();
        System.out.println();

        switch (action){
            case "ARI":
                readability.printAri();
                break;
            case "FK":
                readability.printFk();
                break;
            case "SMOG":
                readability.printSmog();
                break;
            case "CL":
                readability.prinCl();
                break;
            default:
                readability.printAri();
                readability.printFk();
                readability.printSmog();
                readability.prinCl();
                System.out.println();
                readability.printAverage();
        }
    }
}

class Readability {
    private int sentences = 0;
    private int words = 0;
    private int characters = 0;
    private int syllables = 0;
    private int polysyllables = 0;
    private double ari = 0;
    private double fk = 0;
    private double smog = 0;
    private double cl = 0;
    private double average = 0;
    private String ariLvl = "";
    private String fkLvl = "";
    private String smogLvl = "";
    private String clLvl = "";
    private Set<Character> vowel = Set.of('a', 'e', 'i', 'o', 'u', 'y');
    private ArrayList<Integer> listOfSyllables = new ArrayList<>();

    public void readFile (String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner s = new Scanner(file);
        StringBuilder textBuild = new StringBuilder();
        while (s.hasNext()) {
            String text = s.nextLine().trim();
            textBuild.append(text);
        }
        System.out.println();
        System.out.println();
        System.out.println(textBuild);
        System.out.println();
        System.out.println();
        String text = String.valueOf(textBuild);
        s.close();
        setSentences(text);
        setWords(text);
        setCharacters(text);
        setSyllables(text);
        setPolySyllables();
        setLevel();
    }

    private void setSentences (String text) {
        String[] sentList = text.split("[\\.\\?\\!]");
        ArrayList<String> sentArr = new ArrayList<>();
        for (String str : sentList) {
            if (str.length() > 0) {
                sentArr.add(str.trim());
            }
        }
        this.sentences = sentArr.size();
    }

    private void setWords (String text) {
        String[] sentList = text.split(" +");
        ArrayList<String> sentArr = new ArrayList<>();
        for (String str : sentList) {
            if (str.length() > 0) {
                sentArr.add(str.trim());
            }
        }
        this.words = sentArr.size();

    }

    private void setCharacters (String text) {
        String[] sentList = text.split(" +");
        for (String str : sentList) {
            char[] c = str.toCharArray();
            this.characters += c.length;
        }

    }

    private void setSyllables (String text) {
        String[] sent = text.split("[\\.\\?!]");
        for (String sentence : sent) {
            String[] wordList = sentence.split(" +");
            for (String str : wordList) {
                if (!str.equals("")) {
                    str = str.endsWith(",") ? str.substring(0, str.length() - 1) : str.toLowerCase();
                    String formatted = str.endsWith("e") ? str.substring(0, str.length() - 1) : str.toLowerCase();
                    boolean previous = false;
                    int syllableCount = 0;
                    for (char c : formatted.toCharArray()) {
                        if (vowel.contains(c)) {
                            if (!previous) {
                                syllableCount++;
                            }
                            previous = true;
                        } else {
                            previous = false;
                        }
                    }
                    if (syllableCount > 0) {
                        listOfSyllables.add(syllableCount);
                    } else {
                        listOfSyllables.add(1);
                    }
                }
            }
        }
    }

    private void setPolySyllables () {
        for (Integer n : listOfSyllables) {
            this.syllables += n;
            if (n > 2) {
                this.polysyllables ++;
            }
        }
    }

    private void setLevel () {
        this.ari = (4.71 * characters) / words + (0.5 * words) / sentences - 21.43;
        this.fk = (0.39 * words) / sentences + (11.8 * syllables) / words - 15.59;
        this.smog = 1.043 * Math.sqrt((polysyllables * 30.0 )/ sentences) + 3.1291;
        double L = (1.0 * characters / words) * 100;
        double S = (1.0 * sentences / words) * 100;
        this.cl = 0.0588 * L - 0.296 * S - 15.8;

        this.ariLvl = returnLvl(ari);
        this.fkLvl = returnLvl(fk);
        this.smogLvl = returnLvl(smog);
        this.clLvl = returnLvl(cl);

        double a = ariLvl.equals("24+") ? 25 : Integer.parseInt(ariLvl);
        double b = fkLvl.equals("24+") ? 25 : Integer.parseInt(fkLvl);
        double c = smogLvl.equals("24+") ? 25 : Integer.parseInt(smogLvl);
        double d = clLvl.equals("24+") ? 25 : Integer.parseInt(clLvl);
        this.average = (1.0 * (a + b + c  + d)) / 4;
    }

    private String returnLvl (double score) {
        if (score < 1.0) {
            return "6";
        } else if (score < 2.0) {
            return "7";
        } else if (score < 3.0) {
            return "9";
        } else if (score < 4.0) {
            return "10";
        } else if (score < 5.0) {
            return "11";
        } else if (score < 6.0) {
            return "12";
        } else if (score < 7.0) {
            return "13";
        } else if (score < 8.0) {
            return "14";
        } else if (score < 9.0) {
            return "15";
        } else if (score < 10.0) {
            return "16";
        } else if (score < 11.0) {
            return "17";
        } else if (score < 12.0) {
            return "18";
        } else if (score < 13.0) {
            return "24";
        } else {
            return "24+";
        }
    }

    public int getCharacters() {
        return characters;
    }

    public int getPolysyllables() {
        return polysyllables;
    }

    public int getSentences() {
        return sentences;
    }

    public int getSyllables() {
        return syllables;
    }

    public int getWords() {
        return words;
    }

    public void printAri () {
        System.out.printf("Automated Readability Index: %.2f (about %s year olds).\n", ari, ariLvl);
    }

    public void printFk () {
        System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds).\n", fk, fkLvl);
    }

    public void printSmog () {
        System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds).\n", smog, smogLvl);
    }

    public void prinCl () {
        System.out.printf("Coleman–Liau index: %.2f (about %s year olds).\n", cl, clLvl);
    }

    public void printAverage () {
        System.out.println();
        System.out.printf("This text should be understood in average by %.2f year olds.", average);
    }
}
