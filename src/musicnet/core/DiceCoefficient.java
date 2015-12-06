package musicnet.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mt on 12/6/2015.
 * This class implements string comparison algorithm
 * based on character pair similarity
 * Source: http://www.catalysoft.com/articles/StrikeAMatch.html
 */
public final class DiceCoefficient {
    public static double percentMatch(String str1, String str2) {
        List<String> pairs1 = wordLetterPairs(str1.toUpperCase());
        List<String> pairs2 = wordLetterPairs(str2.toUpperCase());

        int intersection = 0;
        int union = pairs1.size() + pairs2.size();

        for (int i = 0; i < pairs1.size(); i++) {
            for (int j = 0; j < pairs2.size(); j++) {
                if (pairs1.get(i).equals(pairs2.get(j))) {
                    intersection++;
                    pairs2.remove(j);//Must remove the match to prevent "GGGG" from appearing to match "GG" with 100% success

                    break;
                }
            }
        }

        return (2.0 * intersection) / union;
    }

    /**
     * Gets all letter pairs for each
     * individual word in the string
     *
     * @param str
     * @return
     */
    private static List<String> wordLetterPairs(String str) {
        List<String> allPairs = new ArrayList<String>();

        // Tokenize the string and put the tokens/words into an array
        String[] words = str.split(" ");

        // For each word
        for (int w = 0; w < words.length; w++) {
            if (words[w] != null && words[w].length() > 0) {
                // Find the pairs of characters
                String[] pairsInWord = letterPairs(words[w]);

                for (int p = 0; p < pairsInWord.length; p++) {
                    allPairs.add(pairsInWord[p]);
                }
            }
        }

        return allPairs;
    }

    /**
     * Generates an array containing every
     * two consecutive letters in the input string
     *
     * @param str
     * @return
     */
    private static String[] letterPairs(String str) {
        int numPairs = str.length() - 1;

        String[] pairs = new String[numPairs];

        for (int i = 0; i < numPairs; i++) {
            pairs[i] = str.substring(i, i + 2);
        }

        return pairs;
    }
}
