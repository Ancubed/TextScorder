import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class TextScorder {
    private String fileName;
    private int sentencesCount;
    private int wordCount;
    private int symbolCount;
    private int syllablesCount;
    private  int polysyllablesCount;
    private double scoreARI;
    private double scoreFK;
    private double scoreSMOG;
    private double scoreCL;
    private int ageARI;
    private int ageFK;
    private int ageSMOG;
    private int ageCL;
    private String text;
    public TextScorder (String fileName) {
        this.fileName = fileName;
        this.sentencesCount = 0;
        this.wordCount = 0;
        this.symbolCount = 0;
        this.syllablesCount = 0;
        this.polysyllablesCount = 0;
        this.scoreARI = 0.0;
        this.scoreFK = 0.0;
        this.scoreSMOG = 0.0;
        this.scoreCL = 0.0;
        this.ageARI = 0;
        this.ageFK = 0;
        this.ageSMOG = 0;
        this.ageCL = 0;
        this.text = null;
    }
    private String readFileAsString() throws IOException {
        return new String(Files.readAllBytes(Paths.get(this.fileName)));
    }
    private void getText() {
        try {
            this.text = readFileAsString().trim();
        } catch (IOException e) {
            System.out.println("Cannot read file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    private void calculateSentenceWordSymbolsSyllables() {
        getText();
        if (this.text != null) {
            boolean lastCharacterPunctuationMark = true;
            String[] textArr = this.text.split("[\\.\\?!]");
            this.sentencesCount = textArr.length;
            this.wordCount = 0;
            this.symbolCount = this.sentencesCount;
            if (!this.text.substring(text.length() - 1).matches("[\\.\\?!]")) {
                this.symbolCount--;
            }
            for (String sentence : textArr) {
                String[] sentenceArr = sentence.trim().split("\\s+");
                this.wordCount += sentenceArr.length;
                for (String word : sentenceArr) {
                    this.symbolCount += word.length();
                    String[] symbolArr = word.split("");
                    String prevCharacter = "-";
                    int syllablesNow = 0;
                    boolean noSyllables = true;
                    for (int i = 0; i < symbolArr.length; i++) {
                        if (!prevCharacter.matches("[aeiouyAEIOUY]") && (!(i == symbolArr.length - 1)
                                && symbolArr[i].matches("[aeiouyAEIOUY]") || i == symbolArr.length - 1
                                && symbolArr[i].matches("[aiouyAIOUY]"))) {
                            this.syllablesCount++;
                            syllablesNow++;
                            noSyllables = false;
                        }
                        prevCharacter = symbolArr[i];
                    }
                    if (noSyllables) {
                        this.syllablesCount++;
                        syllablesNow++;
                    }
                    if (syllablesNow > 2) {
                        this.polysyllablesCount++;
                    }
                }
            }
        } else {
            System.out.println("File don`t open. Please, open file first.");
        }
    }
    private int getAge(double score) {
        HashMap<Integer, Integer> map = new HashMap<>(Map.of(1, 6, 2, 7, 3, 9, 4, 10, 5, 11, 6, 12, 7, 13, 8, 14, 9, 15, 10, 16));
        map.putAll(Map.of(11, 17, 12, 18, 13, 24, 14, 25));
        return map.get((int)Math.round(score));
    }
    private void calculateScoreARI() {
        this.scoreARI = 4.71 * (double)this.symbolCount / this.wordCount + 0.5 * (double)this.wordCount / this.sentencesCount - 21.43;
        this.ageARI = getAge(this.scoreARI);
    }
    private void calculateScoreFK() {
        this.scoreFK = 0.39 * (double)this.wordCount / this.sentencesCount + 11.8 * (double)this.syllablesCount / this.wordCount - 15.59;
        this.ageFK = getAge(this.scoreFK);
    }
    private void calculateScoreSMOG() {
        this.scoreSMOG = 1.043 * Math.sqrt((double)this.polysyllablesCount * 30.0 / this.sentencesCount) + 3.1291;
        this.ageSMOG = getAge(this.scoreSMOG);
    }
    private void calculateScoreCL() {
        double L = (double)this.symbolCount / this.wordCount * 100;
        double S = (double)this.sentencesCount / this.wordCount * 100;
        this.scoreCL = 0.0588 * L - 0.296 * S - 15.8;
        this.ageCL= getAge(this.scoreCL);
    }
    public void getResult(String fileName) {
        if (fileName != null) {
            this.fileName = fileName;
        }
        calculateSentenceWordSymbolsSyllables();
        System.out.print("Words: " + this.wordCount +
                "\nSentences: " + this.sentencesCount +
                "\nCharacters: " + this.symbolCount +
                "\nSyllables: " + this.syllablesCount +
                "\nPolysyllables: " + this.polysyllablesCount +
                "\nEnter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        switch (new Scanner(System.in).next()) {
            case ("ARI"):
                calculateScoreARI();
                System.out.println("\nAutomated Readability Index: " + this.scoreARI + " (about " + this.ageARI + " year olds).");
                break;
            case ("FK"):
                calculateScoreFK();
                System.out.println("\nFlesch–Kincaid readability tests: " + this.scoreFK + " (about " + this.ageFK + " year olds).");
                break;
            case ("SMOG"):
                calculateScoreSMOG();
                System.out.println("\nSimple Measure of Gobbledygook: " + this.scoreSMOG + " (about " + this.ageSMOG + " year olds).");
                break;
            case ("CL"):
                calculateScoreCL();
                System.out.println("\nColeman–Liau index: " + this.scoreCL + " (about " + this.ageCL + " year olds).");
                break;
            default:
                calculateScoreARI();
                calculateScoreFK();
                calculateScoreSMOG();
                calculateScoreCL();
                System.out.println("\nAutomated Readability Index: " + this.scoreARI + " (about " + this.ageARI + " year olds).");
                System.out.println("Flesch–Kincaid readability tests: " + this.scoreFK + " (about " + this.ageFK + " year olds).");
                System.out.println("Simple Measure of Gobbledygook: " + this.scoreSMOG + " (about " + this.ageSMOG + " year olds).");
                System.out.println("Coleman–Liau index: " + this.scoreCL + " (about " + this.ageCL + " year olds).");
                System.out.println("\nThis text should be understood in average by " + (double)(this.ageARI + this.ageFK + this.ageSMOG + this.ageCL)/4 + " year olds.");
                break;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        String fileName = "text.txt";
        //String fileName = args[0];
        TextScorder txtscrd = new TextScorder(fileName);
        txtscrd.getResult(null);
    }
}
