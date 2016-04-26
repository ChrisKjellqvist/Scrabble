import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chris on 4/26/16.
 */
public class Dictionary {
    private static final byte[] defaultArray = new byte[26];
    //@TODO make a dictionary
    private static ArrayList<String> dictionary = new ArrayList<>();
    private static ArrayList<byte[]> letterDistributions = new ArrayList<>();

    public Dictionary(File textFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(textFile));
        String string;
        while ((string = bufferedReader.readLine()) != null) {
            dictionary.add(string);
            letterDistributions.add(getLetterDistribution(string));
        }
    }

    public static byte[] getLetterDistribution(String word) {
        byte[] distribution = defaultArray.clone();
        for (char c : word.toCharArray()) {
            distribution[c - 97]++;
        }
        return distribution;
    }

    public static boolean isWord(String word) {
        return true;
    }
}
