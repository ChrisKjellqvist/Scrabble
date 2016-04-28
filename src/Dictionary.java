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
    //@TODO make a dictionary
    private static String[] dictionary;
    private static byte[][] letterDistributions;
    private static int[] scores = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

    public Dictionary(File textFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(textFile));
        String string;
        ArrayList<String> temp = new ArrayList<String>();
        ArrayList<byte[]> temp2 = new ArrayList<byte[]>();
        while ((string = bufferedReader.readLine()) != null) {
            temp.add(string);
            temp2.add(getLetterDistribution(string));
        }
        dictionary = new String[temp.size()];
        letterDistributions = new byte[temp2.size()][26];
        temp.toArray(dictionary);
        temp2.toArray(letterDistributions);
    }

    public static byte[] getLetterDistribution(String word) {
        byte[] distribution = defaultArray.clone();
        for (char c : word.toCharArray()) {
            distribution[c - 97]++;
        }
        return distribution;
    }

    public static boolean isWord(String word) {
        return Arrays.binarySearch(dictionary, word) > 0;
    }

    public static String findBestWord(byte[] distribution) {
        ArrayList<String> possibleWords = new ArrayList<String>();
        ArrayList<Integer> scores = new ArrayList<Integer>();
        System.out.println(Arrays.toString(distribution));
        for (int i = 0; i < dictionary.length; i++) {
            if (isCompatible(distribution, letterDistributions[i])) {
                possibleWords.add(dictionary[i]);
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
        for (int i = 0; i < dictionary.length; i++) {
            if (isCompatible(distribution, letterDistributions[i])) {
                possibleWords.add(dictionary[i]);
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

    private static boolean isCompatible(byte[] hand, byte[] distribution) {
        for (int i = 0; i < 26; i++) {
            if (distribution[i] > hand[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean arContainsChar(char[] ar, char c) {
        for (char t : ar) {
            if (t == c) {
                return true;
            }
        }
        return false;
    }
}
