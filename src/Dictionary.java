import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by chris on 4/26/16.
 */
public class Dictionary {
    private static final byte[] defaultArray = new byte[26];
    //@TODO make a dictionary **DONE 4/28/16
    private static ArrayList<String> dictionary = new ArrayList<>();
    private static ArrayList<byte[]> letterDistributions = new ArrayList<>();
    private static int[] scores = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

    //creates the database holding all words and their letter distributions
    public Dictionary(File textfile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(textfile));
        String string;
        while ((string = bufferedReader.readLine()) != null) {
            dictionary.add(string);
            letterDistributions.add(getLetterDistribution(string));
        }
    }

    //Letter distribution is an array parallel to the 26 letters, refering to the number of times each letter appears in the word.
    public static byte[] getLetterDistribution(String word) {
        byte[] distribution = defaultArray.clone();
        for (char c : word.toCharArray()) {
            distribution[c - 97]++;
        }
        return distribution;
    }
    //Determines if a String is a word in the dictionary. 
    public static boolean isWord(String word) {
        for(int i = 0; i < dictionary.size(); i++){
            if(word.equalsIgnoreCase(dictionary.get(i))){
                return true;
            }
        }
        return false;
    }
    
    //Returns the word with the best score given a specific letter distribution.
    public static String findBestWord(byte[] distribution) {
        ArrayList<String> possibleWords = new ArrayList<String>();
        ArrayList<Integer> scores = new ArrayList<Integer>();
        System.out.println(Arrays.toString(distribution));
        for (int i = 0; i < dictionary.size(); i++) {
            if (isCompatible(distribution, letterDistributions.get(i))) {
                possibleWords.add(dictionary.get(i));
            }
        }
        for (int i = 0; i < possibleWords.size(); i++) {
            scores.add(i, getScore(possibleWords.get(i)));
        }
        int maxScore = 0;
        int maxIndex = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) > maxScore) {
                maxScore = scores.get(i);
                maxIndex = i;
            }
        }
        return possibleWords.get(maxIndex);
    }
    
    
    public static String findBestWordContaining(byte[] distribution, char c) {
        ArrayList<String> possibleWords = new ArrayList<String>();
        ArrayList<Integer> scores = new ArrayList<Integer>();
        System.out.println(Arrays.toString(distribution));
        for (int i = 0; i < dictionary.size(); i++) {
            if (isCompatible(distribution, letterDistributions.get(i))) {
                possibleWords.add(dictionary.get(i));
            }
        }
        for (int i = 0; i < possibleWords.size(); i++) {
            scores.add(i, getScore(possibleWords.get(i)));
        }
        int maxScore = 0;
        int maxIndex = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) > maxScore) {
                maxScore = scores.get(i);
                maxIndex = i;
            }
        }
        if (arContainsChar(possibleWords.get(maxIndex).toCharArray(), c)) {
            return possibleWords.get(maxIndex);
        } else {
            for (int i = 0; i < possibleWords.size(); i++) {
                if (arContainsChar(possibleWords.get(i).toCharArray(), c)) {
                    return possibleWords.get(i);
                }
            }
        }
        return null;
    }

    public static int getScore(String str) {
        int score = 0;
        for (char c : str.toCharArray()) {
            score += scores[c - 97];
        }
        return score;
    }

    //Determines if the hand is able to play the word in question.
    private static boolean isCompatible(byte[] hand, byte[] distribution) {
        for (int i = 0; i < 26; i++) {
            if (distribution[i] > hand[i]) {
                return false;
            }
        }
        return true;
    }

    //Checks if a character is present in a certain word.
    private static boolean arContainsChar(char[] ar, char c) {
        for (char t : ar) {
            if (t == c) {
                return true;
            }
        }
        return false;
    }
}
