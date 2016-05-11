import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Chris Kjellqvist and Seth Dalton on 4/26/16.
 * Dictionary class that is used for obtaining words and scores for game play.
 */
public class Dictionary {
    //The scores that each letter yields
    private static final int[] scores = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    //Array for storing all of the words
    private static String[] dictionary;
    //Counts the number of letters that make up each word. Array of these counters.
    private static byte[][] letterDistributions;

    /**
     * Constructor that creates the dictionary and the parallel letter distribution array.
     *
     * @param textFile - name of text file
     * @throws IOException
     */
    public Dictionary(File textFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(textFile));
        String string;
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<byte[]> temp2 = new ArrayList<>();
        while ((string = bufferedReader.readLine()) != null) {
            temp.add(string);
            temp2.add(getLetterDistribution(string));
        }
        dictionary = new String[temp.size()];
        letterDistributions = new byte[temp2.size()][26];
        temp.toArray(dictionary);
        temp2.toArray(letterDistributions);
    }

    /**
     * Gets the letter distribution of a word.
     * @param word - word to get distribution of
     * @return  - the letter distribution
     */
    public static byte[] getLetterDistribution(String word) {
        byte[] distribution = new byte[26];
        for (char c : word.toCharArray()) {
            distribution[c - 97]++;
        }
        return distribution;
    }

    /**
     * Gets letter distribution of a hand.
     * @param hand - hand of Tiles
     * @return - letter distribution array
     */
    public static byte[] getLetterDistribution(Tile[] hand) {
        byte[] distribution = new byte[26];
        for (Tile t : hand) {
            distribution[t.letter - 97]++;
        }
        return distribution;
    }

    /**
     * Finds if the word is in the dictionary
     * @param word - word to check for
     * @return - whether or not the word is in the provided dictionary
     */
    public static boolean isWord(String word) {
        return Arrays.binarySearch(dictionary, word) > 0;
    }

    /**
     * Only used if computer goes first. Finds the best possible word with a given hand.
     * Code is simple and self explanatory.
     *
     * Also only used on first turn.
     *
     * @param distribution - letter distribution of hand
     * @return - best word
     */
    public static String findBestWord(byte[] distribution) {
        ArrayList<String> possibleWords = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
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

    /**
     * Finds the best word containing a letter given a letter distrubution.
     * You can also choose to not do certain words for whatever reason you
     * want.
     * @param distribution - hand letter distribution
     * @param c - fixed letter character
     * @param blacklist - words not to choose
     * @return - desired word
     */
    public static String findBestWordContaining(byte[] distribution, char c, ArrayList<String> blacklist) {
        ArrayList<String> possibleWords = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
        for (int i = 0; i < dictionary.length; i++) {
            if (isCompatible(distribution, letterDistributions[i])) {
                if (arContainsChar(dictionary[i].toCharArray(), c)) {
                    possibleWords.add(dictionary[i]);
                }
            }
        }
        for (String str : blacklist) {
            if (possibleWords.contains(str)) {
                possibleWords.remove(str);
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
        try {
            return possibleWords.get(maxIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns a good guess as to the score of a word. A lot faster and
     * easier to do that the other method of score retrieval.
     * @param str - word to score
     * @return - integer value of score
     */
    public static int getScore(String str) {
        int score = 0;
        for (char c : str.toCharArray()) {
            score += scores[c - 97];
        }
        return score;
    }

    /**
     * @param c - character to find score for
     * @return - integer value of score
     */
    public static int getLetterScore(char c) {
        return scores[c - 97];
    }

    /**
     * Finds whether or not you have enough letters to spell a
     * word with a given letter distribution.
     * @param hand - letter distribution of your hand
     * @param distribution - letter distribution of the word
     * @return - boolean
     */
    private static boolean isCompatible(byte[] hand, byte[] distribution) {
        for (int i = 0; i < 26; i++) {
            if (distribution[i] > hand[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that a char array contains a character.
     * @param ar - array of chars
     * @param c - character to check for
     * @return - boolean
     */
    private static boolean arContainsChar(char[] ar, char c) {
        for (char t : ar) {
            if (t == c) {
                return true;
            }
        }
        return false;
    }
}
