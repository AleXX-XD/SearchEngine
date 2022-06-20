package SearchEngineApp.utils;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateLemmasUtil
{
    public static HashMap<String,Integer> createLemmas (String text) throws IOException {
        LuceneMorphology luceneMorphRus= new RussianLuceneMorphology();
        LuceneMorphology luceneMorphEng= new EnglishLuceneMorphology();
        HashMap<String,Integer> lemmas = new HashMap<>();
        String[] stringArray = text.split("[^а-яА-ЯёЁa-zA-Z]");

        for(String string : stringArray) {
            if (string.matches("[а-яА-ЯёЁ]+")) {
                for (String word  : getLemmas(string, luceneMorphRus)) {
                    if (lemmas.containsKey(word)) {
                        int value = lemmas.get(word);
                        lemmas.put(word, value + 1);
                    } else {
                        lemmas.put(word, 1);
                    }
                }
            } else {
                for (String word  : getLemmas(string, luceneMorphEng)) {
                    if (lemmas.containsKey(word)) {
                        int value = lemmas.get(word);
                        lemmas.put(word, value + 1);
                    } else {
                        lemmas.put(word, 1);
                    }
                }
            }
        }
        return lemmas;
    }

    private static List<String> getLemmas (String string, LuceneMorphology luceneMorph) {
        List<String> rightWords = new ArrayList<>();
            if (!string.equals("")) {
                List<String> wordList = luceneMorph.getNormalForms(string.toLowerCase());
                List<String> wordInfo = luceneMorph.getMorphInfo(string.toLowerCase());
                String[] infoArray = wordInfo.get(0).split("[|\\s]+");
                if (!(infoArray[1].equals("l") || infoArray[1].equals("n") || infoArray[1].equals("o") || infoArray[1].equals("p"))) {
                    rightWords.addAll(wordList);
                    }
                }
        return rightWords;
    }
}
